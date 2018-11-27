package com.example.cpu11268.musicapp.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import static com.example.cpu11268.musicapp.Constant.BROADCAST_CHANGE_PLAY;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_CHANGE_SONG;

public class NotificationBroadcast extends BroadcastReceiver {
    private Intent intentChangePlay = new Intent(BROADCAST_CHANGE_PLAY);

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(NotificationGenerator.NOTIFY_PLAY)) {
            intentChangePlay.putExtra("isPlay", true);
            context.sendBroadcast(intentChangePlay);

            Toast.makeText(context, "NOTIFY_PAUSE", Toast.LENGTH_LONG).show();
        } else if (intent.getAction().equals(NotificationGenerator.NOTIFY_NEXT)) {
            Toast.makeText(context, "NOTIFY_PLAY", Toast.LENGTH_LONG).show();
        } else if (intent.getAction().equals(NotificationGenerator.NOTIFY_NEXT)) {
            Toast.makeText(context, "NOTIFY_NEXT", Toast.LENGTH_LONG).show();
        } else if (intent.getAction().equals(NotificationGenerator.NOTIFY_PREVIOUS)) {
            Toast.makeText(context, "NOTIFY_PREVIOUS", Toast.LENGTH_LONG).show();
        }
    }
}
