package com.quickbird.speedtest.gui.activity;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.quickbird.speedtest.R;
import com.quickbird.utils.AMapUtil;

/**
 * AMapV2地图中简单介绍定位.
 */
public class LocationActivity extends FragmentActivity implements
		LocationSource, AMapLocationListener ,OnMapLoadedListener{
	
	private AMap aMap;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private LatLng markers[] = new LatLng[10];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.source_demo);
		init();
	}
	
	/**
	 * 根据用户当前位置生成一些测试用的模拟值
	 */
	private void getInValidMarkers(LatLng latLng) {
		markers[0] = new LatLng(latLng.latitude+0.1, latLng.longitude+0.1);
		markers[1] = new LatLng(latLng.latitude+0.2, latLng.longitude+0.2);
		markers[2] = new LatLng(latLng.latitude+0.3, latLng.longitude+0.3);
		markers[3] = new LatLng(latLng.latitude+0.4, latLng.longitude+0.4);
		markers[4] = new LatLng(latLng.latitude-0.1, latLng.longitude-0.1);
		markers[5] = new LatLng(latLng.latitude-0.2, latLng.longitude-0.2);
		markers[6] = new LatLng(latLng.latitude-0.3, latLng.longitude-0.3);
		markers[7] = new LatLng(latLng.latitude-0.4, latLng.longitude-0.4);
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (aMap == null) {
			aMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
			if (AMapUtil.checkReady(this, aMap)) {
				setUpMap();
			}
		}
	}

	private void setUpMap() {
		mAMapLocationManager = LocationManagerProxy.getInstance(LocationActivity.this);
		aMap.setLocationSource(this);
		aMap.setMyLocationEnabled(true);
		// 设置LOGO位置
		aMap.getUiSettings().setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);
		// 隐藏缩放按钮
		aMap.getUiSettings().setZoomControlsEnabled(false);
//	    drawMarkers();//
	}
	
	/**
	 * 绘制系统默认的10种marker背景图片
	 */
//	public void drawMarkers() {
//		if (Base.latLng != null) {
//			getInValidMarkers(Base.latLng);
//			aMap.addMarker(new MarkerOptions()
//			.position(Base.latLng)
//			.title("Marker1 ")
//			.icon(BitmapDescriptorFactory.fromBitmap(getViewBitmap(getView("2013/06/20", "241KB/s")))));
//			aMap.addMarker(new MarkerOptions()
//			.position(markers[0])
//			.title("Marker2 ")
//			.icon(BitmapDescriptorFactory.fromBitmap(getViewBitmap(getView("2013/06/21", "242KB/s")))));
//			aMap.addMarker(new MarkerOptions()
//			.position(markers[1])
//			.title("Marker3 ")
//			.icon(BitmapDescriptorFactory.fromBitmap(getViewBitmap(getView("2013/06/21", "242KB/s")))));
//			aMap.addMarker(new MarkerOptions()
//			.position(markers[2])
//			.title("Marker4 ")
//			.icon(BitmapDescriptorFactory.fromBitmap(getViewBitmap(getView("2013/06/21", "242KB/s")))));
//			aMap.addMarker(new MarkerOptions()
//			.position(markers[3])
//			.title("Marker5 ")
//			.icon(BitmapDescriptorFactory.fromBitmap(getViewBitmap(getView("2013/06/21", "242KB/s")))));
//			aMap.addMarker(new MarkerOptions()
//			.position(markers[4])
//			.title("Marker6 ")
//			.icon(BitmapDescriptorFactory.fromBitmap(getViewBitmap(getView("2013/06/21", "242KB/s")))));
//			aMap.addMarker(new MarkerOptions()
//			.position(markers[5])
//			.title("Marker6 ")
//			.icon(BitmapDescriptorFactory.fromBitmap(getViewBitmap(getView("2013/06/21", "242KB/s")))));
//			aMap.addMarker(new MarkerOptions()
//			.position(markers[6])
//			.title("Marker6 ")
//			.icon(BitmapDescriptorFactory.fromBitmap(getViewBitmap(getView("2013/06/21", "242KB/s")))));
//			aMap.addMarker(new MarkerOptions()
//			.position(markers[7])
//			.title("Marker6 ")
//			.icon(BitmapDescriptorFactory.fromBitmap(getViewBitmap(getView("2013/06/21", "242KB/s")))));
//		}
//	}
	
	/**
	 * 把一个xml布局文件转化成view
	 */
	public View getView(String title, String text) {
		View view = getLayoutInflater().inflate(R.layout.marker, null);
		TextView text_title = (TextView) view.findViewById(R.id.marker_title);
		TextView text_text = (TextView) view.findViewById(R.id.marker_text);
		text_title.setText(title);
		text_text.setText(text);
		return view;
	}
	
	/**
	 * 把一个view转化成bitmap对象
	 */
	public static Bitmap getViewBitmap(View view) {
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();
		return bitmap;
	}
	

	@Override
	protected void onPause() {
		super.onPause();
		deactivate();
	}

	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	/**
	 * 定位成功后回调函数
	 */
	@Override
	public void onLocationChanged(AMapLocation aLocation) {
		if (mListener != null) {
			mListener.onLocationChanged(aLocation);
			aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(aLocation.getLatitude(), aLocation.getLongitude()), 12));
		}
	}

	/**
	 * 激活定位
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
		}
		// Location API定位采用GPS和网络混合定位方式，时间最短是5000毫秒
		mAMapLocationManager.requestLocationUpdates(
				LocationProviderProxy.AMapNetwork, 5000, 10, this);
	}

	/**
	 * 停止定位
	 */
	@Override
	public void deactivate() {
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
		}
		mAMapLocationManager = null;
	}

	@Override
	public void onMapLoaded() {
	}
}