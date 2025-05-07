package io.sommers.aiintheipaw
package route

import http.request.PostMessageRequest._
import logic.ChannelLogic
import logic.message.MessageLogic

import io.sommers.aiintheipaw.http.directive.ChannelDirectives
import io.sommers.aiintheipaw.http.request.PostMessageRequest
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.server.Route

case class MessageRoutes(
  channelLogic: ChannelLogic,
  messageLogics: Set[MessageLogic]
) extends CollectedRoutes with ChannelDirectives with SprayJsonSupport {


  override def routes: Route = pathPrefix("bot" / "message") {
    post {
      findChannel[PostMessageRequest](channelLogic)(sprayJsonUnmarshaller[PostMessageRequest]) { (request, channel) =>
        complete("")
      }
    }
  }
}
