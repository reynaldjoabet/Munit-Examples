package snippets.monads

import cats.instances.list._
import cats.instances.option._
import cats.syntax.all._
import cats.Monad

object MonadExample extends App {

  def increment[F[_]: Monad](value: F[Int]): F[Int] =
    Monad[F].flatMap(value)(x => Monad[F].pure(x + 1))

  val optionValue: Option[Int]       = Some(5)
  val incrementedOption: Option[Int] = increment(optionValue)
  println(incrementedOption) // => Some(6)

  val listValue: List[Int]       = List(1, 2, 3)
  val incrementedList: List[Int] = increment(listValue)
  println(incrementedList) // => List(2, 3, 4)

}
