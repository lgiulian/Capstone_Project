package com.crilu.gothandroid.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.crilu.gothandroid.data.GothaContract.MessageEntry;
import com.crilu.gothandroid.data.GothaContract.SubscriptionEntry;
import com.crilu.gothandroid.data.GothaContract.TournamentEntry;

public class GothaDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "gotha.db";
    private static final int DATABASE_VERSION = 8;

    public GothaDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TOURNAMENT_TABLE = "CREATE TABLE " + TournamentEntry.TABLE_NAME + " (" +
                TournamentEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TournamentEntry.COLUMN_IDENTITY + " TEXT, " +
                TournamentEntry.COLUMN_FULL_NAME + " TEXT, " +
                TournamentEntry.COLUMN_SHORT_NAME + " TEXT, " +
                TournamentEntry.COLUMN_LOCATION + " TEXT, " +
                TournamentEntry.COLUMN_DIRECTOR + " TEXT, " +
                TournamentEntry.COLUMN_BEGIN_DATE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                TournamentEntry.COLUMN_CREATOR + " TEXT NOT NULL, " +
                TournamentEntry.COLUMN_CREATION_DATE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                TournamentEntry.COLUMN_LAST_MODIFICATION_DATE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                TournamentEntry.COLUMN_SUBSCRIPTION_TYPE + " TEXT, " +
                TournamentEntry.COLUMN_CONTENT + " TEXT NOT NULL" +
                ");";

        final String SQL_CREATE_SUBSCRIPTION_TABLE = "CREATE TABLE " + SubscriptionEntry.TABLE_NAME + " (" +
                SubscriptionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SubscriptionEntry.COLUMN_IDENTITY + " TEXT, " +
                SubscriptionEntry.COLUMN_TOURNAMENT_ID + " INTEGER, " +
                SubscriptionEntry.COLUMN_UID + " TEXT, " +
                SubscriptionEntry.COLUMN_EGF_PIN + " TEXT, " +
                SubscriptionEntry.COLUMN_FFG_LIC + " TEXT, " +
                SubscriptionEntry.COLUMN_AGA_ID + " TEXT, " +
                SubscriptionEntry.COLUMN_INTENT + " TEXT, " +
                SubscriptionEntry.COLUMN_STATE + " INTEGER, " +
                SubscriptionEntry.COLUMN_SUBSCRIPTION_DATE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";

        final String SQL_CREATE_MESSAGE_TABLE = "CREATE TABLE " + MessageEntry.TABLE_NAME + " (" +
                MessageEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MessageEntry.COLUMN_TOURNAMENT_IDENTITY + " TEXT, " +
                MessageEntry.COLUMN_TITLE + " TEXT, " +
                MessageEntry.COLUMN_MESSAGE + " TEXT, " +
                MessageEntry.COLUMN_COMMAND + " TEXT, " +
                MessageEntry.COLUMN_EGF_PIN + " TEXT, " +
                MessageEntry.COLUMN_MESSAGE_DATE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";

        db.execSQL(SQL_CREATE_TOURNAMENT_TABLE);
        db.execSQL(SQL_CREATE_SUBSCRIPTION_TABLE);
        db.execSQL(SQL_CREATE_MESSAGE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TournamentEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SubscriptionEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MessageEntry.TABLE_NAME);
        onCreate(db);
    }
}
