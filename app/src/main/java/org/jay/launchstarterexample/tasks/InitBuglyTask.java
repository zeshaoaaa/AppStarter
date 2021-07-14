package org.jay.launchstarterexample.tasks;


import com.tencent.bugly.crashreport.CrashReport;
import org.jay.appstarter.Task;

public class InitBuglyTask extends Task {

    @Override
    public void run() {
        CrashReport.initCrashReport(mContext, "注册时申请的APPID", false);
    }
}
