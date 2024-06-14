package snippets.semigroup

import cats.implicits.catsSyntaxSemigroup
import cats.Semigroup

object SemigroupCustomExample extends App {

  final case class CustomClass(value: Int)

  object CustomClass {

    implicit val productIntSemigroup: Semigroup[CustomClass] =
      (x: CustomClass, y: CustomClass) => CustomClass(x.value * y.value)

  }

  print(CustomClass(3) |+| CustomClass(3)) // => CustomClass(9)

}
