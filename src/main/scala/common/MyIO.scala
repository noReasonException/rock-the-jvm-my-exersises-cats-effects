


case class MyIO[A](unsafeRun:()=>A){
  def map[B](f:A=>B):MyIO[B] = MyIO[B](()=>f(unsafeRun()))
  def flatMap[B](f:A=>MyIO[B]):MyIO[B] = MyIO(f(unsafeRun()).unsafeRun)
}

