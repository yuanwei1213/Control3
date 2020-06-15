package com.gengy.control.BeingControl.PhoneSetting;

import com.gengy.control.Base.BaseActivity;
import com.gengy.control.R;

public class ClearActivity extends BaseActivity {

    @Override
    public int intiLayout() {
        return R.layout.activity_clear;
    }

    @Override
    public void initView() {
        action_title.setText("加锁");
    }

    @Override
    public void initData() {

    }
}
