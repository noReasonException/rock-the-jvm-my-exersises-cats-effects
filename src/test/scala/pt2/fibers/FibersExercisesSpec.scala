

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.implicits.catsSyntaxEitherId

import collection.mutable.Stack
import org.scalatest._
import flatspec._
import matchers._
import pt2.fibers.FibersExercises._

import scala.concurrent.duration.DurationInt

class FibersExercisesSpec extends AnyFlatSpec with should.Matchers {

  val expectedException: Throwable = new Throwable("Unexpected Exception")

  "onAnotherThread" should
    "Propagate success" in {
    val computation = IO.pure(12)
    onAnotherThread(computation).unsafeRunSync() shouldBe 12

  }
  "onAnotherThread" should
    "Propagate failure" in {
    val computation = IO.raiseError(new Throwable("Unexpected Error"))
    onAnotherThread(computation).attempt.unsafeRunSync().isLeft shouldBe true

  }
  "onAnotherThread" should
    "Transform cancellation as MonadError" in {
    val cancelledComputation = IO.canceled
    onAnotherThread(cancelledComputation).attempt.unsafeRunSync().isLeft shouldBe true
  }

  "tupleIOs" should
    "return a product of Success computations" in {
    val computation1 = IO.pure(42)
    val computation2 = IO.pure("Hello World")
    tupleIOs(computation1,computation2).unsafeRunSync() shouldBe (42,"Hello World")
  }
  "tupleIOs" should
    "return failure in case of left Throwable" in {
    val computation1: IO[Int] = IO.raiseError[Int](expectedException)
    val computation2 = IO.pure("Hello World")
    tupleIOs(computation1,computation2).attempt.unsafeRunSync() shouldBe expectedException.asLeft
  }
  "tupleIOs" should
    "return failure in case of right Throwable" in {
    val computation1: IO[Int] = IO.pure(42)
    val computation2 = IO.raiseError[Int](expectedException)
    tupleIOs(computation1,computation2).attempt.unsafeRunSync() shouldBe expectedException.asLeft
  }

  "timeout" should
    "should not cancel faster-than-timeout computations" in {
    val computation1: IO[Int] = IO.sleep(1.second) >> IO.pure(42)
    val t = 2.seconds
    timeout(computation1,t).unsafeRunSync() shouldBe 42
  }
  "timeout" should
    "should cancel slower-than-timeout computations" in {
    val computation1: IO[Int] = IO.sleep(2.second) >> IO.pure(42)
    val t = 1.seconds
    timeout(computation1,t).attempt.unsafeRunSync().isLeft shouldBe true
  }


}