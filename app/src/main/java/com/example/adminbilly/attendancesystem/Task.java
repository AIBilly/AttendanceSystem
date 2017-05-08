package com.example.adminbilly.attendancesystem;

import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.model.LatLng;

import java.util.Date;

/**
 * Created by AdminBilly on 2017/5/7.
 */

public class Task {
    private int id;
    private String source;
    private String possessor;
    private Date deadline;
    private LatLng location;
    private int state;
    private Date sign_in_time;
    private LatLng sign_in_loc;

    private Marker marker;
    private Overlay range;

    public Task(int i, String s, String p, Date ddl, LatLng ll, int st, Date sit, LatLng sil){
        id = i;
        source = s;
        possessor = p;
        deadline = ddl;
        location = ll;
        state = st;
        sign_in_time = sit;
        sign_in_loc = sil;
        marker = null;
        range = null;
    }

    public int getId(){
        return id;
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

    public Marker getMarker(){
        return marker;
    }

    public Overlay getRange(){
        return range;
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

    public void setMarker(Marker m){
        marker = m;
    }

    public void setRange(Overlay o){
        range = o;
    }
}
