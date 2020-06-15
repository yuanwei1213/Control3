package com.gengy.control.Untils;

import java.net.URL;

/**
 * @date on 2020/2/13
 * 描述       15:13
 * com.gengy.control.Untils
 */
public class ShareKey {
    public static String TOKEN = "token";
    public static String ID = "id";

    //手势密码
    public static String GESTURE_PASS = "gesture_pass";
    //    0 不是 1是
    public static String VIP = "vip";


    //    0 没有 1打开
    public static String IS_OPEN = "IS_OPEN";

    //    0 没有绑定 1绑定
    public static String IS_BIND = "0";



    public static String ACCOUNT = "account";
    //当前用户绑定账户
    public static String BIND_ACCOUNT = "bind_account";
    //当前用户绑定账户id
    public static String BIND_ACCOUNT_ID = "bind_account_id";




    //会员到期
    public static String EXPIRES = "expires";
    //开始录音
    public static final int START_RECORD =12;
    //开始录音回调
    public static final int START_RECORD_CALLBACK =122;
    //开始录音回调没有权限
    public static final int START_RECORD_CALLBACK_REFUSE=1222;
    //开始录音回调没有wifi
    public static final int START_RECORD_CALLBACK_REFUSE_WIFI=12222

            ;
    //结束录音
    public static final int END_RECORD =14;
    //结束录音回调
    public static final int END_RECORD_CALLBACK=144;

    //结束录音回调wifi上传 目前不是wifi
    public static final int END_RECORD_CALLBACK_WIFI=14444;

    //结束录音上传成功回调
    public static final int END_RECORD_CALLBACK_URL=1444;

    //开始录ping
    public static final int START_RECORD_SCREEN =13;
    //开始录ping回调
    public static final int START_RECORD_SCREEN_CALLBACK =133;
    //开始录ping回调拒绝权限
    public static final int START_RECORD_SCREEN_CALLBACK_REFUSE =1330;

    //开始录音回调没有wifi
    public static final int TART_RECORD_SCREEN_CALLBACK__WIFI=133333;
    //结束录ping
    public static final int END_RECORD_SCREEN =1333;
    //结束录ping回调
    public static final int END_RECORD_SCREEN_CALLBACK=13333;

    //结束录ping上传成功回调
    public static final int END_RECORD_SCREEN_CALLBACK_URL=23333;

    //开始定位
    public static final int START_LOCATION =16;
    //开始定位回调
    public static final int START_LOCATION_CALLBACK =166;
    //结束定位
    public static final int END_LOCATION =17;
    //结束定位回调
    public static final int END_LOCATION_CALLBACK=177;

    //结束定位回调失败
    public static final int END_LOCATION_CALLBACK_FAIL=1777;


    //开始截图
    public static final int START_SCREEN_SCREEN =18;
    //开始截图g回调
    public static final int STARTS_SCREEN_CALLBACK =188;
    //开始截图失败回调
    public static final int STARTS_SCREEN_CALLBACK_FAIL =1888;
    //结束截图
    public static final int END_SCREEN_SCREEN =19;
    //结束截图回调
    public static final int END__SCREEN_CALLBACK=199;
    //结束截图上传成功回调
    public static final int END__SCREEN_CALLBACK_URL=1999;

    //开始拍照 前置
    public static final int START_PHOTO_PREFIX=20;
    //开始拍照 前置
    public static final int START_PHOTO_POST=22;

    //开始拍照回调
    public static final int STARTS_PHOTO_CALLBACK =200;
    //开始拍照失败回调
    public static final int STARTS_SPHOTO_CALLBACK_FAIL =2000;
    //结束拍照
    public static final int END_PHOTO_ =21;
    //结束拍照回调
    public static final int END__PHOTO_CALLBACK=211;

    //结束拍照上传回调
    public static final int END__PHOTO_CALLBACK_URL=2111;

    //发送微信聊天信息
    public static final int SENS_LIST=300;

//    public  static  String   LOGIN_ACCOUNT="login_account";


    //  屏幕截图的路径
    public static final String SCREEN_PATH="";

    //  录屏幕的路径
    public static final String VIDEO_PATH="";


    //是否在wifi下上传录像
    public static final String RECORD_SEND="record_send";

    //是否在wifi下上传录音
    public static final String SOUND_SEND="sound_send";



//    发送的内容
    public static final String SEND_CONTENT="content1";

    //    发送的内容2
    public static final String SEND_CONTENT_2="content2";

}
