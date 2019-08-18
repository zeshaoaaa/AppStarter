package org.jay.launchstarterexample.tasks;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import org.jay.launchstarter.Task;
import org.jay.launchstarterexample.MyApplication;

import static androidx.core.content.PermissionChecker.checkSelfPermission;

public class GetDeviceIdTask extends Task {

    private String mDeviceId;

    @Override
    public void run() {
        MyApplication app = (MyApplication) mContext;
        mDeviceId = Settings.System.getString(mContext.getContentResolver(), Settings.System.ANDROID_ID);
        app.setDeviceId(mDeviceId);
    }

}
