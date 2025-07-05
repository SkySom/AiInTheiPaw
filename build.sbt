ThisBuild / organization := "io.sommers"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.7.1"

lazy val zioVersion = "2.1.19"

lazy val zioConfigVersion = "4.0.4"
lazy val zioHttpVersion = "3.3.3"
lazy val zioSchemaVersion = "1.7.3"

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
  "com.augustnagro" %% "magnumzio" % "2.0.0-M2",
  "com.augustnagro" %% "magnumpg" % "2.0.0-M2",
  "org.postgresql" % "postgresql" % "42.7.7",
  "com.zaxxer" % "HikariCP" % "6.3.0"
)

lazy val root = (project in file("."))
  .aggregate(zioTwitch)
  .dependsOn(zioTwitch)
  .settings(
    name := "AiInTheiPaw",
    idePackagePrefix := Some("io.sommers.aiintheipaw"),
    libraryDependencies ++= zioDependencies ++ zioHttpDependencies ++ zioConfigDependencies ++ zioDBDependencies ++
      Seq(
        "dev.zio" %% "zio-cache" % "0.2.4",
        "dev.zio" %% "zio-config-typesafe" % zioConfigVersion,
        "org.scala-lang.modules" %% "scala-collection-contrib" % "0.4.0"
      )
  )

lazy val zioTwitch = (project in file("zio-twitch"))
  .settings(
    name := "zio-twitch",
    idePackagePrefix := Some("io.sommers.zio.twitch"),
    libraryDependencies ++= zioConfigDependencies ++ zioHttpDependencies ++ zioConfigDependencies
  )
