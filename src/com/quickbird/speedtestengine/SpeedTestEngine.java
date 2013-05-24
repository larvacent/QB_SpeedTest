package com.quickbird.speedtestengine;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

public class SpeedTestEngine
{
  private static final String DEFAULT_CONFIG_URL = "http://www.speedtest.net/speedtest-config.php?mode=android";
  public static final String LOGTAG = "SpeedTestEngine";
  public static final SpeedTestEngine sInstance = new SpeedTestEngine();
  private String mConfigUrl = null;
  private Context mContext = null;
  private Location mCurrentLocation = null;
  private boolean mCustomClientFeed = false;
  private int mCustomerId = -1;
  private boolean mDebug = false;
  private List<WeakReference<SpeedTestEngineDelegate>> mDelegates = null;
  private float mDensity = 1.0F;
  private String mDeviceId = null;
  private int mDownloadTestLength = 15;
  private int mDownloadThreadCount = -1;
  private boolean mDownloadThreadCountFixed = false;
  private Location mFeedLocation = null;
  private Handler mHandler = null;
  private String mHashSuffix = "lol";
  private String mLastestAppVersion = null;
  private int mLatencySampleCount = 5;
  private LocationListener mLocationListener = null;
  private int mLocationMinIntervalUpdate = 1000;
  private int mLocationMinMeterDistanceUpdate = 1000;
  private Logger mLogger = null;
  private int mMaxUpdateCount = 2147483647;
  private int mPingClosestServersCount = -1;
  private int mPingClosestServersSampleCount = 3;
  private int mPrivacyPolicyVersion = 0;
  private boolean mRequestingLocationUpdates = false;
  private String mResultSubmitUrl = "http://www.speedtest.net/api/android.php";
  private boolean mServersAddedManually = false;
  private String mThrottlingCustomerName = null;
  private boolean mUpdateServersOnLocationChange = true;
  private int mUploadBufferSize = -1;
  private int mUploadTestLength = 15;
  private int mUploadThreadCount = -1;
  private boolean mUploadThreadCountFixed = false;
  private boolean mWifiResultEnabled = false;

  public static String cellIdToDecimalString(int paramInt)
  {
    String str;
    if (paramInt != -1)
    {
      int i = paramInt >> 16;
      int j = paramInt - (i << 16);
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = Integer.valueOf(i);
      arrayOfObject[1] = Integer.valueOf(j);
      str = String.format("%d:%d", arrayOfObject);
    }
    else
    {
      str = "";
    }
    return str;
  }

  public static String cellIdToDecimalString(String paramString)
  {
    String str;
    if ((paramString != null) && (paramString.length() != 0))
      str = cellIdToDecimalString(Integer.parseInt(paramString));
    else
      str = "";
    return str;
  }



  public static SpeedTestEngine getInstance()
  {
    return sInstance;
  }

  public static NetworkInfo getNetworkInfo(Context paramContext)
  {
    ConnectivityManager localConnectivityManager = (ConnectivityManager)paramContext.getSystemService("connectivity");
    NetworkInfo localNetworkInfo = localConnectivityManager.getNetworkInfo(1);
    if (!localNetworkInfo.isConnected())
      localNetworkInfo = localConnectivityManager.getActiveNetworkInfo();
    return localNetworkInfo;
  }

  public static int getNetworkType(Context paramContext)
  {
    int i;
    if (!((ConnectivityManager)paramContext.getSystemService("connectivity")).getNetworkInfo(1).isConnected())
      i = ((TelephonyManager)paramContext.getSystemService("phone")).getNetworkType();
    else
      i = -1;
    return i;
  }

  private int getThreadCountFromNetworkType()
  {
    return 1;
  }

  public static boolean isWifiConnected(Context paramContext)
  {
    return ((ConnectivityManager)paramContext.getSystemService("connectivity")).getNetworkInfo(1).isConnected();
  }

  public void addDelegate(SpeedTestEngineDelegate paramSpeedTestEngineDelegate)
  {
    if (!hasDelegate(paramSpeedTestEngineDelegate))
      this.mDelegates.add(new WeakReference(paramSpeedTestEngineDelegate));
  }

  public String getConfigUrl()
  {
    String str;
    if (this.mConfigUrl == null)
      str = "http://www.speedtest.net/speedtest-config.php?mode=android";
    else
      str = this.mConfigUrl;
    return str;
  }

  public Context getContext()
  {
    return this.mContext;
  }

  public int getCustomerId()
  {
    return this.mCustomerId;
  }

