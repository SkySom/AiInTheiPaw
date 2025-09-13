package io.sommers.aiintheipaw

import command.CommandManager
import http.WebServer
import logic.message.{MessageLogic, TwitchServiceMessageLogic}
import logic.{ChannelLogic, SprintLogic, UserLogic}
import route.{AiClient, EventRouterRoutes, MessageRoutes}
import service.{ChannelServiceLive, SprintService, UserServiceLive}
import twitch.TwitchNotificationHandlerImpl

import io.sommers.aiintheipaw.event.{EventRouter, EventScheduler, ZIOEventScheduler}
import io.sommers.aiintheipaw.eventhandler.SprintEventHandler
import io.sommers.zio.localize.{Localizer, ResourceProvider}
import io.sommers.zio.slick.DatabaseZIO
import io.sommers.zio.twitch.ZIOTwitchLayers
import io.sommers.zio.twitch.server.{TwitchMessageHandler, TwitchWebHookConfig}
import slick.jdbc.PostgresProfile
import zio.config.typesafe.TypesafeConfigProvider
import zio.http.{Client, Server}
import zio.logging.backend.SLF4J
import zio.{Runtime, Scope, ZIO, ZIOAppArgs, ZIOAppDefault, ZLayer}

import java.util.Locale

object AiInTheiPaw extends ZIOAppDefault {
  override val bootstrap: ZLayer[Any, Nothing, Unit] = Runtime.setConfigProvider(TypesafeConfigProvider.fromResourcePath(true)) ++
    Runtime.removeDefaultLoggers >>> SLF4J.slf4j

  override def run: ZIO[ZIOAppArgs & Scope, Any, Any] = {
    (for {
      _ <- ZIO.serviceWithZIO[WebServer](_.serve())
    } yield ()).provide(
      WebServer.live,
      Server.default,
      ZIOTwitchLayers.clientLive,
      ChannelLogic.cachedLive,
      MessageLogic.fullLive,
      TwitchServiceMessageLogic.live,
      Client.default,
      TwitchNotificationHandlerImpl.layer,
      ZIOTwitchLayers.webhookLive,
      ChannelServiceLive.live,
      UserServiceLive.live,
      UserLogic.cachedLive,
      SprintLogic.live,
      CommandManager.fullLive,
      SprintService.live,
      DatabaseZIO.live("slick", PostgresProfile),
      ResourceProvider.resourceBundleProvider("localization/localization") >>> Localizer.live,
      eventHandlerLayers(),
      EventScheduler.zioLive,
      EventRouter.live,
      AiClient.liveWeb,
      routeGroupLayers()
    )
  }
  
  private def eventHandlerLayers() = ZLayer.collectAll(List(
    SprintEventHandler.live
  ))
  
  private def routeGroupLayers() = ZLayer.collectAll(List(
    MessageRoutes.live,
    EventRouterRoutes.live
  ))
}
