package com.example.adminbilly.attendancesystem;

import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.model.LatLng;

import java.util.Date;

/**
 * Created by AdminBilly on 2017/5/7.
 */

public class Task {
    private int id;//任务ID
    private int index;//从服务器读取下来的任务编号
    private String source;//任务发布者用户名
    private String possessor;//任务接受者用户名
    private Date deadline;//任务截止日期
    private LatLng location;//任务地点
    private int state;//任务状态
    private Date sign_in_time;//任务签到时间
    private LatLng sign_in_loc;//任务签到地点

    public Task(int i, int in, String s, String p, Date ddl, LatLng ll, int st, Date sit, LatLng sil){
        id = i;
        index = in;
        source = s;
        possessor = p;
        deadline = ddl;
        location = ll;
        state = st;
        sign_in_time = sit;
        sign_in_loc = sil;
    }

    public int getId(){
        return id;
    }

    public int getIndex(){
        return index;
    }

    public String getSource(){
        return source;
    }

    public String getPossessor(){
        return possessor;
    }

    public Date getDeadline(){
        return deadline;
    }

    public LatLng getLocation(){
        return location;
    }

    public int getState(){
        return state;
    }

    public Date getSign_in_time(){
        return sign_in_time;
    }

    public LatLng getSign_in_loc(){
        return sign_in_loc;
    }

    public void setId(int i){
        id = i;
    }

    public void setSource(String s){
        source = s;
    }

    public void setPossessor(String p){
        possessor = p;
    }

    public void setDeadline(Date d){
        deadline = d;
    }

    public void setLocation(LatLng ll){
        location = ll;
    }

    public void setState(int s){
        state = s;
    }

    public void setSign_in_time(Date d){
        sign_in_time = d;
    }

    public void setSign_in_loc(LatLng ll){
        sign_in_loc = ll;
    }
}
