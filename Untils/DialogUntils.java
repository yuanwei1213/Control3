package com.gengy.control.Untils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;

import com.gengy.control.R;


/**
 * @date on 2019/11/21
 * 描述       11:37
 * com.gengy.caipiao3.Untils
 */
public class DialogUntils {

    public static DialogUntils mDialogUntils;

    public static DialogUntils getInstance() {
        if (null == mDialogUntils) {
            mDialogUntils = new DialogUntils();
        }
        return mDialogUntils;
    }

    public Dialog createCenterDialog(Context context, int layoutId) {
        View v = LayoutInflater.from(context).inflate(layoutId, null);// 得到加载view
        Dialog loadingDialog = new Dialog(context, R.style.custom_dialog2);// 创建自定义样式dialog
        loadingDialog.setCancelable(true); // 是否可以按“返回键”消失
        loadingDialog.setCanceledOnTouchOutside(true); // 点击加载框以外的区域
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(110, 0, 110, 0);
        loadingDialog.setContentView(v, params);// 设置布局
        /**
         *将显示Dialog的方法封装在这里面
         */
        Window window = loadingDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setGravity(Gravity.CENTER);
        window.setAttributes(lp);
        return loadingDialog;
    }

    public Dialog createLoadingDialog(Context context, int layoutId) {
        View v = LayoutInflater.from(context).inflate(layoutId, null);// 得到加载view
        Dialog loadingDialog = new Dialog(context, R.style.custom_dialog2);// 创建自定义样式dialog
        loadingDialog.setCancelable(true); // 是否可以按“返回键”消失
        loadingDialog.setCanceledOnTouchOutside(true); // 点击加载框以外的区域

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(40, 0, 30, 0);
        loadingDialog.setContentView(v, params);// 设置布局
        /**
         *将显示Dialog的方法封装在这里面
         */
        Window window = loadingDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setGravity(Gravity.CENTER);
        window.setAttributes(lp);
        return loadingDialog;
    }

    public Dialog createDialogBottom(Context context, int layoutId) {
        View v = LayoutInflater.from(context).inflate(layoutId, null);// 得到加载view
        Dialog loadingDialog = new Dialog(context, R.style.custom_dialog2);// 创建自定义样式dialog
        loadingDialog.setCancelable(true); // 是否可以按“返回键”消失
        loadingDialog.setCanceledOnTouchOutside(true); // 点击加载框以外的区域

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 0);
        loadingDialog.setContentView(v, params);// 设置布局
        /**
         *将显示Dialog的方法封装在这里面
         */
        Window window = loadingDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setGravity(Gravity.BOTTOM);
        window.setAttributes(lp);
        return loadingDialog;
    }

    public PopupWindow createPopWindow(View contentview) {
        contentview.setFocusable(true); // 这个很重要
        contentview.setFocusableInTouchMode(true);
        PopupWindow popupWindow = new PopupWindow(contentview,
                RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(false);
        contentview.setOnKeyListener(new View.OnKeyListener() {//监听系统返回键
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    popupWindow.dismiss();

                    return true;
                }
                return false;
            }
        });
        return popupWindow;
    }


}
