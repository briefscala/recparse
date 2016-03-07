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

    /**
     * add support for "-flag=argument" format
     */
    val args0 = args.flatMap(_.split("="))

    val args1 = args0.toSeq

    /**
     * try to parse in the records from the provided arguments
     */
    val maybeFilePath = args1.getRecord[FilePath]
    val maybeSource = args1.getRecord[IsNew]
    val maybeLang = args1.getRecord[Len]
    val maybeSeparator = args1.getRecord[Separator]
      .orElse(Seq("-sep", separator).getRecord[Separator])

    val failedFilePath = "No file path was specified".failureNel[FilePath]
    val failedSeparator = "An invalid separator was specified".failureNel[Separator]
    val failedLength = "No lenth specified".failureNel[Len]
    val failedIsNew = "No --is-new flag specified".failureNel[IsNew]

    /**
     * put all the records together or fail
     */
    val argsValidation = (
      maybeFilePath.fold(failedFilePath)(_.successNel) |@|
      maybeSeparator.fold(failedSeparator)(_.successNel) |@|
      maybeLang.fold(failedLength)(_.successNel) |@|
      maybeSource.fold(failedIsNew)(_.successNel)
    ){_++_++_++_}

    argsValidation match {
      case scalaz.Failure(nel) =>
        log.error(s"missing required arguments: $nel")
      case scalaz.Success(validArgs) =>
      /**
       * if all went well 'validArgs' is the record with the arguments
       */
        val parsedArgs = (
            selectArg(validArgs, filePathWitness) |@|
            selectArg(validArgs, separatorWitness) |@|
            selectArg(validArgs, lenWitness) |@|
            selectArg(validArgs, isnewWitness)
          ).tupled
          parsedArgs match {
            case scalaz.Failure(nel) =>
              log.error(s"Could not parse all required arguments: $nel")
            case scalaz.Success((filePath, sep, len, isNew)) =>
              println(s"filePath = $filePath\nsep = $sep\nlen = $len\nisNew = $isNew")
          }
    }
  }
  /**
   * extract the value from a record given its singleton(Witness) key
   */
  def selectArg[L <: HList](xs: L, argWitness: Witness.Lt[String])(implicit
   sel: Selector[L, argWitness.T]) = xs(argWitness)
}
