package com.arkay.gkinhindi.utils;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.arkay.gkinhindi.R;
import com.arkay.gkinhindi.ui.activities.MainActivity;
import java.util.Calendar;

public class NotificationHelper {
    public static final String CHANNEL_ID = "daily_reminder_channel";
    public static final int NOTIFICATION_ID = 1001;
    public static final int ALARM_REQUEST_CODE_MORNING = 2002;
    public static final int ALARM_REQUEST_CODE_EVENING = 2003;

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.notification_channel_name);
            String description = context.getString(R.string.notification_channel_desc);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public static void scheduleDailyNotification(Context context) {
        createNotificationChannel(context);

        // Schedule Morning Alarm at 9:00 AM
        scheduleAlarm(context, ALARM_REQUEST_CODE_MORNING, 9, 0, "morning");

        // Schedule Evening Alarm at 6:00 PM (18:00)
        scheduleAlarm(context, ALARM_REQUEST_CODE_EVENING, 18, 0, "evening");
    }

    private static void scheduleAlarm(Context context, int requestCode, int hour, int minute, String type) {
        Intent intent = new Intent(context, com.arkay.gkinhindi.notificationReceiver.NotificationReceiver.class);
        intent.putExtra("reminder_type", type);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            // If scheduled time has already passed today, schedule for tomorrow
            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }

            alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
            );
        }
    }

    public static void showNotification(Context context, String type) {
        createNotificationChannel(context);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        Calendar cal = Calendar.getInstance();
        int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);

        String[] titles;
        String[] descs;

        if ("morning".equals(type)) {
            titles = context.getResources().getStringArray(R.array.morning_notification_titles);
            descs = context.getResources().getStringArray(R.array.morning_notification_descs);
        } else {
            titles = context.getResources().getStringArray(R.array.evening_notification_titles);
            descs = context.getResources().getStringArray(R.array.evening_notification_descs);
        }

        // Dynamically select notification content based on day of year to rotate daily
        int index = dayOfYear % titles.length;
        String title = titles[index];
        String desc = descs[index];

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(desc)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        try {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) 
                        == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    notificationManager.notify(NOTIFICATION_ID, builder.build());
                }
            } else {
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
