package io.sommers.aiintheipaw
package route

import http.request.{SendMessageRequest, SendMessageResponse}
import http.response.ProblemResponse
import logic.ChannelLogic
import logic.message.MessageLogic

import zio.{ZIO, ZNothing}
import zio.http.endpoint.{AuthType, Endpoint}
import zio.http.{Handler, Method, Request, Response, Route, RoutePattern, Status, URL, handler, withContext}

case class MessageRoutes(
  channelLogic: ChannelLogic,
  messageLogics: Set[MessageLogic]
) extends CollectedRoutes[Any with URL] {

  override def routes: Seq[Route[Any with URL, Response]] = Seq(
    botMessageRoute
  )

  private val botMessageEndpoint = Endpoint(RoutePattern.POST / "bot" / "message")
    .in[SendMessageRequest]
    .out[SendMessageResponse]

  private val botMessageRoute: Route[Any with URL, Response] = botMessageEndpoint.implement((request: SendMessageRequest) => {
    withContext((url: URL) => ZIO.succeed(SendMessageResponse(url.toString)))
  })
}


