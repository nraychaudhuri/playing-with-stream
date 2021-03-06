package controllers

import play.api.mvc._
import util.Pagelet
import scala.concurrent.Future
import play.twirl.api.Html
import services.Modules._
import akka.stream.scaladsl._

object Application extends Controller {

  def index = Action {
    val wpasam = toModule("wpasam", WhatPeopleAreSayingAboutYou.latestUpdate(UserPreference.showAll))
    val symbi = toModule("symbi", ServicesYouMightBeInterested.latestUpdate(UserPreference.showAll))
    val ads =  toModule("ads", Ads.latestUpdate(UserPreference.showAll))
    val wyfau = toModule("wyfau", WhatYourFriendsAreUpto.latestUpdate(UserPreference.showAll))

    val stream = interleave(wpasam, symbi, ads, wyfau)
    
    Ok("")
  }

  private def interleave(modules: Source[Html]*): Pagelet = ???

  private def toModule(moduleId: String, html: Future[Html]): Source[Html] = {
    import views.html.modules.module
    Source.apply(html).map(h => module(moduleId, h))
  }

}