  public float getDensity()
  {
    return this.mDensity;
  }

  public String getDeviceId()
  {
    String str;
    if (this.mDeviceId == null)
      str = null;
    try
    {
      str = ((TelephonyManager)this.mContext.getSystemService("phone")).getDeviceId();
      if ((str == null) || (str.length() == 0))
      {
        str = Settings.Secure.getString(this.mContext.getContentResolver(), "android_id");
        str = str;
      }
      if (str == null)
        str = "";
      this.mDeviceId = str;
      return this.mDeviceId;
    }
    catch (Exception localException)
    {
      while (true)
        Log.e("SpeedTestEngine", "Error getting device id", localException);
    }
  }

  public int getDownloadTestLength()
  {
    return this.mDownloadTestLength;
  }

  public int getDownloadThreadCount()
  {
    if (this.mDownloadThreadCount == -1)
      this.mDownloadThreadCount = getThreadCountFromNetworkType();
    return this.mDownloadThreadCount;
  }

  public Location getFeedLocation()
  {
    return this.mFeedLocation;
  }

  public String getHashSuffix()
  {
    return this.mHashSuffix;
  }

  public String getLastestAppVersion()
  {
    return this.mLastestAppVersion;
  }

  public int getLatencySampleCount()
  {
    return this.mLatencySampleCount;
  }

  public Location getLocation()
  {
    return this.mCurrentLocation;
  }

  public int getLocationMinIntervalUpdate()
  {
    return this.mLocationMinIntervalUpdate;
  }

  public int getLocationMinMeterDistanceUpdate()
  {
    return this.mLocationMinMeterDistanceUpdate;
  }



  public int getPingClosestServersCount()
  {
    int i;
    if (this.mPingClosestServersCount != -1)
      i = this.mPingClosestServersCount;
    else
      i = 1;
    return i;
  }

  public int getPingClosestServersSampleCount()
  {
    return this.mPingClosestServersSampleCount;
  }

  public int getPrivacyPolicyVersion()
  {
    return this.mPrivacyPolicyVersion;
  }

  public String getResultSubmitUrl()
  {
    return this.mResultSubmitUrl;
  }

  public boolean getServersAddedManually()
  {
    return this.mServersAddedManually;
  }

  public String getThrottlingCustomerName()
  {
    return this.mThrottlingCustomerName;
  }

  public boolean getUpdateServersOnLocationChange()
  {
    return this.mUpdateServersOnLocationChange;
  }

  public int getUploadBufferSize()
  {
    return this.mUploadBufferSize;
  }

  public int getUploadTestLength()
  {
    return this.mUploadTestLength;
  }

  public int getUploadThreadCount()
  {
    if (this.mUploadThreadCount == -1)
      this.mUploadThreadCount = 1;
    return this.mUploadThreadCount;
  }

  public boolean hasDelegate(SpeedTestEngineDelegate paramSpeedTestEngineDelegate)
  {
    boolean bool = false;
    Iterator localIterator = this.mDelegates.iterator();
    while (localIterator.hasNext())
    {
      SpeedTestEngineDelegate localSpeedTestEngineDelegate = (SpeedTestEngineDelegate)((WeakReference)localIterator.next()).get();
      if ((localSpeedTestEngineDelegate != null) && (localSpeedTestEngineDelegate.equals(paramSpeedTestEngineDelegate)))
      {
        bool = true;
        break;
      }
    }
    return bool;
  }

  public void init(Context paramContext)
  {
    this.mContext = paramContext;
    this.mHandler = new Handler();
    this.mDeviceId = getDeviceId();
  }

  public boolean isCustomClientFeed()
  {
    return this.mCustomClientFeed;
  }

  public boolean isDebug()
  {
    return this.mDebug;
  }

  public boolean isWifiResultEnabled()
  {
    return this.mWifiResultEnabled;
  }

  public void removeDelegate(SpeedTestEngineDelegate paramSpeedTestEngineDelegate)
  {
    Iterator localIterator = this.mDelegates.iterator();
    while (localIterator.hasNext())
    {
      WeakReference localWeakReference = (WeakReference)localIterator.next();
      SpeedTestEngineDelegate localSpeedTestEngineDelegate = (SpeedTestEngineDelegate)localWeakReference.get();
      if ((localSpeedTestEngineDelegate != null) && (localSpeedTestEngineDelegate.equals(paramSpeedTestEngineDelegate)))
        this.mDelegates.remove(localWeakReference);
    }
  }

