package com.example.adminbilly.attendancesystem.BaiduMap;

import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.Overlay;

/**
 * Created by AdminBilly on 2017/5/13.
 */

public class MarkerAndRange {
    private Marker marker;
    private Overlay range;

    public MarkerAndRange(Marker marker, Overlay range){
        this.marker = marker;
        this.range = range;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public Overlay getRange() {
        return range;
    }

    public void setRange(Overlay range) {
        this.range = range;
    }
}
