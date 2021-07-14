package org.jay.appstarter;

import android.os.Looper;
import android.os.MessageQueue;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 延迟初始化分发器
 */
public class DelayInitDispatcher {

    // 延迟任务队列
    private Queue<Task> mDelayTasks = new LinkedList<>();

    private MessageQueue.IdleHandler mIdleHandler = new MessageQueue.IdleHandler() {
        @Override
        public boolean queueIdle() {
            if (mDelayTasks.size() > 0) {
                Task task = mDelayTasks.poll();
                // 执行任务
                new DispatchRunnable(task).run();
            }
            // 没有更多任务时，就不需要 MessageQueue 保留当前 IdleHandler
            return !mDelayTasks.isEmpty();
        }
    };

    // 添加任务到延迟任务列表
    public DelayInitDispatcher addTask(Task task) {
        mDelayTasks.add(task);
        return this;
    }

    // 添加 IdleHandler 到消息队列中
    public void start(){
        Looper.myQueue().addIdleHandler(mIdleHandler);
    }

}
