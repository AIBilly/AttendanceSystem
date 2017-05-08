package com.example.adminbilly.attendancesystem;

import android.util.Log;

import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.model.LatLng;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.example.adminbilly.attendancesystem.Utils.inSameDay;

/**
 * Created by AdminBilly on 2017/5/7.
 */

public class TaskManager {
    //全部的任务
    private ArrayList<Task> TaskList = new ArrayList<Task>();
    //当天的任务
    private ArrayList<Task> todayTaskList = new ArrayList<Task>();

    private static class TaskManagerHolder{
        private static final TaskManager instance = new TaskManager();
    }

    private TaskManager(){
        updateTaskList();
        updateTodayTaskList();
    }

    public static final TaskManager getInstance(){
        return TaskManagerHolder.instance;
    }

    private void updateTaskList(){
        //连接服务器获取任务
        for (int i = 0; i < 1; i++){
            SimpleDateFormat formatter = new SimpleDateFormat ("MMM dd yyyy HH:mm:ss", Locale.ENGLISH);
            Date date1 = formatter.parse("May 08 2017 23:59:59",new ParsePosition(0));
            LatLng ll = new LatLng(39.969925972732035, 116.36477099614513);
            Task tempTask = new Task(i, "Billy", "Billy", date1, ll, 0, null, null);
            TaskList.add(tempTask);
        }
    }

    private void updateTodayTaskList(){
        Date curDate = new Date(System.currentTimeMillis());
        for (int i = 0; i < TaskList.size(); i++){
            if (inSameDay(curDate, TaskList.get(i).getDeadline())){
                todayTaskList.add(TaskList.get(i));
            }
        }
    }

    public ArrayList<Task> getTaskList(){
        return TaskList;
    }

    public ArrayList<Task> getTodayTaskList(){
        return todayTaskList;
    }

    public void setTaskList(ArrayList<Task> arr){
        TaskList = arr;
    }

    public void setTaskListElement(int index){
        int id = todayTaskList.get(index).getId();
        TaskList.set(id, todayTaskList.get(index));
    }
}
