package io.sommers.aiintheipaw
package http

import route.{MessageRoutes, RouteGroup}

import io.sommers.zio.twitch.server.{TwitchMessageHandler, TwitchWebHookConfig, TwitchWebHookRoutes}
import zio.http.{Middleware, Routes, Server}
import zio.{URLayer, ZIO, ZLayer}

case class WebServer(
  twitchWebHookRoutes: TwitchWebHookRoutes,
  routeGroups: List[RouteGroup]
) {
  def serve(): ZIO[Server, Throwable, Any] = Server.serve(
    Routes.fromIterable(
      twitchWebHookRoutes.routes ++ routeGroups.flatMap(_.routes)
    )
  )
}

object WebServer {
  val live: URLayer[TwitchWebHookRoutes & List[RouteGroup], WebServer] = ZLayer.fromFunction(WebServer(_, _))
}