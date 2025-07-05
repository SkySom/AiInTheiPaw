package io.sommers.aiintheipaw
package model.message

import model.user.User

import io.sommers.aiintheipaw.model.channel.Channel
import io.sommers.aiintheipaw.model.service.Service

case class BasicReceivedMessage(
  override val user: User,
  override val channel: Channel,
  override val messageId: String,
  override val text: String
) extends ReceivedMessage
