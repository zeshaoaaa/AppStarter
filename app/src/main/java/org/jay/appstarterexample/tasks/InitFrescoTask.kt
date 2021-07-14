package org.jay.appstarterexample.tasks

import com.facebook.imagepipeline.listener.RequestListener
import org.jay.appstarterexample.net.FrescoTraceListener
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.facebook.drawee.backends.pipeline.Fresco
import org.jay.appstarter.Task
import java.util.HashSet

class InitFrescoTask : Task() {
    override fun run() {
        val listenerset: MutableSet<RequestListener> = HashSet()
        listenerset.add(FrescoTraceListener())
        val config = ImagePipelineConfig.newBuilder(mContext).setRequestListeners(listenerset)
            .build()
        Fresco.initialize(mContext, config)
    }
}