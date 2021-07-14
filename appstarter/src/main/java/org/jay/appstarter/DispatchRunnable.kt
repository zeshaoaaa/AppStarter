package org.jay.appstarter

import org.jay.appstarter.utils.DispatcherLog.isDebug
import org.jay.appstarter.stat.TaskStat
import android.os.Looper
import android.os.Process
import android.os.Trace
import org.jay.appstarter.utils.DispatcherLog

/**
 * 任务真正执行的地方
 */
class DispatchRunnable(private val mTask : Task,
                       private val mTaskDispatcher: TaskDispatcher) : Runnable {

    override fun run() {
        Trace.beginSection(mTask.javaClass.simpleName)
        DispatcherLog.i(
            mTask.javaClass.simpleName
                    + " begin run" + "  Situation  " + TaskStat.getCurrentSituation()
        )
        Process.setThreadPriority(mTask.priority())
        var startTime = System.currentTimeMillis()
        mTask.isWaiting = true
        mTask.waitToSatisfy()
        val waitTime = System.currentTimeMillis() - startTime
        startTime = System.currentTimeMillis()

        // 执行Task
        mTask.isRunning = true
        mTask.run()

        // 执行Task的尾部任务
        val tailRunnable = mTask.getTailRunnable()
        tailRunnable?.run()
        if (!mTask.needCall() || !mTask.runOnMainThread()) {
            printTaskLog(startTime, waitTime)
            TaskStat.markTaskDone()
            mTask.isFinished = true
            mTaskDispatcher.satisfyChildren(mTask)
            mTaskDispatcher.markTaskDone(mTask)
            DispatcherLog.i(mTask.javaClass.simpleName + " finish")
        }
        Trace.endSection()
    }

    /**
     * 打印出来Task执行的日志
     */
    private fun printTaskLog(startTime: Long, waitTime: Long) {
        val runTime = System.currentTimeMillis() - startTime
        if (isDebug) {
            DispatcherLog.i(
                mTask.javaClass.simpleName + "  wait " + waitTime + "    run "
                        + runTime + "   isMain " + (Looper.getMainLooper() == Looper.myLooper())
                        + "  needWait " + (mTask.needWait() || Looper.getMainLooper() == Looper.myLooper())
                        + "  ThreadId " + Thread.currentThread().id
                        + "  ThreadName " + Thread.currentThread().name
                        + "  Situation  " + TaskStat.getCurrentSituation()
            )
        }
    }
}