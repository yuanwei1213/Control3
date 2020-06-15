package com.gengy.control.Untils.Screen;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;


import com.gengy.control.BeingControlActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * @date on 2020/5/11
 * 描述       16:10
 * net.yrom.screenrecorder
 */
public class ScreenShotUntils {
//    private static final int SCREEN_SHOT = 0;
    MediaProjection mediaProjection;
    MediaProjectionManager projectionManager;
    ImageReader imageReader;
    private Bitmap bitmap;
    private Activity mActivity;
    int width;
    int height;
    int dpi;

    public static ScreenShotUntils getInstance() {
        return ScreenRecordHolder.instance;
    }
    static class ScreenRecordHolder {
        private static ScreenShotUntils instance = new ScreenShotUntils();
    }

    public  void init(Activity activity){

        mActivity=activity;
        projectionManager = (MediaProjectionManager) activity.getSystemService(activity.MEDIA_PROJECTION_SERVICE);
        DisplayMetrics metric = new DisplayMetrics();
        mActivity. getWindowManager().getDefaultDisplay().getMetrics(metric);
        width = metric.widthPixels;
        height = metric.heightPixels;
        dpi = metric.densityDpi;

    }

    public  Boolean imageReaderIsInit(){
        if(null==imageReader){
            return true;
        }

        return  false;
    }


    public File startCapture(File file) {





        SystemClock.sleep(1000);
        Image image = imageReader.acquireNextImage();

        if (image == null) {
            Log.e("11", "image is null.");
            return null;
        }
        int width = image.getWidth();
        int height = image.getHeight();
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        image.close();


        Log.i("233", "startCapturestartCapturestartCapturebitmap: "+bitmap);

        return  compressImage(bitmap,file);
    }

    public void StartScreenShot( ) {
        mActivity.startActivityForResult(projectionManager.createScreenCaptureIntent(),
                BeingControlActivity.SCREEN_SHOT);
    }

public File setUpVirtualDisplay(MediaProjection mediaProjections,File  file) {
       mediaProjection=mediaProjections;
        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 1);
        mediaProjection.createVirtualDisplay("ScreenShout",
                width,height,dpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader.getSurface(),null,null);
     return startCapture(file);
    }
    private File compressImage(Bitmap bitmap,File file) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
//        File file = new File(Environment.getExternalStorageDirectory(),  System.currentTimeMillis() + ".png");

        try {
            FileOutputStream fos = new FileOutputStream(file);
            try {
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();
            } catch (IOException e) {

                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }

        return file;


    }


}
