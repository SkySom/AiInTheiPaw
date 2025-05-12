package io.sommers.aiintheipaw
package util

import model.error.{Problem, ProblemProvider}

import zio.http.Header.Accept.MediaTypeWithQFactor
import zio.http.codec.{CodecConfig, ContentCodec, StatusCodec}
import zio.http.endpoint.{AuthType, Endpoint}
import zio.http.{Handler, Header, MediaType, Request, Response, Route, Status}
import zio.{Chunk, Task, Trace, ZIO}

object Enrichment {
  implicit class EnrichEndpoint[PathInput, Input, Err <: ProblemProvider, Output, Auth <: AuthType](endpoint: Endpoint[PathInput, Input, Err, Output, Auth]) {
    def implementWithProblem[Env](passedHandler: Handler[Env, Any, Input, Output])(implicit trace: Trace): Route[Env, Nothing] = {
      val handler: Handler[Env, Nothing, Request, Response] = Handler.fromFunctionZIO { request =>
        val decodedBody: Task[Input] = endpoint.input.decodeRequest(request, CodecConfig.defaultConfig)
        val handlerResponse: ZIO[Env, Any, Output] = decodedBody.flatMap(value => passedHandler(value).asInstanceOf[ZIO[Env, Any, Output]])
        handlerResponse.fold(
          error => {
            val problem = Problem(error, request.url)
            val codec = ContentCodec.content[Problem]("error-response") ++ StatusCodec.status(Status.fromInt(problem.status))
            codec.encodeResponse(Problem(error, request.url), mediaTypes(request), CodecConfig.defaultConfig)
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
}
