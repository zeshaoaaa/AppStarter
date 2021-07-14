package org.jay.appstarterexample.tasks.delayinittask

import org.jay.appstarterexample.utils.LogUtils.i
import org.jay.appstarter.MainTask

class DelayInitTaskB : MainTask() {

    override fun run() {
        // 模拟一些操作
        try {
            Thread.sleep(200)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        i("DelayInitTaskB finished")
    }
}