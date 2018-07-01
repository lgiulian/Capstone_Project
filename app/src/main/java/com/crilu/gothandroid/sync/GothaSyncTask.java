package com.crilu.gothandroid.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.crilu.gothandroid.data.GothaContract;
import com.crilu.gothandroid.data.GothaPreferences;
import com.crilu.gothandroid.data.TournamentDao;
import com.crilu.gothandroid.model.firestore.Subscription;
import com.crilu.gothandroid.model.firestore.Tournament;
import com.crilu.gothandroid.utils.NotificationUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class GothaSyncTask {

    /**
     * Performs the network request for future tournaments and
     * inserts the new tournaments information into our ContentProvider. Will notify the user that new
     * data has been loaded if the user hasn't been notified of the latest published tournament
     * AND they haven't disabled notifications in the preferences screen.
     *
     * @param context Used to access utility methods and the ContentResolver
     */
    synchronized public static void syncTournaments(final Context context) {

        final long latestKnownPublishedTournament = GothaPreferences.getLatestKnownPublishedTournament(context);
        final long startingTimestamp = latestKnownPublishedTournament > 0? latestKnownPublishedTournament: System
                .currentTimeMillis();
        final List<Subscription> tournamentSubscriptions = new ArrayList<>();
        final long fetchTournamentsStartTime = System.currentTimeMillis();
        TournamentDao.fetchTournaments(startingTimestamp, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ContentResolver gothaContentResolver = context.getContentResolver();
                    /* Delete old tournament data */
                    gothaContentResolver.delete(
                            GothaContract.TournamentEntry.CONTENT_URI,
                            GothaContract.TournamentEntry.COLUMN_CREATION_DATE + ">=?",
                            new String[] {Long.toString(startingTimestamp)});

                    long latestPublishedTournamentTime = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Timber.d(document.getId() + " => " + document.getData());
                        Tournament tournament = document.toObject(Tournament.class);
                        String tournamentIdentity = document.getId();
                        tournament.setIdentity(tournamentIdentity);
                        if (latestPublishedTournamentTime < tournament.getCreationDate().getTime()) {
                            latestPublishedTournamentTime = tournament.getCreationDate().getTime();
                        }
                        ContentValues cv = GothaSyncUtils.getSingleTournamentContentValues(tournament);
                        /* Insert our new tournament data into Gotha's ContentProvider */
                        Uri uri = gothaContentResolver.insert(
                                GothaContract.TournamentEntry.CONTENT_URI,
                                cv);
                        String id = uri.getPathSegments().get(1);
                        fetchSubscriptions(context, Long.valueOf(id), tournamentIdentity, tournamentSubscriptions);
                    }
                    if (task.getResult().size() > 0) {
                        ContentValues[] subscriptionValues = GothaSyncUtils.getSubscriptionValues(tournamentSubscriptions);

                        if (subscriptionValues != null && subscriptionValues.length != 0) {
                            /* Insert our new subscriptions data into Gotha's ContentProvider */
                            gothaContentResolver.bulkInsert(
                                    GothaContract.SubscriptionEntry.CONTENT_URI,
                                    subscriptionValues);

                            Timber.d("Tournament's subscriptions downloaded with success");
                        }

                        /*
                         * Finally, after we insert data into the ContentProvider, determine whether or not
                         * we should notify the user that the tournaments has been refreshed.
                         */
                        boolean notificationsEnabled = GothaPreferences.areNotificationsEnabled(context);

                        /*
                         * We only want to show the notification if the user wants them shown and we
                         * have new published tournaments.
                         */
                        if (notificationsEnabled && latestKnownPublishedTournament < latestPublishedTournamentTime) {
                            NotificationUtils.notifyUserOfNewTournament(context, latestPublishedTournamentTime);
                        }
                        Timber.d("Tournaments sync with success");
                        Timber.d("fetchTournaments took %s", (System.currentTimeMillis() - fetchTournamentsStartTime)/1000);
                    }
                } else {
                    Timber.d(task.getException());
                }
            }
        });
    }

    private static void fetchSubscriptions(final Context context, final Long tournamentId, final String tournamentIdentity, final List<Subscription> tournamentSubscriptions) {
        TournamentDao.fetchSubscriptions(tournamentIdentity, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Timber.d(document.getId() + " => " + document.getData());
                        Subscription subscription = document.toObject(Subscription.class);
                        subscription.setIdentity(document.getId());
                        subscription.setTournamentId(tournamentId);
                        subscription.setTournamentIdentity(tournamentIdentity);
                        tournamentSubscriptions.add(subscription);
                    }
                } else {
                    Timber.d(task.getException());
                }
            }
        });
    }
}