package com.example.adminbilly.attendancesystem.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.adminbilly.attendancesystem.Activity.NewTaskActivity;
import com.example.adminbilly.attendancesystem.R;
import com.example.adminbilly.attendancesystem.Task;
import com.example.adminbilly.attendancesystem.TaskView.TaskAdapter;
import com.example.adminbilly.attendancesystem.TaskView.TaskViewItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.os.Handler;
import android.widget.Toast;

/**
 * Created by AdminBilly on 2017/4/6.
 */

public class FragmentTask extends BaseFragment implements OnGetGeoCoderResultListener {

    public static final int UPDATE_ADDRESS = 1;

    private IntentFilter intentFilter;
    private LocalReceiver localReceiver;

    //本地广播数据类型实例
    private LocalBroadcastManager localBroadcastManager;

    //获取地点反编码地址计数器
    int counter_results;

    GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
    //ListView显示列表
    private List<TaskViewItem> taskViewItemsList = new ArrayList<TaskViewItem>();

    int unfinished = R.mipmap.icon_openmap_mark;

    //任务列表
    private ArrayList<Task> mTaskList = mTM.getTaskList();

    private ListView mTaskView;
    private RelativeLayout button_new_task;

    TaskAdapter adapter;

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

    public FragmentTask() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onDestroy(){//在onDestroy()方法中取消注册
        super.onDestroy();
        //取消注册调用的是unregisterReceiver()方法并传入接收器实例
        localBroadcastManager.unregisterReceiver(localReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task, container, false);
        mTaskView = (ListView)view.findViewById(R.id.task_view);
        button_new_task = (RelativeLayout)view.findViewById(R.id.new_task_button);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //获取本地广播实例
        localBroadcastManager = LocalBroadcastManager.getInstance(this.getActivity());

        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);

        adapter = new TaskAdapter(getActivity(), R.layout.task_view_item, taskViewItemsList);
        mTaskView.setAdapter(adapter);

        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.adminbilly.updateUI.LOCAL_BROADCAST");

        //创建广播接收器实例并注册将其接收器与action标签进行绑定
        localReceiver = new LocalReceiver();
        localBroadcastManager.registerReceiver(localReceiver,intentFilter);

        button_new_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FragmentTask.this.getContext(), NewTaskActivity.class);
                startActivity(intent);
            }
        });
    }

    private void updateTaskViewItem(){
        counter_results = 0;
        taskViewItemsList.clear();
        SimpleDateFormat formatter = new SimpleDateFormat ("MMM dd yyyy HH:mm", Locale.ENGLISH);
        for (int i = 0; i < mTaskList.size(); i++){
            int tempState = mTaskList.get(i).getState();
            if (tempState == 0){
                Date tempDdl = mTaskList.get(i).getDeadline();
                LatLng tempLoc = mTaskList.get(i).getLocation();
                TaskViewItem mTVI = new TaskViewItem(formatter.format(tempDdl), null, unfinished);
                taskViewItemsList.add(mTVI);
                mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(tempLoc));
            }
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

    class  LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            updateTaskViewItem();
        }
    }

}
