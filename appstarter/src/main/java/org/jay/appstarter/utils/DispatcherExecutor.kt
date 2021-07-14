package org.jay.appstarter.utils;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

// 分发器
public class DispatcherExecutor {

    /**
     * CPU 密集型任务的线程池
     */
    private static ThreadPoolExecutor sCPUThreadPoolExecutor;

    /**
     * IO 密集型任务的线程池
     */
    private static ExecutorService sIOThreadPoolExecutor;

    /**
     * CPU 核数
     */
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    /**
     * 线程池线程数
     */
    public static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 5));

    /**
     * 线程池线程数的最大值
     */
    private static final int MAXIMUM_POOL_SIZE = CORE_POOL_SIZE;

    /**
     * 存活时间
     */
    private static final int KEEP_ALIVE_SECONDS = 5;

    /**
     * 阻塞队列
     */
    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<>();

    /**
     * 线程工厂
     */
    private static final DefaultThreadFactory sThreadFactory = new DefaultThreadFactory();

    /**
     * 拒绝执行处理器
     */
    private static final RejectedExecutionHandler sHandler = new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            Executors.newCachedThreadPool().execute(r);
        }
    };

    // 初始化线程池
    static {
        sCPUThreadPoolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                sPoolWorkQueue, sThreadFactory, sHandler);
        sCPUThreadPoolExecutor.allowCoreThreadTimeOut(true);
        sIOThreadPoolExecutor = Executors.newCachedThreadPool(sThreadFactory);
    }

    /**
     * 获取CPU线程池
     */
    public static ThreadPoolExecutor getCPUExecutor() {
        return sCPUThreadPoolExecutor;
    }

    /**
     * 获取IO线程池
     */
    public static ExecutorService getIOExecutor() {
        return sIOThreadPoolExecutor;
    }

    /**
     * 默认线程工厂
     */
    private static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "TaskDispatcherPool-" +
                    poolNumber.getAndIncrement() +
                    "-Thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }



}
