package com.crilu.gothandroid.sync;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.crilu.gothandroid.GothandroidApplication;
import com.crilu.gothandroid.data.GothaContract;
import com.crilu.gothandroid.model.firestore.Subscription;
import com.crilu.gothandroid.model.firestore.Tournament;
import com.crilu.gothandroid.utils.AppExecutors;
import com.crilu.opengotha.TournamentInterface;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class GothaSyncUtils {

    private static final int SYNC_INTERVAL_HOURS = 3;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;

    private static boolean sInitialized;

    private static final String GOTHA_SYNC_TAG = "gotha-sync";

    /**
     * Schedules a repeating sync of Gotha data using FirebaseJobDispatcher.
     * @param context Context used to create the GooglePlayDriver that powers the
     *                FirebaseJobDispatcher
     */
    private static void scheduleFirebaseJobDispatcherSync(@NonNull final Context context) {

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        /* Create the Job to periodically sync Gotha */
        Job syncGothaJob = dispatcher.newJobBuilder()
                .setService(GothaFirebaseJobService.class)
                .setTag(GOTHA_SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();

        dispatcher.schedule(syncGothaJob);
    }
    /**
     * Creates periodic sync tasks and checks to see if an immediate sync is required. If an
     * immediate sync is required, this method will take care of making sure that sync occurs.
     *
     * @param context Context that will be passed to other methods and used to access the
     *                ContentResolver
     */
    synchronized public static void initialize(@NonNull final Context context) {
        /*
         * Only perform initialization once per app lifetime. If initialization has already been
         * performed, we have nothing to do in this method.
         */
        if (sInitialized) return;
        sInitialized = true;

        /*
         * This method call triggers Gotha to create its task to synchronize tournaments data
         * periodically.
         */
        scheduleFirebaseJobDispatcherSync(context);

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Uri tournamentsQueryUri = GothaContract.TournamentEntry.CONTENT_URI;

                String[] projectionColumns = {GothaContract.TournamentEntry._ID};

                /* Here, we perform the query to check to see if we have any tournaments data */
                Cursor cursor = context.getContentResolver().query(
                        tournamentsQueryUri,
                        projectionColumns,
                        null,
                        null,
                        null);
                if (null == cursor || cursor.getCount() == 0) {
                    startImmediateSync(context);
                }

                cursor.close();
            }
        });
    }

    /**
     * Helper method to perform a sync immediately using an IntentService for asynchronous
     * execution.
     *
     * @param context The Context used to start the IntentService for the sync.
     */
    public static void startImmediateSync(@NonNull final Context context) {
        Intent intentToSyncImmediately = new Intent(context, GothaSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }

    public static ContentValues[] getTournamentValues(@NonNull List<Tournament> publishedTournament) {
        ContentValues[] contentValues = new ContentValues[publishedTournament.size()];
        int counter = 0;
        for (Tournament tournament: publishedTournament) {
            ContentValues values = getSingleTournamentContentValues(tournament);
            contentValues[counter] = values;
            counter++;
        }

        return contentValues;
    }

    @NonNull
    public static ContentValues getSingleTournamentContentValues(Tournament tournament) {
        ContentValues values = new ContentValues();
        values.put(GothaContract.TournamentEntry.COLUMN_BEGIN_DATE, tournament.getBeginDate().getTime());
        values.put(GothaContract.TournamentEntry.COLUMN_CREATION_DATE, tournament.getCreationDate().getTime());
        values.put(GothaContract.TournamentEntry.COLUMN_CONTENT, tournament.getContent());
        values.put(GothaContract.TournamentEntry.COLUMN_CREATOR, tournament.getCreator());
        values.put(GothaContract.TournamentEntry.COLUMN_DIRECTOR, tournament.getDirector());
        values.put(GothaContract.TournamentEntry.COLUMN_FULL_NAME, tournament.getFullName());
        values.put(GothaContract.TournamentEntry.COLUMN_SHORT_NAME, tournament.getShortName());
        values.put(GothaContract.TournamentEntry.COLUMN_LOCATION, tournament.getLocation());
        values.put(GothaContract.TournamentEntry.COLUMN_IDENTITY, tournament.getIdentity());
        return values;
    }

    @NonNull
    public static ContentValues getSingleSubscriptionContentValues(Subscription subscription) {
        ContentValues values = new ContentValues();
        values.put(GothaContract.SubscriptionEntry.COLUMN_SUBSCRIPTION_DATE, subscription.getSubscriptionDate().getTime());
        values.put(GothaContract.SubscriptionEntry.COLUMN_IDENTITY, subscription.getIdentity());
        values.put(GothaContract.SubscriptionEntry.COLUMN_TOURNAMENT_ID, subscription.getTournamentId());
        values.put(GothaContract.SubscriptionEntry.COLUMN_TOKEN, subscription.getToken());
        values.put(GothaContract.SubscriptionEntry.COLUMN_STATE, subscription.getState());
        values.put(GothaContract.SubscriptionEntry.COLUMN_INTENT, subscription.getState());
        values.put(GothaContract.SubscriptionEntry.COLUMN_FFG_LIC, subscription.getFfgLic());
        values.put(GothaContract.SubscriptionEntry.COLUMN_EGF_PIN, subscription.getEgfPin());
        values.put(GothaContract.SubscriptionEntry.COLUMN_AGA_ID, subscription.getAgaId());
        return values;
    }

    @NonNull
    public static ContentValues getGothaTournamentContentValues(TournamentInterface tournament, String tournamentContent) {
        ContentValues values = new ContentValues();
        values.put(GothaContract.TournamentEntry.COLUMN_BEGIN_DATE, tournament.getTournamentParameterSet().getGeneralParameterSet().getBeginDate().getTime());
        values.put(GothaContract.TournamentEntry.COLUMN_CREATION_DATE, System.currentTimeMillis());
        values.put(GothaContract.TournamentEntry.COLUMN_CONTENT, tournamentContent);
        values.put(GothaContract.TournamentEntry.COLUMN_CREATOR, GothandroidApplication.getCurrentUser());
        values.put(GothaContract.TournamentEntry.COLUMN_DIRECTOR, tournament.getTournamentParameterSet().getGeneralParameterSet().getDirector());
        values.put(GothaContract.TournamentEntry.COLUMN_FULL_NAME, tournament.getFullName());
        values.put(GothaContract.TournamentEntry.COLUMN_SHORT_NAME, tournament.getShortName());
        values.put(GothaContract.TournamentEntry.COLUMN_LOCATION, tournament.getTournamentParameterSet().getGeneralParameterSet().getLocation());
        return values;
    }

    public static ContentValues[] getSubscriptionValues(@NonNull List<Subscription> tournamentSubscriptions) {
        ContentValues[] contentValues = new ContentValues[tournamentSubscriptions.size()];
        int counter = 0;
        for (Subscription subscription: tournamentSubscriptions) {
            ContentValues values = new ContentValues();
            values.put(GothaContract.SubscriptionEntry.COLUMN_SUBSCRIPTION_DATE, subscription.getSubscriptionDate().getTime());
            values.put(GothaContract.SubscriptionEntry.COLUMN_AGA_ID, subscription.getAgaId());
            values.put(GothaContract.SubscriptionEntry.COLUMN_EGF_PIN, subscription.getEgfPin());
            values.put(GothaContract.SubscriptionEntry.COLUMN_FFG_LIC, subscription.getFfgLic());
            values.put(GothaContract.SubscriptionEntry.COLUMN_INTENT, subscription.getIntent());
            values.put(GothaContract.SubscriptionEntry.COLUMN_STATE, subscription.getState());
            values.put(GothaContract.SubscriptionEntry.COLUMN_TOKEN, subscription.getToken());
            values.put(GothaContract.SubscriptionEntry.COLUMN_IDENTITY, subscription.getIdentity());
            values.put(GothaContract.SubscriptionEntry.COLUMN_TOURNAMENT_ID, subscription.getTournamentIdentity());
            contentValues[counter] = values;
            counter++;
        }

        return contentValues;
    }
}
