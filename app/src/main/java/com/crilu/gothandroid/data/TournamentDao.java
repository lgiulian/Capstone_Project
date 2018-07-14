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
import com.crilu.gothandroid.model.firestore.Tournament;
import com.crilu.gothandroid.sync.GothaSyncUtils;
import com.crilu.gothandroid.utils.FileUtils;
import com.crilu.gothandroid.utils.TournamentUtils;
import com.crilu.opengotha.TournamentInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static com.crilu.gothandroid.GothandroidApplication.MESSAGE_DOC_REF_RELATIVE_PATH;
import static com.crilu.gothandroid.GothandroidApplication.SUBSCRIPTION_DOC_REF_RELATIVE_PATH;
import static com.crilu.gothandroid.GothandroidApplication.TOURNAMENT_DOC_REF_PATH;

public class TournamentDao {

    public static List<Tournament> getAllTournaments(Context context) {
        List<Tournament> tournaments = new ArrayList<>();

        Cursor cursor = context.getContentResolver().query(GothaContract.TournamentEntry.CONTENT_URI,
                null,
                null,
                null,
                GothaContract.TournamentEntry.COLUMN_BEGIN_DATE);
        while (cursor.moveToNext()) {
            Tournament tournament = new Tournament();
            tournament.setId(cursor.getLong(cursor.getColumnIndex(GothaContract.TournamentEntry._ID)));
            tournament.setIdentity(cursor.getString(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_IDENTITY)));
            tournament.setBeginDate(new Date(cursor.getLong(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_BEGIN_DATE))));
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
            tournament = new Tournament();
            tournament.setId(cursor.getLong(cursor.getColumnIndex(GothaContract.TournamentEntry._ID)));
            tournament.setIdentity(cursor.getString(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_IDENTITY)));
            tournament.setBeginDate(new Date(cursor.getLong(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_BEGIN_DATE))));
            tournament.setContent(cursor.getString(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_CONTENT)));
            tournament.setCreator(cursor.getString(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_CREATOR)));
            tournament.setDirector(cursor.getString(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_DIRECTOR)));
            tournament.setFullName(cursor.getString(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_FULL_NAME)));
            tournament.setLocation(cursor.getString(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_LOCATION)));
            tournament.setShortName(cursor.getString(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_SHORT_NAME)));
            tournament.setCreationDate(new Date(cursor.getLong(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_CREATION_DATE))));
            tournament.setLastModificationDate(new Date(cursor.getLong(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_LAST_MODIFICATION_DATE))));
            cursor.close();
        }
        return tournament;
    }

    public static void fetchTournaments(long startingTimestamp, OnCompleteListener<QuerySnapshot> listener) {
        FirebaseFirestore db = GothandroidApplication.getFirebaseFirestore();
        Date fromDate;
        if (startingTimestamp > 0) {
            fromDate = new Date(startingTimestamp);
        } else {
            fromDate = new Date();
        }
        db.collection(TOURNAMENT_DOC_REF_PATH).whereGreaterThanOrEqualTo(Tournament.CREATION_DATE, fromDate).get().addOnCompleteListener(listener);
    }

    public static void fetchSubscriptions(String tournamentIdentity, OnCompleteListener<QuerySnapshot> listener) {
        FirebaseFirestore db = GothandroidApplication.getFirebaseFirestore();
        String collectionPath = TOURNAMENT_DOC_REF_PATH + "/" + tournamentIdentity + SUBSCRIPTION_DOC_REF_RELATIVE_PATH;
        db.collection(collectionPath).get().addOnCompleteListener(listener);
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
                tournamentToSave.put(Tournament.LOCATION, tournamentModel.getLocation());
                tournamentToSave.put(Tournament.DIRECTOR, tournamentModel.getDirector());
                tournamentToSave.put(Tournament.CONTENT, tournamentModel.getContent());
                tournamentToSave.put(Tournament.CREATOR, UID);
                tournamentToSave.put(Tournament.LAST_MODIFICATION_DATE, tournamentModel.getLastModificationDate());
                Timber.d("uploading tournament on firestore");
                FirebaseFirestore db = GothandroidApplication.getFirebaseFirestore();
                db.collection(TOURNAMENT_DOC_REF_PATH).document(tournamentModel.getIdentity()).set(tournamentToSave, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Timber.d("saving  tournament in database");
                            saveTournament(context, tournamentModel);
                            Timber.d("Tournament %s was saved and uploaded", tournamentModel.getFullName());
                            if (displayFeedback) {
                                Snackbar.make(coordinatorLayout, context.getString(R.string.tournament_saved_and_uploaded), Snackbar.LENGTH_LONG).show();
                            }
                        } else {
                            Timber.d(task.getException());
                            if (displayFeedback) {
                                Snackbar.make(coordinatorLayout, context.getString(R.string.tournament_saved_and_uploaded_error), Snackbar.LENGTH_LONG).show();
                            }
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
            FirebaseFirestore db = GothandroidApplication.getFirebaseFirestore();
            db.collection(TOURNAMENT_DOC_REF_PATH + "/" + tournamentModel.getIdentity() + MESSAGE_DOC_REF_RELATIVE_PATH).add(messageToSave).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if (task.isSuccessful()) {
                        Timber.d("Message %s was uploaded", message);
                        if (coordinatorLayout != null) {
                            Snackbar.make(coordinatorLayout, context.getString(R.string.tournament_send_message), Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        Timber.d(task.getException());
                        if (coordinatorLayout != null) {
                            Snackbar.make(coordinatorLayout, context.getString(R.string.tournament_send_message_error), Snackbar.LENGTH_LONG).show();
                        }
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

}
