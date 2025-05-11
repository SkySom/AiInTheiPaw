package io.sommers.aiintheipaw
package twitch

import model.twitch.{SendTwitchMessageRequest, SendTwitchMessageResponse}

import zio.ZIO
import zio.http.{Body, Client, URL}
import zio.schema.codec.JsonCodec.schemaBasedBinaryCodec

trait TwitchRestClient {
  def sendMessage(request: SendTwitchMessageRequest): ZIO[Client, Throwable, SendTwitchMessageResponse]
}

class TwitchRestClientImpl extends TwitchRestClient {
  def sendMessage(request: SendTwitchMessageRequest): ZIO[Client, Throwable, SendTwitchMessageResponse] = {
    for {
      twitchUrl <- ZIO.fromEither(URL.decode("https://api.twitch.tv"))
      zClient <- ZIO.serviceWith[Client](_.url(twitchUrl))
      response <- ZIO.scoped(zClient.post("/chat/message")(Body.from(request)))
      responseBody <- response.bodyAs[SendTwitchMessageResponse]
    } yield responseBody
  }
}