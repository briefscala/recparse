package com.briefscala

import com.typesafe.config.ConfigFactory
import com.briefscala.recparse._
import scalaz._, Scalaz._

object Main {
  def main(args: Array[String]): Unit = {

    val config = ConfigFactory.load()
    val separator = config.getString("rec-parse.separator")

    val args0 = args.flatMap(_.split("="))

    val args1 = args0.toSeq

    val maybeFilePath = args1.getRecord[FilePath]
    val maybeSource = args1.getRecord[IsNew]
    val maybeLang = args1.getRecord[Len]
    val maybeSeparator = args1.getRecord[Separator]
      .orElse(Seq("-sep", separator).getRecord[Separator])

    val failedFilePath = "No file path was specified or was invalid".failureNel[FilePath]
    val failedSeparator = "An invalid separator was specified".failureNel[Separator]
    val failedLength = "No lenth specified or was invalid".failureNel[Len]
    val failedIsNew = "No --is-new flag specified or was invalid".failureNel[IsNew]

    val argsValidation = (
      maybeFilePath.fold(failedFilePath)(_.successNel) |@|
      maybeSeparator.fold(failedSeparator)(_.successNel) |@|
      maybeLang.fold(failedLength)(_.successNel) |@|
      maybeSource.fold(failedIsNew)(_.successNel)
    ){_++_++_++_}

    println(argsValidation)
  }
}
