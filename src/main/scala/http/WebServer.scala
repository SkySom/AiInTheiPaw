package io.sommers.aiintheipaw
package http

import route.MessageRoutes

import io.sommers.zio.twitch.server.{TwitchMessageHandler, TwitchWebHookConfig, TwitchWebHookRoutes}
import zio.http.{Middleware, Routes, Server}
import zio.{&, URLayer, ZIO, ZLayer}

case class WebServer(
  messageRoutes: MessageRoutes,
  twitchWebHookRoutes: TwitchWebHookRoutes
) {
  def serve(): ZIO[TwitchWebHookConfig & TwitchMessageHandler & Server, Throwable, Any] = Server.serve(
    Routes.fromIterable(
      messageRoutes.routes ++ twitchWebHookRoutes.routes
    )
  )
}

object WebServer {
  val live: URLayer[MessageRoutes & TwitchWebHookRoutes, WebServer] = ZLayer.fromFunction(WebServer(_, _))
}