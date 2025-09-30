package io.sommers.aiintheipaw
package route

import event.{EventRouter, EventScheduler}
import model.problem.Problem

import zio.http.codec.PathCodec
import zio.http.*
import zio.json.ast.Json
import zio.schema.codec.JsonCodec.schemaBasedBinaryCodec
import zio.schema.codec.json.*
import zio.{URLayer, ZEnvironment, ZIO, ZLayer}

case class EventRouterRoutes(
  eventScheduler: EventScheduler,
  eventRouter: EventRouter
) extends RouteGroup {
  
  override def routes: Seq[Route[Any, Response]] = Seq(
    eventRouterRoute
  )

  def eventRouterRoute: Route[Any, Response] = Method.POST / "event" / PathCodec.string("name") -> eventRouterHandler

  def eventRouterHandler: Handler[Any, Response, (String, Request), Response] = Handler.fromFunctionZIO(
    (handlerName: String, request: Request) => {
      for {
        json <- request.body.to[Json]
          .mapError(Problem(_))
        _ <- eventRouter.route(handlerName, json)
          .provideEnvironment(ZEnvironment(eventScheduler))
      } yield Response(Status.NoContent)
    }.catchAll(problem => ZIO.succeed(Response(Status.InternalServerError, Headers(), Body.fromString(problem.message))))
  )
}

object EventRouterRoutes {
  val live: URLayer[EventScheduler & EventRouter, EventRouterRoutes] = ZLayer.fromFunction(EventRouterRoutes.apply)
}
