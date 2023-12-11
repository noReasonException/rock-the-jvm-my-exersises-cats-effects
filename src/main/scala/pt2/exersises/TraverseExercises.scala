package pt2.exersises

import cats.effect.{IO, IOApp, Sync}
import cats.syntax.parallel._
import cats.syntax.traverse._
import cats.{Parallel, Traverse}
object TraverseExercises extends IOApp.Simple{

  def sequence[A](ios:List[IO[A]]):IO[List[A]] = ios.traverse(identity)
  def sequence2[C[_]:Traverse,A](ios:C[IO[A]]):IO[C[A]] = ios.traverse(identity)
  def sequence3[F[_]:Sync,C[_]:Traverse,A](in:C[F[A]]):F[C[A]] = in.traverse(identity)


  def parSequence[A](ios:List[IO[A]]):IO[List[A]] = ios.parTraverse(identity)
  def parSequence2[C[_]:Traverse,A](ios:C[IO[A]]):IO[C[A]] = ios.parTraverse(identity)




  def parSequence3[F[_]:Parallel,C[_]:Traverse,A](in:C[F[A]]):F[C[A]] = in.parTraverse(identity)



  override def run: IO[Unit] = IO.unit




}
