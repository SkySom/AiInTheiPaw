package io.sommers.aiintheipaw
package model.service

import zio.{ULayer, ZLayer}


class Service(
  name: String
) {

}

case class TwitchService() extends Service("Twitch")

object Service {
  val twitch: ULayer[TwitchService] = ZLayer.succeed(TwitchService())
}
