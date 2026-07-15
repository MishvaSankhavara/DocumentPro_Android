package com.arkay.gkinhindi.notificationReceiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.RemoteMessage;
import com.arkay.gkinhindi.R;
import com.arkay.gkinhindi.ui.activities.MainActivity;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "FCM_Service";
    private static final String CHANNEL_ID = "fcm_push_channel";
    private static final int NOTIFICATION_ID = 5001;

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);
        
        // Track token refresh in analytics
        try {
            android.os.Bundle bundle = new android.os.Bundle();
            bundle.putString("token", token);
            com.arkay.gkinhindi.utils.AnalyticsHelper.logEvent("fcm_token_refresh", bundle);
        } catch (Exception e) {
            Log.e(TAG, "Error logging token refresh", e);
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a notification payload
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            showNotification(title, body);
        } else if (remoteMessage.getData().size() > 0) {
            // Check if message contains a data payload
            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");
            showNotification(title, body);
        }
    }

    private void showNotification(String title, String body) {
        createNotificationChannel();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        try {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) 
                        == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    notificationManager.notify(NOTIFICATION_ID, builder.build());
                }
            } else {
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error displaying notification", e);
            try {
                com.arkay.gkinhindi.utils.AnalyticsHelper.recordException(e);
            } catch (Exception ignored) {}
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notification_channel_name_fcm);
            String description = getString(R.string.notification_channel_desc_fcm);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
