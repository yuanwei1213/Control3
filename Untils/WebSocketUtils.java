package com.gengy.control.Untils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.ResourceBundle;

public class WebSocketUtils {
    private static Activity mActivity;
    private  final String TAG="1111111111111111111111111 ";

    /**客户端**/
    public MyWebSocketClient myWebSocketClient;


    private static WebSocketUtils webSocketUtils;
    public static WebSocketUtils getInstance(Activity activity) {
        mActivity=activity;
        if (null==webSocketUtils) {
            webSocketUtils = new WebSocketUtils();
        }
        return webSocketUtils;
    }

    /**
     * 连接
     */
    public void content(){
        URI uri = URI.create("ws://39.100.12.219:8282");
        Log.i(TAG, "contentmyWebSocketClient: "+myWebSocketClient);
        if(null==myWebSocketClient){
            myWebSocketClient = new MyWebSocketClient(uri);
            if(HttpStatesUntils.isNetworkVailable(mActivity)){
                myWebSocketClient.connect();
            }
        }
    }
    public void contentWfi(){
        URI uri = URI.create("ws://39.100.12.219:8282");
        Log.i(TAG, "contentmyWebSocketClient: "+myWebSocketClient);
        if(null==myWebSocketClient){
            myWebSocketClient=null;

        }

//        if (client.getReadyState().equals(WebSocket.READYSTATE.CLOSING) || client.getReadyState().equals(WebSocket.READYSTATE.CLOSED)) {
//            client.reconnect();
//        }


            myWebSocketClient = new MyWebSocketClient(uri);

            if(HttpStatesUntils.isNetworkVailable(mActivity)){

                myWebSocketClient.reconnect();
            }

    }
    /**
     * 发送消息
     */
    public void sendChatMessage(String message) {
        Log.i(TAG, "sendChatMessage: "+message );
        
        try {
            myWebSocketClient.send(message);
        } catch (WebsocketNotConnectedException e) {
            Log.i(TAG, "sendChatMessage: "+e.toString()  );
            e.printStackTrace();
            content();
            closeHeartbeat();
        }
    }

    /**
     * 打开心跳
     */
    public void openHeartbeat(){
        myHandler.sendEmptyMessage(HEARTBEAT_CODE);
    }

    /**是否主动关闭**/
    private boolean isInitiativeClose=false;
    /**
     * 关闭心跳
     */
    public void closeHeartbeat(){
        isInitiativeClose=true;
        myHandler.removeCallbacksAndMessages(null);
    }

    /**心跳码**/
    private final int HEARTBEAT_CODE = 100;
    /**当前页-首页**/
    private final int CURRENT_PAGE_HOMEPAGE = 101;
    /**当前页-消息列表**/
    private final int CURRENT_PAGE_MESSAGE_LIST = 102;
    /**当前页-聊天**/
    private final int CURRENT_PAGE_CHAT = 103;
    /**发送消息成功**/
    private final int MESSAGE_SEND_SUCCEED=104;
    /**发送消息失败**/
    private final int MESSAGE_SEND_FAILURE=105;
    /**网络未连接**/
    private final int NETWORK_NOT_CONNECT=106;
    /**一次提示**/
    private boolean onceHint=false;
    /**重复登录**/
    private final int DUPLICATE_LOGIN=107;


    Handler myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case  HEARTBEAT_CODE://心跳
                {

//                        MessageUtil.log_i(TAG,"===心跳数据"+SendMessageUtils.getInstance().getSendHeartbeatJson());
//                        sendChatMessage("心跳");

//                    myHandler.sendEmptyMessageDelayed(HEARTBEAT_CODE, 2000);
                }
                    break;
                case  CURRENT_PAGE_HOMEPAGE://当前页-首页
                {
//                    HomePageActivity.activity.getData();
                }
                    break;
                case  CURRENT_PAGE_MESSAGE_LIST://当前页-消息列表
                {
//                    MessageListActivity.activity.refreshData();
                }
                    break;
                case  CURRENT_PAGE_CHAT://当前页-聊天

