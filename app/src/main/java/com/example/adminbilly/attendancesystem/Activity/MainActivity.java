package com.example.adminbilly.attendancesystem.Activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.example.adminbilly.attendancesystem.Fragment.FragmentMe;
import com.example.adminbilly.attendancesystem.Fragment.FragmentSignIn;
import com.example.adminbilly.attendancesystem.Fragment.FragmentTask;
import com.example.adminbilly.attendancesystem.R;
import com.example.adminbilly.attendancesystem.TabEntity;
import com.example.adminbilly.attendancesystem.TaskManager;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;

import java.util.ArrayList;

/**
 * Created by AdminBilly on 2017/4/6.
 */

public class MainActivity extends BaseActivity {
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private CommonTabLayout mTabLayout;
    private String[] mTitles = {"Task", "Sign in", "Me"};
    private int[] mIconUnselectIds = {
            R.mipmap.main_tab_item_task_normal, R.mipmap.main_tab_item_sign_in_normal,
            R.mipmap.main_tab_item_me_normal};
    private int[] mIconSelectIds = {
            R.mipmap.main_tab_item_task_focused, R.mipmap.main_tab_item_sign_in_focused,
            R.mipmap.main_tab_item_me_focused};
    //tab的标题、选中图标、未选中图标
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        TaskManager mTM = TaskManager.getInstance();

        Toolbar mToolbarTb = (Toolbar) findViewById(R.id.tb_toolbar);
        setSupportActionBar(mToolbarTb);

        initView();
        initData();

        //给tab设置数据和关联的fragment
        mTabLayout.setTabData(mTabEntities, this, R.id.fl_change, mFragments);
        //设置红点
        //mTabLayout.showDot(1);
    }

    private void initView() {
        mTabLayout = (CommonTabLayout) findViewById(R.id.tl);
    }

    private void initData() {
        mFragments.add(new FragmentTask());
        mFragments.add(new FragmentSignIn());
        mFragments.add(new FragmentMe());
        //设置tab的标题、选中图标、未选中图标
        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }
    }
}
