package util

import play.twirl.api.HtmlFormat

import scala.collection.immutable.Seq

object PageletFormat extends play.twirl.api.Format[Pagelet]{

  override def raw(text: String): Pagelet = new Pagelet(HtmlFormat.raw(text))

  override def escape(text: String): Pagelet = new Pagelet(HtmlFormat.escape(text))

  override def empty: Pagelet =  new Pagelet(HtmlFormat.empty)

  override def fill(elements: Seq[Pagelet]): Pagelet = elements.reduceLeft(_.andThen(_))
}
