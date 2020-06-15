package com.gengy.control.Control.ControlModular;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.gengy.control.Base.BaseFragment;
import com.gengy.control.ImageActivity;
import com.gengy.control.MapActivity;
import com.gengy.control.R;
import com.gengy.control.Untils.DialogUntils;
import com.gengy.control.Untils.ShareKey;
import com.gengy.control.Untils.SharedPreferencesHelper;
import com.gengy.control.Untils.ToastUtils;
import com.gengy.control.Untils.WebSocketUtils;
import com.gengy.control.http.ApiService;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bluemobi.dylan.http.Http;
import cn.bluemobi.dylan.http.HttpCallBack;
import cn.bluemobi.dylan.http.JsonParse;

import static com.gengy.control.Untils.ShareKey.BIND_ACCOUNT;
import static com.gengy.control.Untils.ShareKey.BIND_ACCOUNT_ID;
import static com.gengy.control.Untils.ShareKey.END_RECORD;
import static com.gengy.control.Untils.ShareKey.END_RECORD_SCREEN;
import static com.gengy.control.Untils.ShareKey.ID;
import static com.gengy.control.Untils.ShareKey.START_RECORD;
import static com.gengy.control.Untils.ShareKey.START_RECORD_CALLBACK;
import static com.gengy.control.Untils.ShareKey.START_RECORD_SCREEN;
import static com.gengy.control.Untils.ShareKey.START_SCREEN_SCREEN;
import static com.gengy.control.Untils.ShareKey.TOKEN;

/**
 * @date on 2019/12/25
 * 描述       15:11
 * com.gengy.control.Control.ControlModular
 */
