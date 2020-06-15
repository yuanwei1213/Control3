package com.gengy.control;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.yorhp.recordlibrary.ScreenShotUtil;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @date on 2020/4/30
 * 描述       14:40
 * com.gengy.control.Untils
 */
public class ScreenRecordsActivity extends AppCompatActivity {
    public static final int REQUEST_MEDIA_PROJECTION = 18;
    public static boolean isScrennShot;

    public ScreenRecordsActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestCapturePermission();
    }

    @RequiresApi(
            api = 21
    )
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 18) {
            if (isScrennShot) {
//                ScreenShotUtil.permissionResult(resultCode, data);
            } else {
//                ScreenRecordUtils.permissionResult(resultCode, data);
            }

            this.finish();
        }

    }

    private void requestCapturePermission() {
        if (Build.VERSION.SDK_INT >= 21) {
//            if(ScreenRecordUtils.mMediaProjection==null){
//                if (isScrennShot) {
////                this.startActivityForResult(ScreenShotUtils.mMediaProjectionManager.createScreenCaptureIntent(), 18);
//                } else {
////                    this.startActivityForResult(ScreenRecordUtils.mMediaProjectionManager.createScreenCaptureIntent(), 18);
//                }

//            }



        }
    }

    protected void onDestroy() {
        super.onDestroy();
        ScreenShotUtil.isInit = true;
        Log.e("ScreenRecordActivity：", "onDestroy");
    }
}
