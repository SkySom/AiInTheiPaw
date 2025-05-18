package io.sommers.aiintheipaw
package http

import route.RouteCollector

import io.sommers.zio.twitch.server.TwitchWebHook
import zio.http.Server
import zio.{&, ULayer, ZIO, ZLayer}

case class WebServer() {
  def serve(): ZIO[Server & RouteCollector, Throwable, Any] = ZIO.serviceWithZIO[RouteCollector]((routeCollector: RouteCollector) =>
    Server.serve(routeCollector.allRoutes)
  )
}

object WebServer {
  val live: ULayer[WebServer] = ZLayer.succeed(WebServer())
}