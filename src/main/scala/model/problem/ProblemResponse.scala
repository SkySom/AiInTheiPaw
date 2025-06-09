package io.sommers.aiintheipaw
package model.problem

import zio.http.URL
import zio.schema.{DeriveSchema, Schema}

case class ProblemResponse(
  typeUri: Option[String],
  status: Int,
  title: String,
  detail: Option[String],
  instance: String,
  custom: Map[String, String]
) extends ProblemProvider {

  override def toProblem(instance: String): ProblemResponse = this
}

trait ProblemProvider {
  def toProblem(instance: String): ProblemResponse
}

object ProblemResponse {
  implicit val schema: Schema[ProblemResponse] = DeriveSchema.gen[ProblemResponse]

  def apply(value: Any, url: URL): ProblemResponse = {
    value match {
      case problemProvider: ProblemProvider => problemProvider.toProblem(url.toString)
      case throwable: Throwable => new ProblemResponse(
        None,
        400,
        s"Threw Exception ${throwable.getMessage}",
        None,
        url.toString,
        Map.empty
      )
      case other: Any => ProblemResponse(
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


