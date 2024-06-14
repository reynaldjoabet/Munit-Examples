import java.nio.file.NoSuchFileException

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Properties

class MySuite extends munit.FunSuite {

  test("hello") {
    val obtained = 42
    val expected = 42
    assertEquals(obtained, expected)
  }

  // implicit val ec: scala.concurrent.ExecutionContextExecutor = scala.concurrent.ExecutionContext.global
  test("async") {
    Future {
      println("Hello Internet!")
    }
  }

  test("sum of two integers") {
    val obtained = 2 + 2
    val expected = 4
    assertEquals(obtained, expected)
  }

  test("all even numbers") {
    val input: List[Int]           = List(1, 2, 3, 4)
    val obtainedResults: List[Int] = input.map(_ * 2)
    // check that obtained values are all even numbers
    assert(obtainedResults.forall(x => x % 2 == 0))
  }

  test("failing test") {
    val obtained = 2 + 3
    val expected = 4
    assertNotEquals(obtained, expected)
  }

  test("requests".flaky) {
    // I/O heavy tests that sometimes fail
  }

  test("addition") {
    assert(1 + 1 == 2)
  }
  test("multiplication") {
    assert(3 * 7 == 21)
  }

  test("remainder") {
    assert(13 % 5 == 3)
  }

  test("square") {
    for {
      squareOf3      <- AsyncMathLib.square(3)
      squareOfMinus4 <- AsyncMathLib.square(-4)
    } yield {
      assertEquals(squareOf3, 9)
      assertEquals(squareOfMinus4, 16)
    }
  }

}
