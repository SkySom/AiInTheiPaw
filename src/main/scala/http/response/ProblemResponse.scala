package io.sommers.aiintheipaw
package http.response

import zio.http.URL
import zio.schema.{DeriveSchema, Schema}

//TODO This is gonna be all custom...
case class ProblemResponse(
  typeUri: Option[String],
  status: String,
  title: String,
  detail: Option[String],
  instance: String,
  custom: Map[String, String]
) {

}

object ProblemResponse {
  private val notCustom: List[String] = List("type", "status", "title", "detail", "instance")

  implicit val schema: Schema[ProblemResponse] = DeriveSchema.gen[ProblemResponse]

  def apply(throwable: Throwable, url: String): ProblemResponse = new ProblemResponse(
    None,
    "400",
    s"Threw Exception ${throwable.getMessage}",
    None,
    url,
    Map.empty
  )
}
