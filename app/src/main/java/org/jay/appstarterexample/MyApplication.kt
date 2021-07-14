package org.jay.appstarterexample

import android.app.Application
import android.content.Context
import android.os.StrictMode
import androidx.multidex.MultiDex
import org.jay.appstarterexample.MyApplication
import org.jay.appstarter.TaskDispatcher
import org.jay.appstarterexample.tasks.InitAMapTask
import org.jay.appstarterexample.tasks.InitStethoTask
import org.jay.appstarterexample.tasks.InitWeexTask
import org.jay.appstarterexample.tasks.InitBuglyTask
import org.jay.appstarterexample.tasks.InitFrescoTask
import org.jay.appstarterexample.tasks.InitJPushTask
import org.jay.appstarterexample.tasks.InitUmengTask
import org.jay.appstarterexample.tasks.GetDeviceIdTask
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import org.jay.appstarterexample.tasks.delayinittask.DelayInitTaskA
import org.jay.appstarterexample.tasks.delayinittask.DelayInitTaskB

/**
 * 是否为开发模式
 */
private val DEV_MODE = BuildConfig.DEBUG

class MyApplication : Application() {

    /**
     * 设备 ID
     */
    var deviceId: String? = null

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        application = this

        // 初始化任务分发器
        TaskDispatcher.init(this)

        // 添加并开始执行初始化任务
        val dispatcher = TaskDispatcher.createInstance()
        dispatcher
            .addTask(InitAMapTask())    // 高德 SDK 初始化任务
            .addTask(InitStethoTask())  // Stetho 初始化任务
            .addTask(InitWeexTask())    // weex 初始化任务
            .addTask(InitBuglyTask())   // Bugly 初始化任务
            .addTask(InitFrescoTask())  // Frescode 初始化任务
            .addTask(InitJPushTask())   // 极光推送 SDK 初始化任务
            .addTask(InitUmengTask())   // 友盟 SDK 初始化任务
            .addTask(GetDeviceIdTask()) // 获取设备 ID 初始化任务
            .addTask(DelayInitTaskA())  // 延迟初始化任务 A
            .addTask(DelayInitTaskB())  // 延迟初始化任务 B
            .start()
        dispatcher.await()
        initStrictMode()
    }

    private fun initStrictMode() {
        if (DEV_MODE) {
            StrictMode.setThreadPolicy(
                ThreadPolicy.Builder()
                    .detectCustomSlowCalls() //API等级11，使用StrictMode.noteSlowCode
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork() // or .detectAll() for all detectable problems
                    .penaltyLog() //在Logcat 中打印违规异常信息
                    .build()
            )
            StrictMode.setVmPolicy(
                VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects() //API等级11
                    .penaltyLog()
                    .build()
            )
        }
    }

    companion object {
        var application: Application? = null
            private set
    }
}