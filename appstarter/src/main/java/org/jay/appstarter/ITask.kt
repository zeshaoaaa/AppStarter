package org.jay.appstarter

import android.os.Process
import androidx.annotation.IntRange
import java.util.concurrent.Executor

// 任务接口
interface ITask {

    /**
     * Task主任务执行完成之后需要执行的任务
     */
    fun getTailRunnable(): Runnable?

    fun setTaskCallBack(callBack: TaskCallBack)

    fun needCall(): Boolean

    /**
     * 优先级的范围，可根据Task重要程度及工作量指定；之后根据实际情况决定是否有必要放更大
     */
    @IntRange(
        from = Process.THREAD_PRIORITY_FOREGROUND.toLong(),
        to = Process.THREAD_PRIORITY_LOWEST.toLong()
    )
    fun priority(): Int

    fun run()

    /**
     * Task执行所在的线程池，可指定，一般默认
     */
    fun runOn(): Executor?

    /**
     * 依赖关系
     */
    fun dependsOn(): List<Class<out Task?>?>?

    /**
     * 异步线程执行的Task是否需要在被调用await的时候等待，默认不需要
     */
    fun needWait(): Boolean

    /**
     * 是否在主线程执行
     */
    fun runOnMainThread(): Boolean

    /**
     * 只是在主进程执行
     */
    fun onlyInMainProcess(): Boolean


}