package org.jay.appstarterexample.tasks

import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.location.AMapLocation
import org.jay.appstarter.Task

/**
 * 高德 SDK 初始化任务
 */
class InitAMapTask : Task() {

    private var mLocationClient: AMapLocationClient? = null
    private var mLocationOption: AMapLocationClientOption? = null
    private val mLocationListener = AMapLocationListener {
        // 一些处理
    }

    override fun run() {
        mLocationClient = AMapLocationClient(mContext)
        mLocationClient!!.setLocationListener(mLocationListener)
        mLocationOption = AMapLocationClientOption()
        mLocationOption!!.locationMode = AMapLocationClientOption.AMapLocationMode.Battery_Saving
        mLocationOption!!.isOnceLocation = true
        mLocationClient!!.setLocationOption(mLocationOption)
        mLocationClient!!.startLocation()
    }
}