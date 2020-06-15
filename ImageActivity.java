package com.gengy.control;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.gengy.control.Base.BaseActivity;

public class ImageActivity extends BaseActivity {



    @Override
    public int intiLayout() {
        return R.layout.activity_image;
    }

    @Override
    public void initView() {
        String url=extras.getString("imgurl");
        ImageView imageView=findViewById(R.id.img);
        Glide.with(mActivity).load(url).into(imageView);

    }

    @Override
    public void initData() {

    }
}
