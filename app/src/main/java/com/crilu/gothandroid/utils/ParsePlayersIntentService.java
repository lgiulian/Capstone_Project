package com.crilu.gothandroid.utils;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.crilu.gothandroid.GothandroidApplication;
import com.crilu.gothandroid.R;
import com.crilu.opengotha.RatingList;

public class ParsePlayersIntentService extends IntentService {
    public ParsePlayersIntentService() {
        super("ParsePlayersIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        GothandroidApplication.setRatingList(new RatingList(RatingList.TYPE_EGF, getResources().openRawResource(R.raw.egf_db)));
    }
}
