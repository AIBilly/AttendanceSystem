package com.example.adminbilly.attendancesystem.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.adminbilly.attendancesystem.BaiduMap.MarkerAndRange;
import com.example.adminbilly.attendancesystem.R;
import com.example.adminbilly.attendancesystem.Task;
import com.example.adminbilly.attendancesystem.TaskManager;
import com.example.adminbilly.attendancesystem.myJsonRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import at.markushi.ui.CircleButton;

import static android.content.Context.SENSOR_SERVICE;
import static com.example.adminbilly.attendancesystem.Activity.LoginActivity.curUser;
import static com.example.adminbilly.attendancesystem.Utils.inSameDay;

/**
 * Created by AdminBilly on 2017/4/6.
 */

public class FragmentSignIn extends BaseFragment implements SensorEventListener, OnGetGeoCoderResultListener,
        BaiduMap.OnMapLongClickListener, BaiduMap.OnMarkerClickListener, BaiduMap.OnMapClickListener {

    private IntentFilter intentFilter;
    private LocalReceiver localReceiver;

    //本地广播数据类型实例
    private LocalBroadcastManager localBroadcastManager;

    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private MyLocationConfiguration.LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;

    MapView mMapView;
    BaiduMap mBaiduMap;

    // UI相关
    private View mInfoWindowView;
    private TextView mTextView;

    InfoWindow mInfoWindow;

    Button requestLocButton;
    CircleButton signInButton;
    boolean isFirstLoc = true; // 是否首次定位
    private MyLocationData locData;
    private float direction;

    //Marker相关
    BitmapDescriptor task_un = BitmapDescriptorFactory
            .fromResource(R.mipmap.icon_openmap_mark);
    BitmapDescriptor task_ac = BitmapDescriptorFactory
            .fromResource(R.mipmap.icon_task_accomplished_mark);
    BitmapDescriptor solid_marker = BitmapDescriptorFactory
            .fromResource(R.mipmap.icon_gcoding);

    //geoSearch相关
    GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用

    //长按地图相关
    // 保存点中的点id
    Marker preMarker = null; //前一个Marker

    //今天的任务
    private ArrayList<Task> mTodayTaskList = mTM.getTodayTaskList();

    //任务的Marker和Range
    private ArrayList<MarkerAndRange> mTaskOverlayList = new ArrayList<MarkerAndRange>();

    //轨迹点
    private ArrayList<LatLng> trackLocList = new ArrayList<LatLng>();

    Polyline mTrackline = null;

    public FragmentSignIn() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        mMapView = (MapView)view.findViewById(R.id.bmapView);
        requestLocButton = (Button) view.findViewById(R.id.mode_switch);
        signInButton = (CircleButton) view.findViewById(R.id.sign_in_button);
        LayoutInflater mInflater = getActivity().getLayoutInflater();
        mInfoWindowView = (View) mInflater.inflate(R.layout.fragment_sign_in_infowindow, null, false);
        mTextView = (TextView)mInfoWindowView.findViewById(R.id.sign_in_info_textview);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //获取本地广播实例
        localBroadcastManager = LocalBroadcastManager.getInstance(this.getActivity());

        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.adminbilly.updateUI.LOCAL_BROADCAST");

        //创建广播接收器实例并注册将其接收器与action标签进行绑定
        localReceiver = new LocalReceiver();
        localBroadcastManager.registerReceiver(localReceiver,intentFilter);

        initBaiduMap();

        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);//获取传感器管理服务
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;

        requestLocButton.setText(getString(R.string.normal));
        View.OnClickListener ReqBtnClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (mCurrentMode) {
                    case NORMAL:
                        requestLocButton.setText(getString(R.string.follow));
                        mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
                        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                                mCurrentMode, true, mCurrentMarker));
                        MapStatus.Builder builder = new MapStatus.Builder();
                        builder.overlook(0);
                        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                        break;
                    case COMPASS:
                        requestLocButton.setText(getString(R.string.normal));
                        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
                        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                                mCurrentMode, true, mCurrentMarker));
                        MapStatus.Builder builder1 = new MapStatus.Builder();
                        builder1.overlook(0);
                        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder1.build()));
                        break;
                    case FOLLOWING:
                        requestLocButton.setText(getString(R.string.compass));
                        mCurrentMode = MyLocationConfiguration.LocationMode.COMPASS;
                        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                                mCurrentMode, true, mCurrentMarker));
                        break;
                    default:
                        break;
                }
            }
        };
        requestLocButton.setOnClickListener(ReqBtnClickListener);

        View.OnClickListener CircleBtnClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                SimpleDateFormat formatter = new SimpleDateFormat ("MMM dd yyyy HH:mm", Locale.ENGLISH);
                Date curDate = new Date(System.currentTimeMillis());
                LatLng currentPos = new LatLng(mCurrentLat, mCurrentLon);
                int flag = 0;
                for (int i = 0; i < mTodayTaskList.size(); i++){
                    Task tempTask = mTodayTaskList.get(i);
                    MarkerAndRange tempMar = mTaskOverlayList.get(i);
                    if (tempTask.getState() == 0){
                        double distance = DistanceUtil.getDistance(currentPos, tempTask.getLocation());
                        if(distance <= 100){
                            tempTask.setState(1);
                            tempTask.setSign_in_loc(currentPos);
                            tempTask.setSign_in_time(curDate);

                            mTM.modifyTaskListByTodayTaskList(i);

                            int id = tempTask.getId();
                            String source = tempTask.getSource();
                            String possessor = tempTask.getPossessor();
                            String deadline = formatter.format(tempTask.getDeadline());
                            String location = String.valueOf(tempTask.getLocation().latitude) + "," + String.valueOf(tempTask.getLocation().longitude);
                            String signInTime = formatter.format(tempTask.getSign_in_time());
                            String signInLoc = String.valueOf(tempTask.getSign_in_loc().latitude) + "," + String.valueOf(tempTask.getSign_in_loc().longitude);

                            tempMar.getMarker().remove();
                            MarkerOptions ooA = new MarkerOptions().position(currentPos).icon(task_ac)
                                    .zIndex(9).draggable(false);
                            Marker mMarker = (Marker) (mBaiduMap.addOverlay(ooA));
                            tempMar.setMarker(mMarker);
                            tempMar.getRange().remove();
                            tempMar.setRange(null);

                            Cache cache = new DiskBasedCache(FragmentSignIn.this.getActivity().getCacheDir(), 1024 * 1024); // 1MB cap
                            JSONObject jsonBody = new JSONObject();
                            try{
                                jsonBody.put("id", id);
                                jsonBody.put("deadline", deadline);
                                jsonBody.put("location", location);
                                jsonBody.put("possessor", possessor);
                                jsonBody.put("signInLoc", signInLoc);
                                jsonBody.put("signInTime", signInTime);
                                jsonBody.put("source", source);
                                jsonBody.put("state", 1);
                            }catch (Exception e){

                            }
                            myJsonRequest.updateTask(jsonBody, cache, new myJsonRequest.volleyCallback(){
                                @Override
                                public void getResponse(JSONObject response){
                                    try{
                                        Log.e("succeed","11111111111111111111111111111111111111111111111");
                                    }catch (Exception e){

                                    }

                                    Intent intent = new Intent("com.example.adminbilly.updateUI.LOCAL_BROADCAST");
                                    //发送本地广播
                                    localBroadcastManager.sendBroadcast(intent);

                                }

                                @Override
                                public void getResponse(VolleyError error){
                                    Log.e("failed","22222222222222222222222222222222222222222222222");
                                }
                            });

                            String str = getString(R.string.sign_in_time) + " " + formatter.format(curDate);
                            mTextView.setText(str);
                            mInfoWindow = new InfoWindow(mInfoWindowView, currentPos, -47);
                            mBaiduMap.showInfoWindow(mInfoWindow);
                            Toast.makeText(FragmentSignIn.this.getActivity(), "Signed in.",
                                    Toast.LENGTH_LONG).show();
                            flag = 1;
                            break;
                        }
                    }
                }
                if(flag == 0){
                    Toast.makeText(FragmentSignIn.this.getActivity(), "Out of sign in range.",
                            Toast.LENGTH_LONG).show();
                }
            }
        };
        signInButton.setOnClickListener(CircleBtnClickListener);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void initBaiduMap(){
        mMapView.removeViewAt(2);
        mBaiduMap = mMapView.getMap();

        // 初始化地图点击监听
        mBaiduMap.setOnMapLongClickListener(this);
        mBaiduMap.setOnMarkerClickListener(this);
        mBaiduMap.setOnMapClickListener(this);

        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);

        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        // 定位初始化
        mLocClient = new LocationClient(this.getContext());
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(5000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    private void drawRangeAndMarker(){

        for(int i = 0; i < mTaskOverlayList.size(); i++){
            mTaskOverlayList.get(i).getMarker().remove();
            if(mTaskOverlayList.get(i).getRange() != null)
                mTaskOverlayList.get(i).getRange().remove();
        }

        mTaskOverlayList.clear();

        Date curDate = new Date(System.currentTimeMillis());

        for (int i = 0; i < mTodayTaskList.size(); i++){
            if (inSameDay(curDate, mTodayTaskList.get(i).getDeadline())){
                int state = mTodayTaskList.get(i).getState();

                if (state == 0){
                    LatLng taskLoc = mTodayTaskList.get(i).getLocation();
                    OverlayOptions ooCircle = new CircleOptions().fillColor(0x661fb293)
                            .center(taskLoc).stroke(new Stroke(5, 0x00FFFFFF))
                            .radius(100);
                    Overlay mRange = mBaiduMap.addOverlay(ooCircle);

                    MarkerOptions ooA = new MarkerOptions().position(taskLoc).icon(task_un)
                            .zIndex(9).draggable(false);
                    Marker mMarker = (Marker) (mBaiduMap.addOverlay(ooA));

                    MarkerAndRange mAR = new MarkerAndRange(mMarker, mRange);
                    mTaskOverlayList.add(mAR);

                }else if (state == 1){
                    LatLng taskLoc = mTodayTaskList.get(i).getSign_in_loc();

                    MarkerOptions ooA = new MarkerOptions().position(taskLoc).icon(task_ac)
                            .zIndex(9).draggable(false);
                    Marker mMarker = (Marker) (mBaiduMap.addOverlay(ooA));

                    MarkerAndRange mAR = new MarkerAndRange(mMarker, null);
                    mTaskOverlayList.add(mAR);
                }
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
            locData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(mCurrentLat)
                    .longitude(mCurrentLon).build();
            mBaiduMap.setMyLocationData(locData);
        }
        lastX = x;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    /**
     * 定位SDK监听函数
     */
    private class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
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
            mBaiduMap.setMyLocationData(locData);
            if(trackLocList.size() > 720){
                trackLocList.remove(0);
            }
            LatLng ll = new LatLng(location.getLatitude(),
                    location.getLongitude());
            trackLocList.add(ll);
            if (isFirstLoc) {
                isFirstLoc = false;
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }else{
                if(mTrackline != null)
                    mTrackline.remove();
                OverlayOptions ooPolyline = new PolylineOptions().width(30)
                        .color(0xAAFF0000).points(trackLocList);
                mTrackline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }

        public void onConnectHotSpotMessage(String s, int i){

        }
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(FragmentSignIn.this.getActivity(), "Sorry, no suitable results.", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        //mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
                //.getLocation()));
        Toast.makeText(FragmentSignIn.this.getActivity(), result.getAddress(),
                Toast.LENGTH_LONG).show();

    }

    @Override
    public void onMapLongClick(LatLng point) {
        // TODO Auto-generated method stub
        Marker TempMarker;
        if(preMarker != null){
            preMarker.remove();
        }
        MarkerOptions ooA = new MarkerOptions().position(point).icon(solid_marker);
        TempMarker = (Marker) (mBaiduMap.addOverlay(ooA));
        // 反Geo搜索
        mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                .location(point));
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(point);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        preMarker = TempMarker;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        mBaiduMap.hideInfoWindow();
        // TODO Auto-generated method stub
        if (marker == null) {
            return false;
        }
        for (int i = 0; i < mTodayTaskList.size(); i++){
            Task tempTask = mTodayTaskList.get(i);
            MarkerAndRange tempMar = mTaskOverlayList.get(i);
            if (marker == tempMar.getMarker()){
                if (tempTask.getState() == 0){
                    LatLng ptCenter = marker.getPosition();
                    // 反Geo搜索
                    mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                            .location(ptCenter));
                    MapStatus.Builder builder = new MapStatus.Builder();
                    builder.target(ptCenter);
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                }else if (tempTask.getState() == 1){
                    SimpleDateFormat formatter = new SimpleDateFormat ("MMM dd yyyy HH:mm", Locale.ENGLISH);
                    String str = getString(R.string.sign_in_time) + " " + formatter.format(mTodayTaskList.get(i).getSign_in_time());
                    mTextView.setText(str);
                    mInfoWindow = new InfoWindow(mInfoWindowView, mTodayTaskList.get(i).getSign_in_loc(), -47);
                    mBaiduMap.showInfoWindow(mInfoWindow);
                }
            }else{
                LatLng ptCenter = marker.getPosition();
                // 反Geo搜索
                mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                        .location(ptCenter));
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ptCenter);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }
        return true;
    }

    @Override
    public void onMapClick(LatLng point) {
        // TODO Auto-generated method stub
        mBaiduMap.hideInfoWindow();
    }

    @Override
    public boolean onMapPoiClick(MapPoi poi) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
        //为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onStop() {
        //取消注册传感器监听
        mSensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {

        //取消注册调用的是unregisterReceiver()方法并传入接收器实例
        localBroadcastManager.unregisterReceiver(localReceiver);

        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mSearch.destroy();
        mMapView = null;
        super.onDestroy();
    }

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            drawRangeAndMarker();
        }
    }

}
