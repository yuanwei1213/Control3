package com.gengy.control.BeingControl.PhoneSetting;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.webkit.WebView;

import com.gengy.control.Base.BaseActivity;
import com.gengy.control.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SystemActivity extends BaseActivity {


    @BindView(R.id.webview)
    WebView webView;

    @Override
    public int intiLayout() {
        return R.layout.activity_system;
    }

    @Override
    public void initView() {
        action_title.setText("系统设置");
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
        webView.getSettings().setJavaScriptEnabled(true);//是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        webView.getSettings().setSupportZoom(true);//是否可以缩放，默认true
        webView.getSettings().setBuiltInZoomControls(false );//是否显示缩放按钮，默认false
        webView.getSettings().setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
        webView.getSettings().setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
        webView.getSettings().setAppCacheEnabled(true);//是否使用缓存
        webView.getSettings().setDomStorageEnabled(true);//DOM Storage
        webView.loadUrl("http://test.gengyouplay.cn/yuanwei/20191227.html");

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
        Intent intent =  new Intent(Settings.ACTION_SETTINGS);
        startActivity(intent);
    }
}
