package io.sommers.aiintheipaw
package event

import route.AiClient

import zio.json.JsonEncoder
import zio.{Duration, Task, URLayer, ZLayer}

trait EventScheduler {
  def schedule[EVENT](duration: Duration, handlerName: String, event: EVENT)(implicit jsonCodec: JsonEncoder[EVENT]): Task[Unit]
}

case class ZIOEventScheduler(aiClient: AiClient) extends EventScheduler {

  override def schedule[EVENT](duration: Duration, handlerName: String, event: EVENT)(implicit jsonCodec: JsonEncoder[EVENT]): Task[Unit] = {
    for {
      _ <- aiClient.callEventRouter(handlerName, event).delay(duration).fork
    } yield ()
  }
}

object EventScheduler {
  val zioLive: URLayer[AiClient, ZIOEventScheduler] = ZLayer.fromFunction(ZIOEventScheduler(_))
}
