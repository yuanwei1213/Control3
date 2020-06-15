package com.gengy.control;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gengy.control.Base.BaseActivity;
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

import static com.gengy.control.Untils.ShareKey.BIND_ACCOUNT_ID;

public class LoginActivity extends BaseActivity {


    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_pass)
    EditText etPass;
    @BindView(R.id.bt_sure)
    Button btSure;
    @BindView(R.id.tv_regist)
    TextView tvRegist;

    @Override
    public int intiLayout() {
        return R.layout.activity_login;
    }


    @Override
    public void initView() {
        action_title.setText("登录");

    }

    @Override
    public void initData() {

    }


    @OnClick({R.id.bt_sure, R.id.tv_regist})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_sure:
                String phone = etPhone.getText().toString();
                String pass = etPass.getText().toString();
                if(TextUtils.isEmpty(phone)){
                    ToastUtils.getInstanc().showToast("请输入账户");
                    return;
                }
                if(TextUtils.isEmpty(pass)){
                    ToastUtils.getInstanc().showToast("请输入密码");
                    return;
                }



                Http.with(mActivity).setObservable(Http.getApiService(ApiService.class).login(phone, pass)).
                        setDataListener(new HttpCallBack() {
                            @Override
                            public void netOnSuccess(Map<String, Object> data) {
//                                expire
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
                                        JsonParse.getString(data, "bindemail"));


                                finish();
                            }

                            @Override
                            public void netOnFailure(Throwable ex) {
                                Log.i(TAG, "netOnFailure: " + ex.toString());
                                super.netOnFailure(ex);
                            }
                        });


//                RequestUtils.login(phone, pass, mActivity,
//                        new MyObserverString<LoginEntity>(mActivity) {
//                    @Override
//                    public void onSuccess(LoginEntity result) {
//                        SharedPreferencesHelper.getInstance(mActivity).put(ShareKey.TOKEN,
//                                result.token);
//                        SharedPreferencesHelper.getInstance(mActivity).put(ShareKey.ID,
//                                result.id+"");
//                        SharedPreferencesHelper.getInstance(mActivity).put(ShareKey.VIP,result
//                        .vip);
//                        finish();
//                    }
//
//                    @Override
//                    public void onFailure(Throwable e, String errorMsg) {
//
//                    }
//                });
                break;
            case R.id.tv_regist:
                actionStart(RegisterActivity.class);
                finish();
                break;
        }
    }
}
