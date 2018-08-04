package com.crilu.gothandroid.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.crilu.gothandroid.model.firestore.Message;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageDao {

    public static List<Message> getAllMessages(Context context) {
        List<Message> messages = new ArrayList<>();

        Cursor cursor = context.getContentResolver().query(GothaContract.MessageEntry.CONTENT_URI,
                null,
                null,
                null,
                GothaContract.MessageEntry.COLUMN_MESSAGE_DATE + " DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Message message = new Message();
                message.setId(cursor.getLong(cursor.getColumnIndex(GothaContract.MessageEntry._ID)));
                message.setTournamentIdentity(cursor.getString(cursor.getColumnIndex(GothaContract.MessageEntry.COLUMN_TOURNAMENT_IDENTITY)));
                message.setMessageDate(new Date(cursor.getLong(cursor.getColumnIndex(GothaContract.MessageEntry.COLUMN_MESSAGE_DATE))));
                message.setTitle(cursor.getString(cursor.getColumnIndex(GothaContract.MessageEntry.COLUMN_TITLE)));
                message.setMessage(cursor.getString(cursor.getColumnIndex(GothaContract.MessageEntry.COLUMN_MESSAGE)));
                message.setCommand(cursor.getString(cursor.getColumnIndex(GothaContract.MessageEntry.COLUMN_COMMAND)));
                message.setEgfPin(cursor.getString(cursor.getColumnIndex(GothaContract.MessageEntry.COLUMN_EGF_PIN)));

                messages.add(message);
            }
            cursor.close();
        }
        return messages;
    }

    private static void insertMessage(Context context, Message message) {
        ContentResolver gothaContentResolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(GothaContract.MessageEntry.COLUMN_MESSAGE_DATE, message.getMessageDate().getTime());
        values.put(GothaContract.MessageEntry.COLUMN_TITLE, message.getTitle());
        values.put(GothaContract.MessageEntry.COLUMN_MESSAGE, message.getMessage());
        values.put(GothaContract.MessageEntry.COLUMN_COMMAND, message.getCommand());
        values.put(GothaContract.MessageEntry.COLUMN_EGF_PIN, message.getEgfPin());
        values.put(GothaContract.MessageEntry.COLUMN_TOURNAMENT_IDENTITY, message.getTournamentIdentity());
        gothaContentResolver.insert(
                GothaContract.MessageEntry.CONTENT_URI,
                values);
    }

    public static void insertMessage(Context context, String title, String messageString) {
        Message message = new Message();
        message.setMessage(messageString);
        message.setTitle(title);
        message.setMessageDate(new Date());
        insertMessage(context, message);
    }
}
