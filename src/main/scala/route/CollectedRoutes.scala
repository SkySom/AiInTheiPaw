package io.sommers.aiintheipaw
package route

import zio.ZLayer
import zio.http.{Response, Route, Routes, URL}

trait CollectedRoutes[ENV] {
  def routes: Seq[Route[ENV, Response]]
}

trait RouteCollector {
  def allRoutes: Routes[Any, Response]
}

object RouteCollector {
  val live: ZLayer[MessageRoutes, Nothing, RouteCollector] = ZLayer.fromFunction((routes: MessageRoutes) => {
    new RouteCollector {
      override val allRoutes: Routes[Any, Response] = Routes.fromIterable(routes.routes)
    }
  })
}
