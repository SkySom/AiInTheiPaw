package io.sommers.aiintheipaw
package logic

import model.error.NotFoundError
import model.service.Service

import zio.{IO, ZIO}

trait ServiceManager[T <: ServiceSpecific] {
  def get(service: Service): IO[NotFoundError, T]
}

case class ServiceManagerImpl[T <: ServiceSpecific](
  name: String,
  managed: Iterable[T]
) extends ServiceManager[T] {

  override def get(service: Service): IO[NotFoundError, T] = ZIO.fromOption(managed.find(_.service == service))
    .mapError(_ => NotFoundError(name, s"Failed to find $name for $service"))
}
