package io.sommers.aiintheipaw
package model.service

import distage.Id
import zio.{ULayer, ZLayer}

class Service(
  name: String
) {

}

case class TwitchService() extends Service("Twitch")

object Service {
  type Twitch = Service @Id("Twitch")
}
