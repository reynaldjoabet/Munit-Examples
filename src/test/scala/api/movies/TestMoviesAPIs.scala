package api.movies

import api.movies.Models.{ Actor, Director, Movie }
import api.movies.Main.buildHttpApp
import org.http4s.client.UnexpectedStatus
import cats.effect.IO
import io.circe.Json
import org.http4s.Status.{ NotFound, Ok }
import org.http4s.{ Method, Request, Response, Uri }
import org.http4s.client.Client
import munit.CatsEffectSuite
import org.http4s.circe.CirceEntityCodec._
import io.circe.syntax._
import io.circe.generic.auto._
import io.circe._

class TestMoviesAPIs extends CatsEffectSuite {
  private val TOP_GUN_ID = "957675e9-5480-426f-83fb-4c1f0c7a060e"
  private val TITANIC_ID = "e73a99e4-2554-4d29-bd94-651b282e81ab"

  private val newMovie: Movie = Movie(
    "The Terminator",
    1984,
    List(Actor("Arnold", "Schwarzenegger", 10)),
    Director("James", "Cameron", "Canadian", 44),
    List("Action", "Horror", "Thriller", "Fantasy"),
    70_000_000,
    3
  )

  private var client: Option[Client[IO]] = None
  private val newDirector: Director      = Director("Peter", "Jackson", "New Zealand", 30)

  private def getUriFromPath(path: String): Uri =
    Uri.fromString(s"http://localhost:9090/api/$path").toOption.get

  override def beforeAll(): Unit = {
    val moviesStore = MoviesStore.createWithSeedData[IO].unsafeRunSync()
    client = Some(Client.fromHttpApp(buildHttpApp[IO](moviesStore)))
  }

  test("Wrong route") {
    val request: Request[IO] = Request(method = Method.GET, uri = getUriFromPath("wrong-path"))
    for {
      response <- client.get.expect[Json](request).attempt
      _ <- assertIO(
        IO(
          response.isLeft && response.left
            .exists(_.isInstanceOf[org.http4s.client.UnexpectedStatus])
        ),
        true
      )
    } yield ()
  }

  /* Movies crud */
  test("Get all movies") {
    val request: Request[IO] = Request(method = Method.GET, uri = getUriFromPath("movies"))
    for {
      json <- client.get.expect[Json](request)
      _    <- assertIO(IO(json.asArray.fold(0)(_.size)), 2)
      _ <- assertIO(
        IO(json.asArray.get.head.hcursor.downField("movie").get[String]("title").toOption),
        Some("Titanic")
      )
    } yield ()
  }

  test("Get movie by id") {
    val request: Request[IO] =
      Request(method = Method.GET, uri = getUriFromPath(s"movie/$TOP_GUN_ID"))
    for {
      json <- client.get.expect[Json](request)
      _ <- assertIO(
        IO(json.hcursor.downField("movie").get[String]("title").toOption),
        Some("Top Gun")
      )
    } yield ()
  }

  test("Create a new movie") {
    val request: Request[IO] =
      Request(method = Method.POST, uri = getUriFromPath("movie")).withEntity(newMovie.asJson)
    for {
      json <- client.get.expect[Json](request)
      _ <- assertIO(
        IO(json.hcursor.downField("movie").get[String]("title").toOption),
        Some(newMovie.title)
      )
    } yield ()
  }

  test("Update a movie") {
    val request: Request[IO] =
      Request(method = Method.PUT, uri = getUriFromPath(s"movie/$TOP_GUN_ID"))
        .withEntity(newMovie.asJson)
    for {
      json <- client.get.expect[Json](request)
      _ <- assertIO(
        IO(json.hcursor.downField("movie").get[String]("title").toOption),
        Some(newMovie.title)
      )
    } yield ()
  }

  test("Delete a movie") {
    val requestAllMoviesBefore: Request[IO] =
      Request(method = Method.GET, uri = getUriFromPath("movies"))
    val request: Request[IO] =
      Request(method = Method.DELETE, uri = getUriFromPath(s"movie/$TOP_GUN_ID"))
    val requestAllMoviesAfter: Request[IO] =
      Request(method = Method.GET, uri = getUriFromPath("movies"))
    for {
      jsonAllMoviesBefore <- client.get.expect[Json](requestAllMoviesBefore)
      moviesStoreSize     <- IO(jsonAllMoviesBefore.asArray.fold(0)(_.size))
      json                <- client.get.expect[Json](request)
      _ <- IO(json.asString.get).map(s =>
        assert(
          s == "Successfully deleted movie " +
            TOP_GUN_ID
        )
      )
      jsonAllMovies <- client.get.expect[Json](requestAllMoviesAfter)
      _             <- assertIO(IO(jsonAllMovies.asArray.fold(0)(_.size)), moviesStoreSize - 1)
    } yield ()
  }

  /* Movies list with filter */

