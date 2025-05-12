ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.16"

lazy val zioVersion = "2.1.17"
lazy val zioHttpVersion = "3.2.0"
lazy val zioConfig = "4.0.4"

lazy val root = (project in file("."))
  .settings(
    name := "AiInTheiPaw",
    idePackagePrefix := Some("io.sommers.aiintheipaw"),
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % zioVersion,
      "dev.zio" %% "zio-http" % zioHttpVersion,
      "dev.zio" %% "zio-config" % zioConfig,
      "dev.zio" %% "zio-config-typesafe" % zioConfig,
      "dev.zio" %% "zio-config-magnolia" % zioConfig,
      "dev.zio" %% "zio-schema" % "1.7.0",
      "dev.zio" %% "zio-schema-json" % "1.7.0",

      "dev.zio" %% "zio-test" % zioVersion % Test,
      "dev.zio" %% "zio-test-sbt" % zioVersion % Test,
      "dev.zio" %% "zio-http-testkit" % zioHttpVersion % Test
    )
  )
