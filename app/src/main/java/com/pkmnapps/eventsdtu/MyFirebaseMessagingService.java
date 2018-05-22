package com.pkmnapps.eventsdtu;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by prerak on 16/4/18.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    String CHANNEL_ID = "channelId";
    String contentTitle,contentText;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getNotification()!=null) {
            contentTitle = remoteMessage.getNotification().getTitle();
            contentText = remoteMessage.getNotification().getBody();

            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, SplashActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_check_white_24dp)
                    .setContentTitle(contentTitle)
                    .setContentText(contentText)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(contentText))
                    .setContentIntent(contentIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Create the NotificationChannel, but only on API 26+ because
                // the NotificationChannel class is new and not in the support library
                CharSequence name = CHANNEL_ID;
                String description = "description";
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription(description);
                // Register the channel with the system
                if(notificationManager!=null)
                    notificationManager.createNotificationChannel(channel);
            }

            if(notificationManager != null)
                notificationManager.notify(1, mBuilder.build());

        }


    }
}
