package pt2.blocking

import cats.effect.{IO, IOApp}
import common.CommonExtensionMethods.IODebugOps

import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

object BlockingExersises extends IOApp.Simple {

  //Challenge: Implement the exact same thing as the course(no exercises provided so..)

  //Schematic Blocking Experimentation's
  //Check this, With IO.delay(Thread.sleep(1000)) schemantic blocking is not possible, it literally blocks!

  /** *
    * [io-compute-1] NonBlockingComputation 1
    * [io-compute-blocker-1] NonBlockingComputation 2
    */
  def someComputationsThreadSleep =
    for {
      _ <- IO.delay(Thread.sleep(1000))
      _ <- IO("NonBlockingComputation 1").myDebug()
      _ <- IO.delay(Thread.sleep(1000))
      _ <- IO("NonBlockingComputation 2").myDebug()
    } yield ()
  //Therefore, IO.sleep is not just a sleep, it assists with cooperative scheduling

  /**
    * [io-compute-3] NonBlockingComputation 1
    * [io-compute-0] NonBlockingComputation 2
    */
  def someComputationsIOSleep =
    for {
      _ <- IO.sleep(1.second)
      _ <- IO("NonBlockingComputation 1").myDebug()
      _ <- IO.sleep(1.second)
      _ <- IO("NonBlockingComputation 2").myDebug()
    } yield ()

  //Well thats cool with pure thunks, why there is not an api to capture a effectful operation such as..

  /**
    * IO.blocking(someIOs):IO[A]
    */
  def blockingFlow =
    for {
      _ <- IO.sleep(1.second)
      _ <- IO.blocking("NonBlockingComputation 1").myDebug()
      _ <- IO.sleep(1.second)
      _ <- IO.blocking("NonBlockingComputation 2").myDebug()
    } yield ()

  /**
    * [io-compute-blocker-2] NonBlockingComputation 1
    * [io-compute-blocker-6] NonBlockingComputation 2
    */

  //What about flatten?
  def blockingFlowFlatten =
    IO.blocking {

      for {
        _ <- IO.sleep(1.second)
        _ <- IO("NonBlockingComputation 1").myDebug()
        _ <- IO.sleep(1.second)
        _ <- IO("NonBlockingComputation 2").myDebug()
      } yield ()
    }.flatten

  //That wont work, as the scheduling op will remain blocking, not the individual job threads :thinkingFace

  val myEc: ExecutionContext =
    ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(3))

  //cede (ex: IO.swift for the CE2)
  /*
    [io-compute-6] NonBlockingComputation 1
    [io-compute-blocker-6] NonBlockingComputation 2
    [io-compute-blocker-6] NonBlockingComputation 3
   */
  def someComputationsNoCede =
    for {
      _ <- IO("NonBlockingComputation 1").myDebug()
      _ <- IO("NonBlockingComputation 2").myDebug()
      _ <- IO("NonBlockingComputation 3").myDebug()
    } yield ()

  /*
    [io-compute-6] NonBlockingComputation 1
    [io-compute-blocker-6] NonBlockingComputation 2
    [io-compute-blocker-6] NonBlockingComputation 3
   */
  def someComputationsCede =
    for {
      _ <- IO("NonBlockingComputation 1").myDebug()
      _ <- IO.cede
      _ <- IO("NonBlockingComputation 2").myDebug()
      _ <- IO("NonBlockingComputation 3").myDebug()
    } yield ()

  /**
   * //It actually worked without a custom ThreadPool! Noice!
   *  [io-compute-3] NonBlockingComputation 1
      [io-compute-2] NonBlockingComputation 2
      [io-compute-blocker-2] NonBlockingComputation 3
   * @return
   */
  override def run: IO[Unit] = someComputationsCede.void
}
