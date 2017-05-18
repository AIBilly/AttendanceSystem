package com.example.adminbilly.attendancesystem.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.DiskBasedCache;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.adminbilly.attendancesystem.R;
import com.example.adminbilly.attendancesystem.Task;
import com.example.adminbilly.attendancesystem.TaskView.TaskAdapter;
import com.example.adminbilly.attendancesystem.TaskView.TaskViewItem;
import com.example.adminbilly.attendancesystem.myJsonRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.adminbilly.attendancesystem.Activity.LoginActivity.curUser;

/**
 * Created by AdminBilly on 2017/5/9.
 */

public class HistoryActivity extends BaseActivity implements OnGetGeoCoderResultListener {

    public static final int UPDATE_ADDRESS = 1;

    GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用

    //获取地点反编码地址计数器
    int counter_results;

    private Toolbar hToolbar;

    private ListView historyTaskView;
    private LinearLayout admin_bar;
    private EditText input_username;
    private Button look_up;

    int finished = R.mipmap.icon_task_accomplished_mark;

    //任务列表
    private ArrayList<Task> mTaskList = mTM.getTaskList();

    //ListView显示列表
    private List<TaskViewItem> taskViewItemsList = new ArrayList<TaskViewItem>();

    TaskAdapter adapter = null;

    private Handler handler = new Handler(){

        public void handleMessage(Message msg){
            switch (msg.what){
                case UPDATE_ADDRESS:
                    String str = msg.getData().getString("result");
                    TaskViewItem temp = taskViewItemsList.get(counter_results);
                    temp.setLocation(str);
                    adapter.notifyDataSetChanged();
                    counter_results++;
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        hToolbar = (Toolbar) findViewById(R.id.history_toolbar);
        setSupportActionBar(hToolbar);

        //使用app bar的导航功能
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);

        historyTaskView = (ListView)this.findViewById(R.id.history_list);
        admin_bar = (LinearLayout)this.findViewById(R.id.admin_look_up_bar);
        input_username = (EditText) this.findViewById(R.id.username_input);
        look_up = (Button) this.findViewById(R.id.look_up);

        look_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter_results = 0;
                taskViewItemsList.clear();
                if(input_username.getText().toString().equals("")){
                    Toast.makeText(HistoryActivity.this, "Please input employee's username!",
                            Toast.LENGTH_LONG).show();
                }else{
                    String e_username = input_username.getText().toString();

                    //连接服务器获取任务
                    Cache cache3 = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

                    myJsonRequest.getTask(cache3, new myJsonRequest.volleyArrayCallback(){
                        @Override
                        public void getResponse(JSONArray response){

                            SimpleDateFormat formatter = new SimpleDateFormat ("MMM dd yyyy HH:mm", Locale.ENGLISH);
                            try{
                                int flag = 0;
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

                                    if(possessor.equals(e_username) && state == 1){
                                        flag = 1;
                                        String[] locll = location.split(",");
                                        LatLng signll = null;
                                        locll = signInLoc.split(",");
                                        signll = new LatLng(Double.parseDouble(locll[0]), Double.parseDouble(locll[1]));

                                        TaskViewItem mTVI = new TaskViewItem(signInTime, null, finished);
                                        taskViewItemsList.add(mTVI);
                                        adapter = new TaskAdapter(HistoryActivity.this, R.layout.task_view_item, taskViewItemsList);
                                        historyTaskView.setAdapter(adapter);
                                        mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(signll));
                                    }
                                }
                                if(flag == 0){
                                    Toast.makeText(HistoryActivity.this, "No sign in history!",
                                            Toast.LENGTH_LONG).show();
                                }
                            }catch (Exception e){

                            }

                        }

                        @Override
                        public void getResponse(VolleyError error){
                            Toast.makeText(HistoryActivity.this, "Failed to get task list!",
                                    Toast.LENGTH_LONG).show();
                            Log.e("failed","222222222222222222222");
                        }
                    });
                }
            }
        });

        if(!curUser.equals("admin")){
            admin_bar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        //updateTaskViewItem();
    }

    @Override
    protected void onResume(){
        super.onResume();
        counter_results = 0;
        taskViewItemsList.clear();
        if(!curUser.equals("admin")){
            updateTaskViewItem();
            adapter = new TaskAdapter(this, R.layout.task_view_item, taskViewItemsList);
            historyTaskView.setAdapter(adapter);
        }
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        Message message = new Message();
        message.what = UPDATE_ADDRESS;
        Bundle bundle = new Bundle();
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            bundle.putString("result","");
            message.setData(bundle);
            handler.sendMessage(message);
            return;
        }
        bundle.putString("result",result.getAddress());
        message.setData(bundle);
        handler.sendMessage(message);
    }

    private void updateTaskViewItem(){
        SimpleDateFormat formatter = new SimpleDateFormat ("MMM dd yyyy HH:mm", Locale.ENGLISH);
        for (int i = 0; i < mTaskList.size(); i++){
            int tempState = mTaskList.get(i).getState();
            if (tempState == 1){
                Date tempDdl = mTaskList.get(i).getSign_in_time();
                LatLng tempLoc = mTaskList.get(i).getLocation();
                TaskViewItem mTVI = new TaskViewItem(formatter.format(tempDdl), null, finished);
                taskViewItemsList.add(mTVI);
                mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(tempLoc));
            }
        }
    }

    private void updateLookUpTaskViewItem(){

    }
}
