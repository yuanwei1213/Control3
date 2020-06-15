package com.gengy.control.Untils.Screen;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaCodecInfo;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;

import com.yorhp.recordlibrary.ScreenUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.media.MediaFormat.MIMETYPE_VIDEO_AVC;
import static com.gengy.control.Untils.Screen.ScreenRecorder.AUDIO_AAC;
import static com.gengy.control.Untils.Screen.ScreenRecorder.VIDEO_AVC;


/**
 * @date on 2020/5/11
 * 描述       14:04
 * net.yrom.screenrecorder
 */
public class ScreenRecordUntils {
    private ScreenRecorder mRecorder;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private Activity activity;
    private String TAG="ScreenRecordUntilsScreenRecordUntils";

    public void setmSreenState(ScreenState mSreenState) {
        this.mSreenState = mSreenState;
    }

    public  ScreenState mSreenState;


    public static ScreenRecordUntils getInstance() {
        return ScreenRecordHolder.instance;
    }
    public static void init(){
        Utils.findEncodersByTypeAsync(VIDEO_AVC, infos -> {
            logCodecInfos(infos, VIDEO_AVC);
        });
        Utils.findEncodersByTypeAsync(AUDIO_AAC, infos -> {
            logCodecInfos(infos, AUDIO_AAC);

        });
    }



    static class ScreenRecordHolder {
        private static ScreenRecordUntils instance = new ScreenRecordUntils();
    }



    public void initRecord(Activity activitys, File file,MediaProjection mediaProjection) {
        activity=activitys;
        mediaProjection.registerCallback(mProjectionCallback, new Handler());
        if(mRecorder!=null){
            stopRecordingAndOpenFile(activity);
            return;
        }
        VideoEncodeConfig video = createVideoConfig(activity);

        if (video == null) {

            return;
        }


        mRecorder = newRecorder(mediaProjection, video, null, file);
        if (hasPermissions()) {
            startRecorder();
        } else {
            cancelRecorder();
        }
    }

    private boolean hasPermissions() {
        PackageManager pm = activity.getPackageManager();
        String packageName = activity.getPackageName();
        int granted = (false ? pm.checkPermission(RECORD_AUDIO, packageName) : PackageManager.PERMISSION_GRANTED)
                | pm.checkPermission(WRITE_EXTERNAL_STORAGE, packageName);
        return granted == PackageManager.PERMISSION_GRANTED;
    }
    private void startRecorder() {

        Log.i(TAG, "start: "+hasPermissions()+mRecorder);
        if (mRecorder == null) return;
           mRecorder.start();
        if(null!=mSreenState){
            mSreenState.Start();
        }
        activity.registerReceiver(mStopActionReceiver, new IntentFilter(ACTION_STOP));

    }
    public static final String APPLICATION_ID = "net.yrom.screenrecorder.demo";
    static final String ACTION_STOP = APPLICATION_ID + ".action.STOP";

