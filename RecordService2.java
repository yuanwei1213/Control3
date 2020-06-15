package com.gengy.control;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.security.Provider;

import androidx.annotation.Nullable;

/**
 * @date on 2020/6/11
 * 描述       14:28
 * com.gengy.control
 */
public class RecordService2 extends Service {
    String TAG="RecordService12123";
    private Intent intent = new Intent("com.service.Record");
    private File mPhoneFile;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("onCreate执行了");
        Log.i(TAG, "onCreate: onCreate执行了");

        MediaRecorder  recorder = new MediaRecorder();

//                            recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
//                            recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
//                            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
//        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);//从麦克风采集声音
//        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
//        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB); //内容输出格式
//        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);


//        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
//        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);//存储格式
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);//设置编码

             //输出到缓存目录，此处可以添加上传录音的功能，也可以存到其他位置
                mPhoneFile = new File(Environment.getExternalStorageDirectory(),
                 System.currentTimeMillis() + ".mp3");

//      String path= Environment.getExternalStorageDirectory().getAbsoluteFile();
        Log.i(TAG, "onCreate: "+mPhoneFile.getAbsolutePath());
        recorder.setOutputFile(mPhoneFile.getAbsolutePath());

        try {
            recorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        recorder.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                recorder.stop();
                Log.i(TAG, "run: "+mPhoneFile.getAbsolutePath());
                intent.putExtra("mPhoneFile",mPhoneFile.getAbsolutePath());
                sendBroadcast(intent);
            }
        },1000*30);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("onStartCommand执行了");
        Log.i(TAG, "onCreate: onStartCommand执行了");
        return super.onStartCommand(intent, flags, startId);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onCreate: onDestroy执行了");
        System.out.println("onDestroy执行了");


        intent.putExtra("mPhoneFile",mPhoneFile.getAbsolutePath());
        sendBroadcast(intent);



    }


}
