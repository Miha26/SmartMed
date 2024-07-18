package com.med_app.smartmed1;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;



public class Notification  extends BroadcastReceiver {
    public static final int notificationID = 1;
    public static final String channelID = "channel1";
    public static final String titleExtra = "titleExtra";
    public static final String messageExtra = "messageExtra";

    @Override
    public void onReceive(Context context, Intent intent) {
        android.app.Notification notification = new NotificationCompat.Builder(context, channelID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(intent.getStringExtra(titleExtra))
                .setContentText(intent.getStringExtra(messageExtra))
                .build();

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(notificationID, notification);
    }
}

