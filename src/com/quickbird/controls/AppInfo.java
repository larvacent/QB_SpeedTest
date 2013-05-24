package com.quickbird.controls;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

import com.quickbird.speedtestengine.utils.StringUtil;

public abstract class AppInfo {
	
	/**
	 * 获取应用版本名称
	 * @param context
	 * @return
	 */
	public abstract String getVersionName(Context context);
	/**
	 * 获取应用版本号
	 * @param context
	 * @return
	 */
	public abstract int getVersionCode(Context context);
	/**
	 * 获取应用渠道号
	 * @param context
	 * @return
	 */
	public abstract String getChannelName(Context context);
	

	/***
	 * 打印应用信息
	 * @param context
	 * @return
	 */
	public String printAppInfo(Context context) {
		/*PackageManager packageManager = context.getPackageManager();
		PackageInfo packageInfo;
		try {
			packageInfo = packageManager.getPackageInfo(context.getPackageName(), PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
			if(packageInfo != null){
				DebugUtil.i("=====================packageInfo=====================");
				DebugUtil.i("packageName is :"+packageInfo.packageName);
				DebugUtil.i("versionCode is :"+packageInfo.versionCode);
				DebugUtil.i("versionName is :"+packageInfo.versionName);
				DebugUtil.i("sharedUserId is :"+packageInfo.sharedUserId);
				DebugUtil.i("sharedUserLabel is :"+packageInfo.sharedUserLabel);
				DebugUtil.i("describeContents() is :"+packageInfo.describeContents());
				DebugUtil.i("activities is :"+packageInfo.activities);
				DebugUtil.i("configPreferences is :"+packageInfo.configPreferences);
				DebugUtil.i("services is :"+packageInfo.services);
				DebugUtil.i("signatures is :"+packageInfo.signatures);
				DebugUtil.i("applicationInfo is :"+packageInfo.applicationInfo);
				DebugUtil.i("applicationInfo.className is :"+packageInfo.applicationInfo.className);
				DebugUtil.i("applicationInfo.name is :"+packageInfo.applicationInfo.name);
				DebugUtil.i("applicationInfo.packageName is :"+packageInfo.applicationInfo.packageName);
				DebugUtil.i("applicationInfo.targetSdkVersion is :"+packageInfo.applicationInfo.targetSdkVersion);
				DebugUtil.i("applicationInfo.uid is :"+packageInfo.applicationInfo.uid);
				DebugUtil.i("applicationInfo.dataDir is :"+packageInfo.applicationInfo.dataDir);
				DebugUtil.i("applicationInfo.publicSourceDir is :"+packageInfo.applicationInfo.publicSourceDir);
				DebugUtil.i("applicationInfo.sourceDir is :"+packageInfo.applicationInfo.sourceDir);
				DebugUtil.i("applicationInfo.processName is :"+packageInfo.applicationInfo.processName);
				DebugUtil.i("applicationInfo.manageSpaceActivityName is :"+packageInfo.applicationInfo.manageSpaceActivityName);
				DebugUtil.i("activities size is :"+packageInfo.activities.length);
				for(ActivityInfo a : packageInfo.activities){
					DebugUtil.i("activitie name is :"+a.name);
				}
			}
			return packageInfo.versionCode+"";
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}*/
		return null;
	}
	
	public static class AppInfoImpl extends AppInfo{

		@Override
		public String getVersionName(Context context) {
			PackageManager packageManager = context.getPackageManager();
	        PackageInfo packageInfo;
			try {
				packageInfo = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
				String versionName = packageInfo.versionName;
				if(StringUtil.isNull(versionName))
				    versionName = "";
				return versionName;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			return "";
		}

		@Override
		public int getVersionCode(Context context) {
			PackageManager packageManager = context.getPackageManager();
	        PackageInfo packageInfo;
			try {
				packageInfo = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
				int versionCode = packageInfo.versionCode;
				return versionCode;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			return 0;
		}

		@Override
		public String getChannelName(Context context) {
		    try {
                ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                Bundle bundle = info.metaData;
                String channel = String.valueOf(bundle.get("UMENG_CHANNEL")); // 获取的对象有可能是整型
                if(StringUtil.isNull(channel))
                    channel = "";
                return channel;
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
			return "";
		}
	}
}


