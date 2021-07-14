package org.jay.appstarterexample.tasks

import cn.jpush.android.api.JPushInterface
import org.jay.appstarter.Task
import org.jay.appstarterexample.MyApplication
import java.util.ArrayList

/**
 * 需要在getDeviceId之后执行
 */
class InitJPushTask : Task() {
    override fun dependsOn(): List<Class<out Task?>>? {
        val task: MutableList<Class<out Task?>> = ArrayList()
        task.add(GetDeviceIdTask::class.java)
        return task
    }

    override fun run() {
        JPushInterface.init(mContext)
        val app = mContext as MyApplication
        JPushInterface.setAlias(mContext, 0, app.deviceId)
    }
}