package io.sommers.aiintheipaw
package module

import io.sommers.aiintheipaw.logic.ChannelLogic
import izumi.distage.model.definition.ModuleDef

object LogicModule extends ModuleDef {
  make[ChannelLogic]

}
