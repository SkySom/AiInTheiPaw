package io.sommers.aiintheipaw
package http.exception

import org.apache.pekko.http.scaladsl.model.{StatusCode, StatusCodes}
import org.apache.pekko.http.scaladsl.server.Directives.complete
import org.apache.pekko.http.scaladsl.server.ExceptionHandler

object CustomExceptionHandler {
  implicit val handler: ExceptionHandler = ExceptionHandler {
    case e: IllegalArgumentException =>
      complete(StatusCodes.BadRequest, e.getMessage)
  }
}
