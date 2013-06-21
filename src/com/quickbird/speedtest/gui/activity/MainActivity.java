package com.quickbird.speedtest.gui.activity;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;
import cn.sharesdk.framework.AbstractWeibo;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.model.LatLng;
import com.quickbird.speedtest.R;
import com.quickbird.speedtestengine.utils.SharedPreferenceUtil;
import com.quickbird.utils.AMapUtil;
import com.quickbird.utils.ToastUtil;
import com.quickbird.utils.UmengUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

public class MainActivity extends TabActivity implements AMapLocationListener{

	private TabWidget mTabWidget;
	private Context context;
	private LinearLayout[] tabLayout = new LinearLayout[3];
	private ImageView[] icon = new ImageView[3];
	private ImageView[] tabBg = new ImageView[3];
	private TextView[] text = new TextView[3];
	private TypedArray iconPic, iconFoucsPic;
	private MyClickListener clickListener;
	private LocationManagerProxy locationManager;
	private int tabId;

	private class MyClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tab_website:
				changeTab(0);
				break;
			case R.id.tab_testspeed:
				changeTab(1);
				break;
			case R.id.tab_settings:
				changeTab(2);
				break;
			}
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		AbstractWeibo.initSDK(this);// 初始化分享SDK
	}

	public void changeTab(int id) {
		if (Base.mTabHost.getCurrentTab() != id) {
			Base.mTabHost.setCurrentTab(id);
			changeTabView();
		}
	}

	private void changeTabView() {
		for (int i = 0; i < mTabWidget.getChildCount(); i++) {
			/* 得到每个标签的视图 */
			View view = mTabWidget.getChildAt(i);
			/* 设置每个标签的背景 */
			if (Base.mTabHost.getCurrentTab() == i) {
				icon[i].setImageDrawable(iconFoucsPic.getDrawable(i));
				text[i].setTextColor(this.getResources().getColor(
						R.color.text_tab_focus));
				tabBg[i].setVisibility(View.VISIBLE);
				view.setBackgroundDrawable(this.getResources().getDrawable(
						R.drawable.tab_bar_selected));
			} else {
				icon[i].setImageDrawable(iconPic.getDrawable(i));
				text[i].setTextColor(this.getResources().getColor(
						R.color.text_black));
				tabBg[i].setVisibility(View.INVISIBLE);
				view.setBackgroundDrawable(this.getResources().getDrawable(
						R.drawable.tab_bottom));
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		context = MainActivity.this;
		Base.mTabHost = this.getTabHost();
		mTabWidget = this.getTabWidget();

		clickListener = new MyClickListener();
		tabLayout[0] = (LinearLayout) findViewById(R.id.tab_website);
		tabLayout[1] = (LinearLayout) findViewById(R.id.tab_testspeed);
		tabLayout[2] = (LinearLayout) findViewById(R.id.tab_settings);

		tabBg[0] = (ImageView) findViewById(R.id.tab_bg1);
		tabBg[1] = (ImageView) findViewById(R.id.tab_bg2);
		tabBg[2] = (ImageView) findViewById(R.id.tab_bg3);

		icon[0] = (ImageView) findViewById(R.id.website_icon);
		icon[1] = (ImageView) findViewById(R.id.testspeed_icon);
		icon[2] = (ImageView) findViewById(R.id.settings_icon);
		text[0] = (TextView) findViewById(R.id.website_txt);
		text[1] = (TextView) findViewById(R.id.testspeed_txt);
		text[2] = (TextView) findViewById(R.id.settings_txt);

		iconPic = getResources().obtainTypedArray(R.array.icon_array);
		iconFoucsPic = getResources()
				.obtainTypedArray(R.array.icon_focus_array);

		tabLayout[0].setOnClickListener(clickListener);
		tabLayout[1].setOnClickListener(clickListener);
		tabLayout[2].setOnClickListener(clickListener);

		Resources rs = this.getResources();
		DisplayMetrics metric = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(metric);
		int Dpi = metric.densityDpi;

		Base.mTabHost.addTab(Base.mTabHost.newTabSpec("tab_1")
				.setIndicator("测网站")
				.setContent(new Intent(this, WebSiteTestActivity.class)));
		Base.mTabHost.addTab(Base.mTabHost.newTabSpec("tab_2")
				.setIndicator("测网速")
				.setContent(new Intent(this, SpeedTestActivity.class)));
		Base.mTabHost.addTab(Base.mTabHost.newTabSpec("tab_3")
				.setIndicator("更多")
				.setContent(new Intent(this, MoreActivity.class)));

		/* 当点击Tab选项卡的时候，更改当前Tab标签的背景 */
		Base.mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				changeTabView();
			}
		});
		Base.mTabHost.setCurrentTab(1);
		
		setUmengSDK();// 设置友盟SDK

		AbstractWeibo.initSDK(this);// 初始化分享SDK
		
		MobclickAgent.onEvent(this, "oa");
		
		MapInit();
	}

	private void MapInit() {
		locationManager = LocationManagerProxy.getInstance(this);
		// Location API定位采用GPS和网络混合定位方式，时间最短是5000毫秒
		locationManager.requestLocationUpdates(
				LocationProviderProxy.AMapNetwork, 5000, 10, this);
	}

	private void setUmengSDK() {
		UmengUtil.onCreate(context);
		UmengUpdateAgent.update(this);
		UmengUpdateAgent.setUpdateAutoPopup(false);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			@Override
			public void onUpdateReturned(int updateStatus,
					UpdateResponse updateInfo) {
				switch (updateStatus) {
				case 0: // has update
					UmengUpdateAgent.showUpdateDialog(context, updateInfo);
					break;
				case 1: // has no update
					break;
				case 2: // none wifi
					break;
				case 3: // time out
					break;
				}
			}
		});
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (locationManager != null) {
			locationManager.removeUpdates(this);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AbstractWeibo.stopSDK(this); // 停止分享SDK
		if (locationManager != null) {
			locationManager.removeUpdates(this);
			locationManager.destory();
		}
		locationManager = null;
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
	 * 过滤gps定位返回的数据
	 */
	@Override
	public void onLocationChanged(AMapLocation location) {
		if (location != null) {
				Double geoLat = location.getLatitude();
				Double geoLng = location.getLongitude();
				String str = ("定位成功:(" + geoLng + "," + geoLat + ")"
						+ "\n精    度    :" + location.getAccuracy() + "米"
						+ "\n定位方式:" + location.getProvider() + "\n定位时间:" + AMapUtil.convertToTime(location.getTime()));
				Message msg = new Message();
				msg.obj = str;
				handler.sendMessage(msg);
				SharedPreferenceUtil.saveStringParam(context, SharedPreferenceUtil.LOCATION_LATITUDE, geoLat.toString());
				SharedPreferenceUtil.saveStringParam(context, SharedPreferenceUtil.LOCATION_LONGTITUDE, geoLng.toString());
				Base.latLng = new  LatLng(geoLat, geoLng);
		}
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			ToastUtil.showToast(MainActivity.this, (String) msg.obj);
		}
	};

}
