package org.jay.appstarter

import android.content.Context
import kotlin.jvm.Volatile
import androidx.annotation.UiThread
import android.os.Looper
import android.util.Log
import org.jay.appstarter.sort.TaskSortUtil
import org.jay.appstarter.stat.TaskStat
import org.jay.appstarter.utils.DispatcherLog
import org.jay.appstarter.utils.Utils
import java.lang.RuntimeException
import java.util.ArrayList
import java.util.HashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * 任务分发器
 */
class TaskDispatcher private constructor() {
    /**
     * 开始时间
     */
    private var mStartTime: Long = 0

    private val mFutures: MutableList<Future<*>> = ArrayList()

    /**
     * 全部任务
     */
    private var mAllTasks: MutableList<Task> = ArrayList()
    private val mClsAllTasks: MutableList<Class<out Task>> = ArrayList()

    /**
     * 主线程任务
     */
    @Volatile
    private var mMainThreadTasks: MutableList<Task> = ArrayList()

    private var mCountDownLatch: CountDownLatch? = null

    /**
     * 需要等待的任务数
     */
    private val mNeedWaitCount = AtomicInteger() //

    /**
     * 调用了 await 还没结束且需要等待的任务列表
     */
    private val mNeedWaitTasks: MutableList<Task> = ArrayList()

    /**
     * 已经结束的Task
     */
    @Volatile
    private var mFinishedTasks: MutableList<Class<out Task>> = ArrayList(100) //

    private val mDependedHashMap = HashMap<Class<out Task>, ArrayList<Task>>()

    /**
     * 启动器分析的次数，统计下分析的耗时；
     */
    private val mAnalyseCount = AtomicInteger()

    /**
     * 添加任务
     */
    fun addTask(task: Task?): TaskDispatcher {
        if (task != null) {
            collectDepends(task)
            mAllTasks.add(task)
            mClsAllTasks.add(task.javaClass)
            // 非主线程且需要wait的，主线程不需要CountDownLatch也是同步的
            if (needWait(task)) {
                mNeedWaitTasks.add(task)
                mNeedWaitCount.getAndIncrement()
            }
        }
        return this
    }

    /**
     * 收集依赖
     */
    private fun collectDepends(task: Task) {
        val dependsOn = task.dependsOn()
        if (dependsOn == null || dependsOn.isEmpty()) {
            return
        }
        for (cls in dependsOn) {
            // 根据不同的任务类型获取不同的任务列表
            var tasks = mDependedHashMap[cls]
            if (tasks == null) {
                tasks = ArrayList()
                mDependedHashMap[cls] = tasks
            }
            tasks.add(task)

            // 该类已经在已结束任务列表中
            if (mFinishedTasks.contains(cls)) {
                task.satisfy()
            }
        }
    }

    private fun needWait(task: Task): Boolean {
        // 如果任务不是运行在主线程，并且任务自己设定了不需要等待，那就不需要等待
        return !task.runOnMainThread() && task.needWait()
    }

