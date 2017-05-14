package com.example.adminbilly.attendancesystem.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    }

    @Override
    protected void onStart(){
        super.onStart();
        //updateTaskViewItem();
    }

    @Override
    protected void onResume(){
        super.onResume();
        updateTaskViewItem();
        adapter = new TaskAdapter(this, R.layout.task_view_item, taskViewItemsList);
        historyTaskView.setAdapter(adapter);
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
        counter_results = 0;
        taskViewItemsList.clear();
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
}
