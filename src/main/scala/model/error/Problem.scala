package io.sommers.aiintheipaw
package model.error

import zio.http.URL
import zio.schema.{DeriveSchema, Schema}

case class Problem(
  typeUri: Option[String],
  status: Int,
  title: String,
  detail: Option[String],
  instance: String,
  custom: Map[String, String]
) extends ProblemProvider {

  override def toProblem(instance: String): Problem = this
}

trait ProblemProvider {
  def toProblem(instance: String): Problem
}

object Problem {
  implicit val schema: Schema[Problem] = DeriveSchema.gen[Problem]

  def apply(value: Any, url: URL): Problem = {
    value match {
      case problemProvider: ProblemProvider => problemProvider.toProblem(url.toString)
      case throwable: Throwable => new Problem(
        None,
        400,
        s"Threw Exception ${throwable.getMessage}",
        None,
        url.toString,
        Map.empty
      )
      case other: Any => Problem(
        typeUri = None,
        500,
        s"Found Error, but could not translate to problem",
        None,
        url.toString,
        Map(
          "error" -> other.toString
        )
      )
    }
  }
}


