package com.crilu.gothandroid.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

import com.crilu.gothandroid.GothandroidApplication;
import com.crilu.gothandroid.MainActivity;
import com.crilu.gothandroid.R;
import com.crilu.gothandroid.data.GothaContract;
import com.crilu.gothandroid.data.GothaPreferences;

import java.util.Date;

public class NotificationUtils {

    /*
     * The columns of data that we are interested in displaying within our notification to let
     * the user know there is new tournament published.
     */
    public static final String[] TOURNAMENT_NOTIFICATION_PROJECTION = {
            GothaContract.TournamentEntry.COLUMN_FULL_NAME,
            GothaContract.TournamentEntry.COLUMN_BEGIN_DATE,
            GothaContract.TournamentEntry.COLUMN_LOCATION,
    };

    private static final int TOURNAMENT_NOTIFICATION_ID = 23412;
    public static final String CHANNEL_ID = "Gotha";

    /**
     * Constructs and displays a notification for the newly updated tournaments.
     *
     * @param context Context used to query our ContentProvider
     */
    public static void notifyUserOfNewTournament(Context context, long latestPublishedTournamentTime) {

        Cursor tournamentsCursor = context.getContentResolver().query(
                GothaContract.TournamentEntry.CONTENT_URI,
                TOURNAMENT_NOTIFICATION_PROJECTION,
                GothaContract.TournamentEntry.COLUMN_CREATION_DATE + ">=?",
                new String[] {Long.toString(latestPublishedTournamentTime)},
                GothaContract.TournamentEntry.COLUMN_BEGIN_DATE);

        if (tournamentsCursor != null && tournamentsCursor.moveToFirst()) {

            String fullName = tournamentsCursor.getString(0);
            long beginDate = tournamentsCursor.getLong(1);
            String location = tournamentsCursor.getString(2);

            String notificationTitle = context.getString(R.string.app_name);

            String notificationText = getNotificationText(context, fullName, beginDate, location);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setColor(ContextCompat.getColor(context,R.color.colorPrimary))
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationText)
                    .setAutoCancel(true);

            Intent intent = new Intent(context, MainActivity.class);
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
            taskStackBuilder.addNextIntentWithParentStack(intent);
            PendingIntent resultPendingIntent = taskStackBuilder
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            notificationBuilder.setContentIntent(resultPendingIntent);

            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(TOURNAMENT_NOTIFICATION_ID, notificationBuilder.build());

            /*
             * Since we just showed a notification, save the latest published tournament. That way, we can check
             * next time the tournaments are refreshed if we should show another notification.
             */
            GothaPreferences.saveLatestKnownPublishedTournament(context, latestPublishedTournamentTime);

            tournamentsCursor.close();
        }

    }

    private static String getNotificationText(Context context, String name, Long startDate, String location) {
        String notificationFormat = context.getString(R.string.format_notification);

        String notificationText = String.format(notificationFormat,
                name,
                GothandroidApplication.dateFormatPretty.format(new Date(startDate)),
                location);

        return notificationText;
    }
}
