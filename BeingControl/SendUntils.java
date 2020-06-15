package com.gengy.control.BeingControl;

import android.app.Activity;

import com.gengy.control.Untils.SharedPreferencesHelper;
import com.gengy.control.Untils.WebSocketUtils;

import org.json.JSONException;
import org.json.JSONObject;

import static com.gengy.control.Untils.ShareKey.BIND_ACCOUNT_ID;
import static com.gengy.control.Untils.ShareKey.ID;

/**
 * @date on 2020/4/2
 * 描述       16:43
 * com.gengy.control.BeingControl
 */
public class SendUntils {

    public static  void initSendMsg(WebSocketUtils  webSocketUtils, Activity mActivity, int type) {
        String ids = SharedPreferencesHelper.getInstance(mActivity).getString(ID, "");
        String cids = SharedPreferencesHelper.getInstance(mActivity).getString(BIND_ACCOUNT_ID, "");
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("content", type);
            jsonObject.put("type", "say");
            jsonObject.put("avatar", "13213");
            jsonObject.put("name", "15246220315");
            jsonObject.put("id", ids);
            jsonObject.put("cid", cids);
            webSocketUtils.sendChatMessage(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
