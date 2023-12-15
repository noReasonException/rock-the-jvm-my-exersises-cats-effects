package common

import cats.effect.kernel.Outcome
import cats.effect.IO

object CommonExtensionMethods {
  implicit class IODebugOps[A](io:IO[A]){
    def myDebug():IO[A] = for{
      ret <- io
      _ <- IO.println(s"[${Thread.currentThread().getName}] "+ret.toString)
    }yield ret
    def delay(mils:Long):IO[A] = IO.println(s"Adding Delay of $mils")>>IO.delay(Thread.sleep(mils)) >> io
  }

  implicit class FibOutcomeOps[A](outcome:Outcome[IO,Throwable,A]){
    def toIO:IO[A] = outcome match {
      case Outcome.Succeeded(fa) => fa
      case Outcome.Errored(e) => IO.raiseError(new Throwable(e.toString))
      case Outcome.Canceled() => IO.raiseError(new Throwable("Cancelled"))
    }
  }



}
