package io.sommers.zio.twitch

import client.{TwitchRestClient, TwitchRestClientConfig}

import zio._
import zio.http.Client

object ZIOTwitchLayers {
  val clientLive: ZLayer[Client, Config.Error, TwitchRestClient] =
    (TwitchRestClientConfig.live ++ ZLayer.service[Client]) >>> TwitchRestClient.live
}
