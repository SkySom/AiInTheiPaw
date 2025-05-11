package io.sommers.aiintheipaw
package module

import logic.message.{MessageLogic, TwitchMessageLogic}
import model.service.{Service, TwitchService}

import izumi.distage.model.definition.ModuleDef

object TwitchModule extends ModuleDef {
  make[Service].named("Twitch").from(TwitchService())
  many[MessageLogic].add[TwitchMessageLogic]


}
