package com.briefscala

import shapeless._, labelled._
import scalaz.ValidationNel

package object recparse {
  type Parser[B] = (String) => B
  type ValidFieldType[A, B] = FieldType[A, ValidationNel[Throwable, B]]
  implicit class ArgsOps(val args: Seq[String]) extends AnyVal {
    def getRecord[R <: HList](implicit fm: RecFromArgs[R]): Option[R] = fm(args)
  }
}
