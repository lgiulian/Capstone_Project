package com.crilu.gothandroid.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.crilu.gothandroid.GothandroidApplication;
import com.crilu.gothandroid.MainActivity;
import com.crilu.gothandroid.R;
import com.crilu.gothandroid.data.TournamentDao;
import com.crilu.gothandroid.model.firestore.Tournament;
import com.crilu.gothandroid.utils.FileUtils;
import com.crilu.gothandroid.utils.TournamentUtils;
import com.crilu.opengotha.TournamentInterface;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.File;
import java.io.IOException;
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
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Create the pending intent to launch the activity
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String command = data.get(JSON_KEY_COMMAND);
        String tournamentName = data.get(JSON_KEY_TOURNAMENT_NAME);
        String message = data.get(JSON_KEY_MESSAGE);

        // If the message is longer than the max number of characters we want in our
        // notification, truncate it and add the unicode character for ellipsis
        if (message.length() > NOTIFICATION_MAX_CHARACTERS) {
            message = message.substring(0, NOTIFICATION_MAX_CHARACTERS) + "\u2026";
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(String.format(getString(R.string.notification_message), tournamentName))
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    @Override
    public void onNewToken(String token) {
        GothandroidApplication.setCurrentToken(token);
        Timber.d("new token: %s", token);
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String refreshedToken) {

    }

    private void processMessage(Map<String, String> data) {
        String command = data.get(JSON_KEY_COMMAND);
        switch (command) {
            case JSON_KEY_COMMAND_REGISTRATION:
                String tournamentIdentity = data.get(JSON_KEY_TOURNAMENT_IDENTITY);
                String egfPin = data.get(JSON_KEY_EGF_PIN);
                TournamentUtils.registerPlayerForTournament(this, egfPin, tournamentIdentity);

                // update tournament model with the new content and save it in DB
                final TournamentInterface currentOpenedTournament = GothandroidApplication.getGothaModelInstance().getTournament();
                Tournament tournamentModel = TournamentDao.getTournamentByIdentity(this, tournamentIdentity);
                File file = TournamentUtils.getXmlFile(this, currentOpenedTournament);
                try {
                    String tournamentContent = FileUtils.getFileContents(file);
                    tournamentModel.setContent(tournamentContent);
                    TournamentDao.saveTournament(this, tournamentModel);
                } catch (IOException e) {
                    Timber.d("Failed to read tournament file");
                    e.printStackTrace();
                }
                break;
        }
    }

}
