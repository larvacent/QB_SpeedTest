package com.quickbird.controls;

import com.quickbird.speedtestengine.utils.FileUtil;

/***
 * 
 * @author FenDou
 * 
 */
public class Constants {
    
    /**保存的截图的名称*/
    public static final String PIC_PRE_PATH_NAME  = FileUtil.getImagePath()+"/pic_shoot.png";
    public static final String PIC_THUMB_PATH_NAME  = FileUtil.getImagePath()+"/pic_shoot_thumb.png";
    
    public static final String DOWNLOAD500KURL = "http://upyun.doodoobird.com/speedtest500k.tgz";
//    public static final String DOWNLOAD2MURL = "http://upyun.doodoobird.com/speedtest.zip";
    public static final String DOWNLOAD2MURL = "http://www.apk.anzhi.com/apk/201303/29/com.doodoobird.activity_39891000_0.apk";
    
    /** 用于控制用户导航提示的次数 */
    public static final int USER_GUIDE_SHOW_TIME = 1;
    
    public static final int TIMEOUT_4_CONN = 10*1000;
    public static final int TIMEOUT_4_READ = 10*1000;
    
    public static final String GPRS = "2G/3G";
    public static final String WIFI = "Wi-Fi";
    
    public static final String APP_IS_RUNNING = "app_is_running";
    /** HTTP请求返回的XML数据 */
    public static final String RESPONSE_DATA = "xml_data";
    /** HTTP请求返回的状态码 */
    public static final String RESPONSE_CODE = "StatusCode";
    public static final String CONTACT_PHONE_NUMBER = "data1";
    public static final String CONTACT_PHONE_TYPE = "data2";
    
    /**保存APN的属性文件*/
    public static final String APN_FILE = "Apn.log";

    /** 中国移动 */
    public static final int NETWORK_TYPE_CHINA_MOBILE = 0;
    /** 中国联通 */
    public static final int NETWORK_TYPE_CHINA_UNICOM = 1;
    /** 中国电信 */
    public static final int NETWORK_TYPE_CHINA_TELECOM = 2;
    /** 其他 */
    public static final int NETWORK_TYPE_OTHER = 3;
    
    /** 中国移动域名 */
    public static final String NETWORK_TYPE_CHINA_MOBILE_RPC_DOMAIN = "cm.a.doodoobird.com";
    /** 中国移动端口号 */
    public static final String NETWORK_TYPE_CHINA_MOBILE_RPC_PORT = "63128";
    /** 中国联通域名 */
    public static final String NETWORK_TYPE_CHINA_UNICOM_RPC_DOMAIN = "cu.a.doodoobird.com";
    /** 中国联通端口号 */
    public static final String NETWORK_TYPE_CHINA_UNICOM_RPC_PORT = "63128";
    /** 中国电信域名 */
    public static final String NETWORK_TYPE_CHINA_TELECOM_RPC_DOMAIN = "ct.a.doodoobird.com";
    /** 中国电信端口号 */
    public static final String NETWORK_TYPE_CHINA_TELECOM_RPC_PORT = "63128";
    
    /** RPC command */
    public static final Byte COMMAND_ACTIVE = 1;
    public static final Byte COMMAND_CHECKUPDATE = 2;
    public static final Byte COMMAND_GETREPORT = 3;
    public static final Byte COMMAND_GETUNREADMSGS = 4;
    public static final Byte COMMAND_MARKMSGSASREAD = 5;
    public static final Byte COMMAND_FEEDBACK = 5;
    public static final Byte COMMAND_SPEEDMATCH = 6;
    public static final Byte COMMAND_TRACELOGS = 7;
    public static final Byte COMMAND_DIAGNOSISLOGS = 8;
    public static final Byte COMMAND_ZIPSETTING = 9;
    public static final Byte COMMAND_SYNCAPPOPERATION = 10;
    public static final Byte COMMAND_CATCHPEST = 11;
    public static final Byte COMMAND_ADPLUGLIB = 13;
    public static final Byte COMMAND_REPORTBGTRAFFIC = 14;