                    break;
                case  MESSAGE_SEND_SUCCEED://发送消息成功
                {
                    if(null!=onMessageSendCallback){
                        onMessageSendCallback.onSuccess(msg.getData().getString("message"));
                    }
                }
                    break;
                case  MESSAGE_SEND_FAILURE://发送消息失败
                {
                    if(null!=onMessageSendCallback){
                        onMessageSendCallback.onFailure(msg.getData().getString("message"));
                    }
                }
                    break;
                case  DUPLICATE_LOGIN://重复登录
                {
                    //关闭心跳，跳到登录界面
                    closeHeartbeat();
                    myWebSocketClient.close();
                    isInitiativeClose=true;


                }
                    break;
                case  NETWORK_NOT_CONNECT://网络未连接
                {
                    if(onceHint){

                        ToastUtils.getInstanc().showToast("没有可用的网络");
                        onceHint=false;
                    }
                }
                    break;
            }
            return false;
        }
    });



    public class MyWebSocketClient extends WebSocketClient {
        public MyWebSocketClient(URI serverUri) {
            super(serverUri ,new Draft_6455());
        }


        @Override
        public void onOpen(ServerHandshake handshakedata) {
            onceHint=true;
            isInitiativeClose=false;
            Log.i(TAG, "onOpen=打开通道" + handshakedata.getHttpStatus());

        }

        @Override
        public void onMessage(String message) {
            Log.i(TAG,  "onMessage=接收消息="+message);

            if(null!=onMessageSendCallback){
                onMessageSendCallback.onMessage(message);
            }



//            MessageUtil.log_i(TAG, "onMessage=接收消息="+message);
//            Log.i(TAG,  "onMessage=接收消息="+message);
////            WebSocketBean bean=new Gson().fromJson(message, WebSocketBean.class);
////            switch (bean.getCode()) {
////                case  9000://连接成功，发送登录消息
////                {
////                    MessageUtil.log_i(TAG, "onMessage=连接成功="+message);
////                    sendChatMessage(SendMessageUtils.getInstance().getSendLoginJson());
////                }
////                    break;
////                case  9001://连接失败，重新连接
////                {
////                    MessageUtil.log_i(TAG, "onMessage=连接失败="+message);
////                    content();
////                }
////                    break;
////                case  1000://登录成功，发送心跳消息
////                {
////                    MessageUtil.log_i(TAG, "onMessage=登录成功="+message);
////                    openHeartbeat();
////                }
////                    break;
////                case  1001://登录失败，重新登录
////                {
////                    MessageUtil.log_i(TAG, "onMessage=登录失败="+message);
////                    sendChatMessage(SendMessageUtils.getInstance().getSendLoginJson());
////                }
////                    break;
////                case  1002://重复登录
////                {
////                    MessageUtil.log_i(TAG, "onMessage=重复登录="+message);
////                    myHandler.sendEmptyMessage(DUPLICATE_LOGIN);
////
////                }
//                    break;
//                case  0://接收的消息
//                {
//                    //过滤心跳返回信息
//                    if(!TextUtils.isEmpty(bean.getAction())&&!"beat".equals(bean.getAction())){
//                        MessageUtil.log_i(TAG, "onMessage=接收消息=" + message);
//
//                        Activity mActivity=ActivityManagerUtil.getAppManager().currentActivity();
//                        MessageUtil.log_i(TAG, "onMessage=当前页面=" + mActivity.getClass().getName());
//
//                        Bundle bundle=new Bundle();
//                        bundle.putString("message",message);
//                        Message msg=new Message();
//                        msg.setData(bundle);
//
//                        if (HomePageActivity.class.getName().equals(mActivity.getClass().getName())) {
//                            //首页
//                            msg.what=CURRENT_PAGE_HOMEPAGE;
//
//                        }else if (MessageListActivity.class.getName().equals(mActivity.getClass().getName())) {
//                            //消息列表
//                            msg.what=CURRENT_PAGE_MESSAGE_LIST;
//
//                        }else if (MembersDetailsActivity.class.getName().equals(mActivity.getClass().getName())
//                                ||ChatTeamActivity.class.getName().equals(mActivity.getClass().getName())
//                                ||ChatTeamPatientActivity.class.getName().equals(mActivity.getClass().getName())
//                                ) {
//                            //聊天界面
//                            msg.what=CURRENT_PAGE_CHAT;
//                        }

//                        myHandler.sendMessage(msg);
//                    }
//                }
//                    break;
//            }

//            if(2000==bean.getCode()||2010==bean.getCode()){
//                MessageUtil.log_i(TAG, "onMessage=消息发送成功="+message);
//                Bundle bundle=new Bundle();
//                bundle.putString("message",message);
//                Message msg=new Message();
//                msg.setData(bundle);
//                msg.what=MESSAGE_SEND_SUCCEED;
//                myHandler.sendMessage(msg);
//
//            }else if(2001==bean.getCode()||2011==bean.getCode()){
//                MessageUtil.log_i(TAG, "onMessage=消息发送失败="+message);
//                Bundle bundle=new Bundle();
//                bundle.putString("message",message);
//                Message msg=new Message();
//                msg.setData(bundle);
//                msg.what=MESSAGE_SEND_FAILURE;
//                myHandler.sendMessage(msg);
//            }


        }

        @SuppressLint("LongLogTag")
        @Override
        public void onClose(int code, String reason, boolean remote) {
//            MessageUtil.log_i(TAG, "onClose=通道关闭");

            Log.i(TAG,  "onClose=通道关闭");
            if(!isInitiativeClose){
                myWebSocketClient=null;
                content();
            }
        }

        @Override
        public void onError(Exception ex) {
//            MessageUtil.log_i(TAG, "onError="+ex.getMessage());
            Log.i(TAG,  "onError="+ex.getMessage());
            //无网络，提示
            if("Network is unreachable".equals(ex.getMessage())){
                myHandler.removeMessages(HEARTBEAT_CODE);
                myHandler.sendEmptyMessage(NETWORK_NOT_CONNECT);
            }else{
                content();
            }
        }

    }


    /**
     * 抽象类，消息发送回调回调
     */
    public static abstract class OnMessageSendCallback {
        public void onSuccess(String message) {}
        public void onFailure(String message) {}
        public void onMessage(String message) {}
    }

    OnMessageSendCallback onMessageSendCallback;

    public void setOnMessageSendCallback(OnMessageSendCallback onMessageSendCallback) {
        this.onMessageSendCallback = onMessageSendCallback;
    }
}
