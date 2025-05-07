package io.sommers.aiintheipaw
package route

import org.apache.pekko.http.scaladsl.server.Route

trait CollectedRoutes {
  def routes: Route
}
