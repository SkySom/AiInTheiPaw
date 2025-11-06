package io.sommers.aiintheipaw
package model.guild

import model.service.Service

case class Guild(
  id: Long,
  key: String,
  service: Service,
  displayName: String
) {

}
