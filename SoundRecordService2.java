package com.gengy.control;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.RequiresApi;


/**
 * @date on 2020/6/8
 * 描述       14:37
 * com.gengy.control.Untils
 */
public class SoundRecordService2 extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        int notifyID = 1;
        String channelId = "call_01";
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setChannelId(channelId)
                .build();
        startForeground(notifyID, notification);
    }

    @Override
    public void onCreate() {
        TelephonyManager manager=(TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        manager.listen(MyPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        super.onCreate();
    }



    PhoneStateListener MyPhoneStateListener=new PhoneStateListener(){

        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            super.onCallStateChanged(state, phoneNumber);

            Log.i("onCallStateChanged", "onCallStateChangedonCallStateChanged:3333 "+state);
        }
    };

    @Override
    public void onDestroy() {
        System.out.println("onDestroy invoke");
        super.onDestroy();
    }
}
