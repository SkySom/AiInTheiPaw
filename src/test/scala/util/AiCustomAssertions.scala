package io.sommers.aiintheipaw
package util

import zio.test.CustomAssertion

object AiCustomAssertions {
  def seqHead[T]: CustomAssertion[Seq[T], T] = CustomAssertion.make[Seq[T]] {
    case head :: _ => Right(head)
    case _ => Left("Could not get Head")
  }

  def seqTail[T]: CustomAssertion[Seq[T], T] = CustomAssertion.make[Seq[T]] {
    case _ :+ last => Right(last)
    case _ => Left("Could not get Tail")
  }
}
