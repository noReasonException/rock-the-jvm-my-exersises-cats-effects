package pt2.exersises

import cats.effect.kernel.Outcome
import cats.effect.{IO, IOApp}
import common.CommonExtensionMethods.IODebugOps

object FibersExercises extends IOApp.Simple{

  //Exercise 1
  def onAnotherThread[A](io:IO[A]):IO[A] = for{
    fibA <- io.start
    result <- fibA.join
    converted <- result match {
    case Outcome.Succeeded(fa) => fa
    case Outcome.Errored(e) => IO.raiseError(new Throwable(e.toString))
    case Outcome.Canceled() => IO.raiseError(new Throwable("Cancelled"))
    }
  }yield converted

  //Exercise 2

  //Exercise 3
  override def run: IO[Unit] = onAnotherThread(IO.println("Hello world").myDebug()).myDebug()
}
