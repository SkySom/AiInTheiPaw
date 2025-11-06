package io.sommers.zio.twitch

import client.{TwitchRestClient, TwitchRestClientConfig}
import server.{TwitchMessageHandler, TwitchNotificationHandler, TwitchWebHookConfig, TwitchWebHookRoutes}
import util.TwitchSignatureVerifierImpl

import zio._
import zio.http.Client

object ZIOTwitchLayers {
  val clientLive: ZLayer[Client, Config.Error, TwitchRestClient] =
    (TwitchRestClientConfig.live ++ ZLayer.service[Client]) >>> TwitchRestClient.live

  val webhookLive: ZLayer[TwitchNotificationHandler, Config.Error, TwitchWebHookRoutes] =
    (TwitchWebHookConfig.live ++ TwitchMessageHandler.live ++ (TwitchWebHookConfig.live >>> TwitchSignatureVerifierImpl.live)) >>> TwitchWebHookRoutes.live
}
