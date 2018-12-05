package com.example.cpu11268.musicapp.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;

import static android.view.KeyEvent.KEYCODE_HOME;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_CHANGE_PLAY;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_NEXT_SONG;


public class RemoteControlClientReceiver extends BroadcastReceiver {
    private static long mHeadsetDownTime = 0;
    private static long mHeadsetUpTime = 0;
    private Intent intentIsPlay;
    private Intent intentNext;

    @Override
    public void onReceive(Context context, Intent intent) {
        intentIsPlay = new Intent(BROADCAST_CHANGE_PLAY);
        intentNext = new Intent(BROADCAST_NEXT_SONG);
        String action = intent.getAction();
        if (action.equalsIgnoreCase(Intent.ACTION_MEDIA_BUTTON)) {
            KeyEvent event = (KeyEvent) intent
                    .getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            switch (event.getKeyCode()) {

                case KeyEvent.KEYCODE_HEADSETHOOK:
                    long time = SystemClock.uptimeMillis();
                    switch (event.getAction()) {
                        case KeyEvent.ACTION_DOWN:
                            if (event.getRepeatCount() > 0)
                                break;
                            mHeadsetDownTime = time;
                            break;
                        case KeyEvent.ACTION_UP:
                            if (time - mHeadsetDownTime >= 1000) {
                                Log.d("YUDAIDANGTEST", "long");
                                // double click
                            } else if (time - mHeadsetUpTime <= 500) {
                                Log.d("YUDAIDANGTEST", "double");
                                context.sendBroadcast(intentNext);
                            } else {
                                Log.d("YUDAIDANGTEST", "one");
                                context.sendBroadcast(intentIsPlay);
                            }
                            mHeadsetUpTime = time;
                            break;
                    }
                    break;

            }
            Log.d("YUDAIDANGTEST", KEYCODE_HOME
                    + " RECEIVE2 " + event.getKeyCode() + " " + event.getAction() + " ");

        }
    }
}
