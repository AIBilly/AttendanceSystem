package com.example.adminbilly.attendancesystem.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.DiskBasedCache;
import com.baidu.mapapi.model.LatLng;
import com.example.adminbilly.attendancesystem.Fragment.FragmentMe;
import com.example.adminbilly.attendancesystem.Fragment.FragmentSignIn;
import com.example.adminbilly.attendancesystem.Fragment.FragmentTask;
import com.example.adminbilly.attendancesystem.R;
import com.example.adminbilly.attendancesystem.TabEntity;
import com.example.adminbilly.attendancesystem.Task;
import com.example.adminbilly.attendancesystem.TaskManager;
import com.example.adminbilly.attendancesystem.myJsonRequest;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.example.adminbilly.attendancesystem.Activity.LoginActivity.curUser;
import static com.example.adminbilly.attendancesystem.Utils.inSameDay;

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

    private ArrayList<Task> mTaskList = mTM.getTaskList();

    //今天的任务
    private ArrayList<Task> mTodayTaskList = mTM.getTodayTaskList();

    //本地广播数据类型实例
    private LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //获取本地广播实例
        localBroadcastManager = LocalBroadcastManager.getInstance(this);

        Toolbar mToolbarTb = (Toolbar) findViewById(R.id.tb_toolbar);
        setSupportActionBar(mToolbarTb);

        initView();
        initData();

        //给tab设置数据和关联的fragment
        mTabLayout.setTabData(mTabEntities, this, R.id.fl_change, mFragments);

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

                        if(possessor.equals(curUser)){
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
                    }
                }catch (Exception e){

                }

                Intent intent=new Intent("com.example.adminbilly.updateUI.LOCAL_BROADCAST");
                //发送本地广播
                localBroadcastManager.sendBroadcast(intent);

            }

            @Override
            public void getResponse(VolleyError error){
                Toast.makeText(MainActivity.this, "Failed to get task list!",
                        Toast.LENGTH_LONG).show();
                Log.e("failed","222222222222222222222");
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();

    }

    private void initView() {
        mTabLayout = (CommonTabLayout) findViewById(R.id.tl);
    }

    private void initData() {
        mTaskList.clear();
        mTodayTaskList.clear();
        mFragments.add(new FragmentTask());
        mFragments.add(new FragmentSignIn());
        mFragments.add(new FragmentMe());
        //设置tab的标题、选中图标、未选中图标
        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }
    }
}
