package snippets.sync

import scala.io.Source

import cats.effect.{ExitCode, IO, IOApp, Sync}

object BlockingExample extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    for {
      f <- Sync[IO].blocking(Source.fromResource("readme.txt"))
      t <- Sync[IO].blocking(f.mkString)
      _ <- Sync[IO].blocking(f.close()) >> IO.println(t)
    } yield ExitCode.Success

}
