
package com.briefscala.recparse

import shapeless._, labelled._
import scala.util.{Success, Failure, Try}
import scalaz.{Success => _, Failure => _, _}, Scalaz._

trait RecFromArgs[R <: HList] extends Serializable {
  def apply(args: Seq[String]): Option[R]
}

object RecFromArgs {
  def apply[R <: HList](implicit fxml: RecFromArgs[R]) = fxml

  implicit def hnilFromArgs[T]: RecFromArgs[HNil] =
    new RecFromArgs[HNil] {
      def apply(n: Seq[String]): Option[HNil] = Some(HNil)
    }

  implicit def hlistFromArgs[S, V, T <: HList]
  (implicit wk: Witness.Aux[S], parser: String ~> V, fargs: RecFromArgs[T])
  : RecFromArgs[ValidFieldType[S, V] :: T] =
    new RecFromArgs[ValidFieldType[S, V] :: T] {
      def apply(args: Seq[String]): Option[ValidFieldType[S, V] :: T] =
        args match {
          case flag +: arg +: tail =>
            val typed = parser.parse(arg)
            val maybeRecord = for {
              _ <- (flag == wk.value).option(true)
              rest <- fargs(tail)
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
  def parseString[B](fp: Parser[B]): String ~> B =
    new (String ~> B) {
      def parse(s: String): ValidationNel[Throwable, B] =
        Try(fp(s)) match {
          case Failure(e) => e.failureNel[B]
          case Success(b) => b.successNel[Throwable]
        }
    }
}
