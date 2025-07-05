package io.sommers.aiintheipaw
package model.service

enum Service(val name: String):
  case Twitch extends Service("Twitch")
  case Test extends Service("Test")