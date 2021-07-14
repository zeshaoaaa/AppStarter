package org.jay.appstarterexample.net

import com.facebook.imagepipeline.listener.RequestListener
import com.facebook.imagepipeline.request.ImageRequest

class FrescoTraceListener : RequestListener {
    override fun onRequestStart(
        request: ImageRequest,
        callerContext: Any,
        requestId: String,
        isPrefetch: Boolean
    ) {
    }

    override fun onRequestSuccess(request: ImageRequest, requestId: String, isPrefetch: Boolean) {}
    override fun onRequestFailure(
        request: ImageRequest,
        requestId: String,
        throwable: Throwable,
        isPrefetch: Boolean
    ) {
    }

    override fun onRequestCancellation(requestId: String) {}
    override fun onProducerStart(requestId: String, producerName: String) {}
    override fun onProducerEvent(requestId: String, producerName: String, eventName: String) {}
    override fun onProducerFinishWithSuccess(
        requestId: String,
        producerName: String,
        extraMap: Map<String, String>?
    ) {
    }

    override fun onProducerFinishWithFailure(
        requestId: String,
        producerName: String,
        t: Throwable,
        extraMap: Map<String, String>?
    ) {
    }

    override fun onProducerFinishWithCancellation(
        requestId: String,
        producerName: String,
        extraMap: Map<String, String>?
    ) {
    }

    override fun onUltimateProducerReached(
        requestId: String,
        producerName: String,
        successful: Boolean
    ) {
    }

    override fun requiresExtraMap(requestId: String): Boolean {
        return false
    }
}