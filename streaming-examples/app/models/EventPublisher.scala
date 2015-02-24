package models

import play.api.libs.iteratee.{Concurrent, Iteratee}
import play.api.libs.json.{Json, JsValue}

case class Event(tpe: String, e: Int)

case object Event {
  implicit val format = Json.format[Event]
}

object EventPublisher {
  val in = Iteratee.ignore[JsValue]
  val (out, channel) = Concurrent.broadcast[JsValue]
}
