import scala.concurrent.duration._

import cats.data.OptionT
import cats.effect._
import cats.effect.std.Random
import cats.implicits._
import fs2.Stream

//import com.felstar.openai.image.{CreateImageRequest, ImagesResponse}
import io.circe._
import io.circe.generic.auto._
import io.circe.literal._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io.{Ok, _}
import org.http4s.dsl.Http4sDsl
import org.http4s.ember.server
import org.http4s.headers.Authorization
import org.http4s.implicits._
import org.http4s.server.middleware._
import org.http4s.server.staticcontent._
import org.typelevel.ci._
import org.typelevel.ci.CIString
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.Logger

object MyMain extends IOApp.Simple {

  implicit def logger[F[_]: Sync]: Logger[F] = Slf4jLogger.getLogger[F]

  case class Greeting(message: String)

  val helloWorldService = HttpRoutes.of[IO] { case req @ GET -> Root / "hello" / name =>
    val remoteAddr = req.remoteAddr.map(ip => s"You are on $ip").getOrElse("")
    Logger[IO].info("/hello endpoint") *> Ok(s"Hello, $name. $remoteAddr")
  }

  object NameQueryParamMatcher extends QueryParamDecoderMatcher[String]("name")

  val helloWorldService2 = HttpRoutes.of[IO] {
    case GET -> Root / "hello" :? NameQueryParamMatcher(name) => Ok(s"""Hello, $name.""")
  }

  val greetService = HttpRoutes.of[IO] { case GET -> Root / "greet" =>
    Logger[IO].info("/greet endpoint") *> Ok(Greeting("hello there").asJson)
  }

  val literal = HttpRoutes.of[IO] { case GET -> Root / "literal" =>
    Ok(json"""{ "hello": "buddy" } """)
  }

  val randomDigitIO: IO[Int] = Random.scalaUtilRandom[IO].flatMap(_.nextIntBounded(10))

  val random = HttpRoutes.of[IO] { case GET -> Root / "random" =>
    Ok {
      for {
        int <- randomDigitIO
        _   <- Logger[IO].info(s"Random int is $int")
      } yield int.toString
    }
  }

  var myCounter: Int = 0

  // hack hack
  val counter = HttpRoutes.of[IO] { case GET -> Root / "counter" =>
    Ok {
      for {
        _ <- Logger[IO].info(s"Counter=$myCounter")
        _ <- IO.println(s"Counter=$myCounter ")
        _ <- IO {
               myCounter = myCounter + 1
             }
      } yield myCounter.toString
    }
  }

  val refIntIO: IO[Ref[IO, Int]] = Ref[IO].of(0)

  // better with use of Ref
  def counter2(counter: Ref[IO, Int]) = HttpRoutes.of[IO] { case GET -> Root / "counter2" =>
    Ok {
      for {
        i <- counter.getAndUpdate(_ + 1)
        _ <- IO.println(s"Counter=$i")
      } yield i.toString
    }
  }

  val echoPost = HttpRoutes.of[IO] { case req @ POST -> Root / "echo" =>
    Ok(req.body)
  }

  val lotsoftext = GZip(HttpRoutes.of[IO] { case GET -> Root / "gzip" =>
    Ok(s"ABCD " * 700)
  })

  val seconds = Stream.awakeEvery[IO](2.second)

  val mystream = HttpRoutes.of[IO] { case GET -> Root / "mystream" =>
    Ok(seconds.map(dur => dur.toString))
  }

//  val twirl = HttpRoutes.of[IO] {
//    case GET -> Root / "twirl" => Ok(view.html.index(s"hello from twirl ${new java.util.Date}"))
//  }

  val slow = HttpRoutes.of[IO] { case GET -> Root / "slow" =>
    Logger[IO].info("Sleeping") *> IO.sleep(4.seconds) *> Logger[IO]
      .info("Awake") *> Ok("I am slow, because I have been sleeping")
  }

  import scala.concurrent.ExecutionContext.global

  import org.http4s.client._
  import org.http4s.ember.client._

  case class Post(userId: Int, id: Int, title: String, body: String)

  val HOST = "https://jsonplaceholder.typicode.com"

  // If we choose to hand derive coder/decoder

  //  implicit val postDecoder = jsonOf[IO, Post]
  //  implicit val postEncoder= jsonEncoderOf[IO, Post]

  //  implicit val postsDecoder = jsonOf[IO, List[Post]]
  //  implicit val postsEncoder = jsonEncoderOf[IO, List[Post]]

