package pt2.race

import cats.effect.{IO, IOApp}
import cats.implicits.catsSyntaxApplicativeId

import scala.concurrent.duration.{DurationInt, FiniteDuration}

object RaceExersises extends IOApp.Simple {

  def timeout[A](ioa:IO[A],duration:FiniteDuration):IO[A] = {
    val timeoutJob = IO.sleep(duration)
    ioa.race(timeoutJob).flatMap{
      case Left(a)=>a.pure[IO]
      case Right(_) => IO.raiseError(new Throwable("Operation Timed Out"))
    }
  }

  override def run: IO[Unit] = timeout(IO.sleep(1.seconds),2.second).void
}
