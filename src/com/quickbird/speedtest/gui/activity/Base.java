package com.quickbird.speedtest.gui.activity;

import com.amap.api.maps.model.LatLng;

import android.widget.TabHost;

public class Base {
	public static TabHost mTabHost;
	public static boolean startTest = false;
	public static LatLng latLng = new LatLng(39.983456, 116.3154950); // 默认值为北京市中关村经纬度
}
