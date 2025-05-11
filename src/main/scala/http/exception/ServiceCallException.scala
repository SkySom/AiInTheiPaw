package io.sommers.aiintheipaw
package http.exception

import model.service.Service

class ServiceCallException(message: String, service: Service) extends Exception(message) {

}
