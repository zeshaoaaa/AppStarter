package org.jay.appstarter;

import android.os.Process;
import androidx.annotation.IntRange;

import java.util.List;
import java.util.concurrent.Executor;

// 任务接口
public interface ITask {

    /**
     * 优先级的范围，可根据Task重要程度及工作量指定；之后根据实际情况决定是否有必要放更大
     */
    @IntRange(from = Process.THREAD_PRIORITY_FOREGROUND, to = Process.THREAD_PRIORITY_LOWEST)
    int priority();

    void run();

    /**
     * Task执行所在的线程池，可指定，一般默认
     */
    Executor runOn();

    /**
     * 依赖关系
     */
    List<Class<? extends Task>> dependsOn();

    /**
     * 异步线程执行的Task是否需要在被调用await的时候等待，默认不需要
     */
    boolean needWait();

    /**
     * 是否在主线程执行
     */
    boolean runOnMainThread();

    /**
     * 只是在主进程执行
     */
    boolean onlyInMainProcess();

    /**
     * Task主任务执行完成之后需要执行的任务
     */
    Runnable getTailRunnable();

    void setTaskCallBack(TaskCallBack callBack);

    boolean needCall();
}