  val clientRoute = HttpRoutes.of[IO] {
    // returning the json as a string
    case GET -> Root / "client" / "todos" =>
      val out: IO[String] = EmberClientBuilder
        .default[IO]
        .build
        .use { client =>
          val url = s"$HOST/todos"
          client.expect[String](url)
        }
      out.flatMap(str => Ok(str))
    case GET -> Root / "client" / "todos" / IntVar(id) =>
      val stringIO: IO[String] = EmberClientBuilder
        .default[IO]
        .build
        .use { client =>
          val url = s"$HOST/todos/$id"
          client.expect[String](url)
        }
      // we handle 404s, else seen as 500

      stringIO
        .flatMap(str => Ok(str))
        .recoverWith { case UnexpectedStatus(NotFound, _, _) =>
          NotFound()
        }

    // returning the json as a string
    case GET -> Root / "client" / "users" =>
      val out: IO[String] = EmberClientBuilder
        .default[IO]
        .build
        .use { client =>
          val url = s"$HOST/users"
          client.expect[String](url)
        }
      out.flatMap(str => Ok(str))
    case GET -> Root / "client" / "users" / IntVar(id) =>
      val stringIO: IO[String] = EmberClientBuilder
        .default[IO]
        .build
        .use { client =>
          val url = s"$HOST/users/$id"
          client.expect[String](url)
        }
      // we return a string with 200 on error
      stringIO.handleError(th => s"Something went wrong $th").flatMap(str => Ok(str))
    // returning as a case class
    case GET -> Root / "client" / "posts" =>
      // using codec to derive without having to do it myself
      import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
      import org.http4s.circe.CirceEntityCodec.circeEntityEncoder

      val postsIO = EmberClientBuilder
        .default[IO]
        .build
        .use { client =>
          val url = s"$HOST/posts"
          client.expect[List[Post]](url)
        }
      postsIO.flatMap(posts => Ok(posts))
    case GET -> Root / "client" / "posts" / IntVar(id) =>
      import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
      import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
      val postIO = EmberClientBuilder
        .default[IO]
        .build
        .use { client =>
          val url = s"$HOST/posts/$id"
          client.expect[Post](url)
        }

      // on error (of any type) we simply return some dummy post
      postIO
        .onError(th => Logger[IO].error(s"***Error*** $th"))
        .orElse {
          IO(Post(id, 1000, "", ""))
        }
        .flatMap(post => Ok(post))

    // calling ourself on the /slow endpoint
    // returning the text as a string
    case GET -> Root / "client" / "slow" =>
      val out: IO[String] = EmberClientBuilder
        .default[IO]
        .build
        .use { client =>
          val url = "http://localhost:8080/slow"
          client.expect[String](url)
        }
        .timeoutTo(1.seconds, IO.pure("Timedout so falling back to canned value"))
      out.flatMap(str => Ok(str))

    // calling ourself on the /slow endpoint, twice, in parallel
    // returning the text as a string
    case GET -> Root / "client" / "twiceslow" =>
      val outTuple = EmberClientBuilder
        .default[IO]
        .build
        .use { client =>
          val url = "http://localhost:8080/slow"
          (client.expect[String](url), client.expect[String](url)).parTupled
        }
      Logger[IO].info("/twiceslow endpoint ") *> outTuple
        .flatMap(tuple => Logger[IO].info("ended ") *> Ok(tuple._1 + "\n" + tuple._2))

    // case GET -> Root / "openai" / "dalle" / prompt =>
    //   import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
    //   import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
    //   import org.http4s.circe._
    //   import org.http4s.client.dsl.io._
    //   import org.http4s.headers._

    //   //val imageRequest=CreateImageRequest(prompt = prompt)

    //   val openAIKey: String =  sys.env.getOrElse("OPENAI_API_KEY", "ENTER_OPENAI_API_KEY")

    //   val postRequest = POST(
    //     imageRequest.asJson.deepDropNullValues,
    //     uri"https://api.openai.com/v1/images/generations" ,
    //     Header.Raw(CIString("Content-Type"),"application/json"),
    //     Authorization(Credentials.Token(AuthScheme.Bearer, openAIKey))
    //   )

    //  val imagesResponseIO: IO[String] = EmberClientBuilder.default[IO].build.use { client =>
    //      client.expect[String](postRequest)
    //   }

    //   val url: OptionT[IO, String] =for {
    //     imagesResponse <- OptionT.liftF(imagesResponseIO)
    //     url <- OptionT.fromOption[IO](imagesResponse.data.headOption.flatMap(_.url))
    //   } yield url

    //    import _root_.scalatags.Text.all._
    //    import org.http4s.scalatags._

    //   def image(url:String)= html(body(div(img(src:=url))))
    //    val notFound= html(body(div(h1("Not found"))))

    //   val finalResponse: IO[Response[IO]] = for {
    //     urlOption: Option[String] <- url.value
    //     response: Response[IO] <- urlOption.map(url => Ok()).getOrElse(Ok())
    //   } yield response

    //   finalResponse
  }

  val fs = resourceServiceBuilder[IO]("/").withPathPrefix("/fs").toRoutes

  val handleForm = HttpRoutes.of[IO] { case req @ POST -> Root / "handle_form" =>
    req.decode[UrlForm] { urlForm =>
      val userName = urlForm.getFirstOrElse("username", "")
      Logger[IO].info(s"$userName") *> Ok(s"Hello $userName")
    }
  }

  private val httpApp: IO[HttpApp[IO]] = {
    for {
      ref <- refIntIO
      routes: HttpRoutes[IO] =
        helloWorldService <+> helloWorldService2 <+> greetService <+> literal <+> lotsoftext <+>
          fs <+> mystream <+> echoPost <+> random <+> counter <+> counter2(
            ref
          ) <+> clientRoute <+> slow <+> handleForm
      rest: HttpApp[IO] = routes.orNotFound
    } yield rest
  }

  val run = {

    for {
      app <- httpApp
      _   <- IO.println("logging with a simple IO.println")
      _   <- Logger[IO].info("Logging with Logger[F]")
      _ <- server
             .EmberServerBuilder
             .default[IO]
             // .withHost(8080, host"localhost")
             .withHttpApp(app).build.useForever
    } yield ()
  }

}
