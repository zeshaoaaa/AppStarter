package org.jay.appstarter.utils

import android.text.TextUtils
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context
import android.os.Process
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder

object Utils {

    /**
     * 当前进程名称
     */
    private var sCurProcessName: String? = null

    /**
     * 是否为主进程
     */
    fun isMainProcess(context: Context?): Boolean {
        if (context == null) return false
        val processName = getCurProcessName(context)
        return if (processName != null && processName.contains(":")) {
            false
        } else processName != null && processName == context.packageName
    }

    /**
     * 获取当前进程名
     */
    private fun getCurProcessName(context: Context?): String? {
        if (context == null) return null
        val processName = sCurProcessName
        if (!TextUtils.isEmpty(processName)) {
            return processName
        }
        try {
            val pid = Process.myPid()
            val mActivityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (appProcess in mActivityManager.runningAppProcesses) {
                if (appProcess.pid == pid) {
                    sCurProcessName = appProcess.processName
                    return sCurProcessName
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        sCurProcessName = curProcessNameFromProc
        return sCurProcessName
    }

    // 获取当前进程名
    private val curProcessNameFromProc: String?
        get() {
            var cmdlineReader: BufferedReader? = null
            try {
                cmdlineReader = BufferedReader(
                    InputStreamReader(
                        FileInputStream(
                            "/proc/" + Process.myPid() + "/cmdline"
                        ),
                        "iso-8859-1"
                    )
                )
                var c: Int
                val processName = StringBuilder()
                while (cmdlineReader.read().also { c = it } > 0) {
                    processName.append(c.toChar())
                }
                return processName.toString()
            } catch (e: Throwable) {
                // ignore
            } finally {
                if (cmdlineReader != null) {
                    try {
                        cmdlineReader.close()
                    } catch (e: Exception) {
                        // ignore
                    }
                }
            }
            return null
        }

}