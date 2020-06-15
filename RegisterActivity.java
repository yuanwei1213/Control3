package com.gengy.control;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.gengy.control.Base.BaseActivity;
import com.gengy.control.Entity.RegisterEntity;
import com.gengy.control.Untils.ShareKey;
import com.gengy.control.Untils.SharedPreferencesHelper;
import com.gengy.control.Untils.ToastUtils;
import com.gengy.control.http.ApiService;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bluemobi.dylan.http.Http;
import cn.bluemobi.dylan.http.HttpCallBack;
import cn.bluemobi.dylan.http.JsonParse;

import static com.gengy.control.Untils.ShareKey.BIND_ACCOUNT_ID;

public class RegisterActivity extends BaseActivity {


    @BindView(R.id.et_pass)
    EditText etPass;
    @BindView(R.id.et_pass_sure)
    EditText etPassSure;
    @BindView(R.id.et_phone)
    EditText etPhone;

    @Override
    public int intiLayout() {
        return R.layout.activity_register;
    }

    @Override
    public void initView() {
        action_title.setText("注册");

    }

    @Override
    public void initData() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick(R.id.bt_sure)
    public void onViewClicked() {
        String phone=etPhone.getText().toString();
        String pass=etPass.getText().toString();
        String passSure=etPassSure.getText().toString();
        Http.with(mActivity).setObservable(Http.getApiService(ApiService.class).register(phone,pass,passSure))
                .setDataListener(new HttpCallBack() {
                    @Override
                    public void netOnSuccess(Map<String, Object> data) {
//                        ToastUtils.getInstanc().showToast("注册成功");
                        //去登录
                        Http.with(mActivity).setLoadingMessage("正在登录").setObservable(Http.getApiService(ApiService.class).login(phone, pass)).
                                setDataListener(new HttpCallBack() {
                                    @Override
                                    public void netOnSuccess(Map<String, Object> data) {
//                                expire
                                        ToastUtils.getInstanc().showToast("登录成功");
                                        SharedPreferencesHelper.getInstance(mActivity).put(ShareKey.TOKEN,
                                                JsonParse.getString(data, "token"));
                                        SharedPreferencesHelper.getInstance(mActivity).put(ShareKey.ACCOUNT,
                                                phone);
                                        String expires = JsonParse.getString(data, "expire");
                                        if(!expires.contains("会员已到期")){
                                            SharedPreferencesHelper.getInstance(mActivity).put(ShareKey.EXPIRES,
                                                    expires.substring(0, 10));
                                        }
                                        SharedPreferencesHelper.getInstance(mActivity).put(ShareKey.BIND_ACCOUNT, JsonParse.getString(data, "control"));
                                        SharedPreferencesHelper.getInstance(mActivity).put(BIND_ACCOUNT_ID,  JsonParse.getLong(data, "cid") + "");
                                        SharedPreferencesHelper.getInstance(mActivity).put(ShareKey.ID,
                                                JsonParse.getLong(data, "id") + "");
                                        SharedPreferencesHelper.getInstance(mActivity).put(ShareKey.VIP,
                                                JsonParse.getLong(data, "vip"));



                                        SharedPreferencesHelper.getInstance(mActivity).put(ShareKey.IS_OPEN,
                                                String.valueOf(JsonParse.getInt(data, "isopen") ));



                                        SharedPreferencesHelper.getInstance(mActivity).put(ShareKey.IS_BIND,
                                                JsonParse.getLong(data, "bindemail"));


                                        finish();

                                    }

                                    @Override
                                    public void netOnFailure(Throwable ex) {
                                        Log.i(TAG, "netOnFailure: " + ex.toString());
                                        super.netOnFailure(ex);
                                    }
                                });

                    }

                });




    }
}
