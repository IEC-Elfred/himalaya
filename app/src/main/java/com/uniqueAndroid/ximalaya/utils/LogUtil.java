package com.uniqueAndroid.ximalaya.utils;

import android.util.Log;

import com.uniqueAndroid.ximalaya.base.BaseApplication;

public class LogUtil {
    public static String sTAG = "LogUtil";
    public static boolean sIsRelease = false;

    public static void init( String baseTag, boolean isRelease) {
        sTAG = baseTag;
        sIsRelease = isRelease;
    }

    public static void v(String TAG, String content) {
        if (!sIsRelease) {
            Log.v("[" + sTAG + "]" + TAG, content);
        }
    }

    public static void i(String TAG, String content) {
        if (!sIsRelease) {
            Log.i("[" + sTAG + "]" + TAG, content);
        }
    }

    public static void d(String TAG, String content) {
        if (!sIsRelease) {
            Log.d("[" + sTAG + "]" + TAG, content);
        }
    }

    public static void w(String TAG, String content) {
        if (!sIsRelease) {
            Log.w("[" + sTAG + "]" + TAG, content);
        }
    }

    public static void e(String TAG, String content) {
        if (!sIsRelease) {
            Log.d("[" + sTAG + "]" + TAG, content);
        }
    }
}
