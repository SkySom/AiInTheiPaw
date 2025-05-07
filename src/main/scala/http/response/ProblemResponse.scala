package io.sommers.aiintheipaw
package http.response

import util.Enrichments.EnrichedJsObject

import org.apache.pekko.http.scaladsl.model.StatusCode
import spray.json.{DeserializationException, JsNumber, JsObject, JsString, JsValue, RootJsonFormat, deserializationError}

import java.net.URI
import scala.collection.mutable

case class ProblemResponse(
  typeUri: Option[URI],
  status: StatusCode,
  title: String,
  detail: Option[String],
  instance: URI,
  custom: Map[String, JsValue]
) {

}

object ProblemResponse {
  private val notCustom: List[String] = List("type", "status", "title", "detail", "instance")

  implicit object ProblemResponseFormat extends RootJsonFormat[ProblemResponse] {

    override def read(json: JsValue): ProblemResponse = {
      val jsObject = json.asJsObject

      val custom: Map[String, JsValue] = jsObject.fields.filterNot(notCustom.contains)

      ProblemResponse(
        jsObject.getString("type").map(URI.create),
        200,
        jsObject.getString("title").getOrElse(deserializationError("missing required field", fieldNames = List("title"))),
        jsObject.getString("detail"),
        jsObject.getString("instance").map(URI.create).getOrElse(deserializationError("missing required field", fieldNames = List("instance"))),
        custom
      )
    }

    override def write(obj: ProblemResponse): JsValue = {
      val map: mutable.Map[String, JsValue] = mutable.Map(
        "status" -> JsNumber(obj.status.intValue()),
        "title" -> JsString(obj.title),
        "instance" -> JsString(obj.instance.toString)
      )

      map += obj.custom

      obj.typeUri.foreach(uri => map + ("type", JsString(uri.toString)))
      obj.detail.foreach(detail => map + ("detail", JsString(detail)))


      new JsObject(map.toMap)
    }
  }
}
