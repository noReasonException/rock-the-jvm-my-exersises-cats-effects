package pt1

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.syntax.apply._
object IOExersises {

  def sequenceTakeLast[A,B](ioa:IO[A],iob:IO[B]):IO[B]=
    ioa.flatMap(_=>iob)

  def sequenceTakeLastV2[A,B](ioa:IO[A],iob:IO[B]):IO[B]=
    (ioa,iob).mapN((_,b)=>b)

  def sequenceTakeFirst[A,B](ioa:IO[A],iob:IO[B]):IO[A]=
    ioa.flatMap(a=>iob.map(_=>a))

  def sequenceTakeFirstV2[A,B](ioa:IO[A],iob:IO[B]):IO[A]=
    (ioa,iob).mapN((a,_)=>a)

  def forever[A](io:IO[A]):IO[A]=io.flatMap(_=>forever(io))

  def convert[A,B](ioa:IO[A],v:B):IO[B]=ioa.map(_=>v)

  def asUnit[A](ioa:IO[A]):IO[Unit]=convert(ioa,())

  def sumIO(n:Int):IO[Int] = {
    val curr = IO.pure(n)
    if(n<=0) curr
    else curr.flatMap(c=>sumIO(c-1).map(_+c))
  }

  def fibonacci(n:Int):IO[Int]={
    val curr = IO.pure(n)
    n match {
      case p if  p==0 || p==1 => curr
      case p => for{
        minus2 <- IO.delay(fibonacci(n-2)).flatten
        minus1 <- IO.delay(fibonacci(n-1)).flatten
      }yield minus2+minus1
    }
  }


  def main(args: Array[String]): Unit = {
    println(fibonacci(9).unsafeRunSync())
  }
}
