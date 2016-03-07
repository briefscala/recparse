package com

import shapeless.Witness
import shapeless.record.Record
import scalaz.ValidationNel

package object briefscala {
  type Parsed[A] = ValidationNel[Throwable, A]

  type FilePath = Record.`"--file-path" -> Parsed[String]`.T
  type Separator = Record.`"-sep" -> Parsed[Char]`.T
  type Len = Record.`"-len" -> Parsed[Long]`.T
  type IsNew = Record.`"--is-new" -> Parsed[Boolean]`.T

  val filePathWitness = Witness("--file-path")
  val separatorWitness = Witness("-sep")
  val lenWitness = Witness("-len")
  val isnewWitness = Witness("--is-new")
}
