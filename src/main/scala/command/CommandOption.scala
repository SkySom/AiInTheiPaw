package io.sommers.aiintheipaw
package command

import model.problem.{InvalidValueProblem, Problem, ThrowableProblem}

import zio.{IO, Task, Trace, ZIO}

import scala.concurrent.duration.Duration
import scala.util.Try


trait CommandOption[T] {
  val name: String

  val description: String

  val required: Boolean

  def parseOption(input: String)(implicit trace: Trace): IO[Problem, T]
}

case class IntCommandOption(
  name: String,
  description: String,
  required: Boolean,
  range: Range
) extends CommandOption[Int] {
  override def parseOption(input: String)(implicit trace: Trace): IO[Problem, Int] = {
    ZIO.attempt(input.toInt)
      .foldZIO(
        failure => ZIO.fail(Problem(failure)),
        number => if (range.contains(number)) {
          ZIO.succeed(number)
        } else {
          ZIO.fail(InvalidValueProblem(s"$number is not in $range"))
        }
      )
  }
}

case class DurationCommandOption(
  name: String,
  description: String,
  required: Boolean = false
) extends CommandOption[Duration] {
  override def parseOption(input: String)(implicit trace: Trace): IO[Problem, Duration] = {
    ZIO.attempt(Duration(input))
      .mapError {
        case _: IllegalArgumentException => InvalidValueProblem(s"$input must be a valid duration (ie 10 minutes 30 seconds)")
        case other => Problem(other)
      }
  }

  def find(args: Map[String, AnyVal]): IO[Problem, Option[Duration]] = {
    args.get(this.name)
      .fold(ZIO.succeed(None)) {
        case duration: Duration => ZIO.succeed(duration).asSome
        case other => ZIO.fail(InvalidValueProblem(s"$other is a not a Duration"))
      }
  }
}

