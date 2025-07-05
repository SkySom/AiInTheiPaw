package io.sommers.aiintheipaw

import database.DataSourceProvider
import http.WebServer
import logic.{ChannelLogic, UserLogic}
import logic.message.{MessageLogic, TwitchMessageLogic}
import route.MessageRoutes
import service.{ChannelServiceLive, UserServiceLive}
import twitch.TwitchNotificationHandlerImpl

import io.sommers.zio.twitch.ZIOTwitchLayers
import io.sommers.zio.twitch.server.{TwitchMessageHandler, TwitchWebHookConfig}
import zio.config.typesafe.TypesafeConfigProvider
import zio.http.{Client, Server}
import zio.{Runtime, Scope, ZIO, ZIOAppArgs, ZIOAppDefault, ZLayer}

object AiInTheiPaw extends ZIOAppDefault {
  override val bootstrap: ZLayer[Any, Nothing, Unit] = Runtime.setConfigProvider(TypesafeConfigProvider.fromResourcePath(true))

  override def run: ZIO[ZIOAppArgs & Scope, Any, Any] = {
    (for {
      _ <- ZIO.serviceWithZIO[WebServer](_.serve())
    } yield ()).provide(
      WebServer.live,
      Server.default,
      ZIOTwitchLayers.clientLive,
      MessageRoutes.live,
      ChannelLogic.cachedLive,
      MessageLogic.fullLive,
      TwitchServiceMessageLogic.live,
      Client.default,
      TwitchNotificationHandlerImpl.layer,
      TwitchWebHookConfig.live,
      TwitchMessageHandler.live,
      ZIOTwitchLayers.webhookLive,
      ChannelServiceLive.live,
      DataSourceProvider.transactorLive,
      UserServiceLive.live,
      UserLogic.cachedLive
    )
  }
}
