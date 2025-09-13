package io.sommers.aiintheipaw
package route

import zio.http.{Response, Route}

trait RouteGroup {
  def routes: Seq[Route[Any, Response]]
}
