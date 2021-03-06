package com.crilu.gothandroid.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class GothaContract {

    public static final String AUTHORITY = "com.crilu.gothandroid";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_TOURNAMENTS = "tournament";
    public static final String PATH_SUBSCRIPTION = "subscription";
    public static final String PATH_MESSAGE = "message";

    public static final class TournamentEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TOURNAMENTS).build();

        public static final String TABLE_NAME = "tournament";
        public static final String COLUMN_IDENTITY = "identity";
        public static final String COLUMN_BEGIN_DATE = "begin_date";
        public static final String COLUMN_END_DATE = "end_date";
        public static final String COLUMN_FULL_NAME = "full_name";
        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_SHORT_NAME = "short_name";
        public static final String COLUMN_DIRECTOR = "director";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_CREATOR = "creator";
        public static final String COLUMN_CREATION_DATE = "creation_date";
        public static final String COLUMN_LAST_MODIFICATION_DATE = "last_modification_date";
        public static final String COLUMN_SUBSCRIPTION_TYPE = "subscription_type";
    }

    public static final class SubscriptionEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SUBSCRIPTION).build();

        public static final String TABLE_NAME = "subscription";
        public static final String COLUMN_IDENTITY = "identity";
        public static final String COLUMN_TOURNAMENT_ID = "tournament_id";
        public static final String COLUMN_UID = "uid";
        public static final String COLUMN_EGF_PIN = "egf_pin";
        public static final String COLUMN_FFG_LIC = "ffg_lic";
        public static final String COLUMN_AGA_ID = "aga_id";
        public static final String COLUMN_INTENT = "intent"; // participant, observer
        public static final String COLUMN_SUBSCRIPTION_DATE = "subscription_date";
        public static final String COLUMN_STATE = "state"; // active, inactive
    }

    public static final class MessageEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MESSAGE).build();

        public static final String TABLE_NAME = "message";
        public static final String COLUMN_TOURNAMENT_IDENTITY = "tournament_identity";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_MESSAGE = "message"; // participant, observer
        public static final String COLUMN_COMMAND = "command";
        public static final String COLUMN_EGF_PIN = "egf_pin";
        public static final String COLUMN_MESSAGE_DATE = "message_date";
    }

}