    private BroadcastReceiver mStopActionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_STOP.equals(intent.getAction())) {
                stopRecordingAndOpenFile(context);
            }
        }
    };


    public void stopRecordingAndOpenFile(Context context) {
        stopRecorder();
        StrictMode.VmPolicy vmPolicy = StrictMode.getVmPolicy();
        try {
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().build());
        } finally {
            StrictMode.setVmPolicy(vmPolicy);
        }
    }

    private void viewResult(File file) {
        Intent view = new Intent(Intent.ACTION_VIEW);
        view.addCategory(Intent.CATEGORY_DEFAULT);
        view.setDataAndType(Uri.fromFile(file), VIDEO_AVC);
        view.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            activity.startActivity(view);
        } catch (ActivityNotFoundException e) {
            // no activity can open this video
        }
    }
    private void cancelRecorder() {
        if (mRecorder == null) return;
//        Toast.makeText(this, getString(R.string.permission_denied_screen_recorder_cancel), Toast.LENGTH_SHORT).show();
        stopRecorder();
    }

    public  Boolean  isStop(){

        return   mRecorder == null;
    }

    private void stopRecorder() {

        if (mRecorder != null) {
            mRecorder.quit();
        }
        mRecorder = null;
        if(null!=mSreenState){
            mSreenState.Stop();
        }

        try {
            activity.unregisterReceiver(mStopActionReceiver);
        } catch (Exception e) {
            //ignored
        }
    }
    private VideoEncodeConfig createVideoConfig(Activity activity) {

        final String codec = "OMX.MTK.VIDEO.ENCODER.AVC";
        if (codec == null) {
            // no selected codec ??
            return null;
        }
        ScreenUtil.getScreenSize(activity);
        int screenRecordBitrate = 32 * 1024 * 1024;
        int width = ScreenUtil.SCREEN_WIDTH;
        int height = ScreenUtil.SCREEN_HEIGHT;;
        int framerate =15;
        int iframe = 1;
        int bitrate =screenRecordBitrate;
        MediaCodecInfo.CodecProfileLevel profileLevel = null;
        return new VideoEncodeConfig(width, height, bitrate,
                framerate, iframe, codec, MIMETYPE_VIDEO_AVC, profileLevel);

    }
    private ScreenRecorder  newRecorder(MediaProjection mediaProjection, VideoEncodeConfig video,
                                        AudioEncodeConfig audio, File output) {
        final VirtualDisplay display = getOrCreateVirtualDisplay(mediaProjection, video);
        ScreenRecorder r = new ScreenRecorder(video, audio, display, output.getAbsolutePath());
        return r;
    }
    private VirtualDisplay getOrCreateVirtualDisplay(MediaProjection mediaProjection, VideoEncodeConfig config) {
        if (mVirtualDisplay == null) {
            mVirtualDisplay = mediaProjection.createVirtualDisplay("ScreenRecorder-display0",
                    config.width, config.height, 1 /*dpi*/,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
                    null /*surface*/, null, null);
        } else {
            Point size = new Point();
            mVirtualDisplay.getDisplay().getSize(size);
            if (size.x != config.width || size.y != config.height) {
                mVirtualDisplay.resize(config.width, config.height, 1);
            }
        }
        return mVirtualDisplay;
    }
    public static File getSavingDir() {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
                "Screenshots");
    }



    /**
     * Print information of all MediaCodec on this device.
     */
    private static void logCodecInfos(MediaCodecInfo[] codecInfos, String mimeType) {
        for (MediaCodecInfo info : codecInfos) {
            StringBuilder builder = new StringBuilder(512);
            MediaCodecInfo.CodecCapabilities caps = info.getCapabilitiesForType(mimeType);
            builder.append("Encoder '").append(info.getName()).append('\'')
                    .append("\n  supported : ")
                    .append(Arrays.toString(info.getSupportedTypes()));
            MediaCodecInfo.VideoCapabilities videoCaps = caps.getVideoCapabilities();
            if (videoCaps != null) {
                builder.append("\n  Video capabilities:")
                        .append("\n  Widths: ").append(videoCaps.getSupportedWidths())
                        .append("\n  Heights: ").append(videoCaps.getSupportedHeights())
                        .append("\n  Frame Rates: ").append(videoCaps.getSupportedFrameRates())
                        .append("\n  Bitrate: ").append(videoCaps.getBitrateRange());
                if (VIDEO_AVC.equals(mimeType)) {
                    MediaCodecInfo.CodecProfileLevel[] levels = caps.profileLevels;

                    builder.append("\n  Profile-levels: ");
                    for (MediaCodecInfo.CodecProfileLevel level : levels) {
                        builder.append("\n  ").append(Utils.avcProfileLevelToString(level));
                    }
                }
                builder.append("\n  Color-formats: ");
                for (int c : caps.colorFormats) {
                    builder.append("\n  ").append(Utils.toHumanReadable(c));
                }
            }
            MediaCodecInfo.AudioCapabilities audioCaps = caps.getAudioCapabilities();
            if (audioCaps != null) {
                builder.append("\n Audio capabilities:")
                        .append("\n Sample Rates: ").append(Arrays.toString(audioCaps.getSupportedSampleRates()))
                        .append("\n Bit Rates: ").append(audioCaps.getBitrateRange())
                        .append("\n Max channels: ").append(audioCaps.getMaxInputChannelCount());
            }
            Log.i("@@@", builder.toString());
        }
    }

    public   void   onDestotry(){
        stopRecorder();
        if (mVirtualDisplay != null) {
            mVirtualDisplay.setSurface(null);
            mVirtualDisplay.release();
            mVirtualDisplay = null;
        }
        if (mMediaProjection != null) {
            mMediaProjection.unregisterCallback(mProjectionCallback);
            mMediaProjection.stop();
            mMediaProjection = null;
        }
    }
    private MediaProjection.Callback mProjectionCallback = new MediaProjection.Callback() {
        @Override
        public void onStop() {
            if (mRecorder != null) {
                stopRecorder();
            }
        }
    };

    public  interface  ScreenState{
        void  Stop();
        void  Start();
    }
}
