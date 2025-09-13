package io.sommers.aiintheipaw
package model.problem

case class ProblemException(
  problem: Problem
) extends Exception(problem.message) {

}
