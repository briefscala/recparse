package com.briefscala

import com.typesafe.config.ConfigFactory
import org.slf4j.{Logger, LoggerFactory}
import com.briefscala.recparse._
import shapeless._, record._, ops.record.Selector
import scalaz._, Scalaz._

object Main {
  def main(args: Array[String]): Unit = {

    val log = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)
    log.asInstanceOf[ch.qos.logback.classic.Logger]
      .setLevel(ch.qos.logback.classic.Level.INFO)

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

    argsValidation match {
      case scalaz.Failure(nel) =>
        log.error(s"Invalid or missing arguments: $nel")
      case scalaz.Success(validArgs) =>
        val parsedArgs = (
            selectArg(validArgs, filePathWitness) |@|
            selectArg(validArgs, separatorWitness) |@|
            selectArg(validArgs, lenWitness) |@|
            selectArg(validArgs, isnewWitness)
          ).tupled
          parsedArgs match {
            case scalaz.Failure(nel) =>
              log.error(s"Some arguments could not be parsed: $nel")
            case scalaz.Success((filePath, sep, len, isNew)) =>
              println(s"$filePath\n$sep\n$len\n$isNew")
          }
    }
  }
  def selectArg[L <: HList](xs: L, argWitness: Witness.Lt[String])(implicit
   sel: Selector[L, argWitness.T]) = xs(argWitness)
}
