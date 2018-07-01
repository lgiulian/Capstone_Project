package com.crilu.gothandroid.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

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

    @NonNull
    private static String getStringPref(Context context, int prefKey) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(context.getString(prefKey), "");
    }

    private static void saveStringPref(Context context, String value, int prefKey) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(context.getString(prefKey), value);
        editor.apply();
    }

    public static void saveUserFirstName(Context context, String firstName) {
        saveStringPref(context, firstName, R.string.pref_user_first_name);
    }

    public static void saveUserLastName(Context context, String lastName) {
        saveStringPref(context, lastName, R.string.pref_user_last_name);
    }

    public static void saveUserAgaId(Context context, String agaId) {
        saveStringPref(context, agaId, R.string.pref_user_aga_id);
    }

    public static void saveUserFfgLic(Context context, String ffgLic) {
        saveStringPref(context, ffgLic, R.string.pref_user_ffg_lic);
    }

    public static void saveUserEgfPin(Context context, String egfPin) {
        saveStringPref(context, egfPin, R.string.pref_user_egf_pin);
    }

    @NonNull
    public static String getUserFirstName(Context context) {
        return getStringPref(context, R.string.pref_user_first_name);
    }

    @NonNull
    public static String getUserLastName(Context context) {
        return getStringPref(context, R.string.pref_user_last_name);
    }

    @NonNull
    public static String getUserEgfPin(Context context) {
        return getStringPref(context, R.string.pref_user_egf_pin);
    }

    @NonNull
    public static String getUserFfgLic(Context context) {
        return getStringPref(context, R.string.pref_user_ffg_lic);
    }

    @NonNull
    public static String getUserAgaId(Context context) {
        return getStringPref(context, R.string.pref_user_aga_id);
    }
}
