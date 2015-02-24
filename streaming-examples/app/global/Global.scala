package global

import models.EventStream
import play.api._
import play.api.mvc._

object Global extends GlobalSettings {

  override def onStart(app: Application) = {
  	EventStream.startStreaming()
  }

  
}