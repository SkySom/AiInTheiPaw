package io.sommers.aiintheipaw
package route

import event.{EventRouter, EventScheduler}
import model.problem.{JsonParseProblem, ProblemException}

import zio.json.JsonEncoder
import zio.{Duration, Task, URLayer, ZEnvironment, ZIO, ZLayer}

case class AiTestClient(
  eventRouter: EventRouter
) extends AiClient, EventScheduler {

  override def callEventRouter[EVENT](handlerName: String, event: EVENT)(implicit jsonCodec: JsonEncoder[EVENT]): Task[Unit] = {
    for {
      eventJson <- ZIO.fromEither(jsonCodec.toJsonAST(event))
        .mapError(JsonParseProblem(_))
      _ <- eventRouter.route(handlerName, eventJson)
        .provideEnvironment(ZEnvironment(this))
    } yield ()
  }.mapError(ProblemException(_))

  override def schedule[EVENT](duration: Duration, handlerName: String, event: EVENT)(implicit jsonCodec: JsonEncoder[EVENT]): Task[Unit] = for {
    _ <- callEventRouter(handlerName, event).delay(duration).fork
  } yield ()
}

object AiTestClient {
  def layer: URLayer[EventRouter, AiClient & EventScheduler] = ZLayer.fromFunction(AiTestClient.apply)
}
