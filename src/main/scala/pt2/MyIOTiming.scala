

object Timing {
  val currentTimeMills = MyIO[Long](()=>System.currentTimeMillis())
  def measure[A](computation:MyIO[A]):MyIO[Long]=for{
    currentTime <- currentTimeMills
    _ <- computation
    currentTimeAfterComputation <- currentTimeMills
  }yield currentTimeAfterComputation - currentTime

  def printInConsole(msg:String):MyIO[Unit] = MyIO(()=>println(msg))
  def readFromConsole():MyIO[String] = MyIO(()=>System.in.readNBytes(12).mkString(""))



  def main(args: Array[String]): Unit = {

    (for{
      read<-readFromConsole()
      write <- printInConsole(read)
    }yield ()).unsafeRun()

  }
}