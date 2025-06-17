package io.sommers.aiintheipaw
package model.user

import model.service.Service

case class User(
  id: Long,
  sources: List[UserSource]
)
