package com.gengy.control.Untils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.UUID;

/**
 * @date on 2020/5/29
 * 描述       15:17
 * com.gengy.control.Untils
 */
public class LoadFileUntils {
    private static Context context;
    private static String filePath;
    private static Bitmap mBitmap;
    private static String mSaveMessage = "失败";
    private final static String TAG = "PictureActivity";
    private static ProgressDialog mSaveDialog = null;
    private static int mType;

    public static void donwloadImg(Context contexts, String filePaths,int type) {
        context = contexts;
        filePath = filePaths;
        mType=type;
        mSaveDialog = ProgressDialog.show(context, "保存图片", "图片正在保存中，请稍等...", true);
        new Thread(saveFileRunnable).start();
    }

    private static Runnable saveFileRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                Log.i(TAG, "run: "+filePath);

                if (!TextUtils.isEmpty(filePath)) { //网络图片
//                    // 对资源链接
                    URL url = new URL(filePath);
//                    //打开输入流
                    InputStream inputStream = url.openStream();
                    File dirFile = new File(Environment.getExternalStorageDirectory().getPath());
                    if (!dirFile.exists()) {
                        dirFile.mkdir();
                    }
                    String fileName="";
                    File myCaptureFile = new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/11111/" );
                    switch (mType){
                        case 1:
                        case 4:
                            //图片

                            if(!myCaptureFile.exists()){
            try {
                //按照指定的路径创建文件夹
                myCaptureFile.mkdir();
            } catch (Exception e) {
                Log.i(TAG, "saveFile: "+e.toString());
                // TODO: handle exception
            }
        }
                            fileName = UUID.randomUUID().toString() + ".jpg";
                            mBitmap = BitmapFactory.decodeStream(inputStream);
                            saveFile(mBitmap,new File(myCaptureFile,fileName));
                            mSaveMessage = "图片保存/DCIM/11111/文件夹成功";
                            inputStream.close();
                            break;

                        case 2:
                            //音频
                            fileName = UUID.randomUUID().toString() + ".mp3";
                            writeOs(inputStream,new File(myCaptureFile,fileName));
                            mSaveMessage = "音频保存/DCIM/11111/文件夹成功";
                            break;
                        case 3:
                            //录屏
                            fileName = UUID.randomUUID().toString() + ".mp4";
                            writeOs(inputStream,new File(myCaptureFile,fileName));
//                            mSaveMessage = "录屏保存成功！";
                            mSaveMessage = "录屏保存/DCIM/11111/文件夹成功";
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + mType);
                    }

                }

            } catch (IOException e) {
                mSaveMessage = "图片保存失败！"+e.toString();
                Log.i(TAG, "run: "+e.toString());
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            messageHandler.sendMessage(messageHandler.obtainMessage());
        }
    };


    //将视频保存到本地
    private static  void writeOs(InputStream is,  File file ){
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            byte buf[] = new byte[2*1024];
            int read = 0;
            while ((read = is.read(buf)) != -1){
                os.write(buf,0,read);
            }
            os.flush();
            os.close();
            is.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }



    private static Handler messageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mSaveDialog.dismiss();
            Log.d(TAG, mSaveMessage);
            Toast.makeText(context, mSaveMessage, Toast.LENGTH_SHORT).show();
        }
    };
    /**
     * 保存图片
     * @param bm
     * @throws IOException
     */
    public static void saveFile(Bitmap bm,File myCaptureFile ) throws IOException {
//        File dirFile = new File(Environment.getExternalStorageDirectory().getPath());
//        if (!dirFile.exists()) {
//            dirFile.mkdir();
//        }
//        String fileName = UUID.randomUUID().toString() + ".jpg";
//        File myCaptureFile = new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/11111/" );

//        if(!myCaptureFile.exists()){
//            try {
//                //按照指定的路径创建文件夹
//                myCaptureFile.mkdir();
//            } catch (Exception e) {
//                Log.i(TAG, "saveFile: "+e.toString());
//                // TODO: handle exception
//            }
//        }

        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();

    }

}
