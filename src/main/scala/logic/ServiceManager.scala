package io.sommers.aiintheipaw
package logic

import model.problem.NotFoundProblem
import model.service.Service

import zio.{IO, ZIO}

trait ServiceManager[T <: ServiceSpecific] {
  def get(service: Service): IO[NotFoundProblem, T]
}

case class ServiceManagerImpl[T <: ServiceSpecific](
  name: String,
  managed: Iterable[T]
) extends ServiceManager[T] {

  override def get(service: Service): IO[NotFoundProblem, T] = managed.find(_.service == service)
    .fold[IO[NotFoundProblem, T]](ZIO.fail(NotFoundProblem(name, s"Failed to find $name for $service"))) {
      (value: T) => ZIO.succeed(value)
    }
}
