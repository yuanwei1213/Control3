package com.gengy.control;

import android.text.TextUtils;
import android.util.Log;

import com.gengy.control.Base.BaseActivity;

import com.gengy.control.Control.MineModular.SelectActivity;
import com.gengy.control.Entity.JudgeUserEntity;
import com.gengy.control.Untils.ShareKey;
import com.gengy.control.Untils.SharedPreferencesHelper;
import com.gengy.control.http.ApiService;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.Map;

import cn.bluemobi.dylan.http.Http;
import cn.bluemobi.dylan.http.HttpCallBack;
import cn.bluemobi.dylan.http.JsonParse;


public class MainActivity extends BaseActivity {
    @Override
    public int intiLayout() {
        return R.layout.activity_main;
    }
    @Override
    public void initView() {
        String getsure=SharedPreferencesHelper.getInstance(mActivity).getString(ShareKey
        .GESTURE_PASS,"");
        if(TextUtils.isEmpty(getsure)){
            bundle.putString("type","loginSet");
        }else {
            bundle.putString("type","login");
        }

        actionStart(GestureActivity.class,bundle);



        finish();

    }

    @Override
    public void initData() {
        initPermission();
    }


    private void initPermission() {
        RxPermissions rxPermissions = new RxPermissions(this);
        //判断是否在别的地方登陆和是否会员 0不是 1是
        String id = SharedPreferencesHelper.getInstance(mActivity).getString(ShareKey.ID, "0");
        String token = SharedPreferencesHelper.getInstance(mActivity).getString(ShareKey.TOKEN, "0");
        Http.with(mActivity).setObservable(Http.getApiService(ApiService.class).judegeUser(id,token)).
                setDataListener(new HttpCallBack() {
                    @Override
                    public void netOnFailure(Throwable ex) {
                        super.netOnFailure(ex);
                        Log.i(TAG, "netOnSuccess: "+ex.toString());
                    }

                    @Override
                    public void netOnSuccess(Map<String, Object> data) {
                        Log.i(TAG, "netOnSuccess: "+data.toString());
                        int vip= JsonParse.getInt(data,"vip");

                        SharedPreferencesHelper.getInstance(mActivity).put(ShareKey.BIND_ACCOUNT,JsonParse.getString(data,"control"));

                        SharedPreferencesHelper.getInstance(mActivity).put(ShareKey.VIP,vip);
                        if(vip==1){
                            //是会员
                        }

                        if(JsonParse.getInt(data,"token")==0){
                            //在别的地方登陆
                            SharedPreferencesHelper.getInstance(mActivity).put(ShareKey.TOKEN,"");

                        }
                    }
                });



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}