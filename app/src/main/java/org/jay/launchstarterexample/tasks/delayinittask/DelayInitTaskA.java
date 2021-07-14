package org.jay.launchstarterexample.tasks.delayinittask;


import org.jay.appstarter.MainTask;
import org.jay.launchstarterexample.utils.LogUtils;

public class DelayInitTaskA extends MainTask {

    @Override
    public void run() {
        // 模拟一些操作
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LogUtils.i("DelayInitTaskA finished");
    }
}
