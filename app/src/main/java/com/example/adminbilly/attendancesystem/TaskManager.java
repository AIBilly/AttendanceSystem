package com.example.adminbilly.attendancesystem;

import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.adminbilly.attendancesystem.Fragment.FragmentTask;

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
    //任务的GeoCoder反编码得到的地址
    private ArrayList<String> TaskAddresses = new ArrayList<String>();

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
        TaskList.clear();
        TaskAddresses.clear();
        //连接服务器获取任务
        for (int i = 0; i < 10; i++){
            SimpleDateFormat formatter = new SimpleDateFormat ("MMM dd yyyy HH:mm:ss", Locale.ENGLISH);
            Date date1 = formatter.parse("May 08 2017 23:59:59",new ParsePosition(0));
            LatLng ll = new LatLng(39.969925972732035, 116.36477099614513);
            Task tempTask = new Task( i, i, "Billy", "Billy", date1, ll, 0, null, null);
            TaskList.add(tempTask);
        }
    }

    private void updateTodayTaskList(){
        todayTaskList.clear();
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

    public ArrayList<String> getTaskAddresses(){
        return TaskAddresses;
    }

    public void setTaskListElement(int index){
        int id = todayTaskList.get(index).getIndex();
        TaskList.set(id, todayTaskList.get(index));
    }
}
