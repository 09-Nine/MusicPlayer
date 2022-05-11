package com.nine.musicplayer;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Utils {
    public static final String KEY_SHOW_HOME = "KEY_SHOW_HOME";
    public static final String KEY_SHOW_PLAYER = "KEY_SHOW_PLAYER";
    public static final String KEY_SHOW_SERVICE = "KEY_SHOW_SERVICE";
    public final static int ACTION_START = 1;
    public final static int ACTION_PAUSE = 2;
    public final static int ACTION_RESUME = 3;
    public final static int ACTION_NEXT = 4;
    public final static int ACTION_PREVIOUS = 5;
    public final static int ACTION_CLOSE = 6;

    public static String convertMilliToMinuteString(int milli) {
        return String.format(Locale.getDefault() ,"%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milli),
                TimeUnit.MILLISECONDS.toSeconds(milli) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milli))
        );
    }
}
