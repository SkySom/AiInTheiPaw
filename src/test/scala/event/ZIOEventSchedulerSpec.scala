package io.sommers.aiintheipaw
package event

import event.EventScheduler
import model.problem.{InvalidValueProblem, Problem}
import util.Enrichment.EnrichOption

import io.sommers.aiintheipaw.route.{AiClient, AiTestClient}
import zio.http.TestClient
import zio.json.ast.Json
import zio.json.{JsonDecoder, JsonEncoder}
import zio.test.{Spec, TestClock, TestEnvironment, ZIOSpecDefault, assertTrue}
import zio.{Duration, IO, Promise, RIO, Scope, ULayer, URLayer, ZIO, ZLayer, durationInt}

import scala.collection.mutable

object ZIOEventSchedulerSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment & Scope, Any] = suite("ZIOSchedulerSpec")(
    test("Scheduler#schedule works as expected") {
      for {
        promise <- ZIO.serviceWithZIO[PromiseEventHandler](_.schedulePromise("1st", "Hello", 1.hours))
        startDone <- promise.isDone
        _ <- TestClock.adjust(1.hours)
        done <- promise.isDone
      } yield assertTrue(!startDone) && assertTrue(done)
    }.provide(EventRouter.live, PromiseEventHandler.allEventHandlers, PromiseEventHandler.layer, AiTestClient.layer)
  )
}

case class PromiseEvent(name: String, value: String)

object PromiseEvent {
  implicit val decoder: JsonDecoder[PromiseEvent] = JsonDecoder.derived[PromiseEvent]
  implicit val encoder: JsonEncoder[PromiseEvent] = JsonEncoder.derived[PromiseEvent]
}

case class PromiseEventHandler() extends EventHandler {
  val promises: mutable.Map[String, Promise[Unit, String]] = mutable.HashMap()

  override def name: String = "promise"

  override def handleEvent(json: Json): IO[Problem, Unit] = for {
    event <- ZIO.fromEither(json.as[PromiseEvent])
      .mapError(error => InvalidValueProblem(s"Failed to parse json $error"))
    promise <- promises.get(event.name)
      .getOrZIOFail(InvalidValueProblem(s"No promise with name ${event.name}"))
    _ <- promise.succeed(event.value)
  } yield ()

  def schedulePromise(name: String, value: String, duration: Duration): RIO[EventScheduler, Promise[Unit, String]] = for {
    promise <- Promise.make[Unit, String]
    _ <- ZIO.succeed(promises.put(name, promise))
    _ <- ZIO.serviceWithZIO[EventScheduler](_.schedule(duration, this.name, PromiseEvent(name, value)))
  } yield promise
}

object PromiseEventHandler {
  def layer: ULayer[PromiseEventHandler] = ZLayer.succeed(PromiseEventHandler())

  def allEventHandlers: URLayer[PromiseEventHandler, List[EventHandler]] = ZLayer.collectAll(List(ZLayer.service[PromiseEventHandler]))
}
