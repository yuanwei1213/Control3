package com.gengy.control.http;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Observable;


/**
 * 接口地址
 * Created by YDL on 2017/5/3.
 */

public interface ApiService {

    /**
     * 主服务器正式地址lz.gengyouplay.cn
     */
    String BASE_URL = "http://www.jiayongshoujiguanli.com/api/";

//    String BASE_URL = "http://lz.gengyouplay.cn/api/";
//    String BASE_URL = "http://www.weiyaya.cn/api/";

    @POST("xyft/showlist")
    @FormUrlEncoded
    Observable<Response<ResponseBody>> getLotteryCarRecord(
            @Field("token") String token

    );
    //上传录音文件
    @Multipart
//    @FormUrlEncoded
    @POST("call")
    Observable<Response<ResponseBody>> uploadResourceRecord(@Part("id") RequestBody cid,
                                                            @Part("phone") RequestBody phone,
                                                            @Part("type") RequestBody type,
                                                           @Part MultipartBody.Part file);

    //单个图片上传
    @Multipart
//    @FormUrlEncoded
    @POST("uploads")
    Observable<Response<ResponseBody>> uploadResource(@Part("cid") RequestBody cid,
                                                      @Part MultipartBody.Part file);

    /**
     * 登录
     *
     * @param phone 手机号
     * @param pwd   密码
     * @return
     */
    @FormUrlEncoded
    @POST("login")
    Observable<Response<ResponseBody>> login(@Field("phone") String phone,
                                             @Field("pwd") String pwd);

    //发送定位信息
    @FormUrlEncoded
    @POST("dingwei")
    Observable<Response<ResponseBody>> sendLocation(@Field("id") String id,
                                                    @Field("content") String client_id);


    //获取联系人

    @GET("address")
    Observable<Response<ResponseBody>> getContacts(@Query("id") String id,
                                                   @Query("type") String type);

    //获取通话记录

    @GET("getcall")
    Observable<Response<ResponseBody>> getSoundRecord(@Query("id") String id);

    @FormUrlEncoded
    @POST("bdmail")
    Observable<Response<ResponseBody>> bindEmail(@Field("id") String id,
                                                 @Field("email") String email,
                                             @Field("code") String code

    );

    //删除全部
    @GET("alldel")
    Observable<Response<ResponseBody>> deletaAll(@Query("id") String id,
                                                 @Query("type") String type);

    //上传自动更新
    @GET("autosend")
    Observable<Response<ResponseBody>> uploadEmail(@Query("id") String id,
                                                 @Query("type") String type);



    //删除选中
    @FormUrlEncoded
    @POST("del")
    Observable<Response<ResponseBody>> deleteSelect(@Field("data") String data);

    //手动添加联系人
    @FormUrlEncoded
    @POST("set")
    Observable<Response<ResponseBody>> addContacts(@Field("id") String id,
                                                   @Field("name") String name,
                                                   @Field("phone") String phone,
                                                   @Field("type") String type);

    //获取云端管理

    @GET("show")
    Observable<Response<ResponseBody>> getResource(@Query("id") String id,
                                                   @Query("page") int page


    );


    //进入聊天室
    @FormUrlEncoded
    @POST("init")
    Observable<Response<ResponseBody>> inChat(@Field("id") String id,
                                              @Field("client_id") String client_id);

    //接收控制信息
    @FormUrlEncoded
    @POST("msglist")
    Observable<Response<ResponseBody>> receiveMsg(@Field("id") String id
    );


    //发送控制信息
    @FormUrlEncoded
    @POST("say")
    Observable<Response<ResponseBody>> sendMsg(@Field("id") String id,
                                               @Field("content") String content);

    @FormUrlEncoded
    @POST("binding")
    Observable<Response<ResponseBody>> bind(
            @Field("id") String id,
            @Field("phone") String phone,
            @Field("pwd") String pwd);


    //    @Multipart
////    @FormUrlEncoded
//    @POST("uploads")
//    Observable<Response<ResponseBody>> uploadResource(@Part("cid") RequestBody cid,
//                                                      @Part MultipartBody.Part file);
    //添加通讯录
    @Multipart
    @POST("setall")
    Observable<Response<ResponseBody>> addContacts(
            @Part("data") RequestBody data
    );

    //注册
    @FormUrlEncoded
    @POST("register")
    Observable<Response<ResponseBody>> register(@Field("phone") String phone,
                                                @Field("pwd") String pwd,
                                                @Field("repwd") String repwd
    );

    //激活
    @FormUrlEncoded
    @POST("key")
    Observable<Response<ResponseBody>> activation(@Field("id") String id,
                                                  @Field("key") String key);

    //是否别的地方登录

    @GET("token")
    Observable<Response<ResponseBody>> judegeUser(@Query("id") String id,
                                                  @Query("token") String token);

    @GET("isvip")
    Observable<Response<ResponseBody>> isVip(@Query("id") String id);
}
