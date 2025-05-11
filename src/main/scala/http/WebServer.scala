package io.sommers.aiintheipaw
package http

import route.CollectedRoutes

import io.sommers.aiintheipaw.http.response.ProblemResponse
import zio.ZIO
import zio.http.{Response, Routes, Server, URL}

class WebServer(
  routes: Set[CollectedRoutes[Any with URL]]
) {
  def serve(): ZIO[Any, Throwable, Any] = {
    Server.serve(Routes.fromIterable[Any with URL, Response](routes.flatMap(_.routes)) @@ ProblemMiddleware.getFullUrl)
      .provide(Server.default)
  }
}

object WebServer {

}