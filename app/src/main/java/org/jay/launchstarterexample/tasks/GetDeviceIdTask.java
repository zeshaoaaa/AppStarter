package org.jay.launchstarterexample.tasks;

import android.provider.Settings;

import org.jay.appstarter.Task;
import org.jay.launchstarterexample.MyApplication;

public class GetDeviceIdTask extends Task {

    private String mDeviceId;

    @Override
    public void run() {
        MyApplication app = (MyApplication) mContext;
        mDeviceId = Settings.System.getString(mContext.getContentResolver(), Settings.System.ANDROID_ID);
        app.setDeviceId(mDeviceId);
    }

}
