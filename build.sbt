ThisBuild / organization := "io.sommers"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.7.1"

lazy val zioVersion = "2.1.20"

lazy val zioConfigVersion = "4.0.4"
lazy val zioHttpVersion = "3.4.0"
lazy val zioSchemaVersion = "1.7.4"

lazy val zioDependencies = Seq(
  "dev.zio" %% "zio" % zioVersion,
  "dev.zio" %% "zio-test" % zioVersion % Test,
  "dev.zio" %% "zio-test-sbt" % zioVersion % Test,
)

lazy val zioHttpDependencies = Seq(
  "dev.zio" %% "zio-http" % zioHttpVersion,
  "dev.zio" %% "zio-http-testkit" % zioHttpVersion % Test,
  "dev.zio" %% "zio-schema" % zioSchemaVersion,
  "dev.zio" %% "zio-schema-json" % zioSchemaVersion,
)

lazy val zioConfigDependencies = Seq(
  "dev.zio" %% "zio-config" % zioConfigVersion,
  "dev.zio" %% "zio-config-magnolia" % zioConfigVersion,
)

lazy val zioDBDependencies = Seq(
  "org.postgresql" % "postgresql" % "42.7.7",
  "com.zaxxer" % "HikariCP" % "7.0.2",
  "com.typesafe.slick" %% "slick" % "3.6.1",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.6.1",
  "com.github.tminglei" %% "slick-pg" % "0.23.1"
)

lazy val zioCacheDependencies = Seq(
  "dev.zio" %% "zio-cache" % "0.2.4"
)

lazy val root = (project in file("."))
  .aggregate(zioLocalize, zioSlick, zioTwitch)
  .dependsOn(zioLocalize, zioSlick, zioTwitch)
  .settings(
    name := "AiInTheiPaw",
    idePackagePrefix := Some("io.sommers.aiintheipaw"),
    libraryDependencies ++= zioDependencies ++ zioHttpDependencies ++ zioConfigDependencies ++ zioDBDependencies ++
      zioCacheDependencies ++
      Seq(
        "dev.zio" %% "zio-config-typesafe" % zioConfigVersion,
        "org.scala-lang.modules" %% "scala-collection-contrib" % "0.4.0",
        "dev.zio" %% "zio-logging-slf4j2" % "2.5.1",
        "org.slf4j" % "slf4j-simple" % "2.0.17"
      )
  )

lazy val zioLocalize = (project in file("zio-localize"))
  .settings(
    name := "zio-localize",
    idePackagePrefix := Some("io.sommers.zio.localize"),
    libraryDependencies ++= zioDependencies ++ zioCacheDependencies
  )

lazy val zioSlick = (project in file("zio-slick"))
  .settings(
    name := "zio-slick",
    idePackagePrefix := Some("io.sommers.zio.slick"),
    libraryDependencies ++= zioDBDependencies ++ zioConfigDependencies ++ Seq(
      "dev.zio" %% "zio-config-typesafe" % zioConfigVersion
    )
  )

lazy val zioTwitch = (project in file("zio-twitch"))
  .settings(
    name := "zio-twitch",
    idePackagePrefix := Some("io.sommers.zio.twitch"),
    libraryDependencies ++= zioConfigDependencies ++ zioHttpDependencies
  )
