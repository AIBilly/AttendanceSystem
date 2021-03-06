package com.example.adminbilly.attendancesystem;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.DiskBasedCache;
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

import org.json.JSONObject;

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

    private static class TaskManagerHolder{//用于创建单子
        private static final TaskManager instance = new TaskManager();
    }

    private TaskManager(){
        TaskList.clear();
        todayTaskList.clear();
    }

    public static final TaskManager getInstance(){//获取实例
        return TaskManagerHolder.instance;
    }

    public ArrayList<Task> getTaskList(){
        return TaskList;
    }

    public ArrayList<Task> getTodayTaskList(){
        return todayTaskList;
    }

    public void modifyTaskListByTodayTaskList(int i){
        TaskList.set(todayTaskList.get(i).getIndex(), todayTaskList.get(i));
    }
}
