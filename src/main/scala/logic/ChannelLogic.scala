package io.sommers.aiintheipaw
package logic

import model.channel.Channel

import scala.concurrent.Future
import scala.util.{Failure, Try}

class ChannelLogic {
  def getChannel(id: Long): Future[Channel] = {
    Future.failed(new IllegalArgumentException("No id"))
  }
}
