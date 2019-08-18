package org.jay.launchstarterexample.tasks;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import org.jay.launchstarter.Task;
import org.jay.launchstarterexample.MyApplication;

import static androidx.core.content.PermissionChecker.checkSelfPermission;

public class GetDeviceIdTask extends Task {

    private String mDeviceId;

    @Override
    public void run() {
        // 真正自己的代码
        TelephonyManager tManager = (TelephonyManager) mContext.getSystemService(
                Context.TELEPHONY_SERVICE);
        MyApplication app = (MyApplication) mContext;
        boolean granted = checkSelfPermission(mContext,Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED;
        if (granted) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        mDeviceId = tManager.getDeviceId();
        app.setDeviceId(mDeviceId);
    }
}
