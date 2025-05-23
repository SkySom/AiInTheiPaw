package io.sommers.aiintheipaw
package route

import io.sommers.zio.twitch.server.TwitchWebHook
import zio.{&, ZLayer}
import zio.http.{HandlerAspect, Middleware, Response, Route, Routes}

trait CollectedRoutes[ENV] {
  def routes: Seq[Route[ENV, Response]]
}

trait RouteCollector {
  def allRoutes: Routes[Any, Response]
}

object RouteCollector {
  val live: ZLayer[MessageRoutes & TestRoutes, Nothing, RouteCollector] = ZLayer.fromFunction((routes: MessageRoutes, testRoutes: TestRoutes) => {
    new RouteCollector {
      override val allRoutes: Routes[Any, Response] = Routes.fromIterable(routes.routes ++ testRoutes.routes ++ Seq(TwitchWebHook.route("twitch/callback"))) @@ Middleware.requestLogging(logRequestBody = true, logResponseBody = true)
    }
  })
}
