package pt2.race

import cats.effect.kernel.Outcome
import cats.effect.{IO, IOApp}
import cats.implicits.{catsSyntaxApplicativeId, catsSyntaxEitherId}

import scala.concurrent.duration.{DurationInt, FiniteDuration}

object RaceExersises extends IOApp.Simple {


  private def fibToIo[A](outcome:Outcome[IO,Throwable,A]):IO[A] = outcome match {
    case Outcome.Succeeded(fa) => fa
    case Outcome.Errored(e) => IO.raiseError(new Throwable(e.toString))
    case Outcome.Canceled() => IO.raiseError(new Throwable("Cancelled"))
  }

  def timeout[A](ioa:IO[A],duration:FiniteDuration):IO[A] = {
    val timeoutJob = IO.sleep(duration)
    ioa.race(timeoutJob).flatMap{
      case Left(a)=>a.pure[IO]
      case Right(_) => IO.raiseError(new Throwable("Operation Timed Out"))
    }
  }

  //return the looser
  def unrace[A,B](ioa:IO[A],iob:IO[B]):IO[Either[A,B]] =
    ioa.racePair(iob).flatMap {
      case Left((leftWins,looserFiber)) => looserFiber.join.flatMap(v=>fibToIo(v).map(_.asRight))
      case Right((looserFiber,rightWins)) => looserFiber.join.flatMap(v=>fibToIo(v).map(_.asLeft))
    }

  override def run: IO[Unit] = unrace(IO.sleep(2.seconds)>>IO.delay("Left"),IO.sleep(1.seconds)>>IO.delay("Right")).flatMap(IO.println).void
}
