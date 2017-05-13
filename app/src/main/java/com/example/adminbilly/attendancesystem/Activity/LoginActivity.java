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
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.baidu.mapapi.model.LatLng;
import com.example.adminbilly.attendancesystem.R;
import com.example.adminbilly.attendancesystem.Task;
import com.example.adminbilly.attendancesystem.myJsonRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.example.adminbilly.attendancesystem.Utils.inSameDay;

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

    //本地广播数据类型实例
    private LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mTaskList.clear();
        mTodayTaskList.clear();

        //获取本地广播实例
        localBroadcastManager = LocalBroadcastManager.getInstance(this);

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
                //登录
                Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
                JSONObject jsonBody = new JSONObject();
                try{
                    jsonBody.put("username", "admin");
                    jsonBody.put("password", "admin");
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

                                //连接服务器获取任务
                                Cache cache3 = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

                                myJsonRequest.getTask(cache3, new myJsonRequest.volleyArrayCallback(){
                                    @Override
                                    public void getResponse(JSONArray response){
                                        SimpleDateFormat formatter = new SimpleDateFormat ("MMM dd yyyy HH:mm", Locale.ENGLISH);
                                        try{
                                            for(int i = 0;i < response.length(); i++){
                                                JSONObject jsontemp = response.getJSONObject(i);
                                                String deadline = jsontemp.getString("deadline");
                                                String location = jsontemp.getString("location");
                                                String possessor = jsontemp.getString("possessor");
                                                String signInLoc = jsontemp.getString("signInLoc");
                                                String signInTime = jsontemp.getString("signInTime");
                                                String source = jsontemp.getString("source");
                                                int state = jsontemp.getInt("state");
                                                int id = jsontemp.getInt("id");

                                                String[] locll = location.split(",");
                                                LatLng ll = new LatLng(Double.parseDouble(locll[0]),Double.parseDouble(locll[1]));
                                                Date date1 = formatter.parse(deadline, new ParsePosition(0));
                                                LatLng signll = null;
                                                Date date2 = null;
                                                if(state == 1) {
                                                    locll = signInLoc.split(",");
                                                    signll = new LatLng(Double.parseDouble(locll[0]), Double.parseDouble(locll[1]));
                                                    date2 = formatter.parse(signInTime, new ParsePosition(0));
                                                }

                                                Task temp = new Task( id, i, source, possessor, date1, ll, state, date2, signll);
                                                mTaskList.add(temp);
                                                Date curDate = new Date(System.currentTimeMillis());
                                                Log.e("11111111111111111",mTaskList.get(i).getSource());
                                                if (inSameDay(curDate, date1)) {
                                                    mTodayTaskList.add(temp);
                                                }
                                            }
                                        }catch (Exception e){

                                        }

                                        Intent intent=new Intent("com.example.adminbilly.updateUI.LOCAL_BROADCAST");
                                        //发送本地广播
                                        localBroadcastManager.sendBroadcast(intent);

                                    }

                                    @Override
                                    public void getResponse(VolleyError error){
                                        Log.e("failed","222222222222222222222");
                                    }
                                });

                            }

                            @Override
                            public void getResponse(VolleyError error){
                                Log.e("failed","22222222222222222222222222222222222222222222222");
                            }
                        });

                    }

                    @Override
                    public void getResponse(VolleyError error){
                        Log.e("failed","22222222222222222222222222222222222222222222222");
                    }
                });

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        button_cancel_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
