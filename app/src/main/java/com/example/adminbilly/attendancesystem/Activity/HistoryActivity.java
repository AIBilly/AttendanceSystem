package com.example.adminbilly.attendancesystem.Activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
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

public class HistoryActivity extends BaseActivity {

    private Toolbar hToolbar;

    private ListView historyTaskView;

    int finished = R.mipmap.icon_task_accomplished_mark;

    //任务列表
    private ArrayList<Task> mTaskList = mTM.getTaskList();

    private ArrayList<String> TaskAddresses = mTM.getTaskAddresses();

    //ListView显示列表
    private List<TaskViewItem> taskViewItemsList = new ArrayList<TaskViewItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        hToolbar = (Toolbar) findViewById(R.id.history_toolbar);
        setSupportActionBar(hToolbar);

        //使用app bar的导航功能
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        historyTaskView = (ListView)this.findViewById(R.id.history_list);

        updateTaskViewItem();
        TaskAdapter adapter = new TaskAdapter(this, R.layout.task_view_item, taskViewItemsList);
        historyTaskView.setAdapter(adapter);
    }

    private void updateTaskViewItem(){
        taskViewItemsList.clear();
        SimpleDateFormat formatter = new SimpleDateFormat ("MMM dd yyyy HH:mm", Locale.ENGLISH);
        for (int i = 0; i < mTaskList.size(); i++){
            int tempState = mTaskList.get(i).getState();
            if (tempState == 0){
                Date tempDdl = mTaskList.get(i).getDeadline();
                String tempLoc = TaskAddresses.get(i);
                TaskViewItem mTVI = new TaskViewItem(formatter.format(tempDdl), tempLoc, finished);
                taskViewItemsList.add(mTVI);
            }
        }
    }
}
