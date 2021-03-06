package com.example.adminbilly.attendancesystem.Activity;

import android.content.Intent;
import android.os.Bundle;
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

import org.json.JSONObject;

import com.example.adminbilly.attendancesystem.myJsonRequest;

/**
 * Created by AdminBilly on 2017/4/9.
 */

public class SignUpActivity extends BaseActivity{
    private Toolbar suToolbar;
    private Button button_confirm_sign_up;
    private Button button_cancel_sign_up;
    private EditText input_username;
    private EditText input_password;
    private EditText input_password_again;

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
        input_username = (EditText) findViewById(R.id.username_input);
        input_password = (EditText) findViewById(R.id.password_input);
        input_password_again = (EditText) findViewById(R.id.password_input_again);

        button_confirm_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(input_username.getText().toString().equals("")){
                    Toast.makeText(SignUpActivity.this, "Please input username!",
                            Toast.LENGTH_LONG).show();
                }else if(input_password.getText().toString().equals("")){
                    Toast.makeText(SignUpActivity.this, "Please input password!",
                            Toast.LENGTH_LONG).show();
                }else if(input_password_again.getText().toString().equals("")){
                    Toast.makeText(SignUpActivity.this, "Please input password again!",
                            Toast.LENGTH_LONG).show();
                }else if(!(input_password.getText().toString().equals(input_password_again.getText().toString()))){
                    Toast.makeText(SignUpActivity.this, "The passwords you entered must be the same!",
                            Toast.LENGTH_LONG).show();
                }else{
                    Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
                    JSONObject jsonBody = new JSONObject();
                    try{
                        jsonBody.put("login", input_username.getText().toString());
                        jsonBody.put("password", input_password.getText().toString());
                    }catch (Exception e){

                    }
                    myJsonRequest.signUp(jsonBody, cache, new myJsonRequest.volleyCallback(){
                        @Override
                        public void getResponse(JSONObject response){
                            Log.d("success","1111111111111111111111111111111111111111111111");
                            Intent intent = new Intent(SignUpActivity.this, WelcomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }

                        @Override
                        public void getResponse(VolleyError error){
                            Toast.makeText(SignUpActivity.this, "Sign up failed!",
                                    Toast.LENGTH_LONG).show();
                            Log.d("failed","22222222222222222222222222222222222222222222222");
                        }
                    });
                }

            }
        });

        button_cancel_sign_up.setOnClickListener(new View.OnClickListener() {
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

        input_password_again.setOnFocusChangeListener(new View.OnFocusChangeListener(){
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
