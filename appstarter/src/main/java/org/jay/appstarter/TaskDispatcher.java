package org.jay.appstarter;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.UiThread;
import org.jay.appstarter.sort.TaskSortUtil;
import org.jay.appstarter.stat.TaskStat;
import org.jay.appstarter.utils.DispatcherLog;
import org.jay.appstarter.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 任务分发器
 */
public class TaskDispatcher {

    /**
     * 开始时间
     */
    private long mStartTime;

    /**
     * 等待时间
     */
    private static final int WAITTIME = 10 * 1000;

    private static Context sContext;

    /**
     * 是否在主进程
     */
    private static boolean sIsMainProcess;

    private List<Future> mFutures = new ArrayList<>();

    /**
     * 是否初始化
     */
    private static volatile boolean sHasInit;

    /**
     * 全部任务
     */
    private List<Task> mAllTasks = new ArrayList<>();

    private List<Class<? extends Task>> mClsAllTasks = new ArrayList<>();

    /**
     * 主线程任务
     */
    private volatile List<Task> mMainThreadTasks = new ArrayList<>();

    private CountDownLatch mCountDownLatch;

    /**
     * 需要等待的任务数
     */
    private AtomicInteger mNeedWaitCount = new AtomicInteger();//

    /**
     * 调用了 await 还没结束且需要等待的任务列表
     */
    private List<Task> mNeedWaitTasks = new ArrayList<>();

    /**
     * 已经结束的Task
     */
    private volatile List<Class<? extends Task>> mFinishedTasks = new ArrayList<>(100);//

    private HashMap<Class<? extends Task>, ArrayList<Task>> mDependedHashMap = new HashMap<>();

    /**
     * 启动器分析的次数，统计下分析的耗时；
     */
    private AtomicInteger mAnalyseCount = new AtomicInteger();

    private TaskDispatcher() {
    }

    /**
     * 初始化任务分发器
     */
    public static void init(Context context) {
        if (context != null) {
            sContext = context;
            sHasInit = true;
            sIsMainProcess = Utils.isMainProcess(sContext);
        }
    }

    /**
     * 注意：每次获取的都是新对象
     */
    public static TaskDispatcher createInstance() {
        if (!sHasInit) {
            throw new RuntimeException("must call TaskDispatcher.init first");
        }
        return new TaskDispatcher();
    }

    /**
     * 添加任务
     */
    public TaskDispatcher addTask(Task task) {
        if (task != null) {
            collectDepends(task);
            mAllTasks.add(task);
            mClsAllTasks.add(task.getClass());
            // 非主线程且需要wait的，主线程不需要CountDownLatch也是同步的
            if (needWait(task)) {
                mNeedWaitTasks.add(task);
                mNeedWaitCount.getAndIncrement();
            }
        }
        return this;
    }

    /**
     * 收集依赖
     */
    private void collectDepends(Task task) {
        List<Class<? extends Task>> dependsOn = task.dependsOn();
        if (dependsOn == null || dependsOn.isEmpty()) {
            return;
        }
        for (Class<? extends Task> cls : dependsOn) {
            // 根据不同的任务类型获取不同的任务列表
            ArrayList<Task> tasks = mDependedHashMap.get(cls);

            if (tasks == null) {
                tasks = new ArrayList<>();
                mDependedHashMap.put(cls, tasks);
            }

            tasks.add(task);

            // 该类已经在已结束任务列表中
            if (mFinishedTasks.contains(cls)) {
                task.satisfy();
            }
        }
    }

    private boolean needWait(Task task) {
        // 如果任务不是运行在主线程，并且任务自己设定了不需要等待，那就不需要等待
        return !task.runOnMainThread() && task.needWait();
    }

