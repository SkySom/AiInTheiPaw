ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.16"

lazy val pekkoHttpVersion = "1.1.0"
lazy val pekkoVersion = "1.1.3"

lazy val root = (project in file("."))
  .settings(
    name := "AiInTheiPaw",
    idePackagePrefix := Some("io.sommers.aiintheipaw"),
    libraryDependencies ++= Seq(
      "org.apache.pekko" %% "pekko-http" % pekkoHttpVersion,
      "org.apache.pekko" %% "pekko-http-spray-json" % pekkoHttpVersion,
      "org.apache.pekko" %% "pekko-actor-typed" % pekkoVersion,
      "org.apache.pekko" %% "pekko-stream" % pekkoVersion,
      "ch.qos.logback" % "logback-classic" % "1.5.18",
      "com.typesafe" % "config" % "1.4.3",
      "com.softwaremill.macwire" %% "macros" % "2.6.6" % "provided",
      "com.softwaremill.common" %% "tagging" % "2.3.5",

      "org.apache.pekko" %% "pekko-http-testkit" % pekkoHttpVersion % Test,
      "org.apache.pekko" %% "pekko-actor-testkit-typed" % pekkoVersion % Test,
      "org.scalatest" %% "scalatest" % "3.2.19" % Test
    )
  )