public class ControlFragment extends BaseFragment implements View.OnClickListener {
    @BindView(R.id.tv_sound_record)
    TextView tvSoundRecord;
    @BindView(R.id.tv_sound_record_con)
    TextView tvSoundRecordCon;
    @BindView(R.id.tv_videotape)
    TextView tvVideotape;
    @BindView(R.id.tv_videotape_con)
    TextView tvVideotapeCon;
    @BindView(R.id.tv_screenshot)
    TextView tvScreenshot;
    @BindView(R.id.tv_screenshot_con)
    TextView tvScreenshotCon;
    @BindView(R.id.tv_photograph)
    TextView tvPhotograph;
    @BindView(R.id.tv_photograph_con)
    TextView tvPhotographCon;
    @BindView(R.id.rb1)
    RadioButton rb1;
    @BindView(R.id.rb2)
    RadioButton rb2;
    @BindView(R.id.rg)
    RadioGroup rg;
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.tv_location_con)
    TextView tvLocationCon;
    @BindView(R.id.tv_bind_account)
    TextView tvBindAccount;

    private Dialog mDialogBind;
    private EditText mEditId, mEditPass;
    private String mBindAccount;
    //控制另一端操作状态码
    private int mControlType = 0;
    private JWebSocketClient client;
    private Timer timer;
    private WebSocketUtils webSocketUtils;
    int mRecordTime=0;
    private int mPhotoType=0;
    String mContentImageScreennUrl="",mContentImagePhtotUrl="";
    private String mContentSoundUrl,mContentVideodUrl;
    private  int mCallBackType=0;
    private String mLocationContent;

    @Override
    protected int setLayoutResourceID() {
        return R.layout.frag_control;
    }

    @Override
    public void onStart() {
        super.onStart();
        mBindAccount =
                SharedPreferencesHelper.getInstance(mActivity).getString(BIND_ACCOUNT, "");
        tvBindAccount.setText("绑定账号  " + mBindAccount);
    }

    @Override
    protected void initView() {



        action_back.setVisibility(View.INVISIBLE);
        action_title.setText("远程控制");
        action_right.setImageDrawable(getResources().getDrawable(R.mipmap.control_tips));
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.rb1:
                        mPhotoType=0;
                        break;
                    case R.id.rb2:
                        mPhotoType=1;
                        break;
                }
            }
        });
    }

    @Override
    protected void initData() {

        String id=SharedPreferencesHelper.getInstance(mActivity).getString(ShareKey.ID, "");
        if(!TextUtils.isEmpty(id)){
            initSocket();
        }

        timer=new Timer();
    }


    @Override
    public void onResume() {
        super.onResume();
        String id=SharedPreferencesHelper.getInstance(mActivity).getString(ShareKey.ID, "");
        Log.i(TAG, "runisUserisUserisUseronRestart:onResume "+id+webSocketUtils);
        if(!TextUtils.isEmpty(id)&&null==webSocketUtils){
            initSocket();
        }
    }

    private void initSocket() {

        webSocketUtils =  WebSocketUtils.getInstance(mActivity);
        webSocketUtils.content();
        ;
        webSocketUtils.openHeartbeat();
        ;
        webSocketUtils.setOnMessageSendCallback(new WebSocketUtils.OnMessageSendCallback() {
            @Override
            public void onMessage(String message) {
                super.onMessage(message);
                Log.i(TAG, "onMessage:messagemessage: "+message);
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


                }else  if(message.contains(ShareKey.SEND_CONTENT)){
                    JSONObject  object = null;
                    try {
                        object = new JSONObject(message);
                        int type = object.getInt(ShareKey.SEND_CONTENT);
                        mCallBackType=type;
                        JSONObject finalObject = object;
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initControlUI(type, finalObject);
                            }
                        });




                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onSuccess(String message) {
                super.onSuccess(message);
                Log.i(TAG, "onSuccess: onSuccess");
            }

            @Override
            public void onFailure(String message) {
                super.onFailure(message);
                Log.i(TAG, "onSuccess: onFailure");
            }
        });
    }

    private void initControlUI(int type,JSONObject object) {
        mRecordTime=0;
        switch (type){

            // 开始拍照回调
            case ShareKey.STARTS_PHOTO_CALLBACK:
                tvPhotograph.setText("结束拍照");
                tvPhotographCon.setText("拍照中...");
                setDrawLeft(tvPhotograph,R.mipmap.control_stop);
                break;

            // 开始截图回调 失败
            case ShareKey.STARTS_SPHOTO_CALLBACK_FAIL:
                tvPhotograph.setText("开始拍照");
                tvPhotographCon.setText("拍照失败.");
                setDrawLeft(tvPhotograph,R.mipmap.control_start);
                break;
            // 拍照回调 成功
            case ShareKey.END__PHOTO_CALLBACK:
                tvPhotograph.setText("开始拍照");
                tvPhotographCon.setText("");
                setDrawLeft(tvPhotograph,R.mipmap.control_start);
                break;
            // 拍照回调 上传成功
            case ShareKey.END__PHOTO_CALLBACK_URL:
                tvPhotograph.setText("开始拍照");
                tvPhotographCon.setText("立即查看");

                try {
                    mContentImagePhtotUrl=object.getString(ShareKey.SEND_CONTENT_2);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                setDrawLeft(tvPhotograph,R.mipmap.control_start);
                break;






            // 开始截图回调
            case ShareKey.STARTS_SCREEN_CALLBACK:
                tvScreenshot.setText("结束截图");
                tvScreenshotCon.setText("截图中...");
                setDrawLeft(tvScreenshot,R.mipmap.control_stop);
                break;
            // 开始截图回调 失败
            case ShareKey.STARTS_SCREEN_CALLBACK_FAIL:
                tvScreenshot.setText("开始截图");
                tvScreenshotCon.setText("截图失败.");
                setDrawLeft(tvScreenshot,R.mipmap.control_start);
                break;
            // 截图回调 成功
            case ShareKey.END__SCREEN_CALLBACK:
                tvScreenshot.setText("开始截图");
                tvScreenshotCon.setText("");

                setDrawLeft(tvScreenshot,R.mipmap.control_start);
                break;

            // 截图回调上传 成功
            case ShareKey.END__SCREEN_CALLBACK_URL:
                tvScreenshot.setText("开始截图");
                tvScreenshotCon.setText("立即查看");
                try {
                    mContentImageScreennUrl=object.getString(ShareKey.SEND_CONTENT_2);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setDrawLeft(tvScreenshot,R.mipmap.control_start);
                break;



            // 开始定位回调
            case  ShareKey.START_LOCATION_CALLBACK:
                tvLocation.setText("结束定位");
                tvLocationCon.setText("定位中...");
                tvLocationCon.setOnClickListener(null);
                setDrawLeft(tvLocation,R.mipmap.control_stop);
                break;
            // 结束定位失败回调
            case  ShareKey.END_LOCATION_CALLBACK_FAIL:
                tvLocation.setText("开始定位");
                tvLocationCon.setText("定位失败");
                tvLocationCon.setOnClickListener(null);
                setDrawLeft(tvLocation,R.mipmap.control_start);
                break;

            // 结束定位成功回调
            case  ShareKey.END_LOCATION_CALLBACK:
                try {
                     mLocationContent=  object.getString(ShareKey.SEND_CONTENT_2);
                    Log.i(TAG, "initControlUIcontentcontent: "+mLocationContent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                tvLocation.setText("开始定位");
                tvLocationCon.setText("定位成功,立即查看");
                tvLocationCon.setOnClickListener(this);
                setDrawLeft(tvLocation,R.mipmap.control_start);
                break;


            // 结束 始录ping上传成功回调
            case ShareKey.END_RECORD_SCREEN_CALLBACK_URL:
                if(timer!=null){
                    timer.cancel();
                }
                try {
                    mContentVideodUrl=object.getString(ShareKey.SEND_CONTENT_2);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //结束录音
                tvVideotapeCon.setText("立即查看");
                tvVideotape.setText("开始录像");
                setDrawLeft(tvVideotape,R.mipmap.control_start);
                break;



            // 结束 始录ping回调
            case ShareKey.END_RECORD_SCREEN_CALLBACK:
                if(timer!=null){
                    timer.cancel();
                }
                //结束录音
                tvVideotapeCon.setText("上传中");
                tvVideotape.setText("开始录像");
                setDrawLeft(tvVideotape,R.mipmap.control_start);
                break;

            case ShareKey.START_RECORD_SCREEN_CALLBACK_REFUSE:
                //权限拒绝
                tvVideotapeCon.setText("录像权限没有打开");
                break;


            case ShareKey.TART_RECORD_SCREEN_CALLBACK__WIFI:
                //没有权限录屏没有wifi
                tvVideotapeCon.setText("对方非WiFi！");
                break;

            case ShareKey.START_RECORD_SCREEN_CALLBACK:
//               开始 始录ping回调
                tvVideotapeCon.setText("开始录像");
                tvVideotape.setText("结束录像");
                setDrawLeft(tvVideotape,R.mipmap.control_stop);
                timer=new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mRecordTime++;
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvVideotapeCon.setText("录屏中..."+mRecordTime+"s");
                            }
                        });
                    }
                },1000,1000);

                break;
            case ShareKey.START_RECORD_CALLBACK_REFUSE:
                //没有权限
                tvSoundRecordCon.setText("录音权限没有打开");
                break;
            case ShareKey.START_RECORD_CALLBACK_REFUSE_WIFI:
                //没有权限
                tvSoundRecordCon.setText("对方非WiFi！");
                break;



            case START_RECORD_CALLBACK:

                if(tvSoundRecordCon.getText().equals("开始录音")){
                    return;
                }
                if(timer!=null){

                    timer=null;
                }
                //开始录音
                tvSoundRecordCon.setText("开始录音");
                tvSoundRecord.setText("结束录音");
                setDrawLeft(tvSoundRecord,R.mipmap.control_stop);
                timer=new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mRecordTime++;
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvSoundRecordCon.setText("录音中..."+mRecordTime+"s");
                            }
                        });
                    }
                },1000,1000);

                break;
            case ShareKey.END_RECORD_CALLBACK:
                if(timer!=null){
                    timer.cancel();
                }
                setDrawLeft(tvSoundRecord,R.mipmap.control_start);
                //结束录音
                tvSoundRecordCon.setText("已结束");
                tvSoundRecord.setText("开始录音");
                break;
