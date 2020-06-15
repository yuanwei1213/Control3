package com.gengy.control.Untils;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.RemoteException;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import static androidx.core.content.ContextCompat.checkSelfPermission;

/**
 * @date on 2020/6/5
 * 描述       15:28
 * com.gengy.control.Untils
 */
public class PhoneBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "message";
    private static boolean mIncomingFlag = false;
    private static String mIncomingNumber = null;
    private static boolean callover = false;



    @Override
    public void onReceive(Context context, Intent intent) {


        String number = getResultData();

        Log.i(TAG, "onReceivenumbernumber: "+number);




//        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {// 如果是拨打电话
//            mIncomingFlag = false;
//            String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
//        } else {

        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        TelephonyManager tManager = (TelephonyManager)
                context.getSystemService(Service.TELEPHONY_SERVICE);
        Log.i(TAG, "onReceiveonReceiveonReceive: " + state);
        switch (tManager.getCallState()) {
            case TelephonyManager.CALL_STATE_RINGING:  //来电状态
                mIncomingNumber = intent.getStringExtra("incoming_number");

                break;
            case TelephonyManager.CALL_STATE_OFFHOOK: //摘机状态(接听)
                mIncomingNumber = intent.getStringExtra("incoming_number");
                callover = true;

                break;
            case TelephonyManager.CALL_STATE_IDLE:// 空闲状态，没有任何活动。(挂断)
                android.util.Log.d("fangc", " mIncomingNumber " + "guaduan");
                if (mIncomingFlag || callover) {
                    mIncomingFlag = false;
                    callover = false;

                }
                break;
        }
//    }

    }




}
