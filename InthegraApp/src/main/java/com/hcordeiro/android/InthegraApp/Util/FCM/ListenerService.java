package com.hcordeiro.android.InthegraApp.Util.FCM;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * asdasasd
 * Created by hugo on 25/05/16.
 */
    public class ListenerService extends FirebaseMessagingService {
    private static final String TAG = "MessagingServer";

    public ListenerService() {
        super();
    }

    @Override
    public void onMessageReceived(RemoteMessage message) {
        Log.d(TAG, "###############onMessageReceived Called");
        super.onMessageReceived(message);
        Log.v(TAG, "From: " + message.getFrom());

        String from = message.getFrom();
        Map data = message.getData();

        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    @Override
    public void onMessageSent(String msgId) {
        Log.d(TAG, "###############Message Sent: " + msgId);
        super.onMessageSent(msgId);
    }

    @Override
    public void onSendError(String msgId, Exception exception) {
        Log.d(TAG, "################Message Not Sent: " + msgId + ". Reason: " + exception.getMessage());
        super.onSendError(msgId, exception);
    }
}