//上传成功回调
            case ShareKey.END_RECORD_CALLBACK_URL:
                if(timer!=null){
                    timer.cancel();
                }
                setDrawLeft(tvSoundRecord,R.mipmap.control_start);
                //结束录音
                try {
                    mContentSoundUrl=object.getString(ShareKey.SEND_CONTENT_2);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                tvSoundRecordCon.setText("立即查看");
                tvSoundRecord.setText("开始录音");
                break;




        }
    }

    private void setDrawLeft(TextView tvVideotape,int resource) {

        Drawable drawable= getResources().getDrawable(resource);
        /// 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        tvVideotape.setCompoundDrawables(drawable,null,null,null);

    }


    @Override
    public void onDestroy() {
        if(null!=timer){
            timer.cancel();
        }
        super.onDestroy();
    }

    public class JWebSocketClient extends WebSocketClient {

        public JWebSocketClient(URI serverUri) {
            super(serverUri);
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            Log.i("JWebSClientService", "onOpen()");
        }

        @Override
        public void onMessage(String message) {
            Log.i("JWebSClientService", "onMessage()");
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            Log.i("JWebSClientService", "onClose()");


        }

        @Override
        public void onError(Exception ex) {
            Log.i("JWebSClientService", "onMessage()" + ex.toString());
        }
    }

