package com.quickbird.speedtestengine;

/*******************************************************************
 * Copyright @ 2012 ChenFengYun (BeiJing) Technology LTD
 * <P>
 * ====================================================================
 * <P>
 * Project:　　　　　TestSpeedy
 * <P>
 * FileName:　　　　　Speed.java
 * <P>
 * Description:　　　
 * <P>
 * Author:　　　　　　XD.LIU
 * <P>
 * Create Date:　　　2012-10-26 下午3:57:50
 ********************************************************************/
public class SpeedValue {

    private int speedId = 0;
    private String testDateTime = "";
    private long testTime = -1L;
    private String networkType = "";
    private String internalIP = "";
    private String externalIP = "";
    private String server = "";
    private Double latitude = -1.0D;
    private Double longitude = -1.0D;
    private String locationDesc = "";
    private int ping = 0;
    private int downloadSpeed = -1;
    private int uploadSpeed = -1;
    private long costTime = -1;
    private int downloadByte = -1;
    private int rank = -1;

    public SpeedValue() {
        reset();
    }

    private void reset() {
        speedId = 0;
        testDateTime = "";
        testTime = -1L;
        networkType = "";
        internalIP = "";
        externalIP = "";
        server = "";
        latitude = -1.0D;
        longitude = -1.0D;
        locationDesc = "";
        ping = -1;
        downloadSpeed = -1;
        uploadSpeed = -1;
        costTime = -1;
        downloadByte = -1;
        rank = -1;
    }

    public SpeedValue(String testDateTime, long testTime, String networkType,
            String internalIP, String externalIP, String server,
            Double latitude, Double longitude, String locationDesc, int ping,
            int downloadSpeed, int uploadSpeed, int costTime, int downloadByte,
            int rank) {
        super();
        this.testDateTime = testDateTime;
        this.testTime = testTime;
        this.networkType = networkType;
        this.internalIP = internalIP;
        this.externalIP = externalIP;
        this.server = server;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationDesc = locationDesc;
        this.ping = ping;
        this.downloadSpeed = downloadSpeed;
        this.uploadSpeed = uploadSpeed;
        this.costTime = costTime;
        this.rank = rank;
    }

    public int getSpeedId() {
        return speedId;
    }

    public void setSpeedId(int speedId) {
        this.speedId = speedId;
    }

    public String getTestDateTime() {
        return testDateTime;
    }

    public void setTestDateTime(String testDateTime) {
        this.testDateTime = testDateTime;
    }

    public long getTestTime() {
        return testTime;
    }

    public void setTestTime(long testTime) {
        this.testTime = testTime;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public String getInternalIP() {
        return internalIP;
    }

    public void setInternalIP(String internalIP) {
        this.internalIP = internalIP;
    }

    public String getExternalIP() {
        return externalIP;
    }

    public void setExternalIP(String externalIP) {
        this.externalIP = externalIP;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getLocationDesc() {
        return locationDesc;
    }

    public void setLocationDesc(String locationDesc) {
        this.locationDesc = locationDesc;
    }

    public int getPing() {
        return ping;
    }

    public void setPing(int ping) {
        this.ping = ping;
    }

    public int getDownloadSpeed() {
        return downloadSpeed;
    }

    public void setDownloadSpeed(int downloadSpeed) {
        this.downloadSpeed = downloadSpeed;
    }

    public int getUploadSpeed() {
        return uploadSpeed;
    }

    public void setUploadSpeed(int uploadSpeed) {
        this.uploadSpeed = uploadSpeed;
    }

    public long getCostTime() {
        return costTime;
    }

    public void setCostTime(long costTime) {
        this.costTime = costTime;
    }

    public int getDownloadByte() {
        return downloadByte;
    }

    public void setDownloadByte(int downloadByte) {
        this.downloadByte = downloadByte;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public String toString() {
        return "SpeedValue [speedId=" + speedId + ", testDateTime="
                + testDateTime + ", testTime=" + testTime + ", networkType="
                + networkType + ", internalIP=" + internalIP + ", externalIP="
                + externalIP + ", server=" + server + ", latitude=" + latitude
                + ", longitude=" + longitude + ", locationDesc=" + locationDesc
                + ", ping=" + ping + ", downloadSpeed=" + downloadSpeed
                + ", uploadSpeed=" + uploadSpeed + ", costTime=" + costTime
                + ", downloadByte=" + downloadByte + ", rank=" + rank + "]";
    }

}
