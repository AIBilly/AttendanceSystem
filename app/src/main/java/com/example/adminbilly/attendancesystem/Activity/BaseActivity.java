package com.example.adminbilly.attendancesystem.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.example.adminbilly.attendancesystem.TaskManager;

/**
 * Created by AdminBilly on 2017/4/10.
 */

public abstract class BaseActivity extends AppCompatActivity{
    //TaskManager
    protected TaskManager mTM = TaskManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    }
}
