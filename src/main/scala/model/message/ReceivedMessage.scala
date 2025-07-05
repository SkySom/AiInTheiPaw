package io.sommers.aiintheipaw
package model.message

import model.channel.Channel
import model.service.Service
import model.user.{User, UserSource}

trait ReceivedMessage extends Message {
  val user: User

  lazy val userSource: UserSource = user.sources.find(_.service == service)
    .getOrElse(throw new IllegalArgumentException("Failed to find UserSource"))

  lazy val service: Service = channel.service

  val channel: Channel
  
  val messageId: String
}
