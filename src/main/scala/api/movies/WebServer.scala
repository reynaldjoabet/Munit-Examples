package api.movies

import scala.concurrent.duration._

import cats.effect._
import cats.syntax.all._

import api.movies.routes.{ActorRoutes, DirectorRoutes, MovieRoutes}
import com.comcast.ip4s._
import org.http4s.{EntityEncoder, HttpApp, HttpRoutes}
import org.http4s.dsl.io._
import org.http4s.ember.server._
import org.http4s.implicits._
import org.http4s.server.Router
import org.typelevel.log4cats.noop.NoOpLogger

object Main extends IOApp {

  def createServer(app: HttpApp[IO]): IO[ExitCode] =
    EmberServerBuilder
      .default[IO]
      .withHttp2
      .withHost(ipv4"0.0.0.0")
      .withPort(port"9090")
      .withHttpApp(app)
      .withLogger(NoOpLogger[IO])
      .build
      .useForever
      .as(ExitCode.Success)

  def buildHttpApp[F[_]: Async](moviesStore: MoviesStore[F]): HttpApp[F] =
    (MovieRoutes.routes(moviesStore) <+> ActorRoutes.routes(moviesStore) <+> DirectorRoutes.routes(
      moviesStore
    )).orNotFound

  override def run(args: List[String]): IO[ExitCode] = for {
    moviesStore <- MoviesStore.createWithSeedData[IO]
    httpApp      = buildHttpApp(moviesStore)
    exitCode    <- createServer(httpApp)
  } yield exitCode

}
