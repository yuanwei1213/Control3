package com.gengy.control.Control.WXModular;

import android.util.Log;

import com.gengy.control.Base.BaseFragment;
import com.gengy.control.R;
import com.gengy.control.Untils.SharedPreferencesHelper;
import com.gengy.control.Untils.WebSocketUtils;

import org.json.JSONException;
import org.json.JSONObject;

import static com.gengy.control.Untils.ShareKey.ID;

/**
 * @date on 2019/12/25
 * 描述       15:11
 * com.gengy.control.Control.ControlModular
 */
public class WXFragment extends BaseFragment {

    private WebSocketUtils webSocketUtils;

    @Override
    protected int setLayoutResourceID() {
        return R.layout.frag_wx;
    }

    @Override
    protected void initView() {

    }
    @Override
    protected void initData() {
//        initWorkMan();
    }


    private void initWorkMan() {
//        webSocketUtils = new WebSocketUtils();
        webSocketUtils.content();
        webSocketUtils.openHeartbeat();
        webSocketUtils.setOnMessageSendCallback(new WebSocketUtils.OnMessageSendCallback() {

            @Override
            public void onSuccess(String message) {
                super.onSuccess(message);
            }

            @Override
            public void onMessage(String message) {
                super.onMessage(message);


                if (message.contains("client_id")) {
                    try {
                        String ids = SharedPreferencesHelper.getInstance(mActivity).getString(ID,
                                "");
                        JSONObject object = new JSONObject(message);
                        object.put("type", "login");
                        object.put("id", ids);
                        webSocketUtils.sendChatMessage(object.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else if (message.contains("content")) {
                    JSONObject object = null;
                    try {
                        object = new JSONObject(message);
                        int type = object.getInt("content");
                        Log.i(TAG, "onMessage:messagemessage:" + type);
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //此时已在主线程中，更新UI

                            }
                        });


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


            }

        });

    }

}
