package pt2.resource

import cats.effect.{IO, IOApp, Resource}

import java.io.{File, FileReader}
import java.util.Scanner
import scala.annotation.tailrec

object ResourceExercises extends IOApp.Simple {
  def openFileScanner(path: String): IO[Scanner] = {
    IO(new Scanner(new FileReader(new File(path))))
  }
  @tailrec
  def readScannerByLineFunctional(scanner: Scanner, delay: Long): IO[Unit] = {
    if (scanner.hasNext()) {
      println(scanner.next())
      Thread.sleep(delay)
      readScannerByLineFunctional(scanner, delay)
    } else IO.unit
  }
  def bracketReadFile(path: String): IO[Unit] =
    for {
      scanner <- openFileScanner(path)
        .bracket(readScannerByLineFunctional(_, 100))(x =>
          IO.println("Scanner Closes") >> IO.delay(x.close())
        )
    } yield scanner

  def scannerResource(path:String) = Resource.make(openFileScanner(path))(scanner=>IO.delay(scanner.close()))
  def resourceReadFile(path:String):IO[Unit] = scannerResource(path).use(readScannerByLineFunctional(_,100))
  override def run: IO[Unit] =
    resourceReadFile("src/main/scala/pt2/bracket/BracketExercises.scala").void

}
