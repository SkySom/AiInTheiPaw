package io.sommers.zio.localize

import java.util.Locale

trait LocalizationConfig {
  val baseName: String

  val supportedLocales: List[Locale]
}

case class LocalizationConfigImpl(
  override val baseName: String,
  override val supportedLocales: List[Locale]
) extends LocalizationConfig {

}

