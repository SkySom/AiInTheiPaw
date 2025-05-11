package io.sommers.aiintheipaw
package module

import route.{CollectedRoutes, MessageRoutes}

import izumi.distage.model.definition.ModuleDef
import zio.http.URL

object MessagingModule extends ModuleDef {
  many[CollectedRoutes[Any with URL]].add[MessageRoutes]
}
