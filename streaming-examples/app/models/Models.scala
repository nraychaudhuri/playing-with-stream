package models

import java.util.Date

object Models {

  sealed class LogEvent

  case class InfoLog(message: String, level: String) extends LogEvent {
    def this(message: String) = this(message, "info")
  }
  case class DebugLog(message: String, level: String) extends LogEvent {
    def this(message: String) = this(message, "debug")
  }
  case class Other(message: String) extends LogEvent {
    val level = "other"
  }
}
