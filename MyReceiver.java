package com.gengy.control;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @date on 2020/2/27
 * 描述       16:36
 * com.gengy.control
 */
public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("111", "onReceiveonReceive:1111 ");

    }
}
