package controllers

import actors.LogStreamingActor
import akka.stream.FlowMaterializer
import akka.stream.scaladsl.{OperationAttributes, Source, Sink}
import models.LogMessages
import play.api.libs.EventSource.Event
import play.api.libs.concurrent.Akka
import play.api.libs.streams.Streams
import play.api.mvc._
import play.api.http.MimeTypes
import play.api.libs.iteratee.{Iteratee, Enumerator}
import scala.concurrent.ExecutionContext.Implicits._
import play.api.libs.json.{Json, JsValue}
import play.api.libs.json.Json._
import models.Models.DebugLog
import models.Models.Other
import models.Models._
import play.api.libs.EventSource
import scala.concurrent.Future
import play.api.Play.current

object StreamingLogEvents extends Controller {

  def index = Action { implicit req =>
    Ok(views.html.logs("Your new application is ready."))
  }

  def es = Action {
    val newSource: Source[JsValue] = LogMessages.asSource
      .map(toEvent)
      .map(asJson)

    Ok.chunked(toEventSource(newSource)).as(MimeTypes.EVENT_STREAM)
  }
  
  val logPattern = """\[(INFO|DEBUG)\] - ([a-z ]*)""".r

  val toEvent: String => LogEvent = line => {
    logPattern.findFirstIn(line) match {
      case Some(logPattern(level, message)) =>
        if(level == "INFO") new InfoLog(message) else new DebugLog(message)
      case _ =>
        Other(line)
    }
  }

  val asJson: LogEvent => JsValue = le => le match {
    case InfoLog(e, level)  => logLineToJson(level, e)
    case DebugLog(e, level)  => logLineToJson(level, e)
    case Other(e) if e.trim.length > 0 => logLineToJson("other", e)
  }

  private def logLineToJson(level : String, message : String) = {
    toJson(Map("type" -> toJson(level), "message" -> toJson(message)))
  }

  private def toEventSource(s: Source[JsValue]): Enumerator[Event] = {
    sourceToEnumerator(s).through(EventSource())
  }

  private def sourceToEnumerator[T](s: Source[T]): Enumerator[T] = {
    import play.api.Play.current
    implicit val a = Akka.system
    implicit val mat = FlowMaterializer()
    new Enumerator[T] {
      override def apply[A](i: Iteratee[T, A]): Future[Iteratee[T, A]] = {
        val (sub, next) = Streams.iterateeToSubscriber(i)
        s.to(Sink.apply(sub)).run()
        Future(next)
      }
    }
  }

  def websocket = WebSocket.acceptWithActor[String, JsValue] { request => out =>
    val source: Source[JsValue] = LogMessages.asSource.map(toEvent).map(asJson)
    LogStreamingActor.props(out, source)
  }

}
