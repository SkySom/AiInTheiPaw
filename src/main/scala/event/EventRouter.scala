package io.sommers.aiintheipaw
package event

import model.problem.{InvalidValueProblem, Problem}
import util.Enrichment.EnrichOption

import zio.{IO, URLayer, ZLayer}
import zio.json.ast.Json

trait EventRouter {
  def route(handlerName: String, eventJson: Json): IO[Problem, Unit]
}

object EventRouter {
  def live: URLayer[List[EventHandler], EventRouterLive] = ZLayer.fromFunction(EventRouterLive(_))
}

case class EventRouterLive(eventHandlers: List[EventHandler]) extends EventRouter {

  override def route(handlerName: String, eventJson: Json): IO[Problem, Unit] = for {
    eventHandler <- eventHandlers.find(_.name == handlerName)
      .getOrZIOFail(InvalidValueProblem(s"No handler with name $handlerName found"))
    _ <- eventHandler.handleEvent(eventJson)
  } yield ()
}
