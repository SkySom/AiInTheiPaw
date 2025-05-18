package io.sommers.aiintheipaw
package http.exception

import model.service.Service

class ServiceCallException(message: String, service: Service, cause: Option[Throwable] = None) extends Exception(message, cause.orNull) {

}
