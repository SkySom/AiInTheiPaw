package io.sommers.aiintheipaw
package http

import zio.http.{Handler, HandlerAspect, Middleware, Request, Routes, URL}

object ProblemMiddleware {
  def getFullUrl: HandlerAspect[Any, URL] = {
    HandlerAspect.interceptIncomingHandler { Handler.fromFunction {
      (request: Request) => (request, request.url)
    }}
  }
}