//    mContentImageUrl
    @OnClick({R.id.tv_sound_record, R.id.tv_videotape, R.id.tv_screenshot, R.id.tv_photograph, R.id.tv_photograph_con,
            R.id.tv_videotape_con,
            R.id.tv_location, R.id.img_right, R.id.stv_bind,R.id.tv_screenshot_con,R.id.tv_sound_record_con})
    public void onViewClicked(View view) {

        String ids = SharedPreferencesHelper.getInstance(mActivity).getString(ID, "");
        String cids = SharedPreferencesHelper.getInstance(mActivity).getString(BIND_ACCOUNT_ID, "");
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("type", "say");
            jsonObject.put("avatar", "13213");
            jsonObject.put("name", "15246220315");
            jsonObject.put("id", ids);
            jsonObject.put("cid", cids);
        } catch (JSONException e) {
            e.printStackTrace();
        }



        switch (view.getId()) {
//            mContentSoundUrl

            case R.id.   tv_sound_record_con:
                //录音mContentSoundUrl
                String url =mContentSoundUrl;//示例，实际填你的网络视频链接
                String extension = MimeTypeMap.getFileExtensionFromUrl(url);
                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                Intent mediaIntent = new Intent(Intent.ACTION_VIEW);
                mediaIntent.setDataAndType(Uri.parse(url), mimeType);
                startActivity(mediaIntent);

                break;


            case R.id.tv_videotape_con:
                //视频
//                String url =mContentSoundUrl;//示例，实际填你的网络视频链接
                Log.i(TAG, "onViewClickedmContentVideodUrl: "+mContentVideodUrl);
                String extensionVideo = MimeTypeMap.getFileExtensionFromUrl(mContentVideodUrl);
                String mimeTypeVideo = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extensionVideo);
                Intent mediaIntentVideo = new Intent(Intent.ACTION_VIEW);
                mediaIntentVideo.setDataAndType(Uri.parse(mContentVideodUrl), mimeTypeVideo);
                startActivity(mediaIntentVideo);
//                bundle.putString("imgurl",mContentImagePhtotUrl);
//                actionStart(ImageActivity.class,bundle);
                break;

            case R.id. tv_photograph_con:
                bundle.putString("imgurl",mContentImagePhtotUrl);
                actionStart(ImageActivity.class,bundle);
                break;
            case R.id. tv_screenshot_con:
                bundle.putString("imgurl",mContentImageScreennUrl);
                actionStart(ImageActivity.class,bundle);
                break;
            case R.id.stv_bind:
                mDialogBind = DialogUntils.getInstance().createCenterDialog(mActivity,
                        R.layout.dialog_bind);
                mDialogBind.show();
                mDialogBind.findViewById(R.id.tv_cancel).setOnClickListener(this);
                mDialogBind.findViewById(R.id.tv_sure).setOnClickListener(this);
                mEditId = mDialogBind.findViewById(R.id.edit_id);
                mEditPass = mDialogBind.findViewById(R.id.edit_pass);
                break;
            case R.id.img_right:
                Dialog dialog = DialogUntils.getInstance().createCenterDialog(mActivity,
                        R.layout.dialog_tips);
                dialog.show();

                break;

            case R.id.tv_sound_record:
                //判断vip是否过期 。进行录音
