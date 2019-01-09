package com.example.cpu11268.musicapp.Notification;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.example.cpu11268.musicapp.Main.Activity.ListTrackActivity;
import com.example.cpu11268.musicapp.Main.Activity.PlayMusicActivity;
import com.example.cpu11268.musicapp.Model.Track;
import com.example.cpu11268.musicapp.R;

import static com.example.cpu11268.musicapp.Constant.DATA_TRACK;
import static com.example.cpu11268.musicapp.Constant.EXTRA_DATA;
import static com.example.cpu11268.musicapp.Constant.PLAY_MUSIC_MEDIA;
import static com.example.cpu11268.musicapp.Constant.STATE_START_ACTIVITY_PLAY_MUSIC;

public class NotificationGenerator {
    public static final String NOTIFY_PREVIOUS = "com.example.cpu11268.musicapp.Notification.previous";
    public static final String NOTIFY_PLAY = "com.example.cpu11268.musicapp.Notification.play";

    public static final String NOTIFY_NEXT = "com.example.cpu11268.musicapp.Notification.next";
    private static final int NOTIFICATION_ID_CUSTOM_BIG = 0;
    private static final int ID_PENDING = 1;

    private static Intent intent;
    private static Notification nc;
    private static RemoteViews expandedViewSmall;
    private static NotificationManager nm;
    private static Track trackInfo;

    public static void updateInfo(Context context, Track track, String pathLoad, boolean isLocalAreaLoad) {
        trackInfo = track;

        expandedViewSmall.setTextViewText(R.id.nameSong, track.getName());
        PendingIntent pendingIntent =
                getCommentPendingIntent(context, pathLoad, isLocalAreaLoad);
        nc.contentIntent = pendingIntent;
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

    private static PendingIntent getCommentPendingIntent(Context context, String pathLoad, boolean isLocalAreaLoad) {
        Intent intent = new Intent(context, PlayMusicActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("STATE_LOAD", isLocalAreaLoad);
        intent.putExtra(DATA_TRACK, trackInfo.getId());
        intent.putExtra(EXTRA_DATA,pathLoad);
        intent.putExtra(STATE_START_ACTIVITY_PLAY_MUSIC, 0);
        return PendingIntent.getActivity(context,
                ID_PENDING, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @SuppressLint("RestrictedApi")
    public static void customBigNotification(Context context, Track track, String pathLoad, boolean isLocalAreaLoad) {
        trackInfo = track;
        expandedViewSmall = new RemoteViews(context.getPackageName(), R.layout.small_notification);

        nc = new Notification.Builder(context).build();
        nm = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        Intent notifyIntent = new Intent(context, ListTrackActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent = new Intent(context, PlayMusicActivity.class);

        PendingIntent pendingIntent =
                getCommentPendingIntent(context, pathLoad, isLocalAreaLoad);
        nc.contentIntent = pendingIntent;
        nc.contentView = expandedViewSmall;
        nc.flags = Notification.FLAG_ONGOING_EVENT;
        nc.icon = R.drawable.default_image;
        nc.largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_image);
        if (TextUtils.isEmpty(track.getmImage().trim())) {
            expandedViewSmall.setImageViewResource(R.id.image, R.drawable.default_image);
        }
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
