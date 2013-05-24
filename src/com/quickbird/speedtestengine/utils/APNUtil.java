package com.quickbird.speedtestengine.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.TelephonyManager;

import com.quickbird.controls.Constants;
import com.quickbird.controls.DeviceInfo;

public class APNUtil {
	/**
	 * 激活某个接入点
	 * @param resolver
	 * @param apnID 接入点ID
	 * @return
	 */
	public static boolean chanageApn(ContentResolver resolver, String apnID) {
		if (!StringUtil.isNull(apnID)) {
			ContentValues values = new ContentValues();
			values.put("apn_id", apnID);
			int result = resolver.update(
					Uri.parse("content://telephony/carriers/preferapn"),
					values, null, null);
			DebugUtil.i(">>> result code is : "+result);
			return result > 0;
		}
		return false;
	}
	
	/**获取运营商类型*/
    public static int getNetworkTypeCodeByIMSI(Context context){
        String imsi = getImsi(context);
        if(StringUtil.isNull(imsi)){
            
            TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            imsi = mTelephonyMgr.getSubscriberId();
        }
        return getNetworkTypeCodeByIMSI(imsi);
    }
    
    /** 获取运营商的中文名称 */
    public static String getNetworkTypeName(int networkType) {
        String networkTypeName = "GPRS";
        switch (networkType) {
        case Constants.NETWORK_TYPE_CHINA_MOBILE:
            networkTypeName = "中国移动";
            break;
        case Constants.NETWORK_TYPE_CHINA_UNICOM:
            networkTypeName = "中国联通";
            break;
        case Constants.NETWORK_TYPE_CHINA_TELECOM:
            networkTypeName = "中国电信";
            break;
        }
        return networkTypeName;
    }
    
    
    /**
     * 获取运营商类型 
     * @param imsi
     * @return
     */
    public static int getNetworkTypeCodeByIMSI(String imsi){
        int networkType = 0;
        
        if(!StringUtil.isNull(imsi)){
            
            if(imsi.length() < 5){
                imsi = "46000";
            }
            String networkTypeStr = imsi.substring(4, 5);
            int networkTypeCode = Integer.parseInt(networkTypeStr);
            switch(networkTypeCode){
            case 0:
            case 2:
            case 7:
                networkType = Constants.NETWORK_TYPE_CHINA_MOBILE;
                break;
            case 1:
                networkType = Constants.NETWORK_TYPE_CHINA_UNICOM;
                break;
            case 3:
                networkType = Constants.NETWORK_TYPE_CHINA_TELECOM;
                break;
            default:
                networkType = Constants.NETWORK_TYPE_OTHER;
                break;
            }
            
            return networkType;
        }
        return 0;
    }
	
    /**获取运营商的英文名称*/
    public static String getNetworkTypeENNameByIMSI(String imsi){
        String networkTypeName = "";
        
        int networkType = getNetworkTypeCodeByIMSI(imsi);
        switch(networkType){
        case Constants.NETWORK_TYPE_CHINA_MOBILE:
            networkTypeName = "cmnet";
            break;
        case Constants.NETWORK_TYPE_CHINA_UNICOM:
            networkTypeName = "3gnet";
            break;
        case Constants.NETWORK_TYPE_CHINA_TELECOM:
            networkTypeName = "ctnet";
            break;
        }
        return networkTypeName;
    }
	
	/***
	 * 获取最近服务器的主机IP地址
	 * @return IP地址
	 */
	public static String getServerAddr(Context context) {
		String imsi = DeviceInfo.imsi;
		String addr = null;
		if(StringUtil.isNull(imsi)){
			
			TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			imsi = mTelephonyMgr.getSubscriberId();
		}
		if(!StringUtil.isNull(imsi)){
			int networkCode = getNetworkTypeCodeByIMSI(imsi);
			DebugUtil.i("getServerAddr() networkCode : "+networkCode);
			int times = 3;
			while(times>0 && addr==null){
				times--;
				switch (networkCode) {
				case Constants.NETWORK_TYPE_CHINA_MOBILE:
				case Constants.NETWORK_TYPE_OTHER:
					addr = getServerIP(context,Constants.NETWORK_TYPE_CHINA_MOBILE_RPC_DOMAIN);
				case Constants.NETWORK_TYPE_CHINA_TELECOM:
					addr = getServerIP(context,Constants.NETWORK_TYPE_CHINA_TELECOM_RPC_DOMAIN);
				case Constants.NETWORK_TYPE_CHINA_UNICOM:
					addr = getServerIP(context,Constants.NETWORK_TYPE_CHINA_UNICOM_RPC_DOMAIN);
				}
			}
			DebugUtil.e("================getServerIP: " + addr + "============");
		}else{
			return addr;
		}
		
		return addr;
	}
	
	
	/***
	 * 获取服务端IP地址
	 * @param context
	 * @param domain 域名
	 * @return
	 */
	public static String getServerIP(Context context,String domain) {
        try {
        	DebugUtil.i("getServerIP() domain : "+domain);
        	InetAddress myServer = InetAddress.getByName(domain);
        	DebugUtil.i("getServerIP() myServer : "+myServer);
        	return myServer.getHostAddress();
        } catch (UnknownHostException e) {
        	e.printStackTrace();
        }
        return null;
    }
	
	public static String getCurrentApnType(Context context){
        String type = null;
        Cursor c = null;
        c = context.getContentResolver().query(Uri.parse("content://telephony/carriers/preferapn"), null,null, null, null);
        
        int i = 0;
        while(c.moveToNext())
        {
            DebugUtil.i(" columnName "+c.getColumnName(i));
            i++;
            type = c.getString(c.getColumnIndex("apn"));
            
            DebugUtil.i("======================当前APN接入点共有字段  "+c.getColumnCount() +"个=================");
            DebugUtil.i(">>>当前APN接入点类型为 :"+type);
        }
        c.close();
        return type;
    }
	
	/**获取Numeric*/
	public static String getNumeric(Context context){
		String numeric = "";
		String imsi = getImsi(context);
		
		if(!StringUtil.isNull(imsi)){
			numeric = imsi.substring(0, 5);
		}
		return numeric;
	}
	
	/***
	 * 获取IMSI
	 * @param context
	 * @return
	 */
	public static String getImsi(Context context){
		TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return mTelephonyMgr.getSubscriberId();
	}
	
}
