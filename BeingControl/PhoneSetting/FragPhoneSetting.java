package com.gengy.control.BeingControl.PhoneSetting;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.gengy.control.Base.BaseFragment;
import com.gengy.control.R;
import com.gengy.control.Untils.DeviceMethod;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @date on 2019/12/27
 * 描述       11:37
 * com.gengy.control.BeingControl.PhoneSetting
 */
public class FragPhoneSetting extends BaseFragment {
    @BindView(R.id.tv_set)
    TextView tvSet;
    @BindView(R.id.layout_phone_set)
    RelativeLayout layoutPhoneSet;
    @BindView(R.id.tv_notice)
    TextView tvNotice;
    @BindView(R.id.switch_notice)
    Switch switchNotice;
    @BindView(R.id.tv_window)
    TextView tvWindow;
    @BindView(R.id.tv_window_con)
    TextView tvWindowCon;

    @BindView(R.id.layout_clear)
    LinearLayout layoutClear;
    @BindView(R.id.tv_uninstall)
    TextView tvUninstall;
    @BindView(R.id.switch_uninstall)
    Switch switchUninstall;

    @Override
    protected int setLayoutResourceID() {
        return R.layout.frag_phone_set;
    }

    @Override
    protected void initView() {
        action_title.setText("手机设置");


    }

    @Override
    public void onStart() {
        super.onStart();
//        if (AuthorityUntils.getInstance().notificationListenerEnable(mActivity)) {
//            switchNotice.setChecked(true);
//        } else {
//            switchNotice.setChecked(false);
//
//        }


        if( DeviceMethod.getInstance(getActivity()).isActiveDeviceManager()){

            switchUninstall.setChecked(true);
        }else {
            switchUninstall.setChecked(false);
        }
        switchUninstall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b){
                    DeviceMethod.getInstance(getActivity()).onRemoveActivate();
                }else {
                    DeviceMethod.getInstance(getActivity()).onActivate();
                }

            }
        });

    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.layout_phone_set,R.id.sb_set_window, R.id.layout_window,R.id.sb_set,R.id.layout_clear, R.id.layout_notice, R.id.layout_uninstall})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layout_window:
            case R.id.sb_set_window:
                Intent localIntent = new Intent();
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT >= 9) {
                    localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    localIntent.setData(Uri.fromParts("package", mActivity.getPackageName(), null));
                } else if (Build.VERSION.SDK_INT <= 8) {
                    localIntent.setAction(Intent.ACTION_VIEW);
                    localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
                    localIntent.putExtra("com.android.settings.ApplicationPkgName",  mActivity.getPackageName());
                }
                startActivity(localIntent);

                break;
            case R.id.layout_uninstall:
//                AuthorityUntils.getInstance().startDeviceManager(getActivity());
                break;
            case R.id.layout_notice:
//                AuthorityUntils.getInstance().getNotificationAccessSetting(mActivity);
                break;
            case R.id.layout_phone_set:
            case R.id. sb_set:
                actionStart(SystemActivity.class);
                break;
            case R.id.layout_clear:
                actionStart(ClearActivity.class);
                break;
        }

    }


}
