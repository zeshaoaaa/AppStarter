package org.jay.appstarterexample.tasks

import com.tencent.bugly.crashreport.CrashReport
import org.jay.appstarter.Task

class InitBuglyTask : Task() {
    override fun run() {
        CrashReport.initCrashReport(mContext, "注册时申请的APPID", false)
    }
}