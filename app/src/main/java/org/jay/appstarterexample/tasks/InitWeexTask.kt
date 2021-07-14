package org.jay.appstarterexample.tasks

import android.app.Application
import org.jay.appstarter.MainTask
import com.taobao.weex.InitConfig
import com.taobao.weex.WXSDKEngine

/**
 * 主线程执行的task
 */
class InitWeexTask : MainTask() {
    override fun needWait(): Boolean {
        return true
    }

    override fun run() {
        val config = InitConfig.Builder().build()
        WXSDKEngine.initialize(mContext as Application, config)
    }
}