package com.gengy.control.Base;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.gengy.control.R;
import com.gengy.control.SoundRecordService2;
import com.gengy.control.Untils.NavigationBarUtil;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;


public abstract class BaseActivity extends AppCompatActivity {
    /***是否显示标题栏*/
    private boolean isshowtitle = false;
    /***是否显示标题栏*/
    private boolean isshowstate = true;
    /***封装toast对象**/
    private static Toast toast;
    /***获取TAG的activity名称**/
    protected final String TAG = "1111111111111111";
    public Activity mActivity;
    public Bundle extras;
    public Bundle bundle;
    public String mToken;
    private static volatile Activity mCurrentActivity;
    /**
     * 当前Activity渲染的视图View
     **/
    protected View mContextView = null;
    protected ImageButton action_back,img_right;
    protected TextView action_title;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initWindow();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mActivity = this;
        mContextView = LayoutInflater.from(this).inflate(intiLayout(), null);
        setContentView(mContextView);
        //设置布局
//        setContentView(intiLayout());
        ButterKnife.bind(this);
        extras = getIntent().getExtras();
        bundle = new Bundle();

//        mToken = SharedPreferencesHelper.getInstance(mActivity).getString(Constans.loginToken, "");
//        mToken="bf786614c1b87d055cc474df07c43f19";
        //初始化控件
        initHeadView(mContextView);
        initView();

        //设置数据
        initData();
        if(NavigationBarUtil.hasNavigationBar(this)){
            NavigationBarUtil.initActivity(findViewById(android.R.id.content));
        }



    }
    public void initWindow() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//判断SDK的版本是否>=21
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION); //允许页面可以拉伸到顶部状态栏并且定义顶部状态栏透名
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |  //设置全屏显示
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT); //设置状态栏为透明
            window.setNavigationBarColor(Color.TRANSPARENT); //设置虚拟键为透明
        }

    }
    public void initHeadView(View view) {
        if (findViewById(R.id.img_back)!=null) {
            action_back=findViewById(R.id.img_back);
            action_title=findViewById(R.id.tv_title);
            img_right=findViewById(R.id.img_right);
            action_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                  finish();
                }
            });
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
//        mToken = SharedPreferencesHelper.getInstance(mActivity).getString(Constans.loginToken, "");
        Log.i(TAG, "onStart: "+mToken);
    }

    public static Activity getCurrentActivity() {
        return mCurrentActivity;
    }

    private void setCurrentActivity(Activity activity) {
        mCurrentActivity = activity;
    }


    protected void actionStart(Class<?> ac) {
        startActivity(new Intent(this, ac));
    }

    protected void actionStartForResult(Class<?> ac, int requestCode) {
        Intent intent = new Intent(this, ac);
        startActivityForResult(intent, requestCode);
    }

    protected void actionStartForResult(Class<?> ac, Bundle bundle, int requestCode) {
        Intent intent = new Intent(this, ac);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    protected void actionStart(Class<?> ac, Bundle bundle) {
        Intent intent = new Intent(this, ac);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 设置布局
     *
     * @return
     */
    public abstract int intiLayout();

    /**
     * 初始化布局
     */
    public abstract void initView();

    /**
     * 设置数据
     */
    public abstract void initData();

    /**
     * 是否设置标题栏
     *
     * @return
     */
    public void setTitle(boolean ishow) {
        isshowtitle = ishow;
    }

    /**
     * 设置是否显示状态栏
     *
     * @param ishow
     */
    public void setState(boolean ishow) {
        isshowstate = ishow;
    }

    /**
     * 显示长toast
     *
     * @param msg
     */
    public void toastLong(String msg) {
        if (null == toast) {
            toast = new Toast(this);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setText(msg);
            toast.show();
        } else {
            toast.setText(msg);
            toast.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCurrentActivity(this);
    }

    /**
     * 显示短toast
     *
     * @param msg
     */
    public void toastShort(String msg) {
        if (null == toast) {
            toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
//            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setText(msg);
            toast.show();
        } else {
            toast.setText(msg);
            toast.show();
        }
    }


}
