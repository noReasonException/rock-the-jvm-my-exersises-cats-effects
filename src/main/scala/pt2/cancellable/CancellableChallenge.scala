package pt2.cancellable

import cats.effect.{IO, IOApp}
import common.CommonExtensionMethods.{FibOutcomeOps, IODebugOps}

import scala.concurrent.duration.{DurationInt, FiniteDuration}

object CancellableChallenge extends IOApp.Simple {

  /**
    * Challenge: Create the Same Auth Flow as the Tutorial
    * 1. User types for 5 seconds their password, this step is cancellable
    * 2. AuthService 'authenticates' the user by returning a true(Valid Password) or false (Invalid one) , Uncancellable
    */

  val userInput: IO[String] =
    IO("User Prompt").myDebug >> IO.sleep(1.seconds) >> IO(
      "User typed password"
    ).myDebug() >> IO("rockTheJvm1")
  val authProcess: String => IO[Boolean] = v =>
    IO("Auth Process Initiated").myDebug() >> IO.sleep(2.seconds) >> IO(
      "Auth Complete"
    ).myDebug() >> IO(v == "rockTheJvm1")

  val program = for {
    inp <- userInput.onCancel(IO("User Input TimedOut").myDebug().void)
    result <- authProcess(inp).onCancel(
      IO("AuthProcess should never cancelled : Please fix the state manually")
        .myDebug()
        .void
    )
  } yield result

  val safeProgramm = IO.uncancelable { poll =>
    for {
      inp <- poll(userInput).onCancel(IO("User Input TimedOut").myDebug().void)
      result <-
        authProcess(inp).onCancel(
          IO(
            "AuthProcess should never cancelled : Please fix the state manually"
          ).myDebug().void
        )
    } yield result
  }

  def cancelWrapper[A](program: IO[A], timeout: FiniteDuration) =
    for {
      fib <- program.start
      timeout <-
        IO.sleep(timeout) >> IO("Attempting Cancel").myDebug() >> fib.cancel
      join <- fib.join
    } yield join.toIO
  override def run: IO[Unit] =
    cancelWrapper(safeProgramm, 500.millis).myDebug().void
}
