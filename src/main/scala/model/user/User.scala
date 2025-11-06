package io.sommers.aiintheipaw
package model.user

case class User(
  id: Long,
  sources: List[UserSource]
)
