package com.simplepeng.updater;

import android.util.Log;

public class LogUtils {

    private static final String TAG = "Updater";
    public static boolean isDebug = false;

    public static void debug(String msg) {
        if (isDebug) {
            Log.d(TAG, msg);
        }
    }

}
