package io.sommers.aiintheipaw
package logic

import zio.config.typesafe.TypesafeConfigProvider
import zio.{Config, ConfigProvider, IO, Layer, Trace, URLayer, ZLayer}

trait ConfigLogic {
  def load[CONF](path: String)(implicit trace: Trace, config: Config[CONF]): IO[Config.Error, CONF]
}

case class ConfigLogicImpl(
  configProvider: ConfigProvider
) extends ConfigLogic {

  override def load[CONF](path: String)(implicit trace: Trace, config: Config[CONF]): IO[Config.Error, CONF] =
    configProvider.nested(path)
      .load[CONF]
}

object ConfigLogic {
  val live: Layer[Throwable, ConfigLogic] = ZLayer.fromZIO(TypesafeConfigProvider.fromResourcePathZIO(true)
    .map(ConfigLogicImpl)
  )
}
