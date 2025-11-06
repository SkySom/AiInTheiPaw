package io.sommers.zio.localize

import LocalizerSpec.test

import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue}
import zio.{Scope, ZIO}

import java.text.MessageFormat
import java.util.{Date, ListResourceBundle, Locale}

object LocalizerSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment & Scope, Any] = suite("ResourceLocalizerSpec")(
    test("It should localize a simple string with no args") {
      for {
        localizer <- ZIO.service[Localizer]
        localizedString <- localizer.localize(Locale.US, "test.noArgs")
      } yield assertTrue(localizedString == "Test NoArgs")
    },
    test("It should localize a simple string with one arg") {
      val date = new Date()
      for {
        localizer <- ZIO.service[Localizer]
        localizedString <- localizer.localize(Locale.US, "test.oneArg", date)
      } yield assertTrue(localizedString == MessageFormat.format("Test {0,time}", date))
    },
    test("It should localize a string with a localized arg") {
      val date = new Date()
      for {
        localizer <- ZIO.service[Localizer]
        localizedString <- localizer.localize(Locale.US, "test.localizedArg", localizer.localize(Locale.US, "test.oneArg", date))
      } yield assertTrue(localizedString == MessageFormat.format("Test Test {0,time}", date))
    },
    test("It should localize a string with a localized arg and a regular one") {
      val date = new Date()
      for {
        localizer <- ZIO.service[Localizer]
        localizedString <- localizer.localize(Locale.US, "test.localizedArgPlus", localizer.localize(Locale.US, "test.oneArg", date), 5)
      } yield assertTrue(localizedString == MessageFormat.format("Test Test {0,time} 5 choices", date))
    }
  ).provideLayerShared(
    ResourceProvider.resourceBundleProvider(testResourceBundle) >>> Localizer.live,
  )

  private val testResourceBundle = new ListResourceBundle() {
    override def getContents: Array[Array[AnyRef]] = Array(
      Array[AnyRef]("test.noArgs", "Test NoArgs"),
      Array[AnyRef]("test.oneArg", "Test {0,time}"),
      Array[AnyRef]("test.localizedArg", "Test {0}"),
      Array[AnyRef]("test.localizedArgPlus", "Test {0} {1,choice,0#no choice|1#one choice|1<{1,number,integer} choices}")
    )

    override def getLocale: Locale = Locale.US
  }
}

