package com.example.cpu11268.musicapp.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.example.cpu11268.musicapp.Constant.BROADCAST_CHANGE_PLAY;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_NEXT_SONG;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_PRE_SONG;

public class NotificationBroadcast extends BroadcastReceiver {
    private Intent intentChangePlay = new Intent(BROADCAST_CHANGE_PLAY);

    private Intent intentNext = new Intent(BROADCAST_NEXT_SONG);
    private Intent intentPre = new Intent(BROADCAST_PRE_SONG);

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(NotificationGenerator.NOTIFY_PLAY)) {
            context.sendBroadcast(intentChangePlay);
        } else if (intent.getAction().equals(NotificationGenerator.NOTIFY_NEXT)) {
            context.sendBroadcast(intentNext);
        } else if (intent.getAction().equals(NotificationGenerator.NOTIFY_PREVIOUS)) {
            context.sendBroadcast(intentPre);
        }

    }
}
