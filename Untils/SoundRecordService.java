package com.gengy.control.Untils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.gengy.control.R;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;


/**
 * @date on 2020/6/8
 * 描述       14:37
 * com.gengy.control.Untils
 */
public class SoundRecordService extends Service {
    private Intent intent = new Intent("com.service.RECEIVER");


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
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "call_01";
        String channelName = "record";
        String channelDescription = "this a record";
        int channelImportance =
                NotificationManager.IMPORTANCE_NONE;
        NotificationChannel mChannel =new  NotificationChannel(channelId, channelName, channelImportance);
        mChannel.setDescription(channelDescription);
        mChannel.enableLights(false);
        mChannel.setLightColor(Color.RED);
        mChannel.enableVibration(false);
//        mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400);
        mNotificationManager.createNotificationChannel(mChannel);
        int notifyID = 2;

        Notification  notification = new Notification.Builder(this)
//                .setSmallIcon(R.mipmap.ic_launcher)
                .setChannelId(channelId)
                .build();
        startForeground(notifyID, notification);
//        int notifyID = 1;
//        String channelId = "call_01";
//        Notification notification = new Notification.Builder(this)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setChannelId(channelId)
//                .build();
//        startForeground(notifyID, notification);
    }


    @Override
    public void onCreate() {
        TelephonyManager manager=(TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        manager.listen(MyPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        super.onCreate();
    }

    PhoneStateListener   MyPhoneStateListener=new PhoneStateListener(){
        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            super.onCallStateChanged(state, phoneNumber);



            intent.putExtra("state",state);
            intent.putExtra("phoneNumber",phoneNumber);
            sendBroadcast(intent);


        }
    };




}