  test("Get all movies by genre") {
    val request: Request[IO] =
      Request(method = Method.GET, uri = getUriFromPath("movies?genre=Disaster"))
    for {
      json <- client.get.expect[Json](request)
      _    <- assertIO(IO(json.asArray.fold(0)(_.size)), 1)
      _ <- assertIO(
        IO(json.asArray.get.head.hcursor.downField("movie").get[String]("title").toOption),
        Some("Titanic")
      )
    } yield ()
  }

  test("Get all movies by actor") {
    val request: Request[IO] =
      Request(method = Method.GET, uri = getUriFromPath("movies?actor=Kate%20Winslet"))
    for {
      json <- client.get.expect[Json](request)
      _    <- assertIO(IO(json.asArray.fold(0)(_.size)), 1)
      _ <- assertIO(
        IO(json.asArray.get.head.hcursor.downField("movie").get[String]("title").toOption),
        Some("Titanic")
      )
    } yield ()
  }

  test("Get all movies by director") {
    val request: Request[IO] =
      Request(method = Method.GET, uri = getUriFromPath("movies?director=James%20Cameron"))
    for {
      json <- client.get.expect[Json](request)
      _ <- assertIO(
        IO(json.asArray.fold(0)(_.size)),
        2
      ) // aspect 2 after general execution, aspect 1 on single execution
      _ <- assertIO(
        IO(json.asArray.get.head.hcursor.downField("movie").get[String]("title").toOption),
        Some("The Terminator")
      ) // aspect Titanic on single execution
    } yield ()
  }

  test("Get all movies by director and year") {
    val request: Request[IO] = Request(
      method = Method.GET,
      uri = getUriFromPath("movies?director=James%20Cameron&year=1997")
    )
    for {
      json <- client.get.expect[Json](request)
      _    <- assertIO(IO(json.asArray.fold(0)(_.size)), 1)
      _ <- assertIO(
        IO(json.asArray.get.head.hcursor.downField("movie").get[String]("title").toOption),
        Some("Titanic")
      )
    } yield ()
  }

  test("Get all movies by year") {
    val request: Request[IO] =
      Request(method = Method.GET, uri = getUriFromPath("movies?year=1997"))
    for {
      json <- client.get.expect[Json](request)
      _    <- assertIO(IO(json.asArray.fold(0)(_.size)), 1)
      _ <- assertIO(
        IO(json.asArray.get.head.hcursor.downField("movie").get[String]("title").toOption),
        Some("Titanic")
      )
    } yield ()
  }

  test("Get all movies by wrong year") {
    val request: Request[IO] =
      Request(method = Method.GET, uri = getUriFromPath("movies?year=1991aa"))
    for {
      response <- client.get.expect[Json](request).attempt
      _ <- assertIO(
        IO(
          response.isLeft && response.left
            .exists(_.isInstanceOf[org.http4s.client.UnexpectedStatus])
        ),
        true
      )
    } yield ()
  }

  test("Get the best movie by rating") {
    val request: Request[IO] = Request(method = Method.GET, uri = getUriFromPath("movies/rating"))
    for {
      json <- client.get.expect[Json](request)
      _    <- IO(json.asString.get).map(s => assert(s contains "Titanic"))
    } yield ()
  }

  /* Actors routes */
  test("Get all actors") {
    val request: Request[IO] = Request(method = Method.GET, uri = getUriFromPath("actors"))
    for {
      json <- client.get.expect[Json](request)
      _    <- IO(json.asArray.fold(0)(_.size)).map(size => assert(size > 0))
      _ <- IO(json.asArray.flatMap(_.headOption.flatMap(_.asObject))).map { maybeObject =>
        assert(maybeObject.exists(_.contains("firstName")))
        assert(
          maybeObject.exists(_("firstName").flatMap(_.asString).exists(_ == "Arnold"))
        ) // on single execution aspect Kate
      }
    } yield ()
  }

  /* Directors routes */
  test("Get all directors") {
    val request: Request[IO] = Request(method = Method.GET, uri = getUriFromPath("directors"))
    for {
      json <- client.get.expect[Json](request)
      _    <- IO(json.asArray.fold(0)(_.size)).map(size => assert(size == 2))
    } yield ()
  }

  test("Update director of a specific movie") {
    val request: Request[IO] =
      Request(method = Method.PUT, uri = getUriFromPath(s"director?movieId=$TITANIC_ID"))
        .withEntity(newDirector.asJson)
    for {
      json <- client.get.expect[Json](request)
      _    <- IO(json.asString.get).map(s => assert(s contains "Updated successfully"))
    } yield ()
  }

  test("Update director of a specific movie with wrong Id") {
    val request: Request[IO] =
      Request(method = Method.PUT, uri = getUriFromPath(s"director?movieId=$TITANIC_ID+1"))
        .withEntity(newDirector.asJson)
    for {
      response <- client.get.expect[Json](request).attempt
      _ <- assertIO(
        IO(
          response.isLeft && response.left
            .exists(_.isInstanceOf[java.lang.IllegalArgumentException])
        ),
        true
      )
    } yield ()
  }
}
