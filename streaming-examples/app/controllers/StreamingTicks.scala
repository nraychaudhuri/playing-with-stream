package controllers

import models.EventPublisher
import play.api.libs.iteratee.{Concurrent, Iteratee}
import play.api.libs.json.JsValue
import play.api.mvc._

object StreamingTicks extends Controller {
  
  def index = Action { implicit request =>
    Ok(views.html.ticks("Your new application is ready."))
  }
  
  def events = WebSocket.using { rh =>
     (EventPublisher.in, EventPublisher.out) 
  }
  
}


