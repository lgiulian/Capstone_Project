package com.crilu.gothandroid.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;

import com.crilu.gothandroid.data.GothaContract;
import com.crilu.gothandroid.data.GothaPreferences;
import com.crilu.gothandroid.data.TournamentDao;
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
        final List<Tournament> publishedTournament = new ArrayList<>();
        TournamentDao.fetchTournaments(startingTimestamp, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    long latestPublishedTournamentTime = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Timber.d(document.getId() + " => " + document.getData());
                        Tournament tournament = document.toObject(Tournament.class);
                        tournament.setIdentity(document.getId());
                        if (latestPublishedTournamentTime < tournament.getCreationDate().getTime()) {
                            latestPublishedTournamentTime = tournament.getCreationDate().getTime();
                        }
                        publishedTournament.add(tournament);
                    }
                    ContentValues[] tournamentValues = GothaSyncUtils.getTournamentValues(publishedTournament);

                    if (tournamentValues != null && tournamentValues.length != 0) {
                        ContentResolver gothaContentResolver = context.getContentResolver();

                        /* Delete old tournament data */
                        gothaContentResolver.delete(
                                GothaContract.TournamentEntry.CONTENT_URI,
                                GothaContract.TournamentEntry.COLUMN_CREATION_DATE + ">=?",
                                new String[] {Long.toString(startingTimestamp)});

                        /* Insert our new tournament data into Gotha's ContentProvider */
                        gothaContentResolver.bulkInsert(
                                GothaContract.TournamentEntry.CONTENT_URI,
                                tournamentValues);

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
                    }
                } else {
                    Timber.d(task.getException());
                }
            }
        });
    }
}