package com.gengy.control.BeingControl.BeingControl;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gengy.control.Base.BaseActivity;
import com.gengy.control.R;
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

public class BindMailboxActivity extends BaseActivity {


    @BindView(R.id.tv_help)
    TextView tvHelp;
    @BindView(R.id.et_mail_adress)
    EditText etMailAdress;
    @BindView(R.id.et_mail_pass)
    EditText etMailPass;

    @Override
    public int intiLayout() {
        return R.layout.activity_bind_mailbox;
    }

    @Override
    public void initView() {
        action_title.setText("绑定邮箱");

    }

    @Override
    public void initData() {

    }


    @OnClick({R.id.tv_help, R.id.bt_send})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.bt_send:
                String email = etMailAdress.getText().toString();
                String pass = etMailPass.getText().toString();
                String id = SharedPreferencesHelper.getInstance(mActivity).getString(ShareKey.ID,
                        "");
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(id)
                ) {
                    ToastUtils.getInstanc().showToast("请完善信息");
                    return;
                }
                Http.with(mActivity).setObservable(Http.getApiService(ApiService.class).bindEmail(id,email,pass))

                        .setDataListener(new HttpCallBack() {
                            @Override
                            public void netOnSuccess(Map<String, Object> data) {
                                ToastUtils.getInstanc().showToast("绑定成功");
                                finish();

                            }
                        });

                break;
            case R.id.tv_help:
                Intent intent = new Intent();
                String url = "https://service.mail.qq.com/cgi-bin/help?subtype=1&&no=1001256&&id" +
                        "=28";
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(url);
                intent.setData(content_url);
                startActivity(intent);
                break;


        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }


}