    @UiThread
    public void start() {
        mStartTime = System.currentTimeMillis();
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new RuntimeException("must be called from UiThread");
        }
        if (mAllTasks.size() > 0) {
            mAnalyseCount.getAndIncrement();
            printDependedMsg();
            mAllTasks = TaskSortUtil.getSortResult(mAllTasks, mClsAllTasks);
            mCountDownLatch = new CountDownLatch(mNeedWaitCount.get());

            // 发送并执行异步任务
            sendAndExecuteAsyncTasks();

            DispatcherLog.i("task analyse cost " + (System.currentTimeMillis() - mStartTime) + "  begin main ");
            executeTaskMain();
        }
        DispatcherLog.i("task analyse cost startTime cost " + (System.currentTimeMillis() - mStartTime));
    }

    /**
     * 取消任务
     */
    public void cancel() {
        for (Future future : mFutures) {
            future.cancel(true);
        }
    }

    private void executeTaskMain() {
        mStartTime = System.currentTimeMillis();
        for (Task task : mMainThreadTasks) {
            long time = System.currentTimeMillis();
            new DispatchRunnable(task,this).run();
            DispatcherLog.i("real main " + task.getClass().getSimpleName() + " cost   " +
                    (System.currentTimeMillis() - time));
        }
        DispatcherLog.i("maintask cost " + (System.currentTimeMillis() - mStartTime));
    }

    /**
     * 发送去并且执行异步任务
     */
    private void sendAndExecuteAsyncTasks() {
        for (Task task : mAllTasks) {
            if (task.onlyInMainProcess() && !sIsMainProcess) {
                markTaskDone(task);
            } else {
                sendTaskReal(task);
            }
            task.setSend(true);
        }
    }

    /**
     * 查看被依赖的信息
     */
    private void printDependedMsg() {
        DispatcherLog.i("needWait size : " + (mNeedWaitCount.get()));
        if (false) {
            for (Class<? extends Task> cls : mDependedHashMap.keySet()) {
                DispatcherLog.i("cls " + cls.getSimpleName() + "   " + mDependedHashMap.get(cls).size());
                for (Task task : mDependedHashMap.get(cls)) {
                    DispatcherLog.i("cls       " + task.getClass().getSimpleName());
                }
            }
        }
    }

    /**
     * 通知Children一个前置任务已完成
     */
    public void satisfyChildren(Task launchTask) {
        ArrayList<Task> arrayList = mDependedHashMap.get(launchTask.getClass());
        if (arrayList != null && arrayList.size() > 0) {
            for (Task task : arrayList) {
                task.satisfy();
            }
        }
    }

    public void markTaskDone(Task task) {
        if (needWait(task)) {
            mFinishedTasks.add(task.getClass());
            mNeedWaitTasks.remove(task);
            mCountDownLatch.countDown();
            mNeedWaitCount.getAndDecrement();
        }
    }

    /**
     * 发送任务
     */
    private void sendTaskReal(final Task task) {
        if (task.runOnMainThread()) {
            mMainThreadTasks.add(task);
            if (task.needCall()) {
                task.setTaskCallBack(new TaskCallBack() {
                    @Override
                    public void call() {
                        TaskStat.markTaskDone();
                        task.setFinished(true);
                        satisfyChildren(task);
                        markTaskDone(task);
                        DispatcherLog.i(task.getClass().getSimpleName() + " finish");
                        Log.i("testLog", "call");
                    }
                });
            }
        } else {
            // 提交任务
            Future future = task.runOn().submit(new DispatchRunnable(task,this));
            mFutures.add(future);
        }
    }

    /**
     * 执行任务
     */
    public void executeTask(Task task) {
        if (needWait(task)) {
            mNeedWaitCount.getAndIncrement();
        }
        // 执行任务
        task.runOn().execute(new DispatchRunnable(task,this));
    }

    @UiThread
    public void await() {
        try {
            if (DispatcherLog.isDebug()) {
                DispatcherLog.i("still has " + mNeedWaitCount.get());
                for (Task task : mNeedWaitTasks) {
                    DispatcherLog.i("needWait: " + task.getClass().getSimpleName());
                }
            }

            if (mNeedWaitCount.get() > 0) {
                if (mCountDownLatch == null) {
                    throw new RuntimeException("You have to call start() before call await()");
                }
                mCountDownLatch.await(WAITTIME, TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException e) {

        }
    }

    public static Context getContext() {
        return sContext;
    }

    public static boolean isMainProcess() {
        return sIsMainProcess;
    }

}
