package snippets.fibers

import scala.concurrent.duration.DurationInt

import cats.effect.{IO, IOApp}

object FiberCancelationExample extends IOApp.Simple {

  override val run: IO[Unit] = {
    lazy val loop: IO[Unit] = IO.println("Hello, World!") >> loop

    loop.timeout(5.seconds) // Interrupt execution of fiber
  }

}
