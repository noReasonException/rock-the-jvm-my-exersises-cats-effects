package pt2.async

import cats.effect.{IO, IOApp}
import common.CommonExtensionMethods.IODebugOps

import scala.concurrent.{ExecutionContext, Future}

object AsyncExersises extends IOApp.Simple {


  def asyncToIO[A](computation: () => A)(implicit ec:ExecutionContext):IO[A]={
      //computation returns a A
      //runnable is a Unit
      //Therefore i need to put the callback call within the runnable
      IO.async_(cb =>{
        val ru = new Runnable {
          override def run(): Unit = {
            val re = computation()
            cb(Right(re))
          }
        }
        ec.execute(ru)
      })
    }

  def computation: Int = {
    println(s"${Thread.currentThread().getName} Process Started")
    (1 to (Math.pow(10, 3)).toInt).toList.sum
  }
  implicit val ec = ExecutionContext.global


  lazy val molFuture :Future[Int] = Future{computation}


  def liftFuture[A](future:()=>Future[A])(implicit ec:ExecutionContext):IO[A] = {
    IO.async_(cb=>{
      ec.execute {
        () => future().onComplete(res=>cb(res.toEither))
      }
    })
  }


  override def run: IO[Unit] = liftFuture(()=>molFuture).myDebug().flatMap(IO.println)
}
