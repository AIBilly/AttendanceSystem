package com.example.adminbilly.attendancesystem;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.example.adminbilly.attendancesystem.Activity.LoginActivity.id_token;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by AdminBilly on 2017/5/11.
 */

public class myJsonRequest {

    static public void Login(JSONObject jsonBody, Cache cache, final volleyCallback callback){
        jsonPostRequest(Request.Method.POST, "http://120.25.78.93:8080/api/authenticate", jsonBody, cache, callback);
    }

    static public void signUp(JSONObject jsonBody, Cache cache, final volleyCallback callback){
        jsonPostRequest(Request.Method.POST, "http://120.25.78.93:8080/api/register", jsonBody, cache, callback);
    }

    static public void getUser(Cache cache, final volleyCallback callback){
        jsonGetRequest(Request.Method.GET, "http://120.25.78.93:8080/api/account", cache, callback);
    }

    static public void createTask(JSONObject jsonBody, Cache cache, final volleyCallback callback){
        jsonPostRequestWithHeaders(Request.Method.POST, "http://120.25.78.93:8080/api/tasks", jsonBody, cache, callback);
    }

    static public void getTask(Cache cache, final volleyArrayCallback callback){
        jsonMultiGetRequest(Request.Method.GET, "http://120.25.78.93:8080/api/tasks", cache, callback);
    }

    static public void updateTask(JSONObject jsonBody, Cache cache, final volleyCallback callback){
        jsonPostRequestWithHeaders(Request.Method.PUT, "http://120.25.78.93:8080/api/tasks", jsonBody, cache, callback);
    }

    static private void jsonPostRequest(int method, String url, JSONObject jsonBody, Cache cache, final volleyCallback callback){
        RequestQueue mRequestQueue;

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);

        // Start the queue
        mRequestQueue.start();

        JsonObjectRequest stringRequest = new JsonObjectRequest(method, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("TRUE", response.toString());
                        // Do something with the response
                        callback.getResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("FALSE", error.toString());
                        // Handle error
                        callback.getResponse(error);
                    }
                }) {
        };

        mRequestQueue.add(stringRequest);
    }

    static private void jsonPostRequestWithHeaders(int method, String url, JSONObject jsonBody, Cache cache, final volleyCallback callback){
        RequestQueue mRequestQueue;

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);

        // Start the queue
        mRequestQueue.start();

        JsonObjectRequest stringRequest = new JsonObjectRequest(method, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("TRUE", response.toString());
                        // Do something with the response
                        callback.getResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("FALSE", error.toString());
                        // Handle error
                        callback.getResponse(error);
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> Headers = new HashMap<>();
                //Headers = super.getHeaders();
                Headers.put("Authorization", "Bearer " + id_token);
                return Headers;
            }
        };

        mRequestQueue.add(stringRequest);
    }

    static private void jsonGetRequest(int method, String url, Cache cache, final volleyCallback callback){
        RequestQueue mRequestQueue;

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);

        // Start the queue
        mRequestQueue.start();

        JsonObjectRequest stringRequest = new JsonObjectRequest(method, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("TRUE", response.toString());
                        // Do something with the response
                        callback.getResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("FALSE", error.toString());
                        // Handle error
                        callback.getResponse(error);
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> Headers = new HashMap<String,String>();
                //Headers = super.getHeaders();
                Headers.put("Authorization", "Bearer " + id_token);
                return Headers;
            }
        };

        mRequestQueue.add(stringRequest);
    }

    static private void jsonMultiGetRequest(int method, String url, Cache cache, final volleyArrayCallback callback){
        RequestQueue mRequestQueue;

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);

        // Start the queue
        mRequestQueue.start();

        JsonArrayRequest stringRequest = new JsonArrayRequest(method, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("TRUE", response.toString());
                        // Do something with the response
                        callback.getResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("FALSE", error.toString());
                        // Handle error
                        callback.getResponse(error);
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> Headers = new HashMap<>();
                //Headers = super.getHeaders();
                Headers.put("Authorization", "Bearer " + id_token);
                return Headers;
            }
        };

        mRequestQueue.add(stringRequest);
    }

    public interface volleyCallback{
        public void getResponse(JSONObject response);
        public void getResponse(VolleyError error);
    }

    public interface volleyArrayCallback{
        public void getResponse(JSONArray response);
        public void getResponse(VolleyError error);
    }

}


