package com.quickbird.speedtestengine.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 系统参数工具类
 */
public class SharedPreferenceUtil {
	
	/** 是否已经分享了速度 */
	public static final String SHARED_SPEED = "speed";
	/** 默认保存文件 */
	private static final String SYSTEM_PARAM = "apnProxy_xml";
	/** IMSI */
	public static final String SHAREDPREFERENCE_IMSI = "imsi";
	/** IMEI */
	public static final String SHAREDPREFERENCE_IMEI = "imei";
	/** 服务开关 */
	public static final String SERVICE_ON = "SERVICE_ON";
	public static final String PROXY_OFF = "PROXY_OFF";
	/** RPC userToken */
	public static final String RPC_USERTOKEN = "rpc_usertoken";
	/** 网络开关 */
    public static final String WIFI_ON = "WIFI_ON";
    public static final String DATA_ON = "DATA_ON";
	
	/**
	 *  获取系统参数（值为字符串），没获取到返回“”
	 */
	public static String getStringParam(Context context, String param){
		SharedPreferences pres = context.getSharedPreferences(SYSTEM_PARAM, 
				Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
		String value = pres.getString(param, "");
		return value;
	}
	
	/**
	 *  获取系统参数（值为字符串），没获取到返回“”
	 */
	public static String getStringParam(String fileName, Context context, String param){
		SharedPreferences pres = context.getSharedPreferences(fileName, 
				Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
		String value = pres.getString(param, "");
		return value;
	}
	
	/**
	 *  获取系统参数（值为字符串），没获取到返回defaultValue
	 */
	public static String getStringParam(Context context, String param, String defaultValue){
		SharedPreferences pres = context.getSharedPreferences(SYSTEM_PARAM, 
				Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
		String value = pres.getString(param, defaultValue);
		return value;
	}
	
	/**
	 *  获取系统参数（值为字符串），没获取到返回defaultValue
	 */
	public static String getStringParam(String fileName, Context context, String param, String defaultValue){
		SharedPreferences pres = context.getSharedPreferences(fileName, 
				Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
		String value = pres.getString(param, defaultValue);
		return value;
	}
	
	/**
	 * 保存字符串到系统参数
	 */
	public static void saveStringParam(Context context, String param, String value){
		SharedPreferences pres = context.getSharedPreferences(SYSTEM_PARAM, 
				Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
		Editor editor = pres.edit();
		editor.putString(param, value);
		editor.commit();
	}
	
	/**
	 * 保存字符串到系统参数
	 */
	public static void saveStringParam(String fileName, Context context, String param, String value){
		SharedPreferences pres = context.getSharedPreferences(fileName, 
				Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
		Editor editor = pres.edit();
		editor.putString(param, value);
		editor.commit();
	}
	
	/**
	 * 获取boolean型系统参数
	 */
	public static boolean getBooleanParam(Context context, String param){
		SharedPreferences pres = context.getSharedPreferences(SYSTEM_PARAM, 
				Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
		boolean value = pres.getBoolean(param, true);//默认返回false
		return value;
	}
	
	/**
	 * 获取boolean型系统参数
	 */
	public static boolean getBooleanParam(String fileName, Context context, String param){
		SharedPreferences pres = context.getSharedPreferences(fileName, 
				Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
		boolean value = pres.getBoolean(param, false);//默认返回false
		return value;
	}
	
	public static boolean getBooleanParam(Context context, String param, boolean defValue){
		SharedPreferences pres = context.getSharedPreferences(SYSTEM_PARAM, 
				Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
		boolean value = pres.getBoolean(param, defValue);//默认返回false
		return value;
	}
	
	
	public static boolean getBooleanParam(String fileName, Context context, String param, boolean defValue){
		SharedPreferences pres = context.getSharedPreferences(fileName, 
				Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
		boolean value = pres.getBoolean(param, defValue);//默认返回false
		return value;
	}
	
	/**
	 * 保存boolean型系统参数
	 */
	public static void saveBooleanParam(Context context, String param, boolean value){
		SharedPreferences pres = context.getSharedPreferences(SYSTEM_PARAM, 
				Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
		Editor editor = pres.edit();
		editor.putBoolean(param, value);
		editor.commit();
	}
	
	/**
	 * 保存boolean型系统参数
	 */
	public static void saveBooleanParam(String fileName, Context context, String param, boolean value){
		SharedPreferences pres = context.getSharedPreferences(fileName, 
				Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
		Editor editor = pres.edit();
		editor.putBoolean(param, value);
		editor.commit();
	}

}
