package io.sommers.aiintheipaw

import http.WebServer
import logic.ChannelLogic
import logic.message.{MessageLogic, TwitchMessageLogic}
import model.service.Service
import route.{MessageRoutes, RouteCollector}
import twitch.{TwitchRestClient, TwitchRestConfig}

import zio.http.{Client, Server}
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object AiInTheiPaw extends ZIOAppDefault {


  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
    (for {
      _ <- ZIO.serviceWithZIO[WebServer](_.serve())
    } yield ()).provide(
      WebServer.live,
      Server.default,
      RouteCollector.live,
      MessageRoutes.live,
      ChannelLogic.live,
      MessageLogic.live,
      TwitchMessageLogic.live,
      TwitchRestClient.live,
      Service.twitch,
      TwitchRestConfig.live,
      Client.default
    )
  }
}
