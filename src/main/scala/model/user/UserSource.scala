package io.sommers.aiintheipaw
package model.user

import model.service.Service

case class UserSource(
  id: Long,
  userId: Long,
  service: Service,
  serviceUserId: String,
  displayName: String
)