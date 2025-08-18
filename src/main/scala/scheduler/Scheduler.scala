package io.sommers.aiintheipaw
package scheduler

import zio.{Clock, Duration, Task, ULayer, URIO, ZIO, ZLayer}

trait Scheduler {
  def schedule(duration: Duration, task: ZIO[Any, Nothing, Unit]): Task[Unit]
}

case class ZIOScheduler() extends Scheduler {

  override def schedule(duration: Duration, task: ZIO[Any, Nothing, Unit]): Task[Unit] = {
    for {
      _ <- task.delay(duration).fork
    } yield ()
  }
}

object Scheduler {
  val zioLive: ULayer[ZIOScheduler] = ZLayer.succeed(ZIOScheduler())
}
