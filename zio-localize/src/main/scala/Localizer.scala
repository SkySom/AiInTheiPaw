package io.sommers.zio.localize

import zio.cache.{Cache, Lookup}
import zio.{Duration, Task, URLayer, ZEnvironment, ZIO, ZLayer}

import java.text.MessageFormat
import java.time.temporal.ChronoUnit
import java.util.Locale

trait Localizer {
  def localize(locale: Locale, key: String): AwaitingLocalization

  def localize(locale: Locale, key: String, args: Any*): AwaitingLocalization
}

object Localizer {
  val live: URLayer[ResourceProvider, ResourceLocalizer] = ZLayer.fromZIO(
    for {
      resourceProvider <- ZIO.service[ResourceProvider]
      cache <- Cache.makeWith[(Locale, String), ResourceProvider, Throwable, MessageFormat](
        capacity = Int.MaxValue,
        lookup = Lookup((locale, key) => for {
          localizedKey <- ZIO.serviceWithZIO[ResourceProvider](_.getString(locale, key))
          messageFormat <- ZIO.attempt(new MessageFormat(localizedKey))
        } yield messageFormat)
      ) { exit =>
        exit.foldExit(
          failed = _ => Duration(1, ChronoUnit.MINUTES),
          completed = _ => Duration(10, ChronoUnit.MINUTES)
        )
      }
    } yield ResourceLocalizer(resourceProvider, cache)
  )
}

type AwaitingLocalization = Task[String]

case class ResourceLocalizer(
  resourceProvider: ResourceProvider,
  cache: Cache[(Locale, String), Throwable, MessageFormat]
) extends Localizer {
  override def localize(locale: Locale, key: String): AwaitingLocalization = resourceProvider.getString(locale, key)

  override def localize(locale: Locale, key: String, args: Any*): AwaitingLocalization = {
    for {
      localizedKeys <- ZIO.foldLeft(args)(Array.empty[Any])((list, arg) => {
        for {
          handledArg <- ZIO.when(arg.isInstanceOf[AwaitingLocalization])(arg.asInstanceOf[AwaitingLocalization])
            .map(_.getOrElse(arg))
        } yield list.appended(handledArg)
      })
      messageFormat <- cache.get(locale, key)
      formattedMessage <- ZIO.attempt(messageFormat.format(localizedKeys))
    } yield formattedMessage
  }
}