    @UiThread
    fun start() {
        mStartTime = System.currentTimeMillis()
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw RuntimeException("must be called from UiThread")
        }
        if (mAllTasks.size > 0) {
            mAnalyseCount.getAndIncrement()
            printDependedMsg()
            mAllTasks = TaskSortUtil.getSortResult(mAllTasks, mClsAllTasks)
            mCountDownLatch = CountDownLatch(mNeedWaitCount.get())

            // 发送并执行异步任务
            sendAndExecuteAsyncTasks()
            DispatcherLog.i("task analyse cost " + (System.currentTimeMillis() - mStartTime) + "  begin main ")
            executeTaskMain()
        }
        DispatcherLog.i("task analyse cost startTime cost " + (System.currentTimeMillis() - mStartTime))
    }

    /**
     * 取消任务
     */
    fun cancel() {
        for (future in mFutures) {
            future.cancel(true)
        }
    }

    private fun executeTaskMain() {
        mStartTime = System.currentTimeMillis()
        for (task in mMainThreadTasks) {
            val time = System.currentTimeMillis()
            DispatchRunnable(task, this).run()
            DispatcherLog.i(
                "real main " + task.javaClass.simpleName + " cost   " +
                        (System.currentTimeMillis() - time)
            )
        }
        DispatcherLog.i("maintask cost " + (System.currentTimeMillis() - mStartTime))
    }

    /**
     * 发送去并且执行异步任务
     */
    private fun sendAndExecuteAsyncTasks() {
        for (task in mAllTasks) {
            if (task.onlyInMainProcess() && !isMainProcess) {
                markTaskDone(task)
            } else {
                sendTaskReal(task)
            }
            task.isSend = true
        }
    }

    /**
     * 查看被依赖的信息
     */
    private fun printDependedMsg() {
        DispatcherLog.i("needWait size : " + mNeedWaitCount.get())
        if (DispatcherLog.isDebug) {
            for (cls in mDependedHashMap.keys) {
                DispatcherLog.i("cls " + cls.simpleName + "   " + mDependedHashMap[cls]!!.size)
                for (task in mDependedHashMap[cls]!!) {
                    DispatcherLog.i("cls       " + task.javaClass.simpleName)
                }
            }
        }
    }

    /**
     * 通知Children一个前置任务已完成
     */
    fun satisfyChildren(launchTask: Task) {
        val arrayList = mDependedHashMap[launchTask.javaClass]
        if (arrayList != null && arrayList.size > 0) {
            for (task in arrayList) {
                task.satisfy()
            }
        }
    }

    fun markTaskDone(task: Task) {
        if (needWait(task)) {
            mFinishedTasks.add(task.javaClass)
            mNeedWaitTasks.remove(task)
            mCountDownLatch!!.countDown()
            mNeedWaitCount.getAndDecrement()
        }
    }

    /**
     * 发送任务
     */
    private fun sendTaskReal(task: Task) {
        if (task.runOnMainThread()) {
            // 把任务添加到组线程任务列表中
            mMainThreadTasks.add(task)
            if (task.needCall()) {
                task.setTaskCallBack {
                    TaskStat.markTaskDone()
                    task.isFinished = true
                    satisfyChildren(task)
                    markTaskDone(task)
                    DispatcherLog.i(task.javaClass.simpleName + " finish")
                    Log.i("testLog", "call")
                }
            }
            return
        }

        // 提交任务
        val future = task.runOn().submit(DispatchRunnable(task, this))
        mFutures.add(future)

    }

    /**
     * 执行任务
     */
    fun executeTask(task: Task) {
        if (needWait(task)) {
            mNeedWaitCount.getAndIncrement()
        }
        // 执行任务
        task.runOn().execute(DispatchRunnable(task, this))
    }

    @UiThread
    fun await() {
        try {
            if (DispatcherLog.isDebug) {
                DispatcherLog.i("still has " + mNeedWaitCount.get())
                for (task in mNeedWaitTasks) {
                    DispatcherLog.i("needWait: " + task.javaClass.simpleName)
                }
            }
            if (mNeedWaitCount.get() > 0) {
                if (mCountDownLatch == null) {
                    throw RuntimeException("You have to call start() before call await()")
                }
                // 等待 10 秒
                mCountDownLatch?.await(WAIT_TIME.toLong(), TimeUnit.MILLISECONDS)
            }
        } catch (e: InterruptedException) {
        }
    }

    companion object {

        /**
         * 等待时间
         */
        private const val WAIT_TIME = 10 * 1000

        /**
         * Application Context
         */
        lateinit var context: Context

        /**
         * 是否在主进程
         */
        var isMainProcess = false
            private set

        /**
         * 是否初始化
         */
        @Volatile
        private var sHasInit = false

        /**
         * 初始化任务分发器
         */
        @JvmStatic
        fun init(context: Context) {
            Companion.context = context
            sHasInit = true
            isMainProcess = Utils.isMainProcess(Companion.context)
        }

        /**
         * 注意：每次获取的都是新对象
         */
        @JvmStatic
        fun createInstance(): TaskDispatcher {
            if (!sHasInit) {
                throw RuntimeException("must call TaskDispatcher.init first")
            }
            return TaskDispatcher()
        }

    }

}