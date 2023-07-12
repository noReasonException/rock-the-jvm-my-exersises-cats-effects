package pt2

import cats.effect.{IO, IOApp}
import cats.implicits._

object ParallelIOExample extends IOApp.Simple{

  def printAndPure(v:Int):Int={
    println(Thread.currentThread().getName+":IO("+v+")")
    v
  }


  val computation1:IO[Int] = IO.delay(printAndPure(12))
  val computation2:IO[Int] = IO.delay(Thread.sleep(1000)) >> IO.delay(printAndPure(42))


  val combinedComputation = (computation1,computation2).parMapN((a,b)=>{
    val sum = a+b
    printAndPure(sum)
    sum
  })



  override def run: IO[Unit] = combinedComputation.map(println)
}
