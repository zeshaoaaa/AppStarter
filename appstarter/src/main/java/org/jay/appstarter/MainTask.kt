package org.jay.appstarter

// 在主线程执行的任务
abstract class MainTask : Task() {

    override fun runOnMainThread(): Boolean {
        return true
    }

}