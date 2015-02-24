package models

import akka.stream.scaladsl._
import akka.stream.{FlowMaterializer, OverflowStrategy}
import play.api.libs.concurrent.Akka
import play.api.libs.json.Json

import scala.concurrent.duration._
import scala.util.Random

object EventStream {
  
  def droppySink[T](s: Sink[T]): Sink[T] = {
    Flow[T].buffer(2, OverflowStrategy.dropHead).to(s)
  }

  def startStreaming() = {
   val rawStream = Source.apply(50 milliseconds, 500 milliseconds, () => Random.nextInt)

   def fastSink: Sink[Int] = Sink.foreach(e => EventPublisher.channel.push(Json.toJson(Event("Fast", e))))
   def slowSink: Sink[Int] = Sink.foreach{ e =>
     Thread.sleep(3000)
     EventPublisher.channel.push(Json.toJson(Event("Slow", e)))
   }

   import akka.stream.scaladsl.FlowGraphImplicits._
   val graph = FlowGraph { implicit builder =>
     val bcast = Broadcast[Int]
     rawStream ~> bcast
     bcast ~> fastSink
     bcast ~> droppySink(slowSink)
   }

   implicit val s = Akka.system(play.api.Play.current)
   implicit val mat = FlowMaterializer()
   graph.run()
 }
  
}