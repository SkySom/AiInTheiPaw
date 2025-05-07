package io.sommers.aiintheipaw

import logic.ChannelLogic
import logic.message.{MessageLogic, TwitchMessageLogic}
import model.service.{Service, Twitch, TwitchService}
import route.{CollectedRoutes, MessageRoutes}
import util.Enrichments.EnrichedConfig

import com.softwaremill.macwire.{wire, wireSet}
import com.softwaremill.tagging._
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.server.Directives.{handleExceptions, reject}
import org.apache.pekko.http.scaladsl.server.{Directives, Route}

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn
import http.exception.CustomExceptionHandler.handler

object AiInTheiPaw {
  def main(args: Array[String]): Unit = {
    implicit val actorSystem: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "aiintheipaw")
    implicit val executionContext: ExecutionContextExecutor = actorSystem.executionContext

    val config: Config = ConfigFactory.load()

    val port: Int = config.getInt("http.port", 8080)
    val interface: String = config.getString("http.interface", "localhost")


    val channelLogic: ChannelLogic = wire[ChannelLogic]
    val twitchService: Service @@ Twitch = wire[TwitchService].taggedWith[Twitch]
    val twitchMessageLogic: MessageLogic = wire[TwitchMessageLogic].taggedWith[Twitch]
    val messageLogics: Set[MessageLogic] = wireSet[MessageLogic]

    val messageRoute: MessageRoutes = wire[MessageRoutes]

    val collectedRoutes: Set[CollectedRoutes] = wireSet[CollectedRoutes]

    val routes = handleExceptions(handler) {
      collectedRoutes.map(_.routes).foldLeft[Route](reject)(Directives.concat(_, _))
    }

    val bindingFuture = Http().newServerAt(interface, port)
      .bind(routes)

    StdIn.readLine()
    bindingFuture.flatMap(_.unbind())
      .onComplete(_ => actorSystem.terminate())
  }
}
