package org.jay.launchstarterexample.tasks;

import com.umeng.commonsdk.UMConfigure;
import org.jay.appstarter.Task;

public class InitUmengTask extends Task {

    @Override
    public void run() {
        UMConfigure.init(mContext, "58edcfeb310c93091c000be2", "umeng",
                UMConfigure.DEVICE_TYPE_PHONE, "1fe6a20054bcef865eeb0991ee84525b");
    }
}