//                userIsExpire():
                try {

                    if(tvSoundRecord.getText().toString().contains("开始录音")){
                        jsonObject.put(ShareKey.SEND_CONTENT, START_RECORD);

                        initListenState(START_RECORD);
                    }else {
                        jsonObject.put(ShareKey.SEND_CONTENT, END_RECORD);
                        initListenState(END_RECORD);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                webSocketUtils.sendChatMessage(jsonObject.toString());

                break;
            case R.id.tv_videotape:
                //录像
                mControlType = 2;



                try {
                    if(tvVideotape.getText().toString().contains("开始录像")){
                        jsonObject.put(ShareKey.SEND_CONTENT, START_RECORD_SCREEN);
                        initListenState(START_RECORD_SCREEN);
                    }else {
                        jsonObject.put(ShareKey.SEND_CONTENT, END_RECORD_SCREEN);
                        initListenState(END_RECORD_SCREEN);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                webSocketUtils.sendChatMessage(jsonObject.toString());
                break;
            case R.id.tv_screenshot:

                try {
                    if(tvScreenshot.getText().toString().contains("开始截图")){
                        jsonObject.put(ShareKey.SEND_CONTENT, START_SCREEN_SCREEN);
                        initListenState(START_SCREEN_SCREEN);
                    }else {
                        jsonObject.put(ShareKey.SEND_CONTENT, ShareKey.END_SCREEN_SCREEN);
                        initListenState(ShareKey.END_SCREEN_SCREEN);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "onMessage:messagemessage:: "+jsonObject.toString());

                webSocketUtils.sendChatMessage(jsonObject.toString());

                break;
            case R.id.tv_photograph:

                try {
                    if(tvPhotograph.getText().toString().contains("开始拍照")){
                        jsonObject.put(ShareKey.SEND_CONTENT, ShareKey.START_PHOTO_POST);
                        initListenState(ShareKey.START_PHOTO_POST);
                        if(mPhotoType==0){
                            jsonObject.put(ShareKey.SEND_CONTENT, ShareKey.START_PHOTO_PREFIX);
                            initListenState(ShareKey.START_PHOTO_PREFIX);
                        }

                    }else {
                        jsonObject.put(ShareKey.SEND_CONTENT, ShareKey.END_PHOTO_);
                        initListenState(ShareKey.END_PHOTO_);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                webSocketUtils.sendChatMessage(jsonObject.toString());


                break;
            case R.id.tv_location:
                try {
                    if(tvLocation.getText().toString().contains("开始定位")){
                        jsonObject.put(ShareKey.SEND_CONTENT,ShareKey.START_LOCATION);
                        initListenState(ShareKey.START_LOCATION);
                    }else {
                        jsonObject.put(ShareKey.SEND_CONTENT, ShareKey.END_LOCATION);
                        initListenState(ShareKey.END_LOCATION);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                webSocketUtils.sendChatMessage(jsonObject.toString());
 

                break;
        }
    }

    private void initListenState(int sendType) {

        //监听返回状态
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                //要延时的程序  如果没有返回，则发送失败
                switch (sendType){
//                    录音
                    case START_RECORD:
                    case END_RECORD:
                        if(mCallBackType==START_RECORD||mCallBackType==END_RECORD){
                            if(null!=timer){
                                timer.cancel();
                            }
                            tvSoundRecordCon.setText("对方不在线！");
                        }
                        break;

//                    录像
                    case START_RECORD_SCREEN:
                    case END_RECORD_SCREEN:
                        if(mCallBackType==START_RECORD_SCREEN||mCallBackType==END_RECORD_SCREEN){
                            if(null!=timer){
                                timer.cancel();
                            }
                            tvVideotapeCon.setText("对方不在线！");
                        }
                        break;
                    //   截图
                    case START_SCREEN_SCREEN:
                    case ShareKey.END_SCREEN_SCREEN:
                        if(mCallBackType==START_SCREEN_SCREEN||mCallBackType== ShareKey.END_SCREEN_SCREEN){
                            if(null!=timer){
                                timer.cancel();
                            }
                            tvScreenshotCon.setText("对方不在线！");
                        }
                        break;
                    //  拍照
                    case ShareKey.START_PHOTO_PREFIX:
                    case ShareKey.START_PHOTO_POST:
                    case ShareKey.END_PHOTO_:
                        if(mCallBackType==ShareKey.START_PHOTO_PREFIX||mCallBackType==ShareKey.START_PHOTO_POST||mCallBackType== ShareKey.END_PHOTO_){
                            if(null!=timer){
                                timer.cancel();
                            }
                            tvPhotographCon.setText("对方不在线！");
                        }
                        break;
                    //   定位
                    case ShareKey.START_LOCATION:
                    case ShareKey.END_LOCATION:
                        Log.i(TAG, "initListenState:sendType 3434"+mCallBackType+"START_LOCATION"+ShareKey.START_LOCATION);
                        if(mCallBackType==ShareKey.START_LOCATION||mCallBackType== ShareKey.END_LOCATION){
                            if(null!=timer){
                                timer.cancel();
                            }
                            tvLocationCon.setText("对方不在线！");
                        }
                        break;



                }
                mCallBackType=0;

            }
        },8000); //8000为毫秒单位



    }

    private void userIsExpire() {

        String token = SharedPreferencesHelper.getInstance(mActivity).getString(TOKEN, "");
        if (TextUtils.isEmpty(token)) {
            ToastUtils.getInstanc().showToast("请登录");

            return;
        }


        String id = SharedPreferencesHelper.getInstance(mActivity).getString(ID, "");

        Http.with(mActivity).setObservable(Http.getApiService(ApiService.class).isVip(id)).setDataListener(new HttpCallBack() {
            @Override
            public void netOnSuccess(Map<String, Object> data) {
                String states = JsonParse.getString(data, "vip");

//                initSendMessage();

                if (states.equals("1")) {
                    //用户还是会员可以控制另一方

                    initSendMessage();

                } else {
                    ToastUtils.getInstanc().showToast("请会员充值");
                }

            }
        });


    }

    private void initSendMessage() {
        //自己的id
        String sendId = SharedPreferencesHelper.getInstance(mActivity).getString(ID, "");
        Http.with(mActivity).setObservable(Http.getApiService(ApiService.class).sendMsg(sendId,
                "erertryry"))
                .setDataListener(new HttpCallBack() {
                    @Override
                    public void netOnSuccess(Map<String, Object> data) {
                        ToastUtils.getInstanc().showToast("发送成功");

                        switch (mControlType) {
                            case 1:
                                tvSoundRecordCon.setText("正在启动中");
                                break;
                        }


                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
   
            case R.id.tv_location_con:
                //定位界面
                String[] location = mLocationContent.split(",");


                bundle.putString("lat",location[0]);
                bundle.putString("lon",location[1]);
                bundle.putString("con",location[2]);
                actionStart(MapActivity.class,bundle);





                break;
            case R.id.tv_cancel:
                mDialogBind.dismiss();
                break;

            case R.id.tv_sure:

                String account = mEditId.getText().toString();
                String pass = mEditPass.getText().toString();
                String id = SharedPreferencesHelper.getInstance(mActivity).getString(ID,
                        "");

                if (TextUtils.isEmpty(id)) {
                    ToastUtils.getInstanc().showToast("请先登录");
                    return;
                }
                Http.with(mActivity).setObservable(Http.getApiService(ApiService.class).bind(id,
                        account, pass))
                        .setDataListener(new HttpCallBack() {
                            @Override
                            public void netOnSuccess(Map<String, Object> data) {
                                ToastUtils.getInstanc().showToast("绑定成功");
                                mDialogBind.dismiss();
                                SharedPreferencesHelper.getInstance(mActivity).put(BIND_ACCOUNT,
                                        account);
                                Log.i(TAG, "netOnSuccess: account"+account);
                                SharedPreferencesHelper.getInstance(mActivity).put(BIND_ACCOUNT_ID, JsonParse.getLong(data, "cid") + "");
                                tvBindAccount.setText("绑定账号  " + account);
                            }
                        });


                break;
        }
    }
}
