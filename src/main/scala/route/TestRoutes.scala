package io.sommers.aiintheipaw
package route

import zio.http.{Handler, Method, Request, Response, Route, handler}
import zio.{ULayer, ZIO, ZLayer}

case class TestRoutes() extends CollectedRoutes[Any] {

  override def routes: Seq[Route[Any, Response]] = Seq(
    Method.POST / "bot" / "echo" -> Handler.fromFunctionZIO { (request: Request) =>
      for {
        _ <- ZIO.log(request.headers.map(_.headerName).mkString(","))
      } yield Response.text("Hi")
    }
  )
}

object TestRoutes {
  val live: ULayer[TestRoutes] = ZLayer.succeed(TestRoutes())
}
