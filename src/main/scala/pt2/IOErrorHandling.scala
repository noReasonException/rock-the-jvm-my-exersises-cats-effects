package pt2

import cats.effect.IO

import scala.util.{Failure, Success, Try}

object IOErrorHandling {

  def option2IO[A](option:Option[A])(ifEmpty:Throwable):IO[A] = option match {
    case Some(value) => IO.pure(value)
    case None => IO.raiseError(ifEmpty)
  }

  def try2IO[A](aTry:Try[A]):IO[A] = aTry match {
    case Failure(exception) => IO.raiseError(exception)
    case Success(value) => IO.pure(value)
  }

  def either2IO[A](either:Either[Throwable,A]):IO[A] = either match {
    case Left(value) => IO.raiseError(value)
    case Right(value) => IO.pure(value)
  }

  def handleIOError[A](io:IO[A])(handler:Throwable=>A):IO[A] = {
    io.attempt.map({
      case Left(value) => handler(value)
      case Right(value) => value
    })
  }

  def handleIOErrorWith[A](io:IO[A])(handler:Throwable=>IO[A]):IO[A] = {
    io.attempt.flatMap({
      case Left(value) => handler(value)
      case Right(value) => IO.delay(value)
    })
  }
  def main(args: Array[String]): Unit = {

  }
}
