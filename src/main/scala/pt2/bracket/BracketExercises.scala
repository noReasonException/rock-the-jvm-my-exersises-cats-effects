package pt2.bracket
import cats.effect.{IO, IOApp}
import cats.implicits.catsSyntaxApplicativeId

import java.io.{File, FileReader}
import java.util.Scanner
import scala.annotation.tailrec
object BracketExercises extends IOApp.Simple {

  def openFileScanner(path: String): IO[Scanner] = {
    IO(new Scanner(new FileReader(new File(path))))
  }

  /***
    *
    *Read the file with the bracket pattern
    * 1) open a scanner
    * 2) read the file line by line, every 100 mils
    * 3) close the scanner
    * 4) if cancelled/throws error, also close the scanner
    */

  //   * Thats a great example on how to express natively procedural processes in a functional way
  def readScannerByLine(scanner: Scanner, delay: Long): IO[Unit] = {
    IO.delay {
      while (scanner.hasNext) {
        println(scanner.next())
        Thread.sleep(delay)
      }
    }
  }
  //   * Thats a more functional way
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
  override def run: IO[Unit] =
    bracketReadFile("src/main/scala/pt2/bracket/BracketExercises.scala").void
}
