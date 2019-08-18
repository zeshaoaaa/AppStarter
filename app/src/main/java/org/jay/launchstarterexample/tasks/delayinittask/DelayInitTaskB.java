package org.jay.launchstarterexample.tasks.delayinittask;


import org.jay.launchstarter.MainTask;
import org.jay.launchstarterexample.utils.LogUtils;

public class DelayInitTaskB extends MainTask {

    @Override
    public void run() {
        // 模拟一些操作

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LogUtils.i("DelayInitTaskB finished");
    }
}
