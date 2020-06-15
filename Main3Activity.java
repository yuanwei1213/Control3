package com.gengy.control;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.gengy.control.Untils.SoundRecordService;

public class Main3Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        phoneStateReceiver mPhoneStateReceiver = new phoneStateReceiver();
        IntentFilter intentFilter=new IntentFilter("com.service.RECEIVER");
        registerReceiver(mPhoneStateReceiver,intentFilter);
        
        Intent i = new Intent(this, SoundRecordService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(i);
        } else {
            startService(i);
        }

    }
    public  class  phoneStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("11", "onCallStateChangedonCallStateChanged: onReceiveonReceiveonReceive");
            int state=intent.getIntExtra("state",0);
            String phone=intent.getStringExtra("phoneNumber");

            Log.i("11", "onCallStateChangedonCallStateChanged: "+state+"phone"+phone);


        }
    }
    
}
