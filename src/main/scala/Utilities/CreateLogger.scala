package Utilities

import org.slf4j.{Logger, LoggerFactory}

/** Returns the logger object specific to the class which was used to create the logger.
 */
object CreateLogger:
  /** Returns the logger that can be used for logging and debugging.
   *
   * @param loggerClass : Any : The class for which the logger needs to be created.
   * @return Returns a [[Logger]] type object that provides a method for logging and debugging.
   */
  def apply(loggerClass: Any): Logger =
    val logger: Logger = LoggerFactory.getLogger(loggerClass.getClass)
    logger
  end apply
end CreateLogger