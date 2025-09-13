package io.sommers.zio.localize

import zio.{Task, TaskLayer, ULayer, ZIO, ZLayer}

import java.util.{Locale, ResourceBundle}

trait ResourceProvider {
  def getString(locale: Locale, key: String): Task[String]
}

object ResourceProvider {
  def resourceBundleProvider(name: String): TaskLayer[ResourceProvider] = ZLayer.fromZIO(
    for {
      resourceBundle <- ZIO.attempt(ResourceBundle.getBundle(name))
    } yield ResourceBundleProvider(resourceBundle)
  )

  def resourceBundleProvider(resourceBundle: ResourceBundle): ULayer[ResourceProvider] =
    ZLayer.succeed(ResourceBundleProvider(resourceBundle))
}

case class ResourceBundleProvider(
  resourceBundles: Map[Locale, ResourceBundle]
) extends ResourceProvider {
  override def getString(locale: Locale, key: String): Task[String] = for {
    resourceBundle <- ZIO.getOrFailWith(new NoSuchElementException(s"No locale loaded for $locale"))(resourceBundles.get(locale).orElse(resourceBundles.get(Locale.ROOT)))
    string <- ZIO.attempt(resourceBundle.getString(key))
  } yield string
}

object ResourceBundleProvider {
  def apply(resourceBundle: ResourceBundle) = new ResourceBundleProvider(Map(resourceBundle.getLocale -> resourceBundle))
}
