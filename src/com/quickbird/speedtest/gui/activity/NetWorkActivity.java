package com.quickbird.speedtest.gui.activity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.quickbird.speedtest.R;
import com.quickbird.speedtestengine.utils.APNUtil;
import com.quickbird.speedtestengine.utils.DebugUtil;
import com.quickbird.speedtestengine.utils.NetWorkUtil;
import com.quickbird.speedtestengine.utils.StringUtil;

public class NetWorkActivity extends BaseActivity {
    private Button wifiBtn, dataBtn;
    private TextView wifiTxt, dataTxt;
    private Context context;
    private boolean setWifiState, setMobileState;
    private String wifiInfoStr, dataInfoStr;
    private ConnectivityManager conMgr;
    private WifiManager wifiManager;
	private BroadcastReceiver mNetworkStateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				if (setWifiState)
					wifiTxt.setText(getWifiInfo(context));
				else
					wifiTxt.setText("");
				if (setMobileState)
					dataTxt.setText(APNUtil.getNetworkTypeENNameByIMSI(APNUtil.getImsi(context)));
				else
					dataTxt.setText("");
			} catch (Exception e) {
				DebugUtil.d("BroadcastReceiver:" + e.getMessage());
			}
		}
	};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_networkset);
        context = NetWorkActivity.this;
        wifiBtn = (Button) findViewById(R.id.wifi_btn);
        dataBtn = (Button) findViewById(R.id.data_btn);
        wifiTxt = (TextView) findViewById(R.id.wifi_info);
        dataTxt = (TextView) findViewById(R.id.data_info);
        wifiBtn.setOnClickListener(this);
        dataBtn.setOnClickListener(this);
        conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        IntentFilter filter = new IntentFilter();        
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkStateReceiver, filter);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch(v.getId())
        {
        case R.id.wifi_btn:
            setWifiState = !setWifiState;
            wifiManager.setWifiEnabled(setWifiState);
            changeButtonStatus(wifiBtn, setWifiState, setWifiState == true ? "已打开" : "已关闭");
			if (setWifiState)
				wifiTxt.setText(getWifiInfo(context));
			else 
				wifiTxt.setText("");
            break;
        case R.id.data_btn:
            setMobileState = !setMobileState;
            toggleMobileData(context, setMobileState);
            changeButtonStatus(dataBtn, setMobileState, setMobileState == true ? "已打开" : "已关闭");
            if (setMobileState)
            	dataTxt.setText(APNUtil.getNetworkTypeENNameByIMSI(APNUtil.getImsi(context)));
			else 
				dataTxt.setText("");
            break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshBtnStatus();
    }
    @Override
    public void onBackPressed() {
    	super.onBackPressed();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mNetworkStateReceiver);
    }

    private void refreshBtnStatus() {
        setWifiState = NetWorkUtil.getWifiState(context);
        setMobileState = NetWorkUtil.getMobileStatus(context);
        if (setWifiState)
			wifiTxt.setText(getWifiInfo(context));
		else 
			wifiTxt.setText("");
        if (setMobileState)
        	dataTxt.setText(APNUtil.getNetworkTypeENNameByIMSI(APNUtil.getImsi(context)));
		else 
			dataTxt.setText("");
        
        changeButtonStatus(wifiBtn, setWifiState, setWifiState == true ? "已打开" : "已关闭");
        changeButtonStatus(dataBtn, setMobileState, setMobileState == true ? "已打开" : "已关闭");
    }
    
    private String getWifiInfo(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid = wifiInfo.getSSID();
        if(StringUtil.isNull(ssid)||ssid.equals("<unknow ssid>")||ssid.contains("0x"))
        	ssid = "";
        return ssid;
    }

    private void toggleMobileData(Context context, boolean enabled) {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == conMgr)
            return;
        Class<?> conMgrClass = null; // ConnectivityManager类
        Field iConMgrField = null; // ConnectivityManager类中的字段
        Object iConMgr = null; // IConnectivityManager类的引用
        Class<?> iConMgrClass = null; // IConnectivityManager类
        Method setMobileDataEnabledMethod = null; // setMobileDataEnabled方法
        try {
            // 取得ConnectivityManager类
            conMgrClass = Class.forName(conMgr.getClass().getName());
            // 取得ConnectivityManager类中的对象mService
            iConMgrField = conMgrClass.getDeclaredField("mService");
            // 设置mService可访问
            iConMgrField.setAccessible(true);
            // 取得mService的实例化类IConnectivityManager
            iConMgr = iConMgrField.get(conMgr);
            // 取得IConnectivityManager类
            iConMgrClass = Class.forName(iConMgr.getClass().getName());
            // 取得IConnectivityManager类中的setMobileDataEnabled(boolean)方法
            setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod("setMobileDataEnabled",
                    Boolean.TYPE);
            // 设置setMobileDataEnabled方法可访问
            setMobileDataEnabledMethod.setAccessible(true);
            // 调用setMobileDataEnabled方法
            Object object = setMobileDataEnabledMethod.invoke(iConMgr, enabled);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 设置按钮显示状态
     * 
     * @param bt
     *            按钮
     * @param status
     *            是否开启
     * @param resId
     *            文字资源id
     */

    private void changeButtonStatus(Button bt, boolean status, String string) {
        if (status) {
            bt.setBackgroundResource(R.drawable.status_opened);
            bt.setText(string);
            bt.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            bt.setTag("opened");
            int padding = (int) getResources().getDimension(R.dimen.padding);
            bt.setPadding(padding, 0, 0, 0);
        } else {
            bt.setBackgroundResource(R.drawable.status_unopen);
            bt.setText(string);
            bt.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
            bt.setTag("unopen");
            int padding = (int) getResources().getDimension(R.dimen.padding);
            bt.setPadding(0, 0, padding, 0);
        }
    }

}
