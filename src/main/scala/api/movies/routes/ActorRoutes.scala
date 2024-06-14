package api.movies.routes

import cats.effect.Async
import cats.syntax.all._
import cats.FlatMap
//import cats.implicits._
import cats.Show

import api.movies.MoviesStore
//import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.Http4sDsl
import org.http4s.HttpRoutes

object ActorRoutes {

  def routes[F[_]: Async](moviesStore: MoviesStore[F]): HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    HttpRoutes.of[F] {
      /**
        * Get actors list
        */
      case GET -> Root / "api" / "actors" =>
        moviesStore
          .getAllMoviesActors
          .flatMap {
            case actors if actors.nonEmpty => Ok(actors.asJson)
            case _                         => NoContent()
          }
    }
  }

  FlatMap
  // instances for Show are defined in the companion object
  Show[Int].show(22)

  22.show
  // cats.implicits has both the extension methods and the instances
  // cats.syntax has only the extension methods

}
