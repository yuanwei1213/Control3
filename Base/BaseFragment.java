package com.gengy.control.Base;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;


import com.gengy.control.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public abstract   class BaseFragment extends Fragment {

    public View mContentView;
    public Activity mActivity;

    protected final String TAG = "1111111111111111111";
    public Bundle bundle;
    public String mToken;

    public ImageButton action_back,action_right;
    public TextView action_title;




    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mActivity = getActivity();
         bundle = new Bundle();
        mContentView = inflater.inflate(setLayoutResourceID(), container, false);
        ButterKnife.bind(this, mContentView);
        initHeadView(mContentView);
        init();


        initView();
        initData();
        return mContentView;
    }
    public void initHeadView(View view) {



        if (view.findViewById(R.id.img_back)!=null) {
            action_back=view.findViewById(R.id.img_back);
            action_title=view.findViewById(R.id.tv_title);
            action_right=view.findViewById(R.id.img_right);
            action_back.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
//        mToken= SharedPreferencesHelper.getInstance(mActivity).getString(Constans.loginToken,"");
        Log.i(TAG, "initView: "+mToken);
    }

    protected  void actionStart(Class<?> ac){
        startActivity(new Intent(getActivity(),ac));
    }
    protected  void actionStart(Class<?> ac, Bundle bundle){
        Intent intent=new Intent(getActivity(),ac);
        if(null!=bundle){
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 此方法用于返回Fragment设置ContentView的布局文件资源ID * * @return 布局文件资源ID
     */
    protected abstract int setLayoutResourceID();

    /**
     * 一些View的相关操作
     */
    protected abstract void initView();

    /**
     * 一些Data的相关操作
     */
    protected abstract void initData();

    /**
     * 此方法用于初始化成员变量及获取Intent传递过来的数据 * 注意：这个方法中不能调用所有的View，因为View还没有被初始化，要使用View在initView方法中调用
     */
    protected void init() {
    }

    public View getContentView() {
        return mContentView;
    }

    public Context getMContext() {
        return mActivity;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}
