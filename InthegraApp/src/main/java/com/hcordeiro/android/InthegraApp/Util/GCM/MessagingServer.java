package com.hcordeiro.android.InthegraApp.Util.GCM;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hcordeiro.android.InthegraApp.Activities.MenuPrincipalActivity;
import com.hcordeiro.android.InthegraApp.R;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by hugo on 25/05/16.
 */
public class MessagingServer extends FirebaseMessagingService {
    private final String TAG = "MessagingServer";
    private AtomicInteger msgId = new AtomicInteger();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
    }

    @Override
    public void onMessageSent(String msgId) {
        Log.d(TAG, "Message Sent: " + msgId);
    }

    @Override
    public void onSendError(String msgId, Exception exception) {
        Log.d(TAG, "Message Not Sent: " + msgId + ". Reason: " + exception.getMessage());
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MenuPrincipalActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Builder notificationBuilder = new Builder(this)
                .setSmallIcon(R.drawable.common_plus_signin_btn_text_light)
                .setContentTitle("FCM Message")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}
