package com.quickbird.speedtestengine.utils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.quickbird.controls.Constants;
import com.quickbird.controls.DeviceInfo;
import com.quickbird.controls.DeviceInfo.DeviceInfoImpl;
import com.quickbird.speedtestengine.SpeedValue;

public class ProtocolUtil {

    public static byte[] prepareHeader(Byte cmd) {
        DebugUtil.i("http", "// 1. Prepare header");

        byte[] header = new byte[] { 'd', 'd', 'b', 'p', // Magic bytes
                1, 0, // major version, short: 1
                0, 0, // minor version, short: 0
                0, 0, // compression, short: 1
                1, 0, // encryption, short: 2
                cmd, 0, // command, short: 2
        };
        return header;
    }

    public static HttpURLConnection prepareConnection(Context context,
            String serverUrl) throws IOException, MalformedURLException, ProtocolException {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
            }
        } };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (GeneralSecurityException e) {
            
        }
        // Now you can access an https URL without having the certificate in the
        // truststore
        URL url = new URL(serverUrl);
        HttpURLConnection urlConnection = null;
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setConnectTimeout(Constants.TIMEOUT_4_CONN);      // 设置连接主机超时
        urlConnection.setReadTimeout(Constants.TIMEOUT_4_READ);         // 设置读取数据超时
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        urlConnection.connect();                                        // 打开连接
        
        return urlConnection;
    }

    public static HttpURLConnection prepareConnectionLoop(Context context,String serverUrl)
            throws IOException, MalformedURLException, ProtocolException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException, CertificateException {
        DebugUtil.i("http", "3. Prepare Connection");
        HttpURLConnection urlConnection = prepareConnection(context ,serverUrl);

        int times = 3;
        while (urlConnection == null && times > 0) {
            times--;
            urlConnection = prepareConnection(context,serverUrl);
            if (urlConnection != null) {
                break;
            }
        }

        return urlConnection;
    }
    
    public static byte[] prepareRequestBody(Context context, SpeedValue speedValue) {
        byte[] buffer = null;
        String imsi = APNUtil.getImsi(context);
        try {  
            // 首先最外层是{}，是创建一个对象
            JSONObject requestJsonObj = new JSONObject();
            if (!StringUtil.isNull(imsi))
                requestJsonObj.put("imsi", imsi);
            requestJsonObj.put("networking", NetWorkUtil.getNetworkStatus(context));
            requestJsonObj.put("speed", speedValue.getDownloadSpeed());
//            if (speedValue.getLatitude() != -1.0D &&speedValue.getLongitude()!= -1.0D) {
//                JSONObject location = new JSONObject();
//                location.put("latitude", speedValue.getLatitude());
//                location.put("longitude", speedValue.getLongitude());
//                speed.put("location", location);
//            }
            if (speedValue.getLatitude() != -1.0D &&speedValue.getLongitude()!= -1.0D) {
                JSONArray location = new JSONArray();  
                location.put(speedValue.getLatitude()).put(speedValue.getLongitude());  
                requestJsonObj.put("location", location);
            }
            buffer = requestJsonObj.toString().getBytes();
        } catch (JSONException ex) {  
            // 键为null或使用json不支持的数字格式(NaN, infinities)  
            throw new RuntimeException(ex);  
        }   
        return buffer;
    }
    
    public static void WriteRequest2Remote(byte[] buffer, HttpURLConnection conn) throws IOException {
        OutputStream out = conn.getOutputStream();
        out.write(buffer);
        out.close();
    }

    public static boolean checkNetworkAvailable(Context context) {
        int networkStatus = NetWorkUtil.getNetworkStatus(context);
        if (networkStatus == Constants.NETWORK_STATUS_NULL) {
            return false;
        } else {
            return true;
        }
    }

    /***
     * 获取新
     * 
     * @param context
     * @param domain
     * @return
     */
    public static String getServerIP(Context context, String domain) {
        try {
            DebugUtil.i("http", "getServerIP() domain : " + domain);
            InetAddress myServer = InetAddress.getByName(domain);
            DebugUtil.i("http", "getServerIP() myServer : " + myServer);
            return myServer.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getNetworkStatusName(Context context, int networkState) {
        String networkStatusName = null;
        switch (networkState) {
        case Constants.NETWORK_STATUS_NULL:
            networkStatusName = "null";
            break;
        case Constants.NETWORK_STATUS_MOBILE_PROXY:
        case Constants.NETWORK_STATUS_MOBILE:
        case Constants.NETWORK_STATUS_MOBILE_SYS:
            // TODO jz.lin
            // 此处的getNetworkTypeENNameByIMSI获取名称不准确，只能获取到中国的运营商，不支持国外用户
            DeviceInfo deviceInfo = new DeviceInfoImpl();
            networkStatusName = getNetworkTypeENNameByIMSI(deviceInfo.getIMSI(context));
            break;
        case Constants.NETWORK_STATUS_WIFI:
            networkStatusName = "wifi";
            break;
        }

        return networkStatusName;
    }

    /**
     * 获取运营商类型
     * 
     * @param imsi
     * @return
     */
    public static int getNetworkTypeCodeByIMSI(String imsi) {
        int networkType = 0;

        if (!StringUtil.isNull(imsi)) {

            if (imsi.length() < 5) {
                imsi = "46000";
            }
            String networkTypeStr = imsi.substring(4, 5);
            int networkTypeCode = Integer.parseInt(networkTypeStr);
            switch (networkTypeCode) {
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

    /** 获取运营商的英文名称 */
    public static String getNetworkTypeENNameByIMSI(String imsi) {
        String networkTypeName = "";

        int networkType = getNetworkTypeCodeByIMSI(imsi);
        switch (networkType) {
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

    private static boolean isRoot() {
        try {
            return ((new File("/system/xbin/su").exists()) || (new File(
                    "/system/bin/su").exists()));
        } catch (Exception e) {
            return false;
        }
    }

}
