package com.example.adminbilly.attendancesystem.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Cache;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.DiskBasedCache;
import com.example.adminbilly.attendancesystem.R;

import org.json.JSONObject;

import com.example.adminbilly.attendancesystem.myJsonRequest;

/**
 * Created by AdminBilly on 2017/4/9.
 */

public class SignUpActivity extends BaseActivity{
    private Toolbar suToolbar;
    private Button button_confirm_sign_up;
    private Button button_cancel_sign_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        suToolbar = (Toolbar) findViewById(R.id.sign_up_toolbar);
        setSupportActionBar(suToolbar);

        //使用app bar的导航功能
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        button_confirm_sign_up = (Button)findViewById(R.id.sign_up_confirm);
        button_cancel_sign_up = (Button)findViewById(R.id.sign_up_cancel);

        button_confirm_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
                JSONObject jsonBody = new JSONObject();
                try{
                    //jsonBody.put("email", "308771053@qq.com");
                    jsonBody.put("login", "LiNing");
                    jsonBody.put("password", "Admin");
                }catch (Exception e){

                }
                myJsonRequest.signUp(jsonBody, cache, new myJsonRequest.volleyCallback(){
                    @Override
                    public void getResponse(JSONObject response){
                        Log.d("success","1111111111111111111111111111111111111111111111");
                    }

                    @Override
                    public void getResponse(VolleyError error){
                        Log.d("failed","22222222222222222222222222222222222222222222222");
                    }
                });

                Intent intent = new Intent(SignUpActivity.this, WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        button_cancel_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
