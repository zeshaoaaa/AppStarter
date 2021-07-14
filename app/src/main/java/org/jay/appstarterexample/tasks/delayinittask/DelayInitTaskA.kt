package org.jay.appstarterexample.tasks.delayinittask

import com.facebook.stetho.common.LogUtil
import org.jay.appstarter.MainTask

class DelayInitTaskA : MainTask() {
    override fun run() {
        // 模拟一些操作
        try {
            Thread.sleep(100)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        LogUtil.i("DelayInitTaskA finished")
    }
}