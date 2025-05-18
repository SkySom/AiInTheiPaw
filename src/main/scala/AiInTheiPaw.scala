package io.sommers.aiintheipaw

import http.WebServer
import logic.{ChannelLogic, ConfigLogic}
import logic.message.{MessageLogic, TwitchMessageLogic}
import model.service.Service
import route.{MessageRoutes, RouteCollector, TestRoutes}

import io.sommers.zio.twitch.ZIOTwitchLayers
import zio.config.typesafe.TypesafeConfigProvider
import zio.http.{Client, Server}
import zio.{Runtime, Scope, ZIO, ZIOAppArgs, ZIOAppDefault, ZLayer}

object AiInTheiPaw extends ZIOAppDefault {
  override val bootstrap: ZLayer[Any, Nothing, Unit] = Runtime.setConfigProvider(TypesafeConfigProvider.fromResourcePath(true))

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
    (for {
      _ <- ZIO.serviceWithZIO[WebServer](_.serve())
    } yield ()).provide(
      WebServer.live,
      Server.default,
      ZIOTwitchLayers.clientLive,
      RouteCollector.live,
      MessageRoutes.live,
      ChannelLogic.live,
      MessageLogic.live,
      TwitchMessageLogic.live,
      Service.twitch,
      Client.default,
      TestRoutes.live
    )
  }
}
