package com.gengy.control.BeingControl.Authority;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.gengy.control.LoginActivity;

/**
 * @date on 2020/1/2
 * 描述       17:43
 * com.gengy.control.BeingControl.Authority
 */
public class PhoneReceiver extends BroadcastReceiver {

    private OnPhoneListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {

            String action = intent.getAction();

            Log.i("1122", "onReceiveonReceive "+intent);
            if (action == null) {
                return;
            }
//            TelephonyManager manager =
//                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            mListener.onPhoneStateChange(intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER));
//            mListener.onPhoneStateChange(manager.getCallState(),manager);
//            if (action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
//                if (mListener != null) {
//                    mListener.onPhoneOutCall(intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER),manager);
//                }
//            } else {
////                if (action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED))
//                if (mListener != null) {
//
//                    mListener.onPhoneStateChange(manager.getCallState(),manager);
//                }
//            }
        }
    }
    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            super.onCallStateChanged(state, phoneNumber);

            phoneNumber = phoneNumber.replace("-", "").replace(" ", "");        // 剔除号码中的分隔符



            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    // 空闲/挂断处理逻辑
                    //2、// CallProcess.HungUp(context, phoneNumber);
//                    onStateIdle(state, phoneNumber);
                    break;

                case TelephonyManager.CALL_STATE_OFFHOOK:
                    // 接听处理逻辑
                    //3、// CallProcess.OffHook(context, phoneNumber);
//                    onStateOffhook(state, phoneNumber);
                    break;

                case TelephonyManager.CALL_STATE_RINGING:
                    // 来电处理逻辑
                    //4、// CallProcess.Ringing(context, phoneNumber);
//                    onStateRinging(state, phoneNumber);
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * 监听器
     */
    public interface OnPhoneListener {
        /**
         * 去电
         */
        void onPhoneOutCall(String number,TelephonyManager  manager);

        /**
         * 来电状态
         *
         * @param state
         */
        void onPhoneStateChange(String number);

        void onPhoneStateChange(int state, TelecomManager number);
    }

    /**
     * 设置监听
     *
     * @param listener
     */
    public void setOnPhoneListener(OnPhoneListener listener) {
        mListener = listener;
    }

}
