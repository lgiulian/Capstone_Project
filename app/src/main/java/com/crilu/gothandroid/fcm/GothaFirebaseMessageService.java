package com.crilu.gothandroid.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.crilu.gothandroid.GothandroidApplication;
import com.crilu.gothandroid.MessageActivity;
import com.crilu.gothandroid.R;
import com.crilu.gothandroid.data.MessageDao;
import com.crilu.gothandroid.utils.TournamentUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import timber.log.Timber;

import static com.crilu.gothandroid.utils.NotificationUtils.CHANNEL_ID;

public class GothaFirebaseMessageService extends FirebaseMessagingService {

    private static final int NOTIFICATION_MAX_CHARACTERS = 30;
    private static final String JSON_KEY_COMMAND = "command";
    private static final String JSON_KEY_TOURNAMENT_NAME = "tournament_name";
    private static final String JSON_KEY_TOURNAMENT_IDENTITY = "tournament_id";
    private static final String JSON_KEY_MESSAGE = "message";
    private static final String JSON_KEY_COMMAND_REGISTRATION = "registration";
    private static final String JSON_KEY_EGF_PIN = "egf_pin";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Timber.d("From: %s", remoteMessage.getFrom());
        Map<String, String> data = remoteMessage.getData();
        if (data.size() > 0) {
            Timber.d("Message data payload: %s", data);

            // Send a notification that we got a new message
            sendNotification(data);
            processMessage(data);
        }
    }

    private void sendNotification(Map<String, String> data) {
        Intent intent = new Intent(this, MessageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Create the pending intent to launch the activity
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String tournamentName = data.get(JSON_KEY_TOURNAMENT_NAME);
        String message = data.get(JSON_KEY_MESSAGE);
        String messageTrimmed = message;
        String title = String.format(getString(R.string.notification_message), tournamentName);

        // If the message is longer than the max number of characters we want in our
        // notification, truncate it and add the unicode character for ellipsis
        if (message.length() > NOTIFICATION_MAX_CHARACTERS) {
            messageTrimmed = message.substring(0, NOTIFICATION_MAX_CHARACTERS) + "\u2026";
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(messageTrimmed)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }

        MessageDao.insertMessage(getApplicationContext(), title, message);
    }

    @Override
    public void onNewToken(String token) {
        GothandroidApplication.setCurrentToken(token);
        Timber.d("new token: %s", token);
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String refreshedToken) {
        // FIXME: here I should update user on firestore
    }

    private void processMessage(Map<String, String> data) {
        String command = data.get(JSON_KEY_COMMAND);
        if (TextUtils.isEmpty(command)) {
            return;
        }
        switch (command) {
            case JSON_KEY_COMMAND_REGISTRATION:
                String tournamentIdentity = data.get(JSON_KEY_TOURNAMENT_IDENTITY);
                String egfPin = data.get(JSON_KEY_EGF_PIN);
                TournamentUtils.registerPlayerForTournament(this, egfPin, tournamentIdentity);

                break;
        }
    }

}
