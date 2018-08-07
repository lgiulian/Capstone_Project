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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

class GothaSyncTask {

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
                .currentTimeMillis()- TimeUnit.DAYS.toSeconds(360)*1000;
        final List<Subscription> tournamentSubscriptions = new ArrayList<>();
        final long fetchTournamentsStartTime = System.currentTimeMillis();
        TournamentDao.fetchTournaments(startingTimestamp, new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ContentResolver gothaContentResolver = context.getContentResolver();
                        /* Delete old tournament data */
                        gothaContentResolver.delete(
                                GothaContract.TournamentEntry.CONTENT_URI,
                                GothaContract.TournamentEntry.COLUMN_CREATION_DATE + ">=?",
                                new String[]{Long.toString(startingTimestamp)});

                        long latestPublishedTournamentTime = 0;
                        for (DataSnapshot tournamentSnapshot: dataSnapshot.getChildren()) {
                            Tournament tournament = tournamentSnapshot.getValue(Tournament.class);
                            if (tournament != null) {
                                String tournamentIdentity = tournamentSnapshot.getKey();
                                Timber.d( tournamentIdentity + " => " + tournament.toString());
                                tournament.setIdentity(tournamentIdentity);
                                if (latestPublishedTournamentTime < tournament.getCreationDate().getTime()) {
                                    latestPublishedTournamentTime = tournament.getCreationDate().getTime();
                                }
                                ContentValues cv = GothaSyncUtils.getSingleTournamentContentValues(tournament);
                                /* Insert our new tournament data into Gotha's ContentProvider */
                                Uri uri = gothaContentResolver.insert(
                                        GothaContract.TournamentEntry.CONTENT_URI,
                                        cv);
                                String id = null;
                                if (uri != null) {
                                    id = uri.getPathSegments().get(1);
                                }
                                fetchSubscriptions(Long.valueOf(id), tournamentIdentity, gothaContentResolver, tournamentSubscriptions);
                            }
                        }
                        Timber.d("Tournaments sync with success");
                        Timber.d("fetchTournaments took %s", (System.currentTimeMillis() - fetchTournamentsStartTime) / 1000);
                        NotificationUtils.notifyUserOfNewTournament(context, latestPublishedTournamentTime);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Timber.e(databaseError.toException());
                    }
                });
    }

    private static void fetchSubscriptions(final Long tournamentId, final String tournamentIdentity, final ContentResolver gothaContentResolver, final List<Subscription> tournamentSubscriptions) {
        TournamentDao.fetchSubscriptions(tournamentIdentity, new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot subscriptionSnapshot: dataSnapshot.getChildren()) {
                            String key = subscriptionSnapshot.getKey();
                            Subscription subscription = subscriptionSnapshot.getValue(Subscription.class);
                            if (subscription != null) {
                                Timber.d(key + " => " + subscription.getEgfPin());
                                subscription.setIdentity(key);
                                subscription.setTournamentId(tournamentId);
                                subscription.setTournamentIdentity(tournamentIdentity);
                                tournamentSubscriptions.add(subscription);
                            }
                        }
                        saveSubscriptions(tournamentSubscriptions, gothaContentResolver);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Timber.e(databaseError.toException());
                    }
                });
    }

    private static void saveSubscriptions(List<Subscription> tournamentSubscriptions, ContentResolver gothaContentResolver) {
        if (tournamentSubscriptions.size() > 0) {
            ContentValues[] subscriptionValues = GothaSyncUtils.getSubscriptionValues(tournamentSubscriptions);

            if (subscriptionValues != null && subscriptionValues.length != 0) {
                /* Insert our new subscriptions data into Gotha's ContentProvider */
                gothaContentResolver.bulkInsert(
                        GothaContract.SubscriptionEntry.CONTENT_URI,
                        subscriptionValues);

                Timber.d("Tournament's subscriptions saved with success");
            }
        }
    }
}