package com.example.adminbilly.attendancesystem.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
import com.example.adminbilly.attendancesystem.R;

/**
 * Created by AdminBilly on 2017/4/9.
 */

public class LoginActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar lgToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(lgToolbar);

        //使用app bar的导航功能
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button button_confirm_login = (Button)findViewById(R.id.login_confirm);
        Button button_cancel_login = (Button)findViewById(R.id.login_cancel);
        EditText input_username = (EditText)findViewById(R.id.username_input) ;
        EditText input_password = (EditText)findViewById(R.id.password_input) ;

        button_confirm_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestQueue mRequestQueue;

// Instantiate the cache
                Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

// Set up the network to use HttpURLConnection as the HTTP client.
                Network network = new BasicNetwork(new HurlStack());

// Instantiate the RequestQueue with the cache and network.
                mRequestQueue = new RequestQueue(cache, network);

// Start the queue
                mRequestQueue.start();
                String url = "http://10.0.2.2:8080/";

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("TRUE",response);
                                // Do something with the response
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("FALSE",error.toString());
                                // Handle error
                            }
                        });

                mRequestQueue.add(stringRequest);

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
