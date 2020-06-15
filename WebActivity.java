package com.gengy.control;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.gengy.control.Base.BaseActivity;

public class WebActivity extends BaseActivity {


    private WebView webView;

    @Override
    public int intiLayout() {
        return R.layout.activity_web;
    }

    @Override
    public void initView() {
        webView=(WebView) findViewById(R.id.webView);


    }

    @Override
    public void initData() {

        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(webViewClient);




        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
        webView.getSettings().setJavaScriptEnabled(true);//是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        webView.getSettings().setSupportZoom(true);//是否可以缩放，默认true
        webView.getSettings().setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
        webView.getSettings().setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
        webView.getSettings().setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
        webView.getSettings().setAppCacheEnabled(true);//是否使用缓存
        webView.getSettings().setDomStorageEnabled(true);//DOM Storage


        String url="http://test.gengyouplay.cn/yuanwei/shuo2.html";

        int type=extras.getInt("type");
//        if(type==1){
//            url= " http://test.gengyouplay.cn/yuanwei/why2.html";
//        }

        switch (type){
            case 1:
                url= " http://test.gengyouplay.cn/yuanwei/why2.html";
                break;

        }



//
        webView.loadUrl(url);
    }
    //WebViewClient主要帮助WebView处理各种通知、请求事件
    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {
            //页面加载完成
//            progressbar.setVisibility(View.GONE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {//页面开始加载
//            progressbar.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }
    };
    private WebChromeClient webChromeClient = new WebChromeClient() { //不支持js的alert弹窗，需要自己监听然后通过dialog弹窗
        @Override
        public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(webView.getContext());
            localBuilder.setMessage(message).setPositiveButton("确定", null);
            localBuilder.setCancelable(false);
            localBuilder.create().show(); //注意: //必须要这一句代码:result.confirm()表示: //处理结果为确定状态同时唤醒WebCore线程 //否则不能继续点击按钮
            result.confirm();
            return true;
        } //获取网页标题

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            Log.i("ansen", "网页标题:" + title);
        }

        //加载进度回调
        @Override
        public void onProgressChanged(WebView view, int newProgress) {

        }
    };



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) { if (keyCode == KeyEvent.KEYCODE_BACK) { if (webView.canGoBack()) { webView.goBack();//返回上一页面
        return true; } } return super.onKeyDown(keyCode, event); }
}
