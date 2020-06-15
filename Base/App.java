package com.gengy.control.Base;

import android.app.Application;
import android.content.Context;

import com.baidu.mapapi.SDKInitializer;
import com.gengy.control.http.ApiService;
import com.tencent.bugly.crashreport.CrashReport;

import cn.bluemobi.dylan.http.BuildConfig;
import cn.bluemobi.dylan.http.Http;


public class App extends Application {
    public  static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context=this;
        Http.getHttp().setDebugMode(true);
        Http.getHttp().init(ApiService.class, ApiService.BASE_URL, "code", "data", "message", 1);
        CrashReport.initCrashReport(getApplicationContext(), "b5a3a76bdc", false);
        SDKInitializer.initialize(this);
    }
}
