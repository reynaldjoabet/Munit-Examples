import java.time.Instant
import munit.FunSuite
import munit.Printer

class CompareDatesOnlyTest extends FunSuite {

  test("dates only") {
    val expected = Instant.parse("2022-02-15T18:36:01.00Z") // "2022-02-15T18:35:24.00Z")
    val actual   = Instant.parse("2022-02-15T18:36:01.00Z")
    assertEquals(actual, expected) // true
  }
}
