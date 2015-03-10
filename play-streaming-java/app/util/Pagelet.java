package util;

import akka.stream.javadsl.Source;
import play.twirl.api.*;

public class Pagelet implements play.twirl.api.Appendable<Pagelet> {
    private final Source<Html> s;
    public Pagelet(Html html) {
       s = Source.single(html);
    }

    public Pagelet(Source<Html> s) {
        this.s = s;
    }

    public Pagelet andThen(Pagelet another) {
        return new Pagelet(s.concat(another.s));
    }

    public Source<Html> source() { return s; }
}
