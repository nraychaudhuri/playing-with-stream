package controllers;

import akka.stream.ActorFlowMaterializer;
import akka.stream.FlowMaterializer;
import akka.stream.javadsl.*;
import play.libs.Akka;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Html;
import util.Pagelet;
import views.html.modules.module;
import views.pagelet.index;

import static services.Services.*;

public class Application extends Controller {

    public static Result index() {
        Source<Html> wpasam = toModule("wpasam", WhatPeopleAreSayingAboutYou.latestUpdate(UserPref.showAll()));
        Source<Html> symbi = toModule("symbi", ServicesYouMightBeInterested.latestUpdate(UserPref.showAll()));
        Source<Html> ads = toModule("ads", Ads.latestUpdate(UserPref.showAll()));
        Source<Html> wyfau = toModule("wyfau", WhatYourFriendsAreUpto.latestUpdate(UserPref.showAll()));

        Pagelet stream = interleave(wpasam, symbi, ads, wyfau);

        Pagelet page = views.pagelet.index.render(stream);
        StringChunks chunks = StringChunks.whenReady( out -> {
            final ActorFlowMaterializer mat = ActorFlowMaterializer.create(Akka.system());
            Source<Html> endSignal = Source.single(Html.apply("<end/>"));
            Sink<Html> s = Sink.foreach(html -> {
                if(html.equals("<end/>")) {
                    out.close();
                } else {
                    out.write(html.toString());
                }
            });
            page.source().concat(endSignal).to(s).run(mat);
        });
        return ok(chunks).as("text/html");
    }


    private static Pagelet interleave(Source<Html> wpasam, Source<Html> symbi, Source<Html> ads, Source<Html> wyfau) {
        Merge<Html> merge = Merge.<Html>create();
        UndefinedSink<Html> undefined = UndefinedSink.create();
        Source<Html> s = Source.fromGraph(builder -> {
            builder.addEdge(wpasam, merge)
                    .addEdge(symbi, merge)
                    .addEdge(ads, merge)
                    .addEdge(wyfau, merge)
                    .addEdge(merge, undefined);
            return undefined;
        });
        return new Pagelet(s);
    }

    private static Source<Html> toModule(String moduleId, F.Promise<Html> html) {
      return Source.from(html.wrapped()).map(h -> module.render(moduleId, h));

    }
}
