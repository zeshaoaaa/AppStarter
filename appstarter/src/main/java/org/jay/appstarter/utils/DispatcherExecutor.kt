package org.jay.appstarter.utils

import org.jay.appstarter.utils.DispatcherExecutor
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

// 线程池
object DispatcherExecutor {

    /**
     * CPU 密集型任务的线程池
     */
    var cPUExecutor: ThreadPoolExecutor
        private set

    /**
     * 获取IO线程池
     */
    /**
     * IO 密集型任务的线程池
     */
    var iOExecutor: ExecutorService
        private set

    /**
     * CPU 核数
     */
    private val CPU_COUNT = Runtime.getRuntime().availableProcessors()

    /**
     * 线程池线程数
     */
    private val CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 5))

    /**
     * 线程池线程数的最大值
     */
    private val MAXIMUM_POOL_SIZE = CORE_POOL_SIZE

    /**
     * 存活时间
     */
    private const val KEEP_ALIVE_SECONDS = 5

    /**
     * 阻塞队列
     */
    private val sPoolWorkQueue: BlockingQueue<Runnable> = LinkedBlockingQueue()

    /**
     * 线程工厂
     */
    private val sThreadFactory = DefaultThreadFactory()

    /**
     * 拒绝执行处理器
     */
    private val sHandler =
        RejectedExecutionHandler { r, executor -> Executors.newCachedThreadPool().execute(r) }

    // 初始化线程池
    init {
        cPUExecutor = ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS.toLong(), TimeUnit.SECONDS,
            sPoolWorkQueue, sThreadFactory, sHandler
        )
        cPUExecutor?.allowCoreThreadTimeOut(true)
        iOExecutor = Executors.newCachedThreadPool(sThreadFactory)
    }

    /**
     * 默认线程工厂
     */
    private class DefaultThreadFactory() : ThreadFactory {
        private val group: ThreadGroup
        private val threadNumber = AtomicInteger(1)
        private val namePrefix: String
        override fun newThread(r: Runnable): Thread {
            val t = Thread(
                group, r,
                namePrefix + threadNumber.getAndIncrement(),
                0
            )
            if (t.isDaemon) {
                t.isDaemon = false
            }
            if (t.priority != Thread.NORM_PRIORITY) {
                t.priority = Thread.NORM_PRIORITY
            }
            return t
        }

        companion object {
            private val poolNumber = AtomicInteger(1)
        }

        init {
            val s = System.getSecurityManager()
            group = if (s != null) s.threadGroup else Thread.currentThread().threadGroup
            namePrefix = "TaskDispatcherPool-" +
                    poolNumber.getAndIncrement() +
                    "-Thread-"
        }
    }


}