package common

import cats.effect.IO

object CommonExtensionMethods {
  implicit class IODebugOps[A](io:IO[A]){
    def myDebug():IO[A] = io.map(s"[${Thread.currentThread().getName}] "+_.toString).flatMap(IO.println)>>io
    def delay(mils:Long):IO[A] = IO.println(s"Adding Delay of $mils")>>IO.delay(Thread.sleep(mils)) >> io

  }



}
