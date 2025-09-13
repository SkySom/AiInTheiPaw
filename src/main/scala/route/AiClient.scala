package io.sommers.aiintheipaw
package route

import zio.http.{Body, Client, Request, Server}
import zio.json.JsonEncoder
import zio.{Task, ZIO, ZLayer}

trait AiClient {
  def callEventRouter[EVENT](handlerName: String, event: EVENT)(implicit jsonCodec: JsonEncoder[EVENT]): Task[Unit]
}

object AiClient {
  def liveWeb: ZLayer[Client & Server, Nothing, AiClientWeb] = ZLayer.fromFunction(AiClientWeb.apply)
}

case class AiClientWeb(
  client: Client,
  server: Server
) extends AiClient {

  override def callEventRouter[EVENT](handlerName: String, event: EVENT)(implicit jsonCodec: JsonEncoder[EVENT]): Task[Unit] = {
    for {
      port <- server.port
      _ <- ZIO.scoped {
        client.batched(
          Request.post(
            s"localhost:$port/event/$handlerName",
            Body.fromString(jsonCodec.encodeJson(event).toString)
          )
        )
      }
    } yield ()
  }
}
