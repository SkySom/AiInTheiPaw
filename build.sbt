ThisBuild / organization := "io.sommers"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.16"

lazy val zioVersion = "2.1.19"

lazy val zioConfigVersion = "4.0.4"
lazy val zioHttpVersion = "3.3.3"
lazy val zioSchemaVersion = "1.7.2"

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

lazy val zioQuillDependencies = Seq(
  "io.getquill" %% "quill-jdbc-zio" % "4.8.5",
  "org.postgresql" % "postgresql" % "42.7.6"
)

lazy val root = (project in file("."))
  .aggregate(zioTwitch)
  .dependsOn(zioTwitch)
  .settings(
    name := "AiInTheiPaw",
    idePackagePrefix := Some("io.sommers.aiintheipaw"),
    libraryDependencies ++= zioDependencies ++ zioHttpDependencies ++ zioConfigDependencies ++ zioQuillDependencies ++
      Seq(
        "dev.zio" %% "zio-cache" % "0.2.4",
        "dev.zio" %% "zio-config-typesafe" % zioConfigVersion
      )
  )

lazy val zioTwitch = (project in file("zio-twitch"))
  .settings(
    name := "zio-twitch",
    idePackagePrefix := Some("io.sommers.zio.twitch"),
    libraryDependencies ++= zioConfigDependencies ++ zioHttpDependencies ++ zioConfigDependencies
  )
