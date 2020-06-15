package com.gengy.control;

import androidx.annotation.NonNull;
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

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
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
import com.gengy.control.Untils.FileUntils;
import com.gengy.control.Untils.HttpStatesUntils;
import com.gengy.control.Untils.NavigationBarUtil;
import com.gengy.control.Untils.RecordService;
import com.gengy.control.Untils.SoundRecordService;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.gengy.control.Untils.ShareKey.BIND_ACCOUNT_ID;
import static com.gengy.control.Untils.ShareKey.ID;

public class Main2Activity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, TakePicture.PictureComplate {

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
//    private PhoneReceiverHelper mPhoneReceiverHelper;
    //来电号码
    private String mCallPhone;
    private File file;
    private MediaRecorder mediaRecorder;


    private WebSocketUtils webSocketUtils;
    private RxPermissions rxPermissions;

    private int mControlType;
    //    private int camaraType = Camera.CameraInfo.CAMERA_FACING_FRONT;
//    private SurfaceView preview;
//    private SurfaceHolder holder;
//    private Camera camera = null;
    private WindowManager wm;
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
    private int mNumberType =1;
    private File mPhoneFile;
//    private TelephonyManager manager;
//    private ServiceConnection serviceConnection;
    private phoneStateReceiver mPhoneStateReceiver;


    //    private ScreenHelp screenRecordHelper = null;



    @Override
    public int intiLayout() {
        return R.layout.activity_control_being;
    }

    @Override
    public void initView() {


        Intent intents= new Intent(Main2Activity.this, RecordService2.class);
        startService(intents);// 启动服务



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


                Log.i(TAG, "onStoponStoponStoponStop: "+file);
////            上传录音   开启wifi上传
//
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
                Log.i(TAG, "onFailuremessage: " + message);
            }

