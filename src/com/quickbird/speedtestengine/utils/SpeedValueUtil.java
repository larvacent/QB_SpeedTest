package com.quickbird.speedtestengine.utils;

import com.quickbird.controls.Constants;

public class SpeedValueUtil {

    /**
     * @param 测速毫秒时间
     * @return 历史事件
     */
    public static String getTime(long time) {
        long peirodTime = System.currentTimeMillis() - time;
        if (peirodTime > 1000L * 60 * 60 * 24 * 30 * 12)
            return (int) (peirodTime / (1000F * 60 * 60 * 24 * 30 * 12)) + "年前";
        if (peirodTime > 1000L * 60 * 60 * 24 * 30)
            return (int) (peirodTime / (1000F * 60 * 60 * 24 * 30f)) + "月前";
        if (peirodTime > 1000L * 60 * 60 * 24)
            return (int) (peirodTime / (1000F * 60 * 60 * 24f)) + "天前";
        if (peirodTime > 1000L * 60 * 60)
            return (int) (peirodTime / (1000F * 60 * 60f)) + "小时前";
        if (peirodTime > 1000L * 60)
            return (int) (peirodTime / (1000F * 60)) + "分钟前";
        if (peirodTime > 1000L)
            return (int) (peirodTime / (1000F)) + "秒前";
        return "1分钟前";
    }
    
    /**
     * @param 测速毫秒时间
     * @return 历史事件
     */
    public static String getTimebak(String string) {
        Long time = Long.parseLong(string);
        Long peirodTime = (long) 0;
        if (time != null)
            peirodTime = System.currentTimeMillis() - time;
        if (peirodTime > 1000L * 60 * 60 * 24 * 30 * 12)
            return (int) (peirodTime / (1000F * 60 * 60 * 24 * 30 * 12)) + "年前";
        if (peirodTime > 1000L * 60 * 60 * 24 * 30)
            return (int) (peirodTime / (1000F * 60 * 60 * 24 * 30f)) + "月前";
        if (peirodTime > 1000L * 60 * 60 * 24)
            return (int) (peirodTime / (1000F * 60 * 60 * 24f)) + "天前";
        if (peirodTime > 1000L * 60 * 60)
            return (int) (peirodTime / (1000F * 60 * 60f)) + "小时前";
        if (peirodTime > 1000L * 60)
            return (int) (peirodTime / (1000F * 60)) + "分钟前";
        if (peirodTime > 1000L)
            return (int) (peirodTime / (1000F)) + "秒前";
        return "1分钟前";
    }

    /**
     * 获取速度等级
     * 
     * @param networkType
     * @param speed
     * @return
     */
    public static int getSpeedLevel(int networkType, float speed) {
        if (speed > 500) {
            return 5;
        }
        int temp1 = (networkType == Constants.NETWORK_STATUS_MOBILE) ? 250
                : 300;
        int temp2 = (networkType == Constants.NETWORK_STATUS_MOBILE) ? 120
                : 150;
        if (speed > temp1) {
            return 4;
        }
        if (speed > temp2) {
            return 3;
        }
        if (speed > 50) {
            return 2;
        }
        if (speed > 20) {
            return 1;
        }
        return 0;
    }

    /**
     * @param brokeRecords
     * @return
     */
    public static int getMetal(long brokeRecords) {
        if (brokeRecords > 800) {
            return 4;
        }
        if (brokeRecords > 500) {
            return 3;
        }
        if (brokeRecords > 200) {
            return 2;
        }
        if (brokeRecords > 100) {
            return 1;
        }
        return 0;
    }

    public static int getNetWorkType(String networkType) {
        if (StringUtil.isNull(networkType))
            return Constants.NETWORK_STATUS_UNKNOWN;
        if (networkType.equals(Constants.WIFI))
            return Constants.NETWORK_STATUS_WIFI;
        if (networkType.equals(Constants.GPRS))
            return Constants.NETWORK_STATUS_MOBILE;
        return Constants.NETWORK_STATUS_UNKNOWN;
    }

    public static String getSpeed(int speed) {
        return StringUtil.formatSpeed(speed / 1024.0f);
    }
    
}
