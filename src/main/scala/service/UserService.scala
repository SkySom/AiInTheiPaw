package io.sommers.aiintheipaw
package service

import java.sql.Timestamp

case class UserEntity(
  id: Long,
  createdAt: Timestamp,
  updatedAt: Timestamp
)

case class UserSourceEntity(
  id: Long,
  userId: Long,
  service: String,
  serviceUserId: String,
  createdAt: Timestamp,
  updatedAt: Timestamp
)