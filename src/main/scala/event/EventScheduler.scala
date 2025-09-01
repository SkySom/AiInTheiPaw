package io.sommers.aiintheipaw
package event

import zio.json.{JsonCodec, JsonEncoder}
import zio.{Clock, Duration, Task, ULayer, URIO, URLayer, ZIO, ZLayer}

trait EventScheduler {
  def schedule[EVENT](duration: Duration, handlerName: String, event: EVENT)(implicit jsonCodec: JsonEncoder[EVENT]): Task[Unit]
}

case class ZIOEventScheduler(eventRouter: EventRouter) extends EventScheduler {

  override def schedule[EVENT](duration: Duration, handlerName: String, event: EVENT)(implicit jsonCodec: JsonEncoder[EVENT]): Task[Unit] = {
    for {
      json <- ZIO.fromEither(jsonCodec.toJsonAST(event))
        .mapError(string => new IllegalStateException("Failed to parse event"))
      _ <- eventRouter.route(handlerName, json).delay(duration).fork
    } yield ()
  }
}

object EventScheduler {
  val zioLive: URLayer[EventRouter, ZIOEventScheduler] = ZLayer.fromFunction(ZIOEventScheduler(_))
}
