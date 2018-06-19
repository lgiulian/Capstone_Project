package com.crilu.gothandroid.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.crilu.gothandroid.R;

public class GothaPreferences {

    public static boolean areNotificationsEnabled(Context context) {
        String displayNotificationsKey = context.getString(R.string.pref_enable_notifications_key);
        boolean shouldDisplayNotificationsByDefault = context
                .getResources()
                .getBoolean(R.bool.show_notifications_by_default);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        return sp.getBoolean(displayNotificationsKey, shouldDisplayNotificationsByDefault);
    }

    public static void saveLatestKnownPublishedTournament(Context context, long timeOfTournament) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        String lastNotificationKey = context.getString(R.string.pref_latest_known_published_tournament);
        editor.putLong(lastNotificationKey, timeOfTournament);
        editor.apply();
    }

    public static long getLatestKnownPublishedTournament(Context context) {
        String latestKnownPublishedTournamentKey = context.getString(R.string.pref_latest_known_published_tournament);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        long latestKnownPublishedTournament = sp.getLong(latestKnownPublishedTournamentKey, 0);
        return latestKnownPublishedTournament;
    }
}
