package com.quickbird.controls;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.DisplayMetrics;

import com.quickbird.speedtestengine.utils.DebugUtil;

public abstract class DeviceInfo {
	
	//==================设备屏幕基本参数======================
	/**当前设备的密度*/
	public static float mDensity = 1.5f;
	/**当前设备真实屏幕宽度*/
	public static int mExactScreenWidth;
	/**当前设备真实屏幕高度*/
	public static int mExactScreenHeight;
	
	//==================屏幕大小======================
	public static final int SCREEN_320_480 = 1;
	public static final int SCREEN_480_800 = 2;
	public static final int SCREEN_480_854 = 3;
	
	//==================一些特殊的机型作特殊处理======================
	public static final int OMS_MOTO_710 = 1;
	public static final int OMS_GW_880 = 2;
	
	//==================一些全局的系统配置参数======================
	public static boolean mIsCmwap = false; 
	public static String IMSI = "";
	public static int mSdkVersion = 7;
	public static int mScreenType = SCREEN_320_480;
	public static int mPhoneType = -1;
	public static boolean mIsOphoneSystem = false;
	public static boolean mRunningInEmulator = false;
	
	
	//==================SIM卡和Device的一些信息======================
	/**语言环境*/
	public static String language = null;
	/**国家*/
	public static String country = null;
	/**IMEI号*/
	public static String imei = null;
	/**IMSI号*/
	public static String imsi = null;
	/**CELLID*/
	public static int cellid = 0;
	/**LAC*/
	public static int lac = 0;
	/**手机型号*/
	public static String model = null;
	/**手机品牌*/
	public static String brand = null;
	/**操作系统名称*/
	public static String osname = null;
	/**操作系统版本*/
	public static String osVersion = null;
	public static String ipAddress = null;
	private static boolean isCracked = false;
	private static boolean isAllowUnknownSource = false;
	
	public abstract String getLanguage();
	public abstract String getCountry();
	public abstract String getIMEI(Context context);
	public abstract String getIMSI(Context context);
	public abstract String getCell(Context context);
	public abstract String getLac(Context context);
	public abstract String getModel();
	public abstract String getBrand();
	public abstract String getOsname();
	public abstract String getOsversion();
	public abstract String getIpAddress();
	public abstract boolean isCracked();
	public abstract boolean isAllowUnknownSource();
	public abstract boolean isAboveIceCream();
	
	public abstract int getExactScreenWidth(Activity activity);
	public abstract int getExactScreenHeight(Activity activity);
	public abstract float getDensity(Activity activity);
	
	public static void initializeSystemParameters(Activity activity) {
		mSdkVersion = Integer.parseInt(Build.VERSION.SDK);
		mIsOphoneSystem = isOphoneSystem();
		mRunningInEmulator = runningInEmulator(activity);
		
		initDisplayMetrics(activity);
		
		if(mExactScreenHeight == 800 ) {
			mScreenType = SCREEN_480_800;
			mDensity = 1.5f;
			if(mSdkVersion == 3 && mIsOphoneSystem) 
				mPhoneType = OMS_GW_880;
		} else if(mExactScreenHeight == 854) {
			mScreenType = SCREEN_480_854;
			mDensity = 1.5f;
			if(mSdkVersion == 3 && mIsOphoneSystem) 
				mPhoneType = OMS_MOTO_710;
		} else if(mExactScreenHeight == 480) {
			mScreenType = SCREEN_320_480;
		}
		
		String screenType = "unknow";
		switch(mScreenType) {
		case SCREEN_320_480:
			screenType = "SCREEN_320_480";
			break;
		case SCREEN_480_800:
			screenType = "SCREEN_480_800";
			break;
		case SCREEN_480_854:
			screenType = "SCREEN_480_854";
			break;
		}
		
		DebugUtil.v("\nmSdkVersion:" + mSdkVersion + 
				"\nmIsOphoneSystem:" + mIsOphoneSystem + 
				"\nmScreenType:" + screenType +
				"\nmRunInEmulator:" + mRunningInEmulator );

	}
	
	// dip转化为像素
	public static int dipToPx(int dip) {
		if (Config.mDensity > 1.0 && Config.mExactScreenWidth > 320 
				&& Config.mExactScreenWidth < 540)
			return (int) (dip * Config.mDensity);
		else if(Config.mDensity > 1.0 && Config.mExactScreenWidth == 540){
			return dip == 320 ? 540 : (int)(1.75 * dip);
		} else
			return dip;
	}

	// 像素转化为dip
	public static int pxToDip(int px) {
		if (Config.mDensity > 1.0 && Config.mExactScreenWidth > 320)
			return (int) (px / Config.mDensity);
		else
			return px;
	}
	
