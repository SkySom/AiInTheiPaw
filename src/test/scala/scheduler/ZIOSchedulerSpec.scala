package io.sommers.aiintheipaw
package scheduler

import zio.test.{Spec, TestClock, TestEnvironment, ZIOSpecDefault, assertTrue}
import zio.{Promise, Scope, ZIO, durationInt}

object ZIOSchedulerSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment & Scope, Any] = suite("ZIOSchedulerSpec")(
    test("Scheduler#schedule works as expected") {
      for {
        promise <- Promise.make[Unit, Boolean]
        _ <- ZIO.serviceWithZIO[Scheduler](_.schedule(1.hour, fulfillPromise(promise)))
        startDone <- promise.isDone
        _ <- TestClock.adjust(1.hours)
        done <- promise.isDone
      } yield assertTrue(!startDone) && assertTrue(done)
    }.provide(Scheduler.zioLive)
  )

  private def fulfillPromise(value: Promise[Unit, Boolean]): ZIO[Any, Nothing, Unit] = {
    for {
      succeeded <- value.succeed(true)
    } yield ()
  }
}
