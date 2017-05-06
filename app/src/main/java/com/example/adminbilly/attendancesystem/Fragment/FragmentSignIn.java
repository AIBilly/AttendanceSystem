package com.example.adminbilly.attendancesystem.Fragment;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.adminbilly.attendancesystem.R;

import java.util.ArrayList;
import java.util.List;

import at.markushi.ui.CircleButton;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by AdminBilly on 2017/4/6.
 */

public class FragmentSignIn extends BaseFragment implements SensorEventListener, OnGetGeoCoderResultListener,
        BaiduMap.OnMapLongClickListener, BaiduMap.OnMarkerClickListener, BaiduMap.OnMapClickListener {

    public static FragmentSignIn newInstance() {
        return new FragmentSignIn();
    }
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
    Button requestLocButton;
    CircleButton signInButton;
    boolean isFirstLoc = true; // 是否首次定位
    private MyLocationData locData;
    private float direction;

    //Marker相关
    private Marker mMarker;
    BitmapDescriptor bd = BitmapDescriptorFactory
            .fromResource(R.mipmap.icon_openmap_mark);

    //geoSearch相关
    GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用

    //长按地图相关
    // 保存点中的点id
    Marker preMarker = null; //前一个Marker
    List<Marker> markers = new ArrayList<Marker>();

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

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
                LatLng currentPos = new LatLng(mCurrentLat,
                        mCurrentLon);
                double distance = DistanceUtil. getDistance(currentPos, mMarker.getPosition());
                if(distance <= 100){
                    Toast.makeText(FragmentSignIn.this.getActivity(), "Signed in.",
                            Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(FragmentSignIn.this.getActivity(), "Sign in failed..",
                            Toast.LENGTH_LONG).show();
                }

            }
        };
        signInButton.setOnClickListener(CircleBtnClickListener);

        drawRangeAndMarker();

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
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    private void drawRangeAndMarker(){
        LatLng llCircle = new LatLng(39.969925972732035, 116.36477099614513);
        OverlayOptions ooCircle = new CircleOptions().fillColor(0x661fb293)
                .center(llCircle).stroke(new Stroke(5, 0x00FFFFFF))
                .radius(100);
        mBaiduMap.addOverlay(ooCircle);
        MarkerOptions ooA = new MarkerOptions().position(llCircle).icon(bd)
                .zIndex(9).draggable(false);
        mMarker = (Marker) (mBaiduMap.addOverlay(ooA));
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
    public class MyLocationListenner implements BDLocationListener {
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
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
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
        MarkerOptions ooA = new MarkerOptions().position(point).icon(bd);
        TempMarker = (Marker) (mBaiduMap.addOverlay(ooA));
        // 反Geo搜索
        mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                .location(point));
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(point);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        //MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(point);
        //mBaiduMap.setMapStatus(update);
        preMarker = TempMarker;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        mBaiduMap.hideInfoWindow();
        // TODO Auto-generated method stub
        if (marker == null) {
            return false;
        }
        LatLng ptCenter = marker.getPosition();
        // 反Geo搜索
        mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                .location(ptCenter));
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(marker.getPosition());
        mBaiduMap.setMapStatus(update);
        //currentID = marker.getExtraInfo().getString("id");
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
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mSearch.destroy();
        mMapView = null;
        super.onDestroy();
    }

}
