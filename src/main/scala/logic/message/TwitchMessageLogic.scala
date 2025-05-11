package io.sommers.aiintheipaw
package logic.message

import model.channel.Channel
import model.message.Message
import model.service.Service.Twitch
import model.service.{Service, TwitchService}

import zio.ZLayer

import scala.util.{Success, Try}

class TwitchMessageLogic(
  twitch: Twitch
) extends MessageLogic {

  override def sendMessage(channel: Channel, replyToId: Option[String], message: String): Try[Message] = {
    Success(new Message {
      override def getText: String = "hi"
    })
  }

  override val service: Service = twitch
}

object TwitchMessageLogic {
  def layer: ZLayer[TwitchService, Nothing, TwitchMessageLogic] = ZLayer.fromFunction(new TwitchMessageLogic(_))
}
