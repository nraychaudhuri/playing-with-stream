package models

import akka.stream.OverflowStrategy
import akka.stream.scaladsl.Source
import scala.util.Random

object LogMessages {

  def asSource: Source[String] = {
    def nextMessage(): String = {
      if(Random.nextBoolean)
        "[INFO] - some info log message"
      else
        "[DEBUG] - some debug log message"
    }
    import scala.concurrent.duration._

    Source.apply(100 milliseconds, 500 milliseconds, () => nextMessage())
  }

}
