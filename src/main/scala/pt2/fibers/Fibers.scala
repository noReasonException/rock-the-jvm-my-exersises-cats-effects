package pt2.fibers

import cats.effect.kernel.Outcome.{Canceled, Errored, Succeeded}
import cats.effect.{IO, IOApp}
import com.sun.net.httpserver.Authenticator.Failure
import common.CommonExtensionMethods.IODebugOps

object Fibers extends IOApp.Simple{


  val fiberWorkflow: IO[Unit] = for {
    fiba <- IO("Fib - A").delay(1000).myDebug().void.start
    fibb <- IO("Fib - B").myDebug().void.start
    _ <- fiba.cancel
    re1 <- fiba.join
    re2 <- fibb.join
    retval <- (re1, re2) match {
      case (Succeeded(a), Succeeded(b)) => IO.println(s"Done, $a,$b")
      case (Errored(a), _) => IO.println(s"A Failed , $a")
      case (_, Errored(b)) => IO.println(s"B Failed, $b")
      case (Canceled(), _) => IO.println(s"A Cancelled")
      case (_, Canceled()) => IO.println(s"B Cancelled")
    }
  } yield retval

  override def run: IO[Unit] = fiberWorkflow
}
