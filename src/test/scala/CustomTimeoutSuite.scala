import scala.concurrent.duration.Duration
import scala.concurrent.Future
class CustomTimeoutSuite extends munit.FunSuite {
  implicit val ec: scala.concurrent.ExecutionContextExecutor = scala.concurrent.ExecutionContext.global
  // await one second instead of default
   //override val munitTimeout = Duration(1, "s")
  test("slow-async") {
    Future {
      Thread.sleep(1000)
      // Test times out before `println()` is evaluated.
      println("pass")
    }
  }
}
