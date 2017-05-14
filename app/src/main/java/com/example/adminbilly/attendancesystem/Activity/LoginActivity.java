package com.example.adminbilly.attendancesystem.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.DiskBasedCache;
import com.example.adminbilly.attendancesystem.R;
import com.example.adminbilly.attendancesystem.Task;
import com.example.adminbilly.attendancesystem.myJsonRequest;

import org.json.JSONObject;
import java.util.ArrayList;

/**
 * Created by AdminBilly on 2017/4/9.
 */

public class LoginActivity extends BaseActivity {

    public static String id_token = null;
    public static String curUser = null;

    private Toolbar lgToolbar;
    private Button button_confirm_login;
    private Button button_cancel_login;
    private EditText input_username;
    private EditText input_password;

    private ArrayList<Task> mTaskList = mTM.getTaskList();

    //今天的任务
    private ArrayList<Task> mTodayTaskList = mTM.getTodayTaskList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mTaskList.clear();
        mTodayTaskList.clear();

        lgToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(lgToolbar);

        //使用app bar的导航功能
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        button_confirm_login = (Button) findViewById(R.id.login_confirm);
        button_cancel_login = (Button) findViewById(R.id.login_cancel);
        input_username = (EditText) findViewById(R.id.username_input);
        input_password = (EditText) findViewById(R.id.password_input);

        button_confirm_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(input_username.getText().toString().equals("")){
                    Toast.makeText(LoginActivity.this, "Please input username!",
                            Toast.LENGTH_LONG).show();
                }else if(input_password.getText().toString().equals("")){
                    Toast.makeText(LoginActivity.this, "Please input password!",
                            Toast.LENGTH_LONG).show();
                }else{
                    //登录
                    Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
                    JSONObject jsonBody = new JSONObject();
                    try{
                        jsonBody.put("username", input_username.getText().toString());
                        jsonBody.put("password", input_password.getText().toString());
                        jsonBody.put("rememberMe", true);
                    }catch (Exception e){

                    }
                    myJsonRequest.Login(jsonBody, cache, new myJsonRequest.volleyCallback(){
                        @Override
                        public void getResponse(JSONObject response){
                            id_token = "";
                            try{
                                id_token = response.getString("id_token");
                            }catch (Exception e){

                            }
                            Log.d("success",id_token);
                            //获取当前用户
                            Cache cache2 = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

                            myJsonRequest.getUser(cache2, new myJsonRequest.volleyCallback(){
                                @Override
                                public void getResponse(JSONObject response){
                                    try{
                                        curUser = response.get("login").toString();
                                    }catch (Exception e){

                                    }
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }

                                @Override
                                public void getResponse(VolleyError error){
                                    Toast.makeText(LoginActivity.this, "Failed to get profile!",
                                            Toast.LENGTH_LONG).show();
                                    Log.e("failed","22222222222222222222222222222222222222222222222");
                                }
                            });

                        }

                        @Override
                        public void getResponse(VolleyError error){
                            Toast.makeText(LoginActivity.this, "Login failed!",
                                    Toast.LENGTH_LONG).show();
                            Log.e("failed","22222222222222222222222222222222222222222222222");
                        }
                    });
                }
            }
        });

        button_cancel_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        input_username.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean hasFocus){
                EditText et = (EditText)view;
                if (!hasFocus) {// 失去焦点
                    et.setHint(et.getTag().toString());
                } else {
                    String hint=et.getHint().toString();
                    et.setTag(hint);//保存预设字
                    et.setHint(null);
                }
            }
        });

        input_password.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean hasFocus){
                EditText et = (EditText)view;
                if (!hasFocus) {// 失去焦点
                    et.setHint(et.getTag().toString());
                } else {
                    String hint=et.getHint().toString();
                    et.setTag(hint);//保存预设字
                    et.setHint(null);
                }
            }
        });
    }
}
