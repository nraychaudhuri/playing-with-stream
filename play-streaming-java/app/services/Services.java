package services;

import play.libs.F;
import play.twirl.api.Html;

import java.util.concurrent.TimeUnit;

public class Services {

    public static class UserPref {
        public static UserPreference showAll() { return new UserPreference() {}; }
    }

    public interface UserPreference {
    }

    public static class WhatPeopleAreSayingAboutYou {
        public static F.Promise<Html> latestUpdate(UserPreference u) {
            return F.Promise.timeout(Html.apply("This is What people are saying about you"), 2, TimeUnit.SECONDS);
        }
    }

    public static class ServicesYouMightBeInterested {
        public static F.Promise<Html> latestUpdate(UserPreference user) {
            return F.Promise.timeout(Html.apply("here are few services you might like"), 5, TimeUnit.SECONDS);
        }
    }

    public static class WhatYourFriendsAreUpto {
        public static F.Promise<Html> latestUpdate(UserPreference user) {
            return F.Promise.timeout(Html.apply("Your friend just got a new job"), 4, TimeUnit.SECONDS);
        }
    }

    public static class Ads {
        public static F.Promise<Html> latestUpdate(UserPreference user) {
            return F.Promise.timeout(Html.apply("Take Play training course from Typesafe"), 4, TimeUnit.SECONDS);
        }
    }

}
