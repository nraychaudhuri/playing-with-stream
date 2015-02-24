package util

import akka.stream.FlowMaterializer
import akka.stream.scaladsl.{Sink, Source}
import play.api.libs.concurrent.Akka
import play.api.libs.iteratee.{Iteratee, Enumerator}
import play.api.libs.streams.Streams

import scala.concurrent.Future

object Bridge {

  //This is to bridge between Akka streams and Play enumerator
  def sourceToEnumerator[T](s: Source[T]): Enumerator[T] = {
    import play.api.Play.current
    import play.api.libs.concurrent.Execution.Implicits.defaultContext
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

}
