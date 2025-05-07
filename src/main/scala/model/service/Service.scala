package io.sommers.aiintheipaw
package model.service

class Service(
  name: String
) {

}

case class TwitchService() extends Service("Twitch") with Twitch

trait Twitch
