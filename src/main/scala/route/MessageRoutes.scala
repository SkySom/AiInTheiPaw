package io.sommers.aiintheipaw
package route

import http.request.{SendMessageRequest, SendMessageResponse}
import logic.ChannelLogic
import logic.message.MessageLogic
import model.problem.NotFoundProblem
import util.Enrichment.EnrichEndpoint

import zio.http.Status.NotFound
import zio.http.endpoint.Endpoint
import zio.http.{Response, Route, RoutePattern, handler}
import zio.{URLayer, ZLayer}

case class MessageRoutes(
  channelLogic: ChannelLogic,
  messageLogic: MessageLogic
) extends RouteGroup {

  override def routes: Seq[Route[Any, Response]] = Seq(
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
          channel <- channelLogic.getChannel(request.channelId)
          message <- messageLogic.sendMessage(channel, None, request.message)
        } yield new SendMessageResponse(message.text)
      }
    )
  )
}

object MessageRoutes {
  val live: URLayer[ChannelLogic & MessageLogic, MessageRoutes] = ZLayer.fromFunction(MessageRoutes(_, _))
}


