package com.gengy.control.Control.MineModular;

import android.os.Bundle;
import android.widget.RadioGroup;

import com.gengy.control.Base.BaseActivity;


import com.gengy.control.BeingControlActivity;
import com.gengy.control.ControlActivity;
import com.gengy.control.Main2Activity;
import com.gengy.control.Main3Activity;
import com.gengy.control.R;
import com.gengy.control.Untils.SharedPreferencesHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {
    @BindView(R.id.rg)
    RadioGroup rg;
    private  int mType=1;

    @Override
    public int intiLayout() {
        return R.layout.activity_select;
    }

    @Override
    public void initView() {
        action_title.setText("重选控制界面");
rg.setOnCheckedChangeListener(this);
    }

    @Override
    public void initData() {

    }


    @OnClick(R.id.bt_sure)
    public void onViewClicked() {
        SharedPreferencesHelper.getInstance(mActivity).put("selectType",mType);
        if(mType==1){
            actionStart(ControlActivity.class);
        }else {
            actionStart(Main2Activity.class);

        }

        finish();

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i){
            case R.id.rb1:
                mType=1;

                break;
            case R.id.rb2:
                mType=2;
                break;
        }
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_select);


}
