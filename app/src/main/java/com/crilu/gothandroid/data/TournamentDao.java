package com.crilu.gothandroid.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.crilu.gothandroid.model.firestore.Tournament;

import java.util.ArrayList;
import java.util.List;

public class TournamentDao {

    public static List<Tournament> getAllTournaments(Context context) {
        List<Tournament> tournaments = new ArrayList<>();

        SQLiteOpenHelper dbHelper = new GothaDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(GothaContract.TournamentEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                GothaContract.TournamentEntry.COLUMN_BEGIN_DATE);
        while (cursor.moveToNext()) {
            Tournament tournament = new Tournament();
            tournament.set_id(cursor.getInt(cursor.getColumnIndex(GothaContract.TournamentEntry._ID)));
            tournament.setId(cursor.getString(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_ID)));
            tournament.setBeginDate(cursor.getInt(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_BEGIN_DATE)));
            tournament.setContent(cursor.getString(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_CONTENT)));
            tournament.setCreator(cursor.getString(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_CREATOR)));
            tournament.setDirector(cursor.getString(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_DIRECTOR)));
            tournament.setFullName(cursor.getString(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_FULL_NAME)));
            tournament.setLocation(cursor.getString(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_LOCATION)));
            tournament.setShortName(cursor.getString(cursor.getColumnIndex(GothaContract.TournamentEntry.COLUMN_SHORT_NAME)));

            tournaments.add(tournament);
        }
        return tournaments;
    }
}