  public void serversUpdated()
  {
    Iterator localIterator = this.mDelegates.iterator();
    while (localIterator.hasNext())
    {
      SpeedTestEngineDelegate localSpeedTestEngineDelegate = (SpeedTestEngineDelegate)((WeakReference)localIterator.next()).get();
      if (localSpeedTestEngineDelegate != null)
        localSpeedTestEngineDelegate.speedTestEngineServersUpdated();
    }
  }

  public void setConfigUrl(String paramString)
  {
    this.mConfigUrl = paramString;
  }

  public void setCustomClientFeed(boolean paramBoolean)
  {
    this.mCustomClientFeed = paramBoolean;
  }

  public void setCustomerId(int paramInt)
  {
    this.mCustomerId = paramInt;
  }

  public void setDebug(boolean paramBoolean)
  {
    this.mDebug = paramBoolean;
  }

  public void setDensity(float paramFloat)
  {
    this.mDensity = paramFloat;
  }

  public void setDeviceId(String paramString)
  {
    this.mDeviceId = paramString;
  }

  public void setDownloadTestLength(int paramInt)
  {
    this.mDownloadTestLength = paramInt;
  }

  public void setDownloadThreadCount(int paramInt)
  {
    if (!this.mDownloadThreadCountFixed)
    {
      if (this.mDebug)
      {
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = Integer.valueOf(paramInt);
        Log.v("SpeedTestEngine", String.format("Setting download thread count: %d", arrayOfObject));
      }
      this.mDownloadThreadCount = paramInt;
    }
  }

  public void setDownloadThreadCountFixed(boolean paramBoolean)
  {
    this.mDownloadThreadCountFixed = paramBoolean;
  }


  public void setHashSuffix(String paramString)
  {
    this.mHashSuffix = paramString;
  }

  public void setLatencySampleCount(int paramInt)
  {
    this.mLatencySampleCount = paramInt;
  }


  public void setLocationMinIntervalUpdate(int paramInt)
  {
    this.mLocationMinIntervalUpdate = paramInt;
  }

  public void setLocationMinMeterDistanceUpdate(int paramInt)
  {
    this.mLocationMinMeterDistanceUpdate = paramInt;
  }

  public void setLogger(Logger paramLogger)
  {
    this.mLogger = paramLogger;
  }

  public void setMaxUpdateCount(int paramInt)
  {
    this.mMaxUpdateCount = paramInt;
  }

  public void setPingClosestServersCount(int paramInt)
  {
    this.mPingClosestServersCount = paramInt;
  }

  public void setPingClosestServersSampleCount(int paramInt)
  {
    this.mPingClosestServersSampleCount = paramInt;
  }

  public void setPrivacyPolicyVersion(int paramInt)
  {
    this.mPrivacyPolicyVersion = paramInt;
  }

  public void setResultSubmitUrl(String paramString)
  {
    this.mResultSubmitUrl = paramString;
  }

  public void setServersAddedManually(boolean paramBoolean)
  {
    this.mServersAddedManually = paramBoolean;
    if (paramBoolean)
      this.mUpdateServersOnLocationChange = false;
  }

  public void setThrottlingCustomerName(String paramString)
  {
    this.mThrottlingCustomerName = paramString;
  }

  public void setUpdateServersOnLocationChange(boolean paramBoolean)
  {
    this.mUpdateServersOnLocationChange = paramBoolean;
  }

  public void setUploadBufferSize(int paramInt)
  {
    this.mUploadBufferSize = paramInt;
  }

  public void setUploadTestLength(int paramInt)
  {
    this.mUploadTestLength = paramInt;
  }

  public void setUploadThreadCount(int paramInt)
  {
    if (!this.mUploadThreadCountFixed)
    {
      if (this.mDebug)
      {
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = Integer.valueOf(paramInt);
        Log.v("SpeedTestEngine", String.format("Setting upload thread count: %d", arrayOfObject));
      }
      this.mUploadThreadCount = paramInt;
    }
  }

  public void setUploadThreadCountFixed(boolean paramBoolean)
  {
    this.mUploadThreadCountFixed = paramBoolean;
  }

  public void setWifiResultEnabled(boolean paramBoolean)
  {
    this.mWifiResultEnabled = paramBoolean;
  }

  public static abstract interface SpeedTestEngineDelegate
  {
    public abstract void speedTestClientAppVersion(String paramString);

    public abstract void speedTestEngineLocationUpdated(Location paramLocation1, Location paramLocation2);

    public abstract void speedTestEngineServersUpdated();
  }
}