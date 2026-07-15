package com.arkay.gkinhindi.notificationReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.arkay.gkinhindi.utils.NotificationHelper;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String type = intent.getStringExtra("reminder_type");
        if (type == null) {
            type = "morning";
        }
        NotificationHelper.showNotification(context, type);
    }
}
