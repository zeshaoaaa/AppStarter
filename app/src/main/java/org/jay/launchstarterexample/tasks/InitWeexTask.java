package org.jay.launchstarterexample.tasks;

import android.app.Application;
import com.taobao.weex.InitConfig;
import com.taobao.weex.WXSDKEngine;
import org.jay.appstarter.MainTask;

/**
 * 主线程执行的task
 */
public class InitWeexTask extends MainTask {

    @Override
    public boolean needWait() {
        return true;
    }

    @Override
    public void run() {
        InitConfig config = new InitConfig.Builder().build();
        WXSDKEngine.initialize((Application) mContext, config);
    }
}
