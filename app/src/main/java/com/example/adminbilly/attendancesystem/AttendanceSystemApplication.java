package com.example.adminbilly.attendancesystem;

import com.baidu.mapapi.SDKInitializer;
import com.example.adminbilly.attendancesystem.BaiduMap.LocationService;
import com.jakewharton.threetenabp.AndroidThreeTen;

import android.app.Application;
import android.app.Service;
import android.os.Vibrator;
/**
 * Created by AdminBilly on 2017/5/5.
 */

public class AttendanceSystemApplication extends Application {
    public LocationService locationService;
    public Vibrator mVibrator;
    @Override
    public void onCreate() {
        super.onCreate();
        /***
         * 初始化定位sdk，建议在Application中创建
         */
        locationService = new LocationService(getApplicationContext());
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        SDKInitializer.initialize(getApplicationContext());
        AndroidThreeTen.init(this);
    }
}
