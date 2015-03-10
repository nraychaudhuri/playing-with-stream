package global

import models.TickStream
import play.api._
import play.api.mvc._

object Global extends GlobalSettings {

  override def onStart(app: Application) = {
  	TickStream.startStreaming()
  }

  
}