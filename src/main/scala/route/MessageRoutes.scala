package io.sommers.aiintheipaw
package route

import http.request.{SendMessageRequest, SendMessageResponse}
import logic.message.MessageLogic
import logic.{ChannelLogic, ServiceManager}
import model.problem.NotFoundProblem
import model.service.TwitchService
import util.Enrichment.EnrichEndpoint

import zio.http.Status.NotFound
import zio.http.endpoint.Endpoint
import zio.http.{Response, Route, RoutePattern, handler}
import zio.{URLayer, ZLayer}

case class MessageRoutes(
  channelLogic: ChannelLogic,
  messageLogics: ServiceManager[MessageLogic]
) {

  def routes: Seq[Route[Any, Response]] = Seq(
    botMessageRoute
  )

  private val botMessageEndpoint = Endpoint(RoutePattern.POST / "bot" / "message")
    .in[SendMessageRequest]
    .out[SendMessageResponse]
    .outError[NotFoundProblem](NotFound)

  private val botMessageRoute: Route[Any, Response] = botMessageEndpoint.implementWithProblem(
    handler(
      (request: SendMessageRequest) => {
        for {
          messageLogic <- messageLogics.get(TwitchService)
          message <- messageLogic.sendMessage(null, None, request.message)
        } yield new SendMessageResponse(message.getText)
      }
    )
  )
}

object MessageRoutes {
  val live: URLayer[ChannelLogic & ServiceManager[MessageLogic], MessageRoutes] = ZLayer.fromFunction(MessageRoutes(_, _))
}