	/**
	 * 判断是否模拟器。如果返回TRUE，则当前是模拟器
	 * 
	 * @param context
	 * @return 一般真机都有IMEI的，不过也见过工程机的IMEI是000000000000000还是0。
	 */
	public static boolean runningInEmulator(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = tm.getDeviceId();
		if (imei == null || imei.equals("000000000000000")) {
			Config.mRunningInEmulator = true;
			return true;
		}
		Config.mRunningInEmulator = false;
		return false;
	}
	
	// 是否为OMS系统
	public static boolean isOphoneSystem() {
		String classPath = "oms.dcm.DataConnectivityConstants";
		try {
			@SuppressWarnings("rawtypes")
			Class cc = Class.forName(classPath);
			DebugUtil.e("It should be  Oms system !!!");
			DebugUtil.e(""+cc);
			return true;
		} catch (ClassNotFoundException e) {
			DebugUtil.e("It should be  Android system");
			return false;
		}
	}
	
	// 初始化屏幕的一些参数：屏幕密度及长/宽
	public static void initDisplayMetrics (Activity activity) {
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		
		mDensity = metrics.density;
		mExactScreenWidth = metrics.widthPixels;
		mExactScreenHeight = metrics.heightPixels;
		
		DebugUtil.v("Density:" + metrics.density + 
				"  WidthPixels:" + metrics.widthPixels + 
				"  HeightPixels:" + metrics.heightPixels );
	}
	
	/**
	 * 判断手机是否有SIM卡，返回true表示有SIM卡
	 */
	public static boolean existSimCard(Context context) {
		boolean bExist = false;
		TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		int  state = mTelephonyManager.getSimState();
		String status = readSIMCard(context);
		DebugUtil.e("config", ">>>state:"+state);
		DebugUtil.e("config", ">>>status:"+status);
		if(state == TelephonyManager.SIM_STATE_READY){		//SIM卡状态良好
			bExist = true;
		}
		return bExist;
	}
	
	/**
	 * 判断当前是否有可用网络
	 * 
	 * @param context
	 * @return 如果有可用网络则返回true，反之返回false
	 */
	public static boolean existAvailableNetwork(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static String readSIMCard(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);// 取得相关系统服务
		StringBuffer sb = new StringBuffer();
		switch (tm.getSimState()) { // getSimState()取得SIM的状态 有下面6中状态
		case TelephonyManager.SIM_STATE_ABSENT:
			sb.append("无卡");
			break;
		case TelephonyManager.SIM_STATE_UNKNOWN:
			sb.append("未知状态");
			break;
		case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
			sb.append("需要NetworkPIN解锁");
			break;
		case TelephonyManager.SIM_STATE_PIN_REQUIRED:
			sb.append("需要PIN解锁");
			break;
		case TelephonyManager.SIM_STATE_PUK_REQUIRED:
			sb.append("需要PUK解锁");
			break;
		case TelephonyManager.SIM_STATE_READY:
			sb.append("良好");
			break;
		}

		if (tm.getSimSerialNumber() != null) {
			sb.append("@" + tm.getSimSerialNumber().toString());
		} else {
			sb.append("@无法取得SIM卡号");
		}

		if (tm.getSimOperator().equals("")) {
			sb.append("@无法取得供货商代码");
		} else {
			sb.append("@" + tm.getSimOperator().toString());
		}

		if (tm.getSimOperatorName().equals("")) {
			sb.append("@无法取得供货商");
		} else {
			sb.append("@" + tm.getSimOperatorName().toString());
		}

		if (tm.getSimCountryIso().equals("")) {
			sb.append("@无法取得国籍");
		} else {
			sb.append("@" + tm.getSimCountryIso().toString());
		}

		if (tm.getNetworkOperator().equals("")) {
			sb.append("@无法取得网络运营商");
		} else {
			sb.append("@" + tm.getNetworkOperator());
		}
		if (tm.getNetworkOperatorName().equals("")) {
			sb.append("@无法取得网络运营商名称");
		} else {
			sb.append("@" + tm.getNetworkOperatorName());
		}
		if (tm.getNetworkType() == 0) {
			sb.append("@无法取得网络类型");
		} else {
			sb.append("@" + tm.getNetworkType());
		}
		return sb.toString();
	}
	
