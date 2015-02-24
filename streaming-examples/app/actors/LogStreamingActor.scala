package actors

import akka.actor.{Actor, ActorRef, Props}
import akka.stream.FlowMaterializer
import akka.stream.actor.{ActorSubscriber, RequestStrategy, WatermarkRequestStrategy}
import akka.stream.actor.ActorSubscriberMessage.{OnComplete, OnError, OnNext}
import akka.stream.scaladsl._
import play.api.libs.json.JsValue

object LogStreamingActor {
  
  def props(out: ActorRef, source: Source[JsValue]) = Props(new LogStreamingActor(out, source))
}

class LogStreamingActor(out: ActorRef, source: Source[JsValue]) extends Actor {

  override def preStart(): Unit = {
    implicit val mat = FlowMaterializer()
    val sink = Sink.apply[JsValue](WebSocketSubscriber.props(out))
    source.to(sink).run()
  }
  
  //not handling any input message from websocket
  override def receive: Receive = Actor.emptyBehavior
}

object WebSocketSubscriber {
  def props(out: ActorRef) = Props(new WebSocketSubscriber(out))
}

class WebSocketSubscriber(out: ActorRef) extends ActorSubscriber {
  override protected def requestStrategy: RequestStrategy = WatermarkRequestStrategy(10, 5)

  override def receive: Actor.Receive = {
    case OnNext(msg: JsValue) =>
      out ! msg
    case OnComplete =>
      context.stop(self)
    case OnError(t) => //something happened with the log stream
      t.printStackTrace()
  }

  override def postStop(): Unit = {
    cancel() //cancel the subscription
  }
}
