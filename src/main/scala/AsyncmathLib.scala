import scala.concurrent.{ExecutionContext, Future}

object AsyncMathLib {
  def square(x: Int)(implicit ec: ExecutionContext): Future[Int] = Future(x * x)

}
