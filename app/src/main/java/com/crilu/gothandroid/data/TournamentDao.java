package com.crilu.gothandroid.data;

import android.content.Context;
import android.database.Cursor;

import com.crilu.gothandroid.GothandroidApplication;
import com.crilu.gothandroid.model.firestore.Tournament;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
}
