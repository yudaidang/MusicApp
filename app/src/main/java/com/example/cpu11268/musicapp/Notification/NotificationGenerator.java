package com.example.cpu11268.musicapp.Notification;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.widget.RemoteViews;

import com.example.cpu11268.musicapp.Model.Track;
import com.example.cpu11268.musicapp.Main.Activity.ListTrackActivity;
import com.example.cpu11268.musicapp.R;

public class NotificationGenerator {
    public static final String NOTIFY_PREVIOUS = "com.example.cpu11268.musicapp.Notification.previous";
    public static final String NOTIFY_PLAY = "com.example.cpu11268.musicapp.Notification.play";

    public static final String NOTIFY_NEXT = "com.example.cpu11268.musicapp.Notification.next";
    private static final int NOTIFICATION_ID_CUSTOM_BIG = 1;

    private static Notification nc;
    private static RemoteViews expandedViewSmall;
    private static NotificationManager nm;

    public static void updateInfo(Track track) {
        expandedViewSmall.setTextViewText(R.id.nameSong, track.getName());
        nm.notify(NOTIFICATION_ID_CUSTOM_BIG, nc);
    }

    public static void updateButtonPlay(boolean isPlay) {
        if (isPlay) {
            expandedViewSmall.setImageViewResource(R.id.play, R.drawable.pause_icon);
        } else {
            expandedViewSmall.setImageViewResource(R.id.play, R.drawable.play_icon);
        }
        nm.notify(NOTIFICATION_ID_CUSTOM_BIG, nc);
    }

    public static void cancelNoti() {
        nm.cancel(NOTIFICATION_ID_CUSTOM_BIG);
    }

    public static boolean isExistNoti() {
        return nc != null ? true : false;
    }

    @SuppressLint("RestrictedApi")
    public static void customBigNotification(Context context) {

        expandedViewSmall = new RemoteViews(context.getPackageName(), R.layout.small_notification);


        nc = new Notification.Builder(context).build();
        nm = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        Intent notifyIntent = new Intent(context, ListTrackActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, 0/*PendingIntent.FLAG_UPDATE_CURRENT*/);
        nc.contentIntent = pendingIntent;
        nc.contentView = expandedViewSmall;
        nc.flags = Notification.FLAG_ONGOING_EVENT;
        nc.icon = R.drawable.default_image;
        nc.largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_image);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            if (nm != null) {
                nm.createNotificationChannel(channel);
            }
        }
        setListeners(expandedViewSmall
                , context);

        ((Service) context).startForeground(NOTIFICATION_ID_CUSTOM_BIG, nc);

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
