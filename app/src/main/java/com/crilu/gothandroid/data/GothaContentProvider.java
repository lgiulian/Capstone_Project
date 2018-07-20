package com.crilu.gothandroid.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class GothaContentProvider extends ContentProvider {
    private GothaDbHelper mDbHelper;

    public static final int TOURNAMENTS = 100;
    public static final int TOURNAMENT_WITH_ID = 101;
    public static final int SUBSCRIPTIONS = 200;
    public static final int SUBSCRIPTION_WITH_ID = 201;
    public static final int MESSAGES = 300;
    public static final int MESSAGE_WITH_ID = 301;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(GothaContract.AUTHORITY, GothaContract.PATH_TOURNAMENTS, TOURNAMENTS);
        uriMatcher.addURI(GothaContract.AUTHORITY, GothaContract.PATH_TOURNAMENTS + "/#", TOURNAMENT_WITH_ID);
        uriMatcher.addURI(GothaContract.AUTHORITY, GothaContract.PATH_SUBSCRIPTION, SUBSCRIPTIONS);
        uriMatcher.addURI(GothaContract.AUTHORITY, GothaContract.PATH_SUBSCRIPTION + "/#", SUBSCRIPTION_WITH_ID);
        uriMatcher.addURI(GothaContract.AUTHORITY, GothaContract.PATH_MESSAGE, MESSAGES);
        uriMatcher.addURI(GothaContract.AUTHORITY, GothaContract.PATH_MESSAGE + "/#", MESSAGE_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mDbHelper = new GothaDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            case TOURNAMENTS:
                //Timber.d("query tournaments table");
                retCursor = db.query(GothaContract.TournamentEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case TOURNAMENT_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String sel = GothaContract.TournamentEntry._ID + "=?";
                String[] selArgs = new String[] {id};
                retCursor = db.query(GothaContract.TournamentEntry.TABLE_NAME,
                        projection,
                        sel,
                        selArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case SUBSCRIPTIONS:
                retCursor = db.query(GothaContract.SubscriptionEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case SUBSCRIPTION_WITH_ID:
                id = uri.getPathSegments().get(1);
                sel = GothaContract.SubscriptionEntry._ID + "=?";
                selArgs = new String[] {id};
                retCursor = db.query(GothaContract.SubscriptionEntry.TABLE_NAME,
                        projection,
                        sel,
                        selArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case MESSAGES:
                //Timber.d("query tournaments table");
                retCursor = db.query(GothaContract.MessageEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case MESSAGE_WITH_ID:
                id = uri.getPathSegments().get(1);
                sel = GothaContract.MessageEntry._ID + "=?";
                selArgs = new String[] {id};
                retCursor = db.query(GothaContract.TournamentEntry.TABLE_NAME,
                        projection,
                        sel,
                        selArgs,
                        null,
                        null,
                        sortOrder);
                break;
            // Default exception
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the desired Cursor
        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri; // URI to be returned

        switch (match) {
            case TOURNAMENTS:
                long id = db.insert(GothaContract.TournamentEntry.TABLE_NAME, null, values);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(GothaContract.TournamentEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case SUBSCRIPTIONS:
                id = db.insert(GothaContract.SubscriptionEntry.TABLE_NAME, null, values);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(GothaContract.SubscriptionEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case MESSAGES:
                id = db.insert(GothaContract.MessageEntry.TABLE_NAME, null, values);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(GothaContract.MessageEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return constructed uri (this points to the newly inserted row of data)
        return returnUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {

            case TOURNAMENTS:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(GothaContract.TournamentEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;

            case SUBSCRIPTIONS:
                db.beginTransaction();
                rowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(GothaContract.SubscriptionEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;

            case MESSAGES:
                db.beginTransaction();
                rowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(GothaContract.MessageEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        if (null == selection) selection = "1";

        int match = sUriMatcher.match(uri);
        int rowsDeleted; // starts as 0

        switch (match) {
            case TOURNAMENTS:
                rowsDeleted = db.delete(GothaContract.TournamentEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TOURNAMENT_WITH_ID:
                String id = uri.getPathSegments().get(1);
                rowsDeleted = db.delete(GothaContract.TournamentEntry.TABLE_NAME, "_id=?", new String[]{id});
                break;
            case SUBSCRIPTIONS:
                rowsDeleted = db.delete(GothaContract.SubscriptionEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case SUBSCRIPTION_WITH_ID:
                id = uri.getPathSegments().get(1);
                rowsDeleted = db.delete(GothaContract.SubscriptionEntry.TABLE_NAME, "_id=?", new String[]{id});
                break;
            case MESSAGES:
                rowsDeleted = db.delete(GothaContract.MessageEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MESSAGE_WITH_ID:
                id = uri.getPathSegments().get(1);
                rowsDeleted = db.delete(GothaContract.MessageEntry.TABLE_NAME, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver of a change and return the number of items deleted
        if (rowsDeleted != 0) {
            // A row was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsUpdated;

        int match = sUriMatcher.match(uri);

        switch (match) {
            case TOURNAMENT_WITH_ID:
                //update a single row by getting the id
                String id = uri.getPathSegments().get(1);
                rowsUpdated = mDbHelper.getWritableDatabase().update(GothaContract.TournamentEntry.TABLE_NAME, values, "_id=?", new String[]{id});
                break;
            case SUBSCRIPTION_WITH_ID:
                //update a single row by getting the id
                id = uri.getPathSegments().get(1);
                rowsUpdated = mDbHelper.getWritableDatabase().update(GothaContract.SubscriptionEntry.TABLE_NAME, values, "_id=?", new String[]{id});
                break;
            case MESSAGE_WITH_ID:
                //update a single row by getting the id
                id = uri.getPathSegments().get(1);
                rowsUpdated = mDbHelper.getWritableDatabase().update(GothaContract.MessageEntry.TABLE_NAME, values, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            //set notifications if a row was updated
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // return number of rows updated
        return rowsUpdated;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
