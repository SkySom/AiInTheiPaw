package io.sommers.aiintheipaw
package http.request

import http.directive.WithChannelId

import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class PostMessageRequest(
  channelId: Long,
  replyToId: Option[String],
  message: String
) extends WithChannelId

object PostMessageRequest extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val postMessageRequestFormat: RootJsonFormat[PostMessageRequest] = jsonFormat3(PostMessageRequest.apply)
}
