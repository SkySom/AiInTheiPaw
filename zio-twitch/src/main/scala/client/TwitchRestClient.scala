package io.sommers.zio.twitch.client

import io.sommers.zio.twitch.model.client.{DataResponse, SendTwitchMessageRequest, SendTwitchMessageResponse, TwitchClientError}
import zio.config.magnolia.deriveConfig
import zio.http.Header.Authorization
import zio.http.{Body, Client, MediaType, Status, URL}
import zio.schema.NameFormat.SnakeCase
import zio.schema.codec.BinaryCodec
import zio.schema.codec.JsonCodec.{Configuration, schemaBasedBinaryCodec}
import zio.{&, Config, IO, Layer, URLayer, ZIO, ZLayer}

trait TwitchRestClient {
  def sendMessage(
    broadcasterId: String,
    replyParentMessageId: Option[String],
    message: String
  ): IO[TwitchClientError, SendTwitchMessageResponse]
}

class TwitchRestClientImpl(twitchConfig: TwitchRestClientConfig, client: Client) extends TwitchRestClient {

  def sendMessage(
    broadcasterId: String,
    replyParentMessageId: Option[String],
    message: String
  ): IO[TwitchClientError, SendTwitchMessageResponse] = {
    val request = SendTwitchMessageRequest(broadcasterId, twitchConfig.botId, replyParentMessageId, message)
    implicit val decoded: BinaryCodec[SendTwitchMessageRequest] = schemaBasedBinaryCodec[SendTwitchMessageRequest](Configuration(fieldNameFormat = SnakeCase))

    for {
      twitchUrl <- ZIO.fromEither(URL.decode("https://api.twitch.tv"))
        .mapError(TwitchClientError(_))
      response <- ZIO.scoped(client.url(twitchUrl)
        .addHeader("Client-Id", twitchConfig.clientId)
        .addHeader(Authorization.Bearer(twitchConfig.clientSecret))
        .post("/helix/chat/messages")(Body.from(request)
          .contentType(Body.ContentType(MediaType.application.`json`))
        )
        .flatMap(response => {
          response.status match {
            case _: Status.Success => response.bodyAs[DataResponse[SendTwitchMessageResponse]]
            case error: Status.Error => ZIO.fail(new IllegalStateException(s"Found status ${error.code}"))
          }
        })
      ).mapError(error => TwitchClientError(error.getMessage, error))
      messageResponse <- ZIO.fromOption(response.data.headOption)
        .mapError(_ => TwitchClientError("No data found on response"))
    } yield new SendTwitchMessageResponse(messageResponse.messageId, true, None)
  }
}

case class TwitchRestClientConfig(
  botId: String,
  clientId: String,
  clientSecret: String
) {

}

object TwitchRestClientConfig {
  implicit val config: Config[TwitchRestClientConfig] = deriveConfig[TwitchRestClientConfig]

  val live: Layer[Config.Error, TwitchRestClientConfig] = ZLayer.fromZIO(ZIO.configProviderWith(_.nested("twitch")
    .load[TwitchRestClientConfig]
  ))
}

object TwitchRestClient {
  val live: URLayer[TwitchRestClientConfig & Client, TwitchRestClient] = ZLayer.fromFunction(new TwitchRestClientImpl(_, _))
}