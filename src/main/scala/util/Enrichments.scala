package io.sommers.aiintheipaw
package util

import com.typesafe.config.Config
import spray.json.{JsObject, JsValue}

private [aiintheipaw] object Enrichments {
  implicit class EnrichedConfig(config: Config) {
    def getInt(path: String, default: Int): Int = getOrElse(path, default, config.getInt)

    def getString(path: String, default: String): String = getOrElse(path, default, config.getString)

    private def getOrElse[T](path: String, default: T, configGetter: (String) => T): T = {
      if (config.hasPath(path)) {
        configGetter.apply(path)
      } else {
        default
      }
    }
  }

  implicit class EnrichedJsObject(jsObject: JsObject) {
    def getString(fieldName: String): Option[String] = {
      jsObject.fields.get(fieldName)
        .map(_.toString)
    }
  }
}

