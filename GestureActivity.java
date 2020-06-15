package com.gengy.control;

import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.gengy.control.Base.BaseActivity;

import com.gengy.control.Control.MineModular.SelectActivity;
import com.gengy.control.Untils.ShareKey;
import com.gengy.control.Untils.SharedPreferencesHelper;
import com.gengy.control.Untils.ToastUtils;
import com.github.ihsg.patternlocker.OnPatternChangeListener;
import com.github.ihsg.patternlocker.PatternLockerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;

public class GestureActivity extends BaseActivity {

    @BindView(R.id.pattern_lock_view)
    PatternLockerView patternLockView;

    @BindView(R.id.tv_tips)
    TextView tvTips;
    private String mType;
    private int mPutNumber = 0;

    @Override
    public int intiLayout() {
        return R.layout.activity_gesture;
    }

    @Override
    public void initView() {
        action_title.setText("密码设置");

        mType = extras.getString("type");
        findViewById(R.id.img_back).setVisibility(View.INVISIBLE);
        if (mType.equals("set")) {
            tvTips.setText("设置手势密码");
            findViewById(R.id.img_back).setVisibility(View.VISIBLE);
        } else if (mType.equals("loginSet")) {
            tvTips.setText("设置手势密码");

        } else {
            tvTips.setText("输入手势密码");

        }

        patternLockView.setOnPatternChangedListener(new OnPatternChangeListener() {
            @Override
            public void onStart(@NotNull PatternLockerView patternLockerView) {

            }

            @Override
            public void onChange(@NotNull PatternLockerView patternLockerView,
                                 @NotNull List<Integer> list) {

            }

            @Override
            public void onComplete(@NotNull PatternLockerView patternLockerView,
                                   @NotNull List<Integer> list) {
                if(list.size()<4){
                    ToastUtils.getInstanc().showToast("请设置最少连接4个点的手势密码");
                    return;
                }

                Log.i(TAG, "onComplete: onCompleteonComplete");
                mPutNumber++;
                if (mType.equals("set")) {
                    initSavePass(list);
                    if(mPutNumber==1){
                        return;
                    }
                    if(tvTips.getText().toString().equals("设置成功")){
                        finish();
                    }

                } else {
                    if(mType.equals("loginSet")){
                        initSavePass(list);
                        if(mPutNumber==1){
                            return;
                        }
                    }else {
                        String getsure =
                                SharedPreferencesHelper.getInstance(mActivity).getString(ShareKey
                                        .GESTURE_PASS, "");
                        if (!getsure.equals(list.toString())) {
                            tvTips.setText("输入错误");
                            return;
                        }
                    }




                    int type = SharedPreferencesHelper.getInstance(mActivity).getInt("selectType"
                            , 0);
                    switch (type) {
                        case 0:
                            actionStart(SelectActivity.class);
                            break;
                        case 1:
                            actionStart(ControlActivity.class);
                            break;
                        case 2:
                            actionStart(Main2Activity.class);
//                            startActivity(new Intent(this, ac));
//                            Intent intent=new Intent(Intent.ACTION_MAIN);
//                            ComponentName  name=new ComponentName (GestureActivity.this,"com.gengy.control.BeingControl.BeingControl");
//                            intent.setComponent(name);
//                            startActivity(intent);

//                            startActivity(new Intent(this, ac));
                            break;


                    }
                    finish();
                }



            }

            @Override
            public void onClear(@NotNull PatternLockerView patternLockerView) {

            }
        });

    }

    private void initSavePass(List<Integer> list) {
//        mPutNumber++;

        if (mPutNumber == 1) {
            SharedPreferencesHelper.getInstance(mActivity).put(ShareKey.GESTURE_PASS,
                    list.toString());
            tvTips.setText("再输入一次");
        }
        if (mPutNumber == 2) {
            String before=  SharedPreferencesHelper.getInstance(mActivity).getString(ShareKey.GESTURE_PASS,"");
            if(before.equals(list.toString())){
                SharedPreferencesHelper.getInstance(mActivity).put(ShareKey.GESTURE_PASS,
                    list.toString());
                        tvTips.setText("设置成功");
                ToastUtils.getInstanc().showToast("设置成功");

            }else {
//                SharedPreferencesHelper.getInstance(mActivity).put(ShareKey.GESTURE_PASS,
//                       "");
//                SharedPreferencesHelper.getInstance(mActivity).clear()
                mPutNumber=0;
                tvTips.setText("两次输入不一致，重新设置");
            }
        }

    }

    @Override
    public void initData() {

    }

}
