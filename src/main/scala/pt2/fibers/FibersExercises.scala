package pt2.fibers

import cats.effect.Outcome
import cats.effect.{IO, IOApp}
import cats.implicits.catsSyntaxTuple2Semigroupal
import common.CommonExtensionMethods.{FibOutcomeOps, IODebugOps}

import scala.concurrent.duration.FiniteDuration

object FibersExercises extends IOApp.Simple{



  //Exercise 1
  def onAnotherThread[A](io:IO[A]):IO[A] = for{
    fibA <- io.start
    result <- fibA.join
    converted <- result.toIO
  }yield converted

  //Exercise 2
  def tupleIOs[A,B](ioa:IO[A],iob:IO[B]):IO[(A,B)]= for{
    fiba <- ioa.start
    fibb <- iob.start
    resulta <- fiba.join
    resultb <- fibb.join
    transformed <- (resulta,resultb) match {
      case (Outcome.Canceled(),Outcome.Canceled())=>IO.raiseError(new Throwable("Runtime Error"))
      case (Outcome.Errored(_),Outcome.Errored(_))=>IO.raiseError(new Throwable("Runtime Error"))
      case (Outcome.Errored(e),_)=>IO.raiseError(e)
      case (Outcome.Canceled(),_)=>IO.raiseError(new Throwable("FIB-A Cancelled"))
      case (_,Outcome.Errored(e))=>IO.raiseError(e)
      case (_,Outcome.Canceled())=>IO.raiseError(new Throwable("FIB-B Cancelled"))
      case (Outcome.Succeeded(a),Outcome.Succeeded(b))=>a.product(b)
    }

  }yield transformed

  //Exercise 3
  def timeout[A](ioa:IO[A],timeout:FiniteDuration):IO[A] = for{
    fiba <- ioa.start
    _ <- (IO.sleep(timeout) >> fiba.cancel).start
    result <- fiba.join
    converted <- result.toIO
  }yield converted
  override def run: IO[Unit] = onAnotherThread(IO.println("Hello world").myDebug()).myDebug()
}
