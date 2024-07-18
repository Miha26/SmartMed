package com.med_app.smartmed1;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {
    public static final String channelID = "channel1";
    public static final String titleExtra = "titleExtra";
    public static final String messageExtra = "messageExtra";
    public static final String notificationIdExtra = "notificationId";

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra(titleExtra);
        String message = intent.getStringExtra(messageExtra);
        int notificationId = intent.getIntExtra(notificationIdExtra, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(notificationId, builder.build());
        }
    }
}
