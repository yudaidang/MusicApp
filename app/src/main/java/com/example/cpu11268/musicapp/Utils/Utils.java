package com.example.cpu11268.musicapp.Utils;

import java.util.concurrent.TimeUnit;

public class Utils {

    private static final Utils utils = new Utils();

    public static Utils getInstance() {
        return utils;
    }


    public String millisecondsToString(int milliseconds) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes((long) milliseconds);
        long seconds = TimeUnit.MILLISECONDS.toSeconds((long) milliseconds) % 60;
        String sec = seconds + "";
        if (seconds < 10) {
            sec = "0" + seconds;
        }
        return minutes + ":" + sec;
    }
}
