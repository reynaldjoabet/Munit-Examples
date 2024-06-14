package snippets.sync

import java.util.concurrent.atomic.AtomicLong

import cats.effect.{ExitCode, IO, IOApp, Sync}

object DelayExample extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    val counter = new AtomicLong()

    for {
      counterIncremented <- Sync[IO].delay(counter.incrementAndGet())
      _                  <- IO.println(counterIncremented)
    } yield ExitCode.Success
  }

}
