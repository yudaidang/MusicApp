package com.example.cpu11268.musicapp.Notification;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.view.WindowManager;
import android.widget.RemoteViews;

import com.example.cpu11268.musicapp.R;
import com.example.cpu11268.musicapp.Service.PlaySongService;

public class NotificationGenerator {
    public static final String NOTIFY_PREVIOUS = "com.example.cpu11268.musicapp.Notification.previous";
    public static final String NOTIFY_PAUSE = "com.example.cpu11268.musicapp.Notification.pause";
    public static final String NOTIFY_PLAY = "com.example.cpu11268.musicapp.Notification.play";

    public static final String NOTIFY_NEXT = "com.example.cpu11268.musicapp.Notification.next";
    private static final int NOTIFICATION_ID_CUSTOM_BIG = 9;


    @SuppressLint("RestrictedApi")
    public static void customBigNotification(Context context) {
        RemoteViews expandedView = new RemoteViews(context.getPackageName(), R.layout.big_notification);
        RemoteViews expandedViewSmall = new RemoteViews(context.getPackageName(), R.layout.small_notification);
        NotificationCompat.Builder nc = new NotificationCompat.Builder(context);
        NotificationManager nm = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        Intent notifyIntent = new Intent(context, PlaySongService.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        nc.setContentIntent(pendingIntent);
        nc.setStyle(new NotificationCompat.DecoratedCustomViewStyle());
        nc.setSmallIcon(R.drawable.play_icon);
        nc.setAutoCancel(true);
        nc.setCustomBigContentView(expandedView);
        nc.setContent(expandedViewSmall);
        nc.setContentTitle("Music Player");
        nc.setContentText("Control Audio");
        nc.setPriority(Notification.PRIORITY_MAX);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            if (nm != null) {
                nm.createNotificationChannel(channel);
            }
        }

        nc.getBigContentView().setTextViewText(R.id.nameSong, "Son Tung MTP");
        setListeners(expandedViewSmall
                , context);
//
        nm.notify(NOTIFICATION_ID_CUSTOM_BIG, nc.build());
    }

    private static void setListeners(RemoteViews view, Context context) {
        Intent previous = new Intent(NOTIFY_PREVIOUS);
        Intent next = new Intent(NOTIFY_NEXT);
        Intent play = new Intent(NOTIFY_PLAY);


        PendingIntent pPrevious = PendingIntent.getBroadcast(context, 0, previous, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.pre, pPrevious);

        PendingIntent pPlay = PendingIntent.getBroadcast(context, 0, play, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.play, pPlay);

        PendingIntent pNext = PendingIntent.getBroadcast(context, 0, next, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.next, pNext);

    }
}
