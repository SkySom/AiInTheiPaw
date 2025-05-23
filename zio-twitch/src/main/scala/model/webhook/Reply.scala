package io.sommers.zio.twitch
package model.webhook

case class Reply(
  parentMessageId: String,
  parentMessageBody: String,
  parentUserId: String,
  parentUserName: String,
  parentUserLogin: String,
  threadMessageId: String,
  threadUserId: String,
  threadUserName: String,
  threadUserLogin: String
)