	public void printDeviceInfo(Context context,Activity activity){
		DebugUtil.i("=================DeviceInfo=================" );
		DebugUtil.i("language is : "+getLanguage()+"" +
				"\ncountry is : "+getCountry()+"" +
				"\nimei is : "+getIMEI(context)+"" +
				"\nimsi is : "+getIMSI(context)+"" +
				"\ncell is : "+getCell(context)+"" +
				"\nlac is : "+getLac(context)+"" +
				"\nBrand is : "+getBrand()+"" +
				//"\nDensity is : "+getDensity(activity)+"" +
				//"\nExactScreenHeight is : "+getExactScreenHeight(activity)+"" +
				//"\nExactScreenWidth is : "+getExactScreenWidth(activity)+"" +
				"\nIpAddress is : "+getIpAddress()+"" +
				"\nModel is : "+getModel()+"" +
				"\nOsname is : "+getOsname()+"" +
				"\nOsversion is : "+getOsversion()+"" +
						"");
	}
	
	public static class DeviceInfoImpl extends DeviceInfo{
		
		@Override
		public String getLanguage() {
			return language = Locale.getDefault().getLanguage();
		}

		@Override
		public String getCountry() {
			return country = Locale.getDefault().getCountry();
		}

		@Override
		public String getIMEI(Context context) {
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			imei = tm.getDeviceId();
			
			return imei;
		}

		@Override
		public String getIMSI(Context context) {

			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			imsi = tm.getSubscriberId();
			
			return imsi;
		}

		@Override
		public String getCell(Context context) {
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			 
			 
			 if(tm.getCellLocation() instanceof GsmCellLocation){
				//GSM用户
				 GsmCellLocation gsmCellLocation = (GsmCellLocation) tm.getCellLocation();
				 if(gsmCellLocation != null){
		             return gsmCellLocation.getCid()+"";
		         }
			 }else if(tm.getCellLocation() instanceof CdmaCellLocation){
				 //CDMA用户
				 CdmaCellLocation cdmaCellLocation = (CdmaCellLocation) tm.getCellLocation(); 
				 if(cdmaCellLocation != null){
		             return cdmaCellLocation.getBaseStationLatitude()+"";
		         }
			 }
			 
			 return 0+"";
		}

		@Override
		public String getLac(Context context) {
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			 
			 if(tm.getCellLocation() instanceof GsmCellLocation){
				 //GSM用户
				 GsmCellLocation gsmCellLocation = (GsmCellLocation) tm.getCellLocation();
				 if(gsmCellLocation != null){
		             return gsmCellLocation.getLac()+"";
		         }
			 }else if(tm.getCellLocation() instanceof CdmaCellLocation){
				 //CDMA用户
				 CdmaCellLocation cdmaCellLocation = (CdmaCellLocation) tm.getCellLocation(); 
				 if(cdmaCellLocation != null){
		             return cdmaCellLocation.getBaseStationLongitude()+"";
		         }
			 }
			 
			 return 0+"";
		}

		@Override
		public String getModel() {
			return model = android.os.Build.MODEL;
		}

		@Override
		public String getBrand() {
			return brand = android.os.Build.BRAND;
		}

		@Override
		public String getOsname() {
			if(osname == null){
				return "android";
			}
			return osname;
		}

		@Override
		public String getOsversion() {
			return osVersion = android.os.Build.VERSION.RELEASE;
		}

		@Override
		public String getIpAddress() {
			String ipaddress = "";
			try {
				for (Enumeration<NetworkInterface> en = NetworkInterface
						.getNetworkInterfaces(); en.hasMoreElements();) {
					NetworkInterface intf = en.nextElement();
					for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); 
							enumIpAddr.hasMoreElements();
							) {
						InetAddress inetAddress = enumIpAddr.nextElement();
						if (!inetAddress.isLoopbackAddress()) {
							ipaddress = ipaddress + ";"
									+ inetAddress.getHostAddress().toString();
						}
					}
				}
			} catch (SocketException ex) {
				DebugUtil.e( ex.toString());
			}
			DebugUtil.i("DeviceInfo getIpAddress() ipaddress is :" + ipaddress);
			ipAddress = ipaddress;
			return ipaddress;
		}

		@Override
		public boolean isCracked() {
			// TODO Auto-generated method stub
			return isCracked;
		}

		@Override
		public boolean isAllowUnknownSource() {
			// TODO Auto-generated method stub
			return isAllowUnknownSource;
		}

		@Override
		public int getExactScreenWidth(Activity activity) {
			DisplayMetrics metrics = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
			
			return metrics.widthPixels;
		}

		@Override
		public int getExactScreenHeight(Activity activity) {
			DisplayMetrics metrics = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
			
			return metrics.heightPixels;
		}

		@Override
		public float getDensity(Activity activity) {
			DisplayMetrics metrics = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
			
			return metrics.density;
		}

		@Override
		public boolean isAboveIceCream() {
			int sdk = Integer.parseInt(Build.VERSION.SDK);
			if(sdk > 13){
				return true;
			}
			return false;
		}

	}
}


