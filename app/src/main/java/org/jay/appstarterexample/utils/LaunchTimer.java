package org.jay.appstarterexample.utils;

/**
 * 埋点工具类
 */
public class LaunchTimer {

    private static long sTime;

    public static void startRecord() {
        sTime = System.currentTimeMillis();
    }

    public static void endRecord() {
        endRecord("");
    }

    public static void endRecord(String msg) {
        long cost = System.currentTimeMillis() - sTime;
        org.jay.appstarterexample.utils.LogUtils.i(msg + "cost " + cost);
    }

}
