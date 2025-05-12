package io.sommers.aiintheipaw
package twitch

import model.service.{Service, TwitchService}
import model.twitch.{DataResponse, SendTwitchMessageRequest, SendTwitchMessageResponse}

import io.sommers.aiintheipaw.http.exception.ServiceCallException
import zio.config.magnolia.deriveConfig
import zio.config.typesafe.TypesafeConfigProvider
import zio.http.Header.Authorization
import zio.http.{Body, Client, MediaType, URL}
import zio.schema.NameFormat.SnakeCase
import zio.schema.codec.BinaryCodec
import zio.schema.codec.JsonCodec.{Configuration, schemaBasedBinaryCodec}
import zio.{&, Config, IO, URLayer, ZIO, ZLayer}

trait TwitchRestClient {
  def sendMessage(
    broadcasterId: String,
    replyParentMessageId: Option[String],
    message: String
  ): IO[Throwable, SendTwitchMessageResponse]
}

class TwitchRestClientImpl(twitch: Service, twitchConfig: TwitchRestConfig, client: Client) extends TwitchRestClient {

  def sendMessage(
    broadcasterId: String,
    replyParentMessageId: Option[String],
    message: String
  ): IO[Throwable, SendTwitchMessageResponse] = {
    val request = SendTwitchMessageRequest(broadcasterId, twitchConfig.botId, replyParentMessageId, message)
    implicit val decoded: BinaryCodec[SendTwitchMessageRequest] = schemaBasedBinaryCodec[SendTwitchMessageRequest](Configuration(fieldNameFormat = SnakeCase))

    for {
      twitchUrl <- ZIO.fromEither(URL.decode("https://api.twitch.tv"))
      response <- ZIO.scoped(client.url(twitchUrl)
        .addHeader("Client-Id", twitchConfig.clientId)
        .addHeader(Authorization.Bearer(twitchConfig.clientSecret))
        .post("/helix/chat/messages")(Body.from(request)
          .contentType(Body.ContentType(MediaType.application.`json`))
        )
        .flatMap(_.bodyAs[DataResponse[SendTwitchMessageResponse]])
      )
      messageResponse <- ZIO.fromOption(response.data.headOption)
        .mapError(_ => new ServiceCallException("No data found on response", twitch))
    } yield new SendTwitchMessageResponse(messageResponse.messageId, true, None)
  }
}

case class TwitchRestConfig(
  botId: String,
  clientId: String,
  clientSecret: String
) {

}

object TwitchRestConfig {
  implicit val configServer: Config[TwitchRestConfig] = deriveConfig[TwitchRestConfig]

  val live: ZLayer[Any, Throwable, TwitchRestConfig] = ZLayer.fromZIO(TypesafeConfigProvider.fromResourcePathZIO(true)
    .map(_.nested("twitch"))
    .flatMap(_.load[TwitchRestConfig])
  )
}

object TwitchRestClient {
  val live: URLayer[TwitchService & TwitchRestConfig & Client, TwitchRestClient] = ZLayer.fromFunction(new TwitchRestClientImpl(_, _, _))
}