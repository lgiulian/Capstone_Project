package com.crilu.gothandroid.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;

import com.crilu.gothandroid.GothandroidApplication;
import com.crilu.gothandroid.R;
import com.crilu.gothandroid.model.firestore.Message;
import com.crilu.gothandroid.model.firestore.Subscription;
import com.crilu.gothandroid.model.firestore.Tournament;
import com.crilu.gothandroid.sync.GothaSyncUtils;
import com.crilu.gothandroid.utils.FileUtils;
import com.crilu.gothandroid.utils.TournamentUtils;
import com.crilu.opengotha.TournamentInterface;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static com.crilu.gothandroid.GothandroidApplication.MESSAGE_DOC_REF_PATH;
import static com.crilu.gothandroid.GothandroidApplication.RESULT_DOC_ID_H9;
import static com.crilu.gothandroid.GothandroidApplication.RESULT_DOC_REF_PATH;
import static com.crilu.gothandroid.GothandroidApplication.SUBSCRIPTION_DOC_REF_PATH;
import static com.crilu.gothandroid.GothandroidApplication.TOURNAMENT_DOC_REF_PATH;

public class TournamentDao {

    public static List<Tournament> getAllTournaments(Context context) {
        List<Tournament> tournaments = new ArrayList<>();

        Cursor cursor = context.getContentResolver().query(GothaContract.TournamentEntry.CONTENT_URI,
                null,
                null,
                null,
                GothaContract.TournamentEntry.COLUMN_BEGIN_DATE + " DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Tournament tournament = new Tournament();
                tournament.setId(cursor.getLong(cursor.getColumnIndex(GothaContract.TournamentEntry._ID)));
                tournament.setIdentity(cursor.getString(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_IDENTITY)));
                tournament.setBeginDate(new Date(cursor.getLong(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_BEGIN_DATE))));
                tournament.setEndDate(new Date(cursor.getLong(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_END_DATE))));
                tournament.setContent(cursor.getString(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_CONTENT)));
                tournament.setCreator(cursor.getString(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_CREATOR)));
                tournament.setDirector(cursor.getString(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_DIRECTOR)));
                tournament.setFullName(cursor.getString(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_FULL_NAME)));
                tournament.setLocation(cursor.getString(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_LOCATION)));
                tournament.setShortName(cursor.getString(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_SHORT_NAME)));
                tournament.setCreationDate(new Date(cursor.getLong(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_CREATION_DATE))));
                tournament.setLastModificationDate(new Date(cursor.getLong(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_LAST_MODIFICATION_DATE))));

                tournaments.add(tournament);
            }
            cursor.close();
        }
        return tournaments;
    }

    public static Tournament getTournamentByIdentity(Context context, String identity) {
        Tournament tournament = null;
        Cursor cursor = context.getContentResolver().query(
                GothaContract.TournamentEntry.CONTENT_URI,
                null,
                GothaContract.TournamentEntry.COLUMN_IDENTITY + "=?",
                new String[] {identity},
                null);

        if (cursor != null && cursor.moveToFirst()) {
            tournament = getTournamentFromCursor(cursor);
            cursor.close();
        }
        return tournament;
    }

    public static Tournament getTournamentByFullName(Context context, String fullName) {
        Tournament tournament = null;
        Cursor cursor = context.getContentResolver().query(
                GothaContract.TournamentEntry.CONTENT_URI,
                null,
                GothaContract.TournamentEntry.COLUMN_FULL_NAME + "=?",
                new String[] {fullName},
                null);

        if (cursor != null && cursor.moveToFirst()) {
            tournament = getTournamentFromCursor(cursor);
            cursor.close();
        }
        return tournament;
    }

    @NonNull
    private static Tournament getTournamentFromCursor(Cursor cursor) {
        Tournament tournament;
        tournament = new Tournament();
        tournament.setId(cursor.getLong(cursor.getColumnIndex(GothaContract.TournamentEntry._ID)));
        tournament.setIdentity(cursor.getString(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_IDENTITY)));
        tournament.setBeginDate(new Date(cursor.getLong(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_BEGIN_DATE))));
        tournament.setEndDate(new Date(cursor.getLong(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_END_DATE))));
        tournament.setContent(cursor.getString(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_CONTENT)));
        tournament.setCreator(cursor.getString(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_CREATOR)));
        tournament.setDirector(cursor.getString(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_DIRECTOR)));
        tournament.setFullName(cursor.getString(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_FULL_NAME)));
        tournament.setLocation(cursor.getString(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_LOCATION)));
        tournament.setShortName(cursor.getString(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_SHORT_NAME)));
        tournament.setCreationDate(new Date(cursor.getLong(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_CREATION_DATE))));
        tournament.setLastModificationDate(new Date(cursor.getLong(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_LAST_MODIFICATION_DATE))));
        return tournament;
    }

    public static void fetchTournaments(long startingTimestamp, ValueEventListener valueEventListener) {
        Date fromDate;
        if (startingTimestamp > 0) {
            fromDate = new Date(startingTimestamp);
        } else {
            fromDate = new Date();
        }
        DatabaseReference db = GothandroidApplication.getFireDatabase();
        Query tournamentsQuery = db.child(TOURNAMENT_DOC_REF_PATH).child("creationDate/time").startAt(String.valueOf(fromDate.getTime()));
        tournamentsQuery.addListenerForSingleValueEvent(valueEventListener);
    }

    public static void fetchTournamentResults(String tournamentIdentity, ValueEventListener valueEventListener) {
        DatabaseReference db = GothandroidApplication.getFireDatabase();
        DatabaseReference tournamentResults = db.child(RESULT_DOC_REF_PATH + "/" + tournamentIdentity + "/" + RESULT_DOC_ID_H9);
        tournamentResults.addListenerForSingleValueEvent(valueEventListener);
    }

    public static void fetchSubscriptions(String tournamentIdentity, ValueEventListener valueEventListener) {
        DatabaseReference db = GothandroidApplication.getFireDatabase();
        String collectionPath = SUBSCRIPTION_DOC_REF_PATH + "/" + tournamentIdentity;
        Query subscriptionsQuery = db.child(collectionPath).orderByChild("subscriptionDate");
        subscriptionsQuery.addListenerForSingleValueEvent(valueEventListener);
    }

    public static void saveCurrentTournamentAndUploadOnFirestore(final Context context, final Tournament tournamentModel, String UID, final boolean displayFeedback, final CoordinatorLayout coordinatorLayout) {
        final TournamentInterface currentOpenedTournament = GothandroidApplication.getGothaModelInstance().getTournament();
        File file = TournamentUtils.getXmlFile(context, currentOpenedTournament);
        try {
            String tournamentContent = FileUtils.getFileContents(file);
            tournamentModel.setContent(tournamentContent);

            String currUser = GothandroidApplication.getCurrentUser();
            if (!TextUtils.isEmpty(currUser) && !TextUtils.isEmpty(tournamentModel.getContent())) {
                tournamentModel.setLastModificationDate(new Date());
                Map<String, Object> tournamentToSave = new HashMap<>();
                tournamentToSave.put(Tournament.FULL_NAME, tournamentModel.getFullName());
                tournamentToSave.put(Tournament.SHORT_NAME, tournamentModel.getShortName());
                tournamentToSave.put(Tournament.BEGIN_DATE, tournamentModel.getBeginDate());
                tournamentToSave.put(Tournament.END_DATE, tournamentModel.getEndDate());
                tournamentToSave.put(Tournament.LOCATION, tournamentModel.getLocation());
                tournamentToSave.put(Tournament.DIRECTOR, tournamentModel.getDirector());
                tournamentToSave.put(Tournament.CONTENT, tournamentModel.getContent());
                tournamentToSave.put(Tournament.CREATOR, UID);
                tournamentToSave.put(Tournament.LAST_MODIFICATION_DATE, tournamentModel.getLastModificationDate());
                Timber.d("uploading tournament on firestore");
                DatabaseReference db = GothandroidApplication.getFireDatabase();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(TOURNAMENT_DOC_REF_PATH + "/" + tournamentModel.getIdentity(), tournamentToSave);
                db.updateChildren(childUpdates)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Timber.d("saving  tournament in database");
                                saveTournament(context, tournamentModel);
                                Timber.d("Tournament %s was saved and uploaded", tournamentModel.getFullName());
                                if (displayFeedback) {
                                    Snackbar.make(coordinatorLayout, context.getString(R.string.tournament_saved_and_uploaded),
                                            Snackbar.LENGTH_LONG).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Timber.d(e);
                                if (displayFeedback) {
                                    Snackbar.make(coordinatorLayout, context.getString(R.string.tournament_saved_and_uploaded_error),
                                            Snackbar.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendMessageToAll(final Context context, final Tournament tournamentModel, final String message, final CoordinatorLayout coordinatorLayout) {
        if (!TextUtils.isEmpty(message)) {
            Map<String, Object> messageToSave = new HashMap<>();
            messageToSave.put(Message.MESSAGE, message);
            messageToSave.put(Message.FROM, tournamentModel.getFullName());
            messageToSave.put(Message.MESSAGE_DATE, new Date());
            Timber.d("uploading message on firestore");
            DatabaseReference db = GothandroidApplication.getFireDatabase();
            String key = db.child(MESSAGE_DOC_REF_PATH).child(tournamentModel.getIdentity()).push().getKey();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(MESSAGE_DOC_REF_PATH + "/" + tournamentModel.getIdentity() + "/" + key, messageToSave);
            db.updateChildren(childUpdates)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Timber.d("Message %s was uploaded", message);
                            if (coordinatorLayout != null) {
                                Snackbar.make(coordinatorLayout, context.getString(R.string.tournament_send_message), Snackbar.LENGTH_LONG).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Timber.d(e);
                            if (coordinatorLayout != null) {
                                Snackbar.make(coordinatorLayout, context.getString(R.string.tournament_send_message_error),
                                        Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    public static void saveTournament(Context context, Tournament tournamentModel) {
        ContentResolver gothaContentResolver = context.getContentResolver();
        ContentValues cv = GothaSyncUtils.getSingleTournamentContentValues(tournamentModel);
        gothaContentResolver.update(
                ContentUris.withAppendedId(GothaContract.TournamentEntry.CONTENT_URI, tournamentModel.getId()),
                cv,
                null,
                null);
    }

    public static Subscription getSubscriptionByTournamentAndUid(Context context, long tournamentId, String UID) {
        Subscription subscription = null;
        Cursor cursor = context.getContentResolver().query(
                GothaContract.SubscriptionEntry.CONTENT_URI,
                null,
                GothaContract.SubscriptionEntry.COLUMN_TOURNAMENT_ID + "=? AND " +
                        GothaContract.SubscriptionEntry.COLUMN_UID + "=?",
                new String[] {String.valueOf(tournamentId), UID},
                null);

        if (cursor != null && cursor.moveToFirst()) {
            subscription = new Subscription();
            subscription.setId(cursor.getLong(cursor.getColumnIndex(GothaContract.SubscriptionEntry._ID)));
            subscription.setIdentity(cursor.getString(cursor.getColumnIndex(GothaContract.SubscriptionEntry.COLUMN_IDENTITY)));
            subscription.setAgaId(cursor.getString(cursor.getColumnIndex(GothaContract.SubscriptionEntry.COLUMN_AGA_ID)));
            subscription.setEgfPin(cursor.getString(cursor.getColumnIndex(GothaContract.SubscriptionEntry.COLUMN_EGF_PIN)));
            subscription.setFfgLic(cursor.getString(cursor.getColumnIndex(GothaContract.SubscriptionEntry.COLUMN_FFG_LIC)));
            subscription.setIntent(cursor.getString(cursor.getColumnIndex(GothaContract.SubscriptionEntry.COLUMN_INTENT)));
            subscription.setState(cursor.getString(cursor.getColumnIndex(GothaContract.SubscriptionEntry.COLUMN_STATE)));
            subscription.setUid(cursor.getString(cursor.getColumnIndex(GothaContract.SubscriptionEntry.COLUMN_UID)));
            subscription.setTournamentId(cursor.getLong(cursor.getColumnIndex(GothaContract.SubscriptionEntry.COLUMN_TOURNAMENT_ID)));
            subscription.setSubscriptionDate(new Date(cursor.getLong(cursor.getColumnIndex(GothaContract.SubscriptionEntry.COLUMN_SUBSCRIPTION_DATE))));
            cursor.close();
        }
        return subscription;
    }
}
