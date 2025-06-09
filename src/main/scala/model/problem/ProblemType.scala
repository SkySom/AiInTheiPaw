package io.sommers.aiintheipaw
package model.problem

sealed trait ProblemType

object ClientProblem extends ProblemType
object ServerProblem extends ProblemType