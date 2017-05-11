package com.example.adminbilly.attendancesystem.Activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.adminbilly.attendancesystem.R;

/**
 * Created by AdminBilly on 2017/5/9.
 */

public class HistoryActivity extends BaseActivity {

    private Toolbar hToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hToolbar = (Toolbar) findViewById(R.id.history_toolbar);
        setSupportActionBar(hToolbar);

        //使用app bar的导航功能
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
