package org.jay.appstarter.stat

import org.jay.appstarter.utils.DispatcherLog.i
import kotlin.jvm.Volatile
import org.jay.appstarter.stat.TaskStatBean
import org.jay.appstarter.stat.TaskStat
import org.jay.appstarter.utils.DispatcherLog
import java.util.ArrayList
import java.util.concurrent.atomic.AtomicInteger

object TaskStat {

    @Volatile
    private var sCurrentSituation = ""
    private val sBeans: MutableList<TaskStatBean> = ArrayList()
    private var sTaskDoneCount = AtomicInteger()

    // 是否开启统计
    private const val sOpenLaunchStat = false

    var currentSituation: String
        get() = sCurrentSituation
        set(currentSituation) {
            if (!sOpenLaunchStat) {
                return
            }
            i("currentSituation   $currentSituation")
            sCurrentSituation = currentSituation
            setLaunchStat()
        }

    fun markTaskDone() {
        sTaskDoneCount.getAndIncrement()
    }

    private fun setLaunchStat() {
        val bean = TaskStatBean()
        bean.situation = sCurrentSituation
        bean.count = sTaskDoneCount.get()
        sBeans.add(bean)
        sTaskDoneCount = AtomicInteger(0)
    }

}