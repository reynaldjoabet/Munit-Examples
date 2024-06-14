package api.movies.routes

import java.util.UUID

import cats.effect.Async
import cats.implicits._

import api.movies.Models.Director
import api.movies.MoviesStore
import api.movies.Utils.QueryParams.MovieIdQueryParamMatcher
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.circe.toMessageSyntax
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.Http4sDsl
import org.http4s.HttpRoutes

object DirectorRoutes {

  def routes[F[_]: Async](moviesStore: MoviesStore[F]): HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    HttpRoutes.of[F] {
      /**
        * Get all directors in stored movies
        */
      case GET -> Root / "api" / "directors" =>
        moviesStore
          .getAllMoviesDirectors
          .flatMap {
            case directors if directors.nonEmpty => Ok(directors.asJson)
            case _                               => NoContent()
          }

      /**
        * Update the director on a specific movie
        */
      case req @ PUT -> Root / "api" / "director" :? MovieIdQueryParamMatcher(movieId) =>
        moviesStore
          .getMovieById(UUID.fromString(movieId))
          .flatMap {
            case Some(_) =>
              req
                .decodeJson[Director]
                .flatMap(newDirector => moviesStore.updateDirectorInAMovie(movieId, newDirector))
                .flatMap(_ => Ok("Updated successfully"))
            case _ => NotFound(s"Movie with id $movieId not found")
          }
    }
  }

}
