package com.example.adminbilly.attendancesystem.Fragment;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.adminbilly.attendancesystem.Activity.LoginActivity;
import com.example.adminbilly.attendancesystem.Activity.NewTaskActivity;
import com.example.adminbilly.attendancesystem.Activity.WelcomeActivity;
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
 * Created by AdminBilly on 2017/4/6.
 */

public class FragmentTask extends BaseFragment implements OnGetGeoCoderResultListener {
    GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
    //ListView显示列表
    private List<TaskViewItem> taskViewItemsList = new ArrayList<TaskViewItem>();

    int unfinished = R.mipmap.icon_openmap_mark;
    //获取地点反编码地址计数器
    int counter_tasks;
    int counter_results;

    //任务列表
    private ArrayList<Task> mTaskList = mTM.getTaskList();

    private ArrayList<String> TaskAddresses = mTM.getTaskAddresses();

    private ListView mTaskView;
    private RelativeLayout button_new_task;

    public FragmentTask() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);
        updateTaskViewItem();
        TaskAdapter adapter = new TaskAdapter(getActivity(), R.layout.task_view_item, taskViewItemsList);
        mTaskView.setAdapter(adapter);

        button_new_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FragmentTask.this.getContext(), NewTaskActivity.class);
                startActivity(intent);
            }
        });
    }

    private void updateTaskViewItem(){
        counter_tasks = 0;
        counter_results = 0;
        taskViewItemsList.clear();
        SimpleDateFormat formatter = new SimpleDateFormat ("MMM dd yyyy HH:mm:ss", Locale.ENGLISH);
        for (int i = 0; i < mTaskList.size(); i++){
            int tempState = mTaskList.get(i).getState();
            if (tempState == 0){
                counter_tasks++;
                Date tempDdl = mTaskList.get(i).getDeadline();
                LatLng tempLoc = mTaskList.get(i).getLocation();
                mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(tempLoc));
                Log.d("test",tempLoc.toString());
                TaskViewItem mTVI = new TaskViewItem(formatter.format(tempDdl), null, unfinished);
                taskViewItemsList.add(mTVI);
            }
        }
    }

    private void addTaskAddress(){
        for(int i = 0; i < counter_results; i++){
            TaskViewItem temp = taskViewItemsList.get(i);
            temp.setLocation(TaskAddresses.get(i));
            taskViewItemsList.set(i, temp);
        }
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            counter_results++;
            TaskAddresses.add(null);
            if (counter_tasks == counter_results){
                addTaskAddress();
            }
            return;
        }
        counter_results++;
        TaskAddresses.add(result.getAddress());
        if (counter_tasks == counter_results){
            addTaskAddress();
        }
    }
}
