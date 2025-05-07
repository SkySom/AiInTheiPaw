package io.sommers.aiintheipaw
package http.directive

import logic.ChannelLogic
import model.channel.Channel

import org.apache.pekko.http.scaladsl.server.Directive
import org.apache.pekko.http.scaladsl.server.Directives.{as, entity, onSuccess}
import org.apache.pekko.http.scaladsl.unmarshalling.FromEntityUnmarshaller

trait ChannelDirectives {
  def findChannel[T <: WithChannelId](channelLogic: ChannelLogic)(implicit unmarshaller: FromEntityUnmarshaller[T]): Directive[(T, Channel)] =
    entity(as[T])
      .flatMap { request =>
        onSuccess(channelLogic.getChannel(request.channelId))
          .map(channel => (request, channel))
      }
}

trait WithChannelId {
  val channelId: Long
}
