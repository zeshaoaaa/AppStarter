package org.jay.appstarterexample.tasks

import android.provider.Settings
import org.jay.appstarter.Task
import org.jay.appstarterexample.MyApplication

class GetDeviceIdTask : Task() {

    private var mDeviceId: String? = null

    override fun run() {
        val app = mContext as MyApplication
        mDeviceId =
            Settings.System.getString(mContext.contentResolver, Settings.System.ANDROID_ID)
        app.deviceId = mDeviceId
    }

}