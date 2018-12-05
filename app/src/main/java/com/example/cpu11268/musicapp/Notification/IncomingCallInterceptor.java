package com.example.cpu11268.musicapp.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class IncomingCallInterceptor extends BroadcastReceiver {                                    // 1

    @Override
    public void onReceive(Context context, Intent intent) {                                         // 2
        Log.d("CALLLLLLL", " alllllllllllllllllllllllll");

    }

}
