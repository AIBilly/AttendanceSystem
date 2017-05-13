package com.example.adminbilly.attendancesystem.Activity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.DiskBasedCache;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.adminbilly.attendancesystem.Fragment.FragmentSignIn;
import com.example.adminbilly.attendancesystem.R;
import com.example.adminbilly.attendancesystem.Task;
import com.example.adminbilly.attendancesystem.myJsonRequest;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.TemporalAccessor;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.example.adminbilly.attendancesystem.Activity.LoginActivity.curUser;
import static com.example.adminbilly.attendancesystem.Utils.inSameDay;

/**
 * Created by AdminBilly on 2017/5/8.
 */

public class NewTaskActivity extends BaseActivity implements BaiduMap.OnMapLongClickListener, OnGetGeoCoderResultListener, OnDateSetListener {

    //本地广播数据类型实例
    private LocalBroadcastManager localBroadcastManager;

    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private MyLocationConfiguration.LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    private static final int accuracyCircleFillColor = 0xAAFFFF88;
    private static final int accuracyCircleStrokeColor = 0xAA00FF00;
    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;

    boolean isFirstLoc = true; // 是否首次定位
    private MyLocationData locData;

    private Toolbar ntToolbar;

    private MapView nMapView;
    private BaiduMap nBaiduMap;

    private EditText input_receiver;
    private TextView time_selected;
    private Button button_select_time;
    private TextView location_selected;
    private Button button_confirm_create;

    private ArrayList<Task> mTaskList = mTM.getTaskList();

    //今天的任务
    private ArrayList<Task> mTodayTaskList = mTM.getTodayTaskList();

    //geoSearch相关
    GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用

    BitmapDescriptor bd = BitmapDescriptorFactory
            .fromResource(R.mipmap.icon_gcoding);

    //TimePicker
    TimePickerDialog mDialogAll;
    SimpleDateFormat sf = new SimpleDateFormat ("MMM dd yyyy HH:mm", Locale.ENGLISH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        //获取本地广播实例
        localBroadcastManager = LocalBroadcastManager.getInstance(this);

        ntToolbar = (Toolbar) findViewById(R.id.new_task_toolbar);
        setSupportActionBar(ntToolbar);

        //使用app bar的导航功能
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);

        nMapView = (MapView)findViewById(R.id.nbmapView);

        nBaiduMap = nMapView.getMap();
        nBaiduMap.setOnMapLongClickListener(this);

        // 开启定位图层
        nBaiduMap.setMyLocationEnabled(true);

        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();

        initUI();

