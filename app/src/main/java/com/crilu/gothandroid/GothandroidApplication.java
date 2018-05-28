package com.crilu.gothandroid;

import android.app.Application;
import android.content.Intent;

import com.crilu.gothandroid.utils.ParsePlayersIntentService;
import com.crilu.opengotha.RatingList;

public class GothandroidApplication extends Application {
    private static RatingList sRatingList;

    @Override
    public void onCreate() {
        super.onCreate();
        Intent parsePlayersIntent = new Intent(this, ParsePlayersIntentService.class);
        startService(parsePlayersIntent);
    }

    public static RatingList getRatingList() {
        return sRatingList;
    }

    public static void setRatingList(RatingList ratingList) {
        GothandroidApplication.sRatingList = ratingList;
    }
}
