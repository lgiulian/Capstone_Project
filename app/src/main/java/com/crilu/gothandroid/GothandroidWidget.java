package com.crilu.gothandroid;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.crilu.gothandroid.data.TournamentDao;
import com.crilu.gothandroid.model.firestore.Tournament;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import timber.log.Timber;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link GothandroidWidgetConfigureActivity GothandroidWidgetConfigureActivity}
 */
public class GothandroidWidget extends AppWidgetProvider {

    static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager,
                                final int appWidgetId) {

        String tournamentName = GothandroidWidgetConfigureActivity.loadTournamentPref(context, appWidgetId);
        if (TextUtils.isEmpty(tournamentName)) {
            return;
        }
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.gothandroid_widget);
        Timber.d("tournamentName = %s", tournamentName);
        views.setTextViewText(R.id.appwidget_title, tournamentName);
        views.setOnClickPendingIntent(R.id.settings_icon, actionPendingIntent(context, appWidgetId));
        appWidgetManager.updateAppWidget(appWidgetId, views);

        Tournament tournament = TournamentDao.getTournamentByFullName(context, tournamentName);
        String tournamentIdentity = tournament.getIdentity();
        if (tournament != null && !TextUtils.isEmpty(tournamentIdentity)) {
            TournamentDao.fetchTournamentResults(tournamentIdentity, new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    HashMap map = (HashMap) dataSnapshot.getValue();
                    if (map != null && map.containsKey(Tournament.RESULT_CONTENT)) {
                        String widgetText = (String) map.get(Tournament.RESULT_CONTENT);
                        Timber.d("### results content: %s", widgetText);
                        if (!TextUtils.isEmpty(widgetText)) {
                            widgetText = widgetText.replace("<br>", "\n");
                        }
                        // Construct the RemoteViews object
                        views.setTextViewText(R.id.appwidget_text, widgetText);

                        // Instruct the widget manager to update the widget
                        appWidgetManager.updateAppWidget(appWidgetId, views);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Timber.d(databaseError.toException());
                }
            });
        }
    }

    private static PendingIntent actionPendingIntent(Context context, int appWidgetId) {
        Intent intent = new Intent(context, GothandroidWidgetConfigureActivity.class);
        intent.setAction("android.appwidget.action.APPWIDGET_CONFIGURE");
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        return PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            GothandroidWidgetConfigureActivity.deleteTournamentPref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