        button_select_time.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mDialogAll.show(getSupportFragmentManager(), "all");
            }
        });

        button_confirm_create.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String receiver = input_receiver.getText().toString();
                String deadline = time_selected.getText().toString();
                String location = location_selected.getText().toString();
                if(receiver.equals("")){
                    Toast.makeText(NewTaskActivity.this, "Please input the receiver!",
                            Toast.LENGTH_LONG).show();
                }else if(deadline.equals("")){
                    Toast.makeText(NewTaskActivity.this, "Please select a time!",
                            Toast.LENGTH_LONG).show();
                }else if(location.equals("")){
                    Toast.makeText(NewTaskActivity.this, "Please longclick the map to select a location!",
                            Toast.LENGTH_LONG).show();
                }else{
                    Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
                    JSONObject jsonBody = new JSONObject();
                    try{
                        jsonBody.put("deadline", deadline);
                        jsonBody.put("location", location);
                        jsonBody.put("possessor", receiver);
                        jsonBody.put("signInLoc", null);
                        jsonBody.put("signInTime", null);
                        jsonBody.put("source", curUser);
                        jsonBody.put("state", 0);
                    }catch (Exception e){

                    }
                    myJsonRequest.createTask(jsonBody, cache, new myJsonRequest.volleyCallback(){
                        @Override
                        public void getResponse(JSONObject response){
                            Log.d("success","11111111111111111111111111111111111111111111111");

                            mTaskList.clear();
                            mTodayTaskList.clear();

                            //连接服务器获取任务
                            Cache cache3 = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

                            myJsonRequest.getTask(cache3, new myJsonRequest.volleyArrayCallback(){
                                @Override
                                public void getResponse(JSONArray response){
                                    SimpleDateFormat formatter = new SimpleDateFormat ("MMM dd yyyy HH:mm", Locale.ENGLISH);
                                    try{
                                        for(int i = 0;i < response.length(); i++){
                                            JSONObject jsontemp = response.getJSONObject(i);
                                            String deadline = jsontemp.getString("deadline");
                                            String location = jsontemp.getString("location");
                                            String possessor = jsontemp.getString("possessor");
                                            String signInLoc = jsontemp.getString("signInLoc");
                                            String signInTime = jsontemp.getString("signInTime");
                                            String source = jsontemp.getString("source");
                                            int state = jsontemp.getInt("state");
                                            int id = jsontemp.getInt("id");

                                            String[] locll = location.split(",");
                                            LatLng ll = new LatLng(Double.parseDouble(locll[0]),Double.parseDouble(locll[1]));
                                            Date date1 = formatter.parse(deadline, new ParsePosition(0));
                                            LatLng signll = null;
                                            Date date2 = null;
                                            if(state == 1) {
                                                locll = signInLoc.split(",");
                                                signll = new LatLng(Double.parseDouble(locll[0]), Double.parseDouble(locll[1]));
                                                date2 = formatter.parse(signInTime, new ParsePosition(0));
                                            }

                                            Task temp = new Task( id, i, source, possessor, date1, ll, state, date2, signll);
                                            mTaskList.add(temp);
                                            Date curDate = new Date(System.currentTimeMillis());
                                            Log.e("11111111111111111",mTaskList.get(i).getSource());
                                            if (inSameDay(curDate, date1)) {
                                                mTodayTaskList.add(temp);
                                            }
                                        }
                                    }catch (Exception e){

                                    }

                                    Intent intent = new Intent("com.example.adminbilly.updateUI.LOCAL_BROADCAST");
                                    //发送本地广播
                                    localBroadcastManager.sendBroadcast(intent);

                                }

                                @Override
                                public void getResponse(VolleyError error){
                                    Log.e("failed","222222222222222222222");
                                }
                            });
                        }

                        @Override
                        public void getResponse(VolleyError error){
                            Log.e("failed","22222222222222222222222222222222222222222222222");
                        }
                    });

                    finish();

                }
            }
        });

        long tenYears = 10L * 365 * 1000 * 60 * 60 * 24L;
        mDialogAll = new TimePickerDialog.Builder()
                .setCallBack(this)
                .setCancelStringId("Cancel")
                .setSureStringId("Sure")
                .setTitleStringId("TimePicker")
                .setYearText("Year")
                .setMonthText("Month")
                .setDayText("Day")
                .setHourText("Hour")
                .setMinuteText("Minute")
                .setCyclic(false)
                .setMinMillseconds(System.currentTimeMillis())
                .setMaxMillseconds(System.currentTimeMillis() + tenYears)
                .setCurrentMillseconds(System.currentTimeMillis())
                .setThemeColor(getResources().getColor(R.color.timepicker_dialog_bg))
                .setType(Type.ALL)
                .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
                .setWheelItemTextSelectorColor(getResources().getColor(R.color.timepicker_toolbar_bg))
                .setWheelItemTextSize(12)
                .build();
    }

    private void initUI() {
        input_receiver = (EditText)findViewById(R.id.new_task_receiver);
        time_selected = (TextView)findViewById(R.id.new_task_deadline);
        button_select_time = (Button)findViewById(R.id.new_task_time_picker);
        location_selected = (TextView)findViewById(R.id.new_task_location);
        button_confirm_create = (Button)findViewById(R.id.new_task_confirm);
    }

    /**
     * 定位SDK监听函数
     */
    private class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || nMapView == null) {
                return;
            }
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAccracy = location.getRadius();
            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            nBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                nBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }

        public void onConnectHotSpotMessage(String s, int i){

        }
    }

    @Override
    public void onMapLongClick(LatLng point) {
        // 反Geo搜索
        mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                .location(point));
        // TODO Auto-generated method stub
        location_selected.setText(String.valueOf(point.latitude) + "," + String.valueOf(point.longitude));
        MarkerOptions ooA = new MarkerOptions().position(point).icon(bd);
        nBaiduMap.clear();
        nBaiduMap.addOverlay(ooA);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(point);
        nBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "Sorry, no suitable results.", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        //mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
        //.getLocation()));
        Toast.makeText(this, result.getAddress(),
                Toast.LENGTH_LONG).show();

    }

    @Override
    public void onDateSet(TimePickerDialog timePickerDialog, long millseconds) {
        String text = getDateToString(millseconds);
        time_selected.setText(text);
    }

    public String getDateToString(long time) {
        Date d = new Date(time);
        return sf.format(d);
    }


    @Override
    protected void onPause() {
        nMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        nMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        nBaiduMap.setMyLocationEnabled(false);
        nMapView.onDestroy();
        nMapView = null;
        super.onDestroy();
    }
}
