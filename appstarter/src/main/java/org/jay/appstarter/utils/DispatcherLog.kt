package org.jay.appstarter.utils

import android.util.Log
import org.jay.appstarter.BuildConfig
import org.jay.appstarter.utils.DispatcherLog

// 日志打印器
object DispatcherLog {

    var isDebug = BuildConfig.DEBUG

    @JvmStatic
    fun i(msg: String?) {
        if (!isDebug) {
            return
        }
        Log.i("TaskDispatcher", msg!!)
    }

}