            @Override
            public void onSuccess(String message) {
                super.onSuccess(message);
                Log.i(TAG, "onFailuremessage:onSuccess: " + message);
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

                //开始录像5分钟一次
//                new Handler().postDelayed(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        Boolean isStop=  ScreenRecordUntils.getInstance().isStop();
//                        if(!isStop){
//                            initStopVideoTape();
//                        }
//
//                    }
//                },5*10000*60);
                break;

            case ShareKey.END_RECORD_SCREEN:
                //结束录录像
//                initStopVideoTape();


                break;


            case ShareKey.START_RECORD:
                //开始录音 10分钟一次

                Log.i(TAG, "runrunrunrunrun: initRecord");

                initRecord();
//
                //开始录音 10分钟一次
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                   Boolean isStop=  mAudioRecoderUtils.isStop();
                  Log.i(TAG, "runrunrunrunrun: "+isStop);
                   if(!isStop){
//                       mAudioRecoderUtils.stopRecord();

                       initRecordStop();
                   }

                    }
                },10*1000*60);






                break;
            case ShareKey.END_RECORD:
                //结束录音
                initRecordStop();
                break;

        }
    }


    @SuppressLint("WrongConstant")
    private void initLocations() {

        rxPermissions
                .request(android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_WIFI_STATE,
                        android.Manifest.permission.CHANGE_WIFI_STATE,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
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
    }




    private void initPhotoGraphs(int state) {
        rxPermissions
                .request(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.CAMERA
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
                .request(android.Manifest.permission.WRITE_EXTERNAL_STORAGE
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
                .request(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.RECORD_AUDIO)
                .subscribe(granted -> {
                    Log.i(TAG, "initVideoTape:START_RECORD_SCREEN_CALLBACK_REFUSE " + granted);
                    if (granted) { // Always true pre-M

                        mRecordSend =
                                SharedPreferencesHelper.getInstance(mActivity).getBoolean(ShareKey.RECORD_SEND,
                                        false);
                        if(mRecordSend){
                            if(HttpStatesUntils.WifiConnected(mActivity)){
                                //录ping
                                initRecording();
                            }else {
                                initSendMsg(ShareKey.TART_RECORD_SCREEN_CALLBACK__WIFI, "");
                            }
                        }else {
                            //录ping
                            initRecording();
                        }


                    } else {
                        //        录屏拒绝了权限
                        initSendMsg(ShareKey.START_RECORD_SCREEN_CALLBACK_REFUSE, "");
                    }
                });

    }

    private void initRecord() {

        rxPermissions
                .request(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.RECORD_AUDIO,
                        android.Manifest.permission.WAKE_LOCK
                )
                .subscribe(granted -> {
                    if (granted) { // Always true pre-M
                        //录音

                        mSoundSend =
                                SharedPreferencesHelper.getInstance(mActivity).getBoolean(ShareKey.SOUND_SEND,
                                        false);
                        if(mSoundSend){
                            if(HttpStatesUntils.WifiConnected(mActivity)){
                                initStartAudioRecoder();
                            }else {
                                initSendMsg(ShareKey.START_RECORD_CALLBACK_REFUSE_WIFI, "");
                            }
                        }else {
                            initStartAudioRecoder();
                        }






                    } else {
                        // Oups permission denied
                        //        录音拒绝了权限
                        initSendMsg(ShareKey.START_RECORD_CALLBACK_REFUSE, "");
                    }
                });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        String id=SharedPreferencesHelper.getInstance(mActivity).getString(ShareKey.ID,"");
        if(!TextUtils.isEmpty(id)&&webSocketUtils==null){
            initWifi();
        }

        Log.i(TAG, "runisUserisUserisUseronRestart:334346546 "+id+webSocketUtils);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void initData() {


        String id=SharedPreferencesHelper.getInstance(mActivity).getString(ShareKey.ID, "");
        if(!TextUtils.isEmpty(id)){
            initWifi();
        }


        rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(android.Manifest.permission.CALL_PHONE,
                        android.Manifest.permission.READ_PHONE_STATE,
                        android.Manifest.permission.RECORD_AUDIO,
                        android.Manifest.permission.FOREGROUND_SERVICE,
                        android.Manifest.permission.READ_CALL_LOG,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .subscribe(granted -> {
                    if (granted) { // Always true pre-M、、、、
//                        电话
                        initRecordPhone();
                    } else {
                        // Oups permission denied
                    }
                });


        getNeedSoundNuber();
        SurfaceView mySurfaceView = findViewById(R.id.sf);
        mTakePicture=new TakePicture(mActivity,mySurfaceView);
        mTakePicture.setmPictureComplate(this);

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





    }

    private void initRecordPhone() {

         mPhoneStateReceiver= new phoneStateReceiver();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("com.service.RECEIVER");
        intentFilter.addAction("com.service.Record");
//        IntentFilter intentFilter=new IntentFilter("com.service.RECEIVER");
        registerReceiver(mPhoneStateReceiver,intentFilter);

        Intent i = new Intent(this, SoundRecordService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(i);
        } else {
            startService(i);
        }



    }


    public  class  phoneStateReceiver extends BroadcastReceiver{

        private MediaRecorder recorder;

        @Override
        public void onReceive(Context context, Intent intent) {

            String cmdAction=intent.getAction();//获取当前广播的action
            if(cmdAction.equals("com.service.Record")){
                //录音
                String path=intent.getStringExtra("mPhoneFile");
                Log.i(TAG, "onReceiveonReceiveonReceiveonReceive: "+path);
                Log.i(TAG, "onReceiveonReceiveonReceiveonReceive: "+new File(path));
                initCallUpload(new File(path));

                return;
            }


            int state=intent.getIntExtra("state",0);

            String phone=intent.getStringExtra("phoneNumber");

            mCallPhone=phone;
            Log.i(TAG, "onCallStateChangedonCallStateChanged:onReceive"+state+"phone="+mPhoneFile);
            switch (state){
                case TelephonyManager.CALL_STATE_IDLE:
//                    挂断
                    if(null==recorder){
                        return;
                    }

                    recorder.stop(); //停止刻录
                    recorder.release(); //释放资源


                    if(null==mPhoneFile){
                        return;
                    }

//                    initCallUpload(mPhoneFile);

                    Intent intents2= new Intent(Main2Activity.this, RecordService2.class);
                    stopService(intents2);// 启动服务

                    //上传音频文件


                    break;

                case TelephonyManager.CALL_STATE_OFFHOOK:



                    for (PhoneNetWorkEntity entity : mListData) {

                        if (entity.phone.equals(mCallPhone)) {



//                            initSavePhone();

//                    接通


//                            recorder = new MediaRecorder();
//
////                            recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
////                            recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
////                            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
//                            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);//从麦克风采集声音
////      \
//                            recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB); //内容输出格式
//                            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//                            //输出到缓存目录，此处可以添加上传录音的功能，也可以存到其他位置
//                            mPhoneFile = new File(Environment.getExternalStorageDirectory(),
//                                    mCallPhone + System.currentTimeMillis() + ".mp3");
//                            recorder.setOutputFile(mPhoneFile.getAbsolutePath());
//
//                            try {
//                                recorder.prepare();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                            try {
//                                Thread.sleep(4);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            recorder.start();




                            return;
                        }

                    }



                    break;
                case TelephonyManager.CALL_STATE_RINGING:
//                    来电号码
                    mNumberType=2;
//
//                    System.out.println("响铃:来电号码"+phoneNumber);
//                    Log.i(TAG, "onPhoneOutCallonPhoneOutCall来电号码: "+phoneNumber);
                    //输出来电号码
                    break;

            }


            Log.i(TAG, "onCallStateChangedonCallStateChanged: "+state+"phone"+phone);


        }
    }

    private void getNeedSoundNuber() {
        String id = SharedPreferencesHelper.getInstance(mActivity).getString(ID, "");
        Http.with(mActivity).setObservable(Http.getApiService(ApiService.class).getContacts(id,
                1 + ""))
                .setDataListener(new HttpCallBack() {
                    @Override
                    public void netOnSuccess(Map<String, Object> datas) {
                        mListData = JsonParse.getList(datas,
                                "data", PhoneNetWorkEntity.class);

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
                                    webSocketUtils.contentWfi();

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
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "OverrideOverrideonStop: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "OverrideOverrideononPause: ");
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.i(TAG, "OverrideOverrideononDestroy ");
        webSocketUtils.closeHeartbeat();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            unregisterReceiver(mPhoneStateReceiver);
        }

        ScreenRecordUntils.getInstance().onDestotry();
        ScreenShotUtil.getInstance().destroy();
        if(null==mVideoFile||TextUtils.isEmpty(mVideoFile.getPath())){
            return;
        }
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
        if(!dir.exists()){
            dir.mkdir();
        }
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

        super.onActivityResult(requestCode, resultCode, data);
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
                //w问题
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
//        mPhoneReceiverHelper = new PhoneReceiverHelper(mActivity);
//        mPhoneReceiverHelper.setOnListener(new PhoneReceiver.OnPhoneListener() {
//            @Override
//            public void onPhoneOutCall(String number,TelephonyManager manager) {
//                //去电
//
//                mNumberType=1;
//                mCallPhone=number;

//去电
//                incomeNumber = number;

//            }
//
//            @Override
//            public void onPhoneStateChange(String number) {
                //来电监听
//                initGetNumber(manager);



//                switch (state) {
//                    case TelephonyManager.CALL_STATE_RINGING:
//
////                        来电
//                        break;
//                    case TelephonyManager.CALL_STATE_OFFHOOK:
////                        Log.i(TAG, "onPhoneOutCallonPhoneOutCall: "+number);
//                        //通讯录录音的号码
//
//                        initGetSound();
//                        break;
//
//                    case TelephonyManager.CALL_STATE_IDLE:  //挂掉电话
//                        if (mediaRecorder != null) {
//                            mediaRecorder.stop();
//                            mediaRecorder.release();
//                            mediaRecorder = null;
//                            //上传录音   开启wifi
//                            initUplodImage(file);
////                            if (mSoundSend) {
////                                if (HttpStatesUntils.WifiConnected(mActivity)) {
////                                    initUplodImage(file);
////                                }
////
////
////                            } else {
////                                initUplodImage(file);
////                            }
//
//
//                        }
//
//                        break;
//                }

//
//            }
//
//            @Override
//            public void onPhoneStateChange(int state, TelecomManager number) {
//
//            }
//        });
//        mPhoneReceiverHelper.register();
    }

//    private void initGetNumber(TelephonyManager manager) {
//        manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE|PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
////        manager.listen(listener, PhoneStateListener.NEW_OUTGOING_CALL);
//
//    }

//    PhoneStateListener listener=new PhoneStateListener(){
//        @Override
//        public void onCallStateChanged(int state, String phoneNumber) {
//            super.onCallStateChanged(state, phoneNumber);
//            Log.i(TAG, "onCallStateChanged: state"+state+"phoneNumber"+phoneNumber+"mPhoneFile"+mPhoneFile);
//            switch(state){
//                case TelephonyManager.CALL_STATE_IDLE:
//                    if(null==mPhoneFile){
//                        return;
//                    }
//                    Log.i(TAG, "onPhoneOutCallonPhoneOutCall挂断:mNumberType "+mNumberType);
//                    Log.i(TAG, "onPhoneOutCallonPhoneOutCall挂断: "+phoneNumber);
////                    initRecordStop
//                    initRecordStop();
//                    //上传音频文件
//                    initCallUpload(mPhoneFile);
//                    break;
//                case TelephonyManager.CALL_STATE_OFFHOOK:
//                    mNumberType=2;
//                    Log.i(TAG, "onPhoneOutCallonPhoneOutCall接听号码: "+phoneNumber+mCallPhone);
//                    if(TextUtils.isEmpty(phoneNumber)){
//                        mNumberType=1;
//                    }
//
////                    判断是否需要录音
//
//                    //  接听
//                    mCallPhone="10000";
//                    for (PhoneNetWorkEntity entity : mListData) {
//                        Log.i(TAG, "initSavePhoneinitSavePhone: "+entity.phone);
//                        Log.i(TAG, "initSavePhoneinitSavePhone: "+mCallPhone);
//                        if (entity.phone.equals(mCallPhone)) {
//
////                            mAudioRecoderUtils.startRecord();
//                            initSavePhone();
//                            return;
//                        }
//
//                    }
//
//
//
//                    break;
//                case TelephonyManager.CALL_STATE_RINGING:
//
//                    System.out.println("响铃:来电号码"+phoneNumber);
//                    Log.i(TAG, "onPhoneOutCallonPhoneOutCall来电号码: "+phoneNumber);
//                    //输出来电号码
//                    break;
//            }
//        }
//    };

    private void initCallUpload(File file) {
        RequestBody requestBodyFile = RequestBody.create(MediaType.parse("multipart/form-data"),
                file);
        MultipartBody.Part bodyFile = MultipartBody.Part.createFormData("file", file.getName(),
                requestBodyFile);


        String descriptionString =
                SharedPreferencesHelper.getInstance(mActivity).getString(ShareKey.ID, "0");

        RequestBody id =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), descriptionString);
        RequestBody phone =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), mCallPhone);
//        （1：uphone给cphone打电话 ，2：uphone接cphone的电话）
        RequestBody type = RequestBody.create(
                MediaType.parse("multipart/form-data"), mNumberType+"");



        Http.with(mActivity).hideLoadingDialog()
                .setObservable(Http.getApiService(ApiService.class).uploadResourceRecord(id,phone,type, bodyFile)).setDataListener(new HttpCallBack() {

            @Override
            public void netOnFailure(Throwable ex) {
                super.netOnFailure(ex);
                Log.i(TAG, "netOnFailurenetOnFailure: "+ex.toString());
            }

            @Override
            public void netOnSuccess(Map<String, Object> data) {
//                mPhoneFile=null;
                FileUntils.deleteDir(mPhoneFile.getPath());
                Log.i(TAG, "netOnFailurenetOnFailure: "+data);


            }
        });



    }



    private void initSavePhoneinitSavePhone() {
        mPhoneFile = new File(Environment.getExternalStorageDirectory(),
                mCallPhone + System.currentTimeMillis() + ".mp3");
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);   //获得声音数据源

        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);   //
        // 按3gp格式输出
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(mPhoneFile.getAbsolutePath());   //输出文件
        try {
            mediaRecorder.prepare();    //准备
        } catch (IOException e) {
            Log.i(TAG, "initSavePhoneinitSavePhone: "+e.toString());
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
                    case ShareKey. START_RECORD_CALLBACK:
                        initSendMsg(ShareKey.END_RECORD_CALLBACK_URL, url);
                        break;
                    case ShareKey.END_RECORD_SCREEN:
                    case ShareKey. START_RECORD_SCREEN_CALLBACK:
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


                break;
            case R.id.rb3:
                currentFragment = 2;

                break;
        }
        viewPager.setCurrentItem(currentFragment);
    }


    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            mLocationClient.stop();
            Log.i(TAG, "onReceiveLocationonReceiveLocation: "+location);
            String addr = location.getAddrStr();    //获取详细地址信息
            if(TextUtils.isEmpty(addr)){
                initSendMsg(ShareKey.END_LOCATION_CALLBACK_FAIL, "");
                return;
            }

            String id = SharedPreferencesHelper.getInstance(mActivity).getString(ID, "");
            Http.with(mActivity).setShowLoadingDialog(false).setObservable(Http.getApiService(ApiService.class).sendLocation(id,
                    addr))
                    .setDataListener(new HttpCallBack() {
                        @Override
                        public void netOnSuccess(Map<String, Object> data) {
                            //结束成功回调
                            initSendMsg(ShareKey.END_LOCATION_CALLBACK,     location.getLatitude()+","+location.getLongitude()+","+ location.getAddrStr());
                        }
                    });


        }
    }
}
