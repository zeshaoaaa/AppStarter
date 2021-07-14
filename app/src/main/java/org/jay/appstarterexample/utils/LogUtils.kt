package org.jay.appstarterexample.utils

import android.util.Log
import org.jay.appstarter.utils.Utils
import org.jay.appstarterexample.MyApplication
import java.util.concurrent.ExecutorService

object LogUtils {

    const val TAG = "performance"

    @JvmStatic
    fun i(msg: String?) {
        if (msg == null) return
        if (Utils.isMainProcess(MyApplication.application)) {
            Log.i(TAG, msg)
        }
    }

}