package com.gengy.control.Control.MineModular;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.gengy.control.Base.BaseFragment;
import com.gengy.control.LoginActivity;
import com.gengy.control.R;
import com.gengy.control.Untils.ShareKey;
import com.gengy.control.Untils.SharedPreferencesHelper;
import com.gengy.control.Untils.ToastUtils;
import com.gengy.control.WebActivity;
import com.gengy.control.http.ApiService;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bluemobi.dylan.http.Http;
import cn.bluemobi.dylan.http.HttpCallBack;
import cn.bluemobi.dylan.http.JsonParse;

/**
 * @date on 2019/12/25
 * 描述       15:12
 * com.gengy.control.Control.MineModular
 */
public class MineFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.tv_phone)
    TextView tvPhone;

    @Override
    protected int setLayoutResourceID() {


        return R.layout.frag_mine;
    }

    @Override
    protected void initView() {

    }


    @Override
    public void onStart() {
        super.onStart();
        String token=SharedPreferencesHelper.getInstance(mActivity).getString(ShareKey.TOKEN,"");;
        String account= SharedPreferencesHelper.getInstance(mActivity).getString(ShareKey.ACCOUNT,"");;
        String exprice= SharedPreferencesHelper.getInstance(mActivity).getString(ShareKey.EXPIRES,"");;

        Log.i(TAG, "onStartonStartonStart: "+account+"tokentoken"+token);

        tvPhone.setOnClickListener(null);
        tvPhone.setText(account+"\n"+exprice);

        if(TextUtils.isEmpty(token)){
            account="去登录";
            tvPhone.setText(account+"\n");
            tvPhone.setOnClickListener(this);
        }

        String id=SharedPreferencesHelper.getInstance(mActivity).getString(ShareKey.ID,"");;
        Log.i(TAG, "onStartonStartonStartid: "+id);
        if(!TextUtils.isEmpty(id)&&!TextUtils.isEmpty(token)){
            initGetMemberState(id,account);
        }



    }

    private void initGetMemberState(String id,String account) {

        Http.with(mActivity).setObservable(Http.getApiService(ApiService.class).isVip(id)).setDataListener(new HttpCallBack() {
            @Override
            public void netOnSuccess(Map<String, Object> data) {
                String exprice = JsonParse.getString(data, "notime");
                tvPhone.setText(account+"\n"+exprice);
            }
        });
    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.tv_activation, R.id.tv_cloud, R.id.tv_instructions, R.id.tv_problem,
            R.id.tv_select,  R.id.bt_logout,R.id.call_log})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.call_log:
actionStart(CallRecordActivity.class);


                break;


            case R.id.bt_logout:
                SharedPreferencesHelper.getInstance(mActivity).clearLogout(mActivity);
                tvPhone.setText("去登录");
                tvPhone.setOnClickListener(this);
                ToastUtils.getInstanc().showToast("退出成功");
                break;



            case R.id.tv_activation:
                actionStart(ActivationActivity.class);
                break;
            case R.id.tv_cloud:
                actionStart(CloudActivity.class);

                break;
            case R.id.tv_instructions:
 
                bundle.putInt("type", 2);
                actionStart(WebActivity.class, bundle);
                break;
            case R.id.tv_problem:

                bundle.putInt("type", 1);
                actionStart(WebActivity.class, bundle);


                break;
            case R.id.tv_select:
                actionStart(SelectActivity.class);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        actionStart(LoginActivity.class);
    }
}
