package com.quickbird.controls;


/*******************************************************************
 * Copyright @ 2013 ChenFengYun (BeiJing) Technology LTD
 * <P>
 * ====================================================================
 * <P>
 * Project:　　　　　TestSpeedy
 * <P>
 * FileName:　　　 　Config.java
 * <P>
 * Description:　　　
 * <P>
 * Author:　　　　　　XD.LIU
 * <P>
 * Create Date:　　　2013-1-28 上午10:37:09
 ********************************************************************/
public class Config {
    
    // 是否在控制台打印信息
    public static final boolean DEBUG = true; //是否在控制台打印信息 
    public static final boolean LOG = false; //是否在SDCARD记录日志
    public static final String DEBUG_TAG = "quickbird";
    //测试代理是否可用的URL
    public static final String PROXY_TEST_URL = "http://www.baidu.com";
    
    public static String ONLINE_URL;
    public static boolean ONLINE_URL_OK = true;
    //RPC接口地址
    public static final String SERVER_URL = "https://api.quickbird.com/speedtest/rank/";
//    public static final String SERVER_URL = "http://10.18.94.19:8088/speedtest/rank/";
    
    // 屏幕大小
    public static final int SCREEN_320_480 = 1;
    public static final int SCREEN_480_800 = 2;
    public static final int SCREEN_480_854 = 3;
    
    // 一些特殊的机型作特殊处理
    public static final int OMS_MOTO_710 = 1;
    public static final int OMS_GW_880 = 2;
    
    // 一些全局的系统配置参数
    public static boolean mIsCmwap = false; 
    public static String IMSI = "";
    public static int mSdkVersion = 7;
    public static int mScreenType = SCREEN_320_480;
    public static int mPhoneType = -1;
    public static boolean mIsOphoneSystem = false;
    public static boolean mRunningInEmulator = false;
    
    // 一些全局的系统配置参数
    
    //==================设备屏幕基本参数======================
    /**当前设备的密度*/
    public static float mDensity = 1.5f;
    /**当前设备真实屏幕宽度*/
    public static int mExactScreenWidth;
    /**当前设备真实屏幕高度*/
    public static int mExactScreenHeight;
    //======================END==========================
}
