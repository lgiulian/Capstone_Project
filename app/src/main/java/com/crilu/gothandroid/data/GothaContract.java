package com.crilu.gothandroid.data;

import android.provider.BaseColumns;

public class GothaContract {

    public static final class TournamentEntry implements BaseColumns {
        public static final String TABLE_NAME = "tournament";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_BEGIN_DATE = "begin_date";
        public static final String COLUMN_FULL_NAME = "full_name";
        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_SHORT_NAME = "short_name";
        public static final String COLUMN_DIRECTOR = "director";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_CREATOR = "creator";
    }

    public static final class SubscriptionEntry implements BaseColumns {
        public static final String TABLE_NAME = "subscriber";
        public static final String COLUMN_TOURNAMENT_ID = "tournament_id";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_INTENT = "intent"; // participant, observer
        public static final String COLUMN_SUBSCRIPTION_DATE = "subscription_date";
        public static final String COLUMN_STATE = "state"; // active, inactive
    }

    public static final class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "user";
        public static final String COLUMN_TOKEN = "token";
        public static final String COLUMN_EGF_PIN = "egf_pin";
        public static final String COLUMN_FFG_LIC = "ffg_lic";
        public static final String COLUMN_AGA_ID = "aga_id";
        public static final String COLUMN_REGISTRATION_DATE = "registration_date";
    }
}
