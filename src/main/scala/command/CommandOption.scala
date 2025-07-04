package io.sommers.aiintheipaw
package command

import model.problem.{InvalidValueProblem, Problem, ThrowableProblem}

import zio.{Duration, IO, Task, Trace, ZIO}

import java.util.concurrent.TimeUnit
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
  val numberPattern = "^(-?\\d*.?\\d*)$".r


  override def parseOption(input: String)(implicit trace: Trace): IO[Problem, Duration] = {
    input match {
      case numberPattern(duration) => Try(duration.toDouble)
        .fold(
          _ => ZIO.fail(InvalidValueProblem("$input is not a valid decimal number")),
          decimalMinutes => ZIO.succeed(Duration.fromSeconds(Math.ceil(decimalMinutes * 60).toLong))
        )
      case _ => ZIO.fail(InvalidValueProblem("$input is not a valid duration"))
    }
  }

  def find(args: Map[String, Any]): IO[Problem, Option[Duration]] = {
    args.get(this.name)
      .fold(ZIO.succeed(None)) {
        case duration: Duration => ZIO.succeed(duration).asSome
        case other => ZIO.fail(InvalidValueProblem(s"$other is a not a Duration"))
      }
  }
}

