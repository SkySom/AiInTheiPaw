package io.sommers.aiintheipaw
package util

import model.problem.{Problem, ProblemResponse}

import zio.http.Header.Accept.MediaTypeWithQFactor
import zio.http.codec.{CodecConfig, ContentCodec, StatusCodec}
import zio.http.endpoint.{AuthType, Endpoint}
import zio.http.{Handler, Header, MediaType, Request, Response, Route, Status}
import zio.{Chunk, IO, Task, Trace, ZIO}

object Enrichment {
  implicit class EnrichEndpoint[PathInput, Input, Err <: Problem, Output, Auth <: AuthType](endpoint: Endpoint[PathInput, Input, Err, Output, Auth]) {
    def implementWithProblem[Env](passedHandler: Handler[Env, Any, Input, Output])(implicit trace: Trace): Route[Env, Nothing] = {
      val handler: Handler[Env, Nothing, Request, Response] = Handler.fromFunctionZIO {
        request =>
          val decodedBody: Task[Input] = endpoint.input.decodeRequest(request, CodecConfig.defaultConfig)
          val handlerResponse: ZIO[Env, Any, Output] = decodedBody.flatMap(value => passedHandler(value).asInstanceOf[ZIO[Env, Any, Output]])
          handlerResponse.fold(
            error => {
              val problem = ProblemResponse(error, request.url)
              val codec = ContentCodec.content[ProblemResponse]("error-response") ++ StatusCodec.status(Status.fromInt(problem.status))
              codec.encodeResponse(ProblemResponse(error, request.url), mediaTypes(request), CodecConfig.defaultConfig)
            },
            value => endpoint.output.encodeResponse(value, mediaTypes(request), CodecConfig.defaultConfig)
          )
      }


      Route.handledIgnoreParams(endpoint.route)(handler)
    }
  }

  private def mediaTypes(request: Request): Chunk[MediaTypeWithQFactor] = {
    request.headers
      .getAll(Header.Accept)
      .flatMap(_.mimeTypes) :+ MediaTypeWithQFactor(MediaType.application.`json`, Some(0.0))
  }
  
  implicit class EnrichZIOOption[IN, ERR, OUT](zio: ZIO[IN, ERR, Option[OUT]]) {
    def getOrFail(fail: => ERR): ZIO[IN, ERR, OUT] = zio.flatMap(opt => opt.getOrZIOFail(fail))
  }
  
  implicit class EnrichOption[OUT](option: Option[OUT]) {
    def getOrZIOFail[ERR](fail: => ERR)(implicit trace: Trace): IO[ERR, OUT] = option.fold(ZIO.fail(fail)) {
      ZIO.succeed(_)
    }
  }
  
  implicit class EnrichBoolean(boolean: Boolean) {
    def toZIO[ERR](fail: => ERR): IO[ERR, Unit] = if (boolean) {
      ZIO.succeed(())
    } else {
      ZIO.fail(fail)
    }
  }
}
