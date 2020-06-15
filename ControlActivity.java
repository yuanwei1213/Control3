package com.gengy.control;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;

import com.gengy.control.Base.BaseActivity;
import com.gengy.control.Control.ControlModular.ControlFragment;
import com.gengy.control.Control.MineModular.MineFragment;
import com.gengy.control.Control.WXModular.WXFragment;
import com.gengy.control.R;


import java.util.ArrayList;

public class ControlActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {
    private ArrayList<Fragment> fragments;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.rg)
    RadioGroup rg;
    private ControlFragment mControlFragment;

    private WXFragment mWXFragment;
    private MineFragment mMineFrag;


    int currentFragment = 0;
    @Override
    public int intiLayout() {
        return R.layout.activity_control;
    }

    @Override
    public void initView() {
        fragments = new ArrayList<>();
        
        mControlFragment = new ControlFragment();
        fragments.add(mControlFragment);

        mWXFragment = new WXFragment();
        fragments.add(mWXFragment);

        mMineFrag = new MineFragment();
        fragments.add(mMineFrag);
        rg.setOnCheckedChangeListener(this);
        //预加载
        viewPager.setOffscreenPageLimit(fragments.size());
        //设置适配器
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            //选中的ITem
            @Override
            public Fragment getItem(int i) {
                return fragments.get(i);
            }

            //返回Item个数
            @Override
            public int getCount() {
                return fragments.size();
            }


        });

    }

    @Override
    public void initData() {


    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        switch (checkedId) {
            case R.id.rb1:
                currentFragment = 0;

                break;
            case R.id.rb2:
                currentFragment = 1;
                break;
            case R.id.rb3:
                currentFragment = 2;
                break;
        }
        viewPager.setCurrentItem(currentFragment);
    }
}
