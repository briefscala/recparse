
package com.briefscala.recparse

import shapeless._, labelled._
import scala.util.{Success, Failure, Try}
import scalaz.{Success => _, Failure => _, _}, Scalaz._

trait RecFromArgs[R <: HList] extends Serializable {
  def apply(args: Seq[String]): Option[R]
}

object RecFromArgs {

  implicit def hnilFromArgs[T]: RecFromArgs[HNil] =
    new RecFromArgs[HNil] {
      def apply(n: Seq[String]): Option[HNil] = Some(HNil)
    }

  implicit def hlistFromArgs[S, V, R <: HList]
  (implicit wk: Witness.Aux[S], parser: String ~> V, fa: RecFromArgs[R])
  : RecFromArgs[ValidFieldType[S, V] :: R] =
    new RecFromArgs[ValidFieldType[S, V] :: R] {
      def apply(args: Seq[String]): Option[ValidFieldType[S, V] :: R] =
        args match {
          case flag +: arg +: tail =>
            val typed = parser.parse(arg)
            val maybeRecord = for {
              _ <- (flag == wk.value).option(true)
              rest <- fa(tail)
            } yield field[S](typed) :: rest
            maybeRecord.orElse(apply(arg +: tail))
          case _ => None
        }
    }
}

trait ~>[A, B] extends Serializable {
  def parse(a: A): ValidationNel[Throwable, B]
}

object ~> {
  implicit val stringParser = parseString(identity)
  implicit val charParser = parseString(_.head)
  implicit val byteParser = parseString(_.toByte)
  implicit val shortParser = parseString(_.toShort)
  implicit val intParser = parseString(_.toInt)
  implicit val longParser = parseString(_.toLong)
  implicit val floatParser = parseString(_.toFloat)
  implicit val doubleParser = parseString(_.toDouble)
  implicit val booleanParser = parseString(_.toBoolean)
  def parseString[B](fp: Parser[B]): String ~> B =
    new (String ~> B) {
      def parse(s: String): ValidationNel[Throwable, B] =
        Try(fp(s)) match {
          case Failure(e) => e.failureNel[B]
          case Success(b) => b.successNel[Throwable]
        }
    }
}
