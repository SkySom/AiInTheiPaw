package io.sommers.aiintheipaw

import http.WebServer
import module.{LogicModule, MessagingModule, TwitchModule}

import distage.{Activation, Injector, Roots}
import izumi.distage.model.definition.ModuleDef
import zio.{Scope, Task, ZIO, ZIOAppArgs, ZIOAppDefault}

object AiInTheiPaw extends ZIOAppDefault {

  private val moduleDef: ModuleDef = new ModuleDef {
    include(TwitchModule)
    include(MessagingModule)
    include(LogicModule)

    make[WebServer]
  }
  private val injector: Injector[Task] = Injector[Task]()

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
    for {
      plan <- ZIO.fromEither(injector.plan(
        bindings = moduleDef,
        activation = Activation.empty,
        roots = Roots.target[WebServer]
      )
      )
      produce <- injector.produce(plan)
        .toZIO
      _ <- produce.get[WebServer]
        .serve()
    } yield ()
  }
}
