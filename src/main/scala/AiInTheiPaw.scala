package io.sommers.aiintheipaw

import command.CommandManager
import command.sprint.SprintCommandGroup
import command.util.UtilCommandGroup
import event.{EventRouter, EventScheduler, ZIOEventScheduler}
import eventhandler.SprintEventHandler
import http.WebServer
import logic.*
import logic.message.{MessageLogic, TwitchServiceMessageLogic}
import route.{AiClient, EventRouterRoutes, MessageRoutes}
import service.*
import twitch.TwitchNotificationHandlerImpl

import io.sommers.zio.localize.{Localizer, ResourceProvider}
import io.sommers.zio.slick.DatabaseZIO
import io.sommers.zio.twitch.ZIOTwitchLayers
import slick.jdbc.PostgresProfile
import zio.config.typesafe.TypesafeConfigProvider
import zio.http.{Client, Server}
import zio.logging.backend.SLF4J
import zio.{Runtime, Scope, ZIO, ZIOAppArgs, ZIOAppDefault, ZLayer}

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
      commandGroupLayers(),
      CommandManager.live,
      SprintService.live,
      DatabaseZIO.live("slick", PostgresProfile),
      ResourceProvider.resourceBundleProvider("localization/localization") >>> Localizer.live,
      eventHandlerLayers(),
      EventScheduler.zioLive,
      EventRouter.live,
      AiClient.liveWeb,
      routeGroupLayers(),
      SprintConfig.live,
      SprintCommandLogic.live,
      BotSettingLogic.cachedLive,
      BotSettingService.live,
      GuildLogic.live,
      GuildService.live
    )
  }

  private def eventHandlerLayers() = ZLayer.collectAll(List(
    SprintEventHandler.live
  )
  )

  private def routeGroupLayers() = ZLayer.collectAll(List(
    MessageRoutes.live,
    EventRouterRoutes.live
  )
  )

  private def commandGroupLayers() = ZLayer.collectAll(List(
    SprintCommandGroup.fullLayer,
    UtilCommandGroup.fullLayer
  )
  )
}
