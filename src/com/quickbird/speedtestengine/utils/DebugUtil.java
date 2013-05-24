package com.quickbird.speedtestengine.utils;

import android.util.Log;

import com.quickbird.controls.Config;


/**
 * 调试打印工具类
 * @author JinZhu
 *
 */
public class DebugUtil {

	/** 在控制台打印信息 */
	public static void i(String msg) {
		i(Config.DEBUG_TAG, msg);
	}
	
	public static void d(String msg) {
		d(Config.DEBUG_TAG, msg);
	}
	
	public static void w(String msg) {
		w(Config.DEBUG_TAG, msg);
	}
	
	public static void v(String msg) {
		v(Config.DEBUG_TAG, msg);
	}
	
	public static void e(String msg) {
		e(Config.DEBUG_TAG, msg);
	}
	
	public static void i(String tag, String msg) {
		String mess = getLogPrefix() + msg;
		if (Config.DEBUG)
			Log.i(tag, mess);
		if(Config.LOG)
			LogUtil.writeLogInSdcard(mess);
	}
	
	public static void d(String tag, String msg) {
		String mess = getLogPrefix() + msg;
		if (Config.DEBUG)
			Log.d(tag, mess);
		if(Config.LOG)
			LogUtil.writeLogInSdcard(mess);
	}
	
	public static void w(String tag, String msg) {
		String mess = getLogPrefix() + msg;
		if (Config.DEBUG)
			Log.w(tag, mess);
		if(Config.LOG)
			LogUtil.writeLogInSdcard(mess);
	}
	
	public static void v(String tag, String msg) {
		String mess = getLogPrefix() + msg;
		if (Config.DEBUG)
			Log.v(tag, mess);
		if(Config.LOG)
			LogUtil.writeLogInSdcard(mess);
	}
	
	public static void e(String tag, String msg) {
		String mess = getLogPrefix() + msg;
		if (Config.DEBUG)
			Log.e(tag, mess);
		if(Config.LOG)
			LogUtil.writeLogInSdcard(mess);
	}
	
	/** 为了方便查看，在打印的日志信息内容前添加一个序号前缀 */
	private static String getLogPrefix() {
		logId++;
		if (logId >= 1000)
			logId = 1;
		return "(" + logId + "). ";
	}

	private static int logId = 0;
}
