package org.jay.appstarterexample.tasks

import android.os.Handler
import android.os.Looper
import com.facebook.stetho.Stetho
import org.jay.appstarter.Task

/**
 * 异步的Task
 */
class InitStethoTask : Task() {

    override fun run() {
        Stetho.initializeWithDefaults(mContext)
    }
}