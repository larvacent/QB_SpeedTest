package com.quickbird.speedtestengine.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.quickbird.controls.Config;
import com.quickbird.controls.Constants;

public class NetWorkUtil {

    
    /**
     * 获取网络类型  gprs/wifi/null
     * @param context
     * @return
     */
    public static String getNetworkStatusStr(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null){
                String type = info.getTypeName();
                if (type.equalsIgnoreCase("MOBILE")) {
                    return Constants.GPRS;
                }else if (type.equalsIgnoreCase("WIFI")) {
                    return Constants.WIFI;
                }
            }
        }
        return "";
    }
    
    /**
     * 获取网络类型
     * @param context
     * @return
     */
    public static int getNetworkStatus(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null){
                String type = info.getTypeName();
                if (type.equalsIgnoreCase("MOBILE")) {
                    return Constants.NETWORK_STATUS_MOBILE;
                }else if (type.equalsIgnoreCase("WIFI")) {
                    return Constants.NETWORK_STATUS_WIFI;
                }
            }
        }
        return Constants.NETWORK_STATUS_NULL;
    }
    
    public static boolean getWifiState(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }
    
    public static boolean getMobileStatus(Context context) {
        try {
            ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conMgr == null)
                return false;
            State state_mobile = conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
            if (state_mobile == State.CONNECTED || state_mobile == State.CONNECTING)
                return true;
        } catch (Exception e) {
            DebugUtil.d("getMobileStatus Exception:" + e.getMessage());
            return false;
        }
        return false;
    }
    
    /**
     * 获取当前网络是否可用
     * @param context
     * @return
     */
    public static boolean networkAvailable(Context context) {
        boolean isAvailable = false;
        int maxTimes = 3;
        while(maxTimes>0){
            try {
                InetAddress address = InetAddress.getByName(Config.PROXY_TEST_URL.substring(7)); 
                if(address!=null){
                    isAvailable = true;
                    return isAvailable;
                }
            } catch (UnknownHostException e) {
                DebugUtil.e("UnknownHostException: " + e.getMessage());
            } catch (Exception e) {
                DebugUtil.e("Exception: " + e.getMessage());
            } finally{
                if(!isAvailable){
                    maxTimes--;
                    try {
                        DebugUtil.e("Thread.sleep(5000)");
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        DebugUtil.e("InterruptedException: " + e.getMessage());
                    }
                }
            }
        }
        
        return isAvailable;
    }

    /**
     * 判断网络速度
     * @param type
     * @param subType
     * @return
     */
    public static boolean isConnectionFast(Context context) {
        int type = getNetworkStatus(context);
        switch (type) {
        case Constants.NETWORK_STATUS_NULL:
            return false;
        case Constants.NETWORK_STATUS_MOBILE:
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            int subType = tm.getNetworkType();
            switch (subType) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return false; // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return true;  // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return true;  // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return false; // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return true;  // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return true;  // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return true;  // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return true;  // ~ 400-7000 kbps
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return false;
            default:
                return false;
            }
        case Constants.NETWORK_STATUS_WIFI:
            return true;
        }
        return false;
    }
    
    /**
     * 获取运营商及网络类型
     * @param context
     * @return
     */
    public static String getNetWorkTypeStr(int networkStatus, Context context) {
        switch (networkStatus) {
        case Constants.NETWORK_STATUS_NULL:
            return "无网络";
        case Constants.NETWORK_STATUS_MOBILE:
            return APNUtil.getNetworkTypeName(APNUtil.getNetworkTypeCodeByIMSI(context));
        case Constants.NETWORK_STATUS_WIFI:
            return "WiFi";
        }
        return "";
    }
    
    /**
     * 获取本机的Mac地址
     * @param context
     * @return
     */
    public static String getLocalMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }
    
    /**
     * 获取本机的IP地址
     * @return
     */
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("WifiPreference IpAddress", ex.toString());
        }
        return null;
    }
}
