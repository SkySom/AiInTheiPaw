package io.sommers.aiintheipaw
package route

import http.request.{SendMessageRequest, SendMessageResponse}
import logic.message.MessageLogic
import logic.{ChannelLogic, ServiceManager}
import model.error.NotFoundError
import model.service.{Service, TwitchService}
import util.Enrichment.EnrichEndpoint

import zio.http.Status.NotFound
import zio.http.endpoint.Endpoint
import zio.http.{Response, Route, RoutePattern, handler}
import zio.{&, URLayer, ZLayer}

case class MessageRoutes(
  twitch: Service,
  channelLogic: ChannelLogic,
  messageLogics: ServiceManager[MessageLogic]
) extends CollectedRoutes[Any] {

  override def routes: Seq[Route[Any, Response]] = Seq(
    botMessageRoute
  )

  private val botMessageEndpoint = Endpoint(RoutePattern.POST / "bot" / "message")
    .in[SendMessageRequest]
    .out[SendMessageResponse]
    .outError[NotFoundError](NotFound)

  private val botMessageRoute: Route[Any, Response] = botMessageEndpoint.implementWithProblem(handler((request: SendMessageRequest) => {
    for {
      messageLogic <- messageLogics.get(twitch)
      message <- messageLogic.sendMessage(null, None, request.message)
    } yield new SendMessageResponse(message.getText)
  }))
}

object MessageRoutes {
  val live: URLayer[TwitchService & ChannelLogic & ServiceManager[MessageLogic], MessageRoutes] = ZLayer.fromFunction(MessageRoutes(_, _, _))
}


