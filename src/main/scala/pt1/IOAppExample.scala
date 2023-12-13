package pt1

import cats.effect.{ExitCode, IO, IOApp}

object IOAppExample  extends IOApp{
  override def run(args: List[String]): IO[ExitCode] =
    IO
      .pure("Hello World")
      .map(println)
      .as(ExitCode.Success)
}


object IOAppSimpleExample extends IOApp.Simple {
  override def run: IO[Unit] = IO
    .pure("Hello World")
    .map(println)

}