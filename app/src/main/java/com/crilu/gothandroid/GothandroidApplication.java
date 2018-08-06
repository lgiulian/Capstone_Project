package com.crilu.gothandroid;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import com.crilu.gothandroid.utils.NotificationUtils;
import com.crilu.opengotha.RatingList;
import com.crilu.opengotha.model.GamesOptions;
import com.crilu.opengotha.model.GamesPair;
import com.crilu.opengotha.model.GamesResults;
import com.crilu.opengotha.model.GothaModel;
import com.crilu.opengotha.model.PlayersManager;
import com.crilu.opengotha.model.Publish;
import com.crilu.opengotha.model.TournamentOptions;
import com.facebook.stetho.Stetho;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import timber.log.Timber;

public class GothandroidApplication extends Application implements GothaModel.GothaListener {

    public static final String TOURNAMENT_DOC_REF_PATH = "tournament";
    public static final String USER_DOC_REF_PATH = "user";
    public static final String SUBSCRIPTION_DOC_REF_RELATIVE_PATH = "/subscription";
    public static final String MESSAGE_DOC_REF_RELATIVE_PATH = "/message";
    public static final String RESULT_DOC_REF_RELATIVE_PATH = "/result";
    public static final String RESULT_DOC_ID_HTML = "resultHtml";
    public static final String RESULT_DOC_ID_H9 = "resultH9";

    public static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static final DateFormat dateFormatPretty = DateFormat.getDateInstance(DateFormat.MEDIUM);

    private static RatingList sRatingList;
    private static GothaModel sGothaModel;
    private static GamesPair sGamesPair;
    private static GamesResults sGamesResults;
    private static PlayersManager sPlayersManager;
    private static String sCurrentUser;
    private static FirebaseFirestore sFirestore;
    private static String sCurrentToken;

    private static GothandroidApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }

        Stetho.initializeWithDefaults(this);

        createNotificationChannel();

        FirebaseApp.initializeApp(this);

        sInstance = this;
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
            sGothaModel.addGothaListener(sInstance);
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

    public static GamesResults getGamesResultsInstance() {
        if (sGamesResults == null && sGothaModel.getTournament() != null) {
            sGamesResults = new GamesResults(sGothaModel.getTournament());
        }
        return sGamesResults;
    }

    public static TournamentOptions getTournamentOptionsInstance() {
        return new TournamentOptions(sGothaModel.getTournament());
    }

    public static GamesOptions getGamesOptionsInstance() {
        return new GamesOptions(sGothaModel.getTournament());
    }

    public static Publish getPublishInstance() {
        return new Publish(sGothaModel.getTournament());
    }

    public static String getCurrentUser() {
        return sCurrentUser;
    }

    public static void setCurrentUser(String currentUser) {
        GothandroidApplication.sCurrentUser = currentUser;
    }

    public static void setCurrentToken(String currentToken) {
        GothandroidApplication.sCurrentToken = currentToken;
    }

    public static String getCurrentToken() {
        return sCurrentToken;
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

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NotificationUtils.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    @NonNull
    public static FirebaseFirestore getFirebaseFirestore() {
        if (sFirestore == null) {
            sFirestore = FirebaseFirestore.getInstance();
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setTimestampsInSnapshotsEnabled(true)
                    .build();
            sFirestore.setFirestoreSettings(settings);
        }
        return sFirestore;
    }

    @Override
    public void onCurrentTournamentChanged() {
        sPlayersManager = null;
        sGamesPair = null;
    }

    @Override
    public void updateTitle() {

    }

    @Override
    public void warnPreliminaryRegisteringStatus(String message) {

    }

    @Override
    public void controlPanelModelUpdated() {

    }

    @Override
    public void updateTime(String time) {

    }

    @Override
    public void roundNumberChanged(int round) {

    }

}
