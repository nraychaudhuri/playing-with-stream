package util

import scala.collection.immutable.Seq

import akka.stream.scaladsl.Source
import play.api.libs.iteratee.Enumerator
import play.twirl.api.{Html, HtmlFormat}

object Pagelet {
  def apply(h: Html) = new Pagelet(Source.single(h))
}

case class Pagelet(e: Source[Html]) extends play.twirl.api.Appendable[Pagelet]  {
}







































//object PageletFormat extends play.twirl.api.Format[Pagelet] {
//
//  override def raw(text: String): Pagelet = Pagelet(HtmlFormat.raw(text))
//
//  override def escape(text: String): Pagelet = Pagelet(HtmlFormat.escape(text))
//
//  override def empty: Pagelet = Pagelet(HtmlFormat.empty)
//
//  override def fill(elements: Seq[Pagelet]): Pagelet = elements.reduce(_.andThen(_))
//}