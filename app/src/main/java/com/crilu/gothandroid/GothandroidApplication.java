package com.crilu.gothandroid;

import android.app.Application;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.crilu.gothandroid.utils.ParsePlayersIntentService;
import com.crilu.opengotha.RatingList;
import com.crilu.opengotha.model.GamesPair;
import com.crilu.opengotha.model.GothaModel;
import com.crilu.opengotha.model.PlayersManager;
import com.google.firebase.FirebaseApp;

import java.text.DateFormat;

import timber.log.Timber;

public class GothandroidApplication extends Application {

    public static final String TOURNAMENT_DOC_REF_PATH = "tournament";

    public static final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);

    private static RatingList sRatingList;
    private static GothaModel sGothaModel;
    private static GamesPair sGamesPair;
    private static PlayersManager sPlayersManager;
    private static String sCurrentUser;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }

        FirebaseApp.initializeApp(this);

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

    public static GamesPair getGamesPairInstance() {
        if (sGamesPair == null && sGothaModel.getTournament() != null) {
            sGamesPair = new GamesPair(sGothaModel.getTournament());
        }
        return sGamesPair;
    }

    //public static GamesPair getGamesPairInstance() {
    //    if (sGamesPair == null && sGothaModel.getTournament() != null) {
    //        sGamesPair = new GamesPair(sGothaModel.getTournament());
    //    }
    //    return sGamesPair;
    //}

    public static String getCurrentUser() {
        return sCurrentUser;
    }

    public static void setCurrentUser(String currentUser) {
        GothandroidApplication.sCurrentUser = currentUser;
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
