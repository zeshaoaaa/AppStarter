package org.jay.appstarter.utils;

import android.util.Log;

import org.jay.appstarter.BuildConfig;

// 日志打印器
public class DispatcherLog {

    private static boolean sDebug = BuildConfig.DEBUG;

    public static void i(String msg) {
        if (!sDebug) {
            return;
        }
        Log.i("TaskDispatcher",msg);
    }

    public static boolean isDebug() {
        return sDebug;
    }

    public static void setDebug(boolean debug) {
        sDebug = debug;
    }

}
