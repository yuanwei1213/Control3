package com.gengy.control.Control.MineModular;

import android.widget.EditText;

import com.gengy.control.Base.BaseActivity;
import com.gengy.control.Entity.RegisterEntity;
import com.gengy.control.R;
import com.gengy.control.Untils.ShareKey;
import com.gengy.control.Untils.SharedPreferencesHelper;
import com.gengy.control.Untils.ToastUtils;
import com.gengy.control.http.ApiService;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bluemobi.dylan.http.Http;
import cn.bluemobi.dylan.http.HttpCallBack;
import cn.bluemobi.dylan.http.JsonParse;

public class ActivationActivity extends BaseActivity {

    @BindView(R.id.edit_code)
    EditText editCode;

    @Override
    public int intiLayout() {
        return R.layout.activity_activation;
    }

    @Override
    public void initView() {
        action_title.setText("激活会员");

    }

    @Override
    public void initData() {

    }

    
    @OnClick(R.id.bt_sure)
    public void onViewClicked() {
        String code=editCode.getText().toString();
        String id= SharedPreferencesHelper.getInstance(mActivity).getString(ShareKey.ID,"");
        Http.with(mActivity).setObservable(Http.getApiService(ApiService.class).activation(id,code))
        .setDataListener(new HttpCallBack() {
            @Override
            public void netOnSuccess(Map<String, Object> data) {
                ToastUtils.getInstanc().showToast("激活成功");
                String expires= JsonParse.getString(data, "time");
                SharedPreferencesHelper.getInstance(mActivity).put(ShareKey.EXPIRES,
                        expires.substring(0,10));


            }
        });



    }
}
