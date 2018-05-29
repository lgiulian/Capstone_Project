package com.crilu.gothandroid;

import android.app.Application;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.crilu.gothandroid.utils.ParsePlayersIntentService;
import com.crilu.opengotha.RatingList;
import com.crilu.opengotha.model.GothaModel;
import com.crilu.opengotha.model.PlayersManager;

import timber.log.Timber;

public class GothandroidApplication extends Application {
    private static RatingList sRatingList;
    private static GothaModel sGothaModel;
    private static PlayersManager sPlayersManager;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }

        Intent parsePlayersIntent = new Intent(this, ParsePlayersIntentService.class);
        startService(parsePlayersIntent);
    }

    public static RatingList getRatingList() {
        return sRatingList;
    }

    public static void setRatingList(RatingList ratingList) {
        GothandroidApplication.sRatingList = ratingList;
    }

    public static GothaModel getGothaModelInstance() {
        if (sGothaModel == null) {
            sGothaModel = new GothaModel();
        }
        return sGothaModel;
    }

    public static PlayersManager getPlayersManagerInstance() {
        if (sPlayersManager == null && sGothaModel.getTournament() != null) {
            sPlayersManager = new PlayersManager(sGothaModel.getTournament());
        }
        return sPlayersManager;
    }

    /** A tree which logs important information for crash reporting. */
    private static class CrashReportingTree extends Timber.Tree {
        @Override protected void log(int priority, String tag, @NonNull String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }

            //FakeCrashLibrary.log(priority, tag, message);

            //if (t != null) {
            //    if (priority == Log.ERROR) {
            //        FakeCrashLibrary.logError(t);
            //    } else if (priority == Log.WARN) {
            //        FakeCrashLibrary.logWarning(t);
            //    }
            //}
        }
    }
}
