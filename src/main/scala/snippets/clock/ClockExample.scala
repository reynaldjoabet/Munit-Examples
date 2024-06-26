package snippets.clock

import scala.concurrent.duration.DurationInt

import cats.effect.{Clock, ExitCode, IO, IOApp}

object ClockExample extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    for {
      start <- Clock[IO].realTime
      _     <- IO.sleep(3.seconds)
      end   <- Clock[IO].realTime
      _     <- IO.println(end - start) // 3014911 microseconds
    } yield ExitCode.Success

}
