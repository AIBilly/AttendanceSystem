package com.example.adminbilly.attendancesystem.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.adminbilly.attendancesystem.R;


/**
 * Created by AdminBilly on 2017/4/9.
 */

public class WelcomeActivity extends BaseActivity {
    private Toolbar welToolbar;
    private Button button_login;
    private Button button_sign_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_welcome);

        welToolbar = (Toolbar) findViewById(R.id.wel_toolbar);
        setSupportActionBar(welToolbar);

        button_login = (Button)findViewById(R.id.login_button);
        button_sign_up = (Button)findViewById(R.id.sign_up_button);

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        button_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }
}
