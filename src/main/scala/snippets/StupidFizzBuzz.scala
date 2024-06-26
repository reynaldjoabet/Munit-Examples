package snippets

import scala.concurrent.duration._

import cats.effect.{IO, IOApp}

object StupidFizzBuzz extends IOApp.Simple {

  val run =
    for {
      ctr <- IO.ref(0)

      wait = IO.sleep(1.second)
      poll = wait *> ctr.get

      _ <- poll.flatMap(IO.println(_)).foreverM.start
      _ <- poll.map(_ % 3 == 0).ifM(IO.println("fizz"), IO.unit).foreverM.start
      _ <- poll.map(_ % 5 == 0).ifM(IO.println("buzz"), IO.unit).foreverM.start

      _ <- (wait *> ctr.update(_ + 1)).foreverM.void
    } yield ()

}

/*
 * The *> operator combines two effectful computations (F[A] and F[B]),
 * discards the result of the first computation (F[A]),
 * and returns the result of the second computation (F[B]).
 *
 * */
