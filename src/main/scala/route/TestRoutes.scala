package io.sommers.aiintheipaw
package route

import model.error.NotFoundError

import zio.http.Status.NotFound
import zio.http.endpoint.Endpoint
import zio.http.{Response, Route, RoutePattern}
import zio.{ULayer, ZIO, ZLayer}

case class TestRoutes() extends CollectedRoutes[Any] {

  override def routes: Seq[Route[Any, Response]] = {
    Seq(botMessageRoute)
  }

  private val botMessageEndpoint = Endpoint(RoutePattern.POST / "test" / "echo")
    .in[Map[String, String]]
    .out[Map[String, String]]
    .outError[NotFoundError](NotFound)

  private val botMessageRoute: Route[Any, Response] = botMessageEndpoint.implement(string => {
    ZIO.succeed(string)
  })
}

object TestRoutes {
  val live: ULayer[TestRoutes] = ZLayer.succeed(TestRoutes())
}
