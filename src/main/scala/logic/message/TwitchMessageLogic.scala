package io.sommers.aiintheipaw
package logic.message

import model.channel.Channel
import model.message.Message
import model.service.{Service, Twitch, TwitchService}

import com.softwaremill.tagging._

import scala.util.{Success, Try}

class TwitchMessageLogic(
  twitch: Service @@ Twitch
) extends MessageLogic {

  override def sendMessage(channel: Channel, replyToId: Option[String], message: String): Try[Message] = {
    Success(new Message {
      override def getText: String = "hi"
    })
  }

  override val service: Service = twitch
}
