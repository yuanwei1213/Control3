package com.gengy.control.BeingControl.Authority;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.RequiresApi;

/**
 * @date on 2019/12/30
 * 描述       15:13
 * com.gengy.control.BeingControl.Authority
 */
@SuppressLint("OverrideAbstract")
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationListener  extends NotificationListenerService {
    private static final String TAG = "NotificationListener";
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i(TAG,"Notification removed");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.i(TAG, "Notification posted");
    }
}
