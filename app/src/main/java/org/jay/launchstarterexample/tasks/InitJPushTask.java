package org.jay.launchstarterexample.tasks;

import cn.jpush.android.api.JPushInterface;
import org.jay.launchstarter.Task;
import org.jay.launchstarterexample.MyApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * 需要在getDeviceId之后执行
 */
public class InitJPushTask extends Task {

    @Override
    public List<Class<? extends Task>> dependsOn() {
        List<Class<? extends Task>> task = new ArrayList<>();
        task.add(GetDeviceIdTask.class);
        return task;
    }

    @Override
    public void run() {
        JPushInterface.init(mContext);
        MyApplication app = (MyApplication) mContext;
        JPushInterface.setAlias(mContext, 0, app.getDeviceId());
    }

}
