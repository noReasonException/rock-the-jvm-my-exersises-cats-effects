package pt2.recap

import cats.effect.{IO, IOApp}

object IORecap extends IOApp.Simple {

  //Read from keyboard
  //Read from keyboard
  //concat
  //print
  //terminate

  def readFromConsole():IO[String] = IO(System.in.readNBytes(12).mkString(""))


  override def run: IO[Unit] = {
    for{
      read <- readFromConsole()
      write <- IO.println(read)
    }yield ()
  }
}
