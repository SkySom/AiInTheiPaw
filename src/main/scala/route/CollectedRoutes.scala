package io.sommers.aiintheipaw
package route

import zio.http.{Response, Route, URL}

trait CollectedRoutes[ENV] {
  def routes: Seq[Route[ENV, Response]]
}
