package io.sommers.aiintheipaw
package mock

import logic.message.MessageLogic
import model.channel.Channel
import model.message.{BotMessage, Message}
import model.problem.Problem

import zio.{IO, ULayer, ZIO, ZLayer}

import scala.collection.mutable

case class MessageLogicMock(

) extends MessageLogic {
  val sentMessages: mutable.MultiDict[Channel, Message] = mutable.MultiDict.newBuilder.result()
  
  def getSentText(channel: Channel): Seq[String] = sentMessages.get(channel)
    .map(_.text)
    .toSeq
  
  override def sendMessage(channel: Channel, replyToId: Option[String], message: String): IO[Problem, Message] = {
    val sentMessage = BotMessage(message)
    sentMessages += (channel -> sentMessage)
    ZIO.succeed(sentMessage)
  }
}

object MessageLogicMock {
  val mock: ULayer[MessageLogicMock] = ZLayer.succeed(MessageLogicMock())
}