    /** 流量统计 */
    public static final String FLOW_ANALYSIS_SECURITY_THREAT_DATE = "flow_analysis_security_threat_date";
    public static final String FLOW_ANALYSIS_SECURITY_THREAT_REASON = "flow_analysis_security_threat_reason";
    public static final String FLOW_ANALYSIS_SECURITY_THREAT_DETAIL = "flow_analysis_security_threat_detail";
    public static final String FLOW_ANALYSIS_APP_ICON = "flow_analysis_app_icon";
    public static final String FLOW_ANALYSIS_APP_NAME = "flow_analysis_app_name";
    public static final String FLOW_ANALYSIS_APP_FLOW = "flow_analysis_app_flow";
    
    /**网络状态*/
    public static final int NETWORK_STATUS_WIFI = 0;
    public static final int NETWORK_STATUS_MOBILE = 1;
    public static final int NETWORK_STATUS_NULL = 2;
    public static final int NETWORK_STATUS_MOBILE_PROXY = 3;
    public static final int NETWORK_STATUS_MOBILE_SYS = 4;
    public static final int NETWORK_STATUS_UNKNOWN = 5;
    
    public static final int NETWORKING_UNKOWN = 0;
    public static final int NETWORKING_WIFI = 1;
    public static final int NETWORKING__GPRS = 2;
    
    /**错误码*/
    public static final int ACTIVE_RESULTCODE_NETWORK_ERROR = 1000; //网络错误
    public static final int ACTIVE_RESULTCODE_NETWORK_HOST_ERROR = 1001;    //获取主机地址错误
    
    /**app操作信息  卸载、限制网络*/
    public static final int OPERATION_UNINSTALL = 1;    //卸载
    public static final int OPERATION_LIMITGPRS = 2;    //限制2G/3G网络
    
    /******流量排行时间类型************/
    public static final String DAY = "day";
    public static final String MONTH = "month";
    
    /*************分隔符****************/
    public static final String SEPARATPR = ":"; 
    
    /***********广告插件关键字符串***************/
//  public static final String ADPLUG = "com.google.ads";
    
    /*********含广告插件的应用平均预计消耗流量**************/
    public static final double AD_TRAFFIC = 2.5;
    
    /*************保存的数据的天数******************/
    public static int DATA_SAVED_DAYS = 60;
    
    /***************appquality类型********************/
    public static final int TYPE_AD = 1;  //含有广告插件
    public static final int TYPE_UNUSED_APP = 2; //很久未使用（60天）
    public static final int TYPE_PEST_APP = 3; //害虫软件
    
    public static final String ADPLUG_LIB = "AdPlugLib.xml"; //广告插件库名称
    public static final String EXPLORER_LIB = "explorer.xml"; //浏览器包名库名称
    
    /**刷新流量排行listview****/
    public static final String REFRESH_LISTVIEW = "com.doodoobird.activity.refreshlistview";
    
    public static boolean isHomePageAlive = false;  //首页是否被销毁
    
    // 注意！！此处必须设置appkey及appsecret，如何获取新浪微博appkey和appsecret请另外查询相关信息，此处不作介绍
    public static final String CONSUMER_KEY = "2348668880";// 替换为开发者的appkey，例如"1646212860";
    public static final String CONSUMER_SECRET = "41d3234e109295aa443931ca31a2fe23";// 替换为开发者的appkey，例如"94097772160b6f8ffc1315374d8861f9";
    public static final String REDIRECT_URL = "http://www.quickbird.com";
    public static final long KUAINIAOID = 2610408071L;
    // 注意！！此处必须设置腾讯微博appkey及appsecret
    public static final String APP_KEY = "801332298";// 替换为开发者的appkey，例如"1646212860";
    public static final String APP_SECRET = "add1ec2b5f36470c2096ac8b14043bcd";// 替换为开发者的appkey，例如"94097772160b6f8ffc1315374d8861f9";
    public static final String TECENT_REDIRECT_URL = "http://www.quickbird.com";
    // APP_ID 微信
    public static final String APP_ID = "wx4df2432b3a90e8d1";
    public static class ShowMsgActivity {
        public static final String STitle = "showmsg_title";
        public static final String SMessage = "showmsg_message";
        public static final String BAThumbData = "showmsg_thumb_data";
    }
}
