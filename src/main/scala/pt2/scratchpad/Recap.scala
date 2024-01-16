package pt2.scratchpad

import cats.effect.{IO, IOApp}

import scala.concurrent.duration.DurationInt
import common.CommonExtensionMethods.{FibOutcomeOps, IODebugOps}

/**
 * Fibers
 *  0. How Blocking works in CE
 *    0.1 Semantic vs Actual Blocking
 *    0.2 MVP Proof
 *  1. Basic Intro
 *  2. Cancellation
 * Race
 * Bracket Pattern
 * Resources
 *
 */
object Recap extends IOApp.Simple {

  def longCompute:IO[Int] = IO.sleep(2.seconds) >> IO(142)
  def longCompute2:IO[Int] = IO.sleep(1.seconds) >> IO(242)
  def runBasic: IO[Unit] = {
    for{
      fib1 <- longCompute.myDebug.start
      fib2 <- fib1.cancel.myDebug.start
      jo1 <- fib1.join.flatMap(_.toIO)
      jo2 <- fib2.join.flatMap(_.toIO)
    }yield (jo1,jo2)
  }.flatMap(IO.println)
  def runCancel: IO[Unit] = {
    for{
      fib1 <- longCompute.myDebug.start
      fib2 <- fib1.cancel.myDebug.start
      jo1 <- fib1.join.flatMap(_.toIO)
      jo2 <- fib2.join.flatMap(_.toIO)
    }yield (jo1,jo2)
  }.flatMap(IO.println)

  override def run: IO[Unit] = {
    for{
      res <- IO.race(longCompute.myDebug,longCompute2.myDebug)
    }yield (res)
  }.flatMap(IO.println)

}
