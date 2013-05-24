package com.quickbird.speedtest.gui.activity;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.quickbird.controls.Constants;
import com.quickbird.enums.SpeedTestError;
import com.quickbird.speedtest.R;
import com.quickbird.speedtestengine.SpeedValue;
import com.quickbird.speedtestengine.TestParameters;
import com.quickbird.speedtestengine.TestParametersLatency;
import com.quickbird.speedtestengine.TestParametersTransfer;
import com.quickbird.speedtestengine.TestTaskCallbacks;
import com.quickbird.speedtestengine.tasks.DownloadTestTask;
import com.quickbird.speedtestengine.tasks.LatencyTestTask;
import com.quickbird.speedtestengine.tasks.TestTask;
import com.quickbird.speedtestengine.utils.DebugUtil;
import com.quickbird.speedtestengine.utils.NetWorkUtil;
import com.quickbird.speedtestengine.utils.StringUtil;
import com.quickbird.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;
public class SpeedTestActivity_bak extends BaseActivity implements AMapLocationListener{
    
    private Button pressedView;
    private Button speedTestBtn;
    private TextView pingTxt, networkTxt, progressTxt;
    private TextView speed_unit;
    private TextView issueInfo;
    private ProgressBar progressBar;
    private ImageView pingRotate;
    private ImageView lightView, lightAbove, lightLow;
    private ImageView needle, breatheLed, breatheLedAbove;
    private ImageView[] speedNam = new ImageView[3];
    private ImageView speedPoint1,speedPoint2;
    
    private int networkStatus;
    private float currentDegree = 0l;
    private boolean speedTestControl = true;
    
    private ClickListener clickListener;
    private MainHandler mHandler = new MainHandler();
    private TestTask mCurrentTestTask = null;
    private Context context;
    protected static final String LOGTAG = "LatencyTestTask";
    java.text.DecimalFormat kps = new java.text.DecimalFormat("#0");
    java.text.DecimalFormat mps = new java.text.DecimalFormat("#0.00");
    private TypedArray speedPic;

    private LocationManagerProxy mAMapLocManager = null;
    private SpeedValue speedValue;
    private boolean onPrepare;
    private boolean onTesting;
    
    private static float minSpeed;
    private static float maxSpeed;
    private static float instantSpeed;
    private float speedShow = 0;
    private long begainTime, lastTime, nextTime;
    public static final int TIME_INTERVAL = 800;
    private long downloadByte;
    private long downloadTime;
    
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        }
    };
    
    private BroadcastReceiver mNetworkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(onTesting)
                if(!NetWorkUtil.getWifiState(context)||!NetWorkUtil.getMobileStatus(context))
                    if(mCurrentTestTask!=null){
                        mCurrentTestTask.cancel(true);
                        prepareNextTest();
                    }
        }
    };
    
    private class CalculateSpeedThread extends Thread {
        @Override
        public void run() {
            int i = 0;
            int count = 0;
            long lastDownloadByte = 0;
            minSpeed = 1000000000;
            maxSpeed = 0;
            instantSpeed = 0;
            lastTime = begainTime;
            while (!Thread.currentThread().isInterrupted() && speedTestControl) {

                try {
                    calaulateSpeed(lastDownloadByte);
                    
                    updateText(instantSpeed, MainHandler.UPDATE_INSTANT_SPEED);
                    lastTime = nextTime;
                    lastDownloadByte = downloadByte;

                    if (count++ == 20) {
                        speedTestControl = false;
                        onTesting = false;
                        mHandler.sendEmptyMessage(MainHandler.SKIP_TO_RESULT);
                        MobclickAgent.onEvent(context, "ss");
                        Thread.interrupted();
                    }
                    Thread.sleep(TIME_INTERVAL);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    DebugUtil.e("calculateSpeedThread InterruptedException:" + e.getMessage());
                }
            }
        }

        int readLength = 100;
        private void calaulateSpeed(long lastDownloadByte) {
            nextTime = System.currentTimeMillis();
            if (readLength != -1 && readLength != 0) {
                instantSpeed = testSpeed(nextTime - lastTime,downloadByte - lastDownloadByte);
            } else {
                instantSpeed = getRandom(instantSpeed);
                downloadByte += instantSpeed * TIME_INTERVAL;
            }
            DebugUtil.d("downloadByte:" + downloadByte + "lastDownloadByte:" + lastDownloadByte);
            downloadTime = nextTime - begainTime;
            speedShow = testSpeed(downloadTime, downloadByte);
//            maxSpeed = instantSpeed > maxSpeed ? instantSpeed: maxSpeed;
//            minSpeed = instantSpeed < minSpeed ? instantSpeed: minSpeed;
//            maxSpeed = speedShow > maxSpeed ? speedShow : maxSpeed;
//            minSpeed = speedShow < minSpeed ? speedShow : minSpeed;
        };
    };
    
    /*
     * 获取随机数
     */
    private float getRandom(float temp_speed) {
        double temp_random = 0;
        double temp = temp_speed / 10;
        temp_random = (Math.random() * temp);
        if (temp_random > temp / 2) {
            temp_speed += temp_random;
        } else if (temp_random < temp / 2) {
            temp_speed -= temp_random;
        }
        temp_speed = Math.round(temp_speed);
        return temp_speed;
    }
    
    private void updateText(float speed, int what) {
        Message msg = new Message();
        msg.what = what;
        msg.getData().putFloat("speedValue", speed);
        mHandler.sendMessage(msg);
    }
    
    /***
     * @param time
     *            时间
     * @param resource
     *            资源量
     * @return 速度
     */
    public float testSpeed(long time, long resource) {
        if (time <= 0)
            time = TIME_INTERVAL;
        float testSpeed = resource / time;
        if (testSpeed == 0)
            return 10;
        return testSpeed;
    }
        
        
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = SpeedTestActivity_bak.this;
        onPrepare = true;
        onTesting = false;
        prepareTest();

        IntentFilter filter = new IntentFilter();        
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkStateReceiver, filter);
        
        mAMapLocManager = LocationManagerProxy.getInstance(this);
        enableMyLocation();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (onPrepare) {
            refreshNetworkType(
                    NetWorkUtil.getNetworkStatus(SpeedTestActivity_bak.this),
                    NetWorkUtil.getNetworkStatusStr(SpeedTestActivity_bak.this));
            if (networkStatus == Constants.NETWORK_STATUS_NULL) {
                loadingFailed();
            } else {
                issueInfo.setVisibility(View.GONE);
                Message msg = mHandler.obtainMessage(MainHandler.FIND_FASTEST_SERVER_TASK_START);
                msg.getData().putInt("TEST_STATUS", 0);
                mHandler.sendMessage(msg);
            }
        }
    }

    private void prepareTest() {
        setContentView(R.layout.activity_start);
        
        pressedView = (Button) findViewById(R.id.pressed_view);
        pressedView.setOnClickListener(this);
        lightView = (ImageView) findViewById(R.id.light_view);
        lightView.startAnimation(AnimationUtils.loadAnimation(SpeedTestActivity_bak.this, R.anim.breathled_ani));
        lightAbove = (ImageView) findViewById(R.id.light_above);
        lightAbove.startAnimation(AnimationUtils.loadAnimation(SpeedTestActivity_bak.this, R.anim.loading_ani_above));
        lightLow = (ImageView) findViewById(R.id.light_low);
        issueInfo = (TextView) findViewById(R.id.issue_info);
        lightLow.startAnimation(AnimationUtils.loadAnimation(SpeedTestActivity_bak.this, R.anim.loading_ani_low));
        
        speedValue = new SpeedValue();
    }

    private void startTest() {
        setContentView(R.layout.activity_testing);
        speedTestBtn = (Button) findViewById(R.id.speed_test_btn);
        pingTxt = (TextView) findViewById(R.id.ping_value);
        networkTxt = (TextView) findViewById(R.id.network_value);
        progressTxt = (TextView) findViewById(R.id.progress_txt);
        pingRotate = (ImageView) findViewById(R.id.ping_rotate);
        breatheLed = (ImageView) findViewById(R.id.breathe_led);
        breatheLedAbove = (ImageView) findViewById(R.id.breathe_led_above);
        needle = (ImageView) findViewById(R.id.needle);
        speedNam[0] = (ImageView)findViewById(R.id.num1);
        speedNam[1] = (ImageView)findViewById(R.id.num2);
        speedNam[2] = (ImageView)findViewById(R.id.num3);
        speedPoint1 = (ImageView)findViewById(R.id.point1);
        speedPoint2 = (ImageView)findViewById(R.id.point2);
        speed_unit = (TextView) findViewById(R.id.speed_unit);
        progressBar = (ProgressBar) findViewById(R.id.pb);
        speedPic = getResources().obtainTypedArray(R.array.speedvalue_array);
        clickListener = new ClickListener();

        refreshPingStr(speedValue.getPing());
        networkTxt.setText(speedValue.getNetworkType());
        
        speedTestBtn.setText("取消测速");
        speedTestBtn.setOnClickListener(clickListener);
        speedTestBtn.setTag("2");
        mHandler.sendEmptyMessage(MainHandler.LATENCY_TEST_TASK_START);
    }
    
    private void prepareNextTest() {
        refreshNetworkType(
                NetWorkUtil.getNetworkStatus(SpeedTestActivity_bak.this),
                NetWorkUtil.getNetworkStatusStr(SpeedTestActivity_bak.this));
        networkTxt.setText(speedValue.getNetworkType());
        refreshProgress(0);
        
        speedNam[0].setImageResource(R.drawable.speed_number_background);
        speedNam[1].setImageResource(R.drawable.speed_number_background);
        speedNam[2].setImageResource(R.drawable.speed_number_background);
        speedPoint1.setVisibility(View.GONE);
        speedPoint2.setVisibility(View.GONE);
        speed_unit.setText("");
        
        setSpeedTestControl(networkStatus);
        if (speedTestControl) {
//            speedTestBtn.startAnimation(AnimationUtils.loadAnimation(context, R.anim.speedtestbtn_ani));
            speedTestBtn.setText("重新测速");
            speedTestBtn.setTag("0");
            progressTxt.setText("网络正常，开始测速吧！");
            return;
        }
        if (networkStatus == Constants.NETWORK_STATUS_NULL) {
//            speedTestBtn.startAnimation(AnimationUtils.loadAnimation(context, R.anim.speedtestbtn_ani));
            speedTestBtn.setText("开启网络");
            speedTestBtn.setTag("1");
            return;
        }
    }
    
    private void loadingFailed() {
        pressedView.setText("请设置网络");
        pressedView.setTag("1");
        issueInfo.setText("网络连接有问题");
    }
    
    private void testFailed() {
        Base.mTabHost.setCurrentTab(1);
        onPrepare = true;
        onTesting = false;
        prepareTest();
        refreshNetworkType(
                NetWorkUtil.getNetworkStatus(SpeedTestActivity_bak.this),
                NetWorkUtil.getNetworkStatusStr(SpeedTestActivity_bak.this));
        if (networkStatus == Constants.NETWORK_STATUS_NULL) {
            loadingFailed();
        } else {
            issueInfo.setText("");
            pressedView.setText("开始测速");
            pressedView.setTextSize(20);
            pressedView.setClickable(true);
            pressedView.setTag("0");
            pressedView.setTextColor(getResources().getColor(R.color.loading_red));
            pressedView.invalidate();
//            Message msg = mHandler.obtainMessage(MainHandler.FIND_FASTEST_SERVER_TASK_START);
//            msg.getData().putInt("TEST_STATUS", 0);
//            mHandler.sendMessage(msg);
        }
    }


    public void setSpeedTestControl(int networkType) {
        if (networkType == Constants.NETWORK_STATUS_MOBILE
                || networkType == Constants.NETWORK_STATUS_WIFI) {
            speedTestControl = true;
            speedTestBtn.setTag("0");
            return;
        }
        if (networkType == Constants.NETWORK_STATUS_NULL) {
            speedTestControl = false;
            speedTestBtn.setTag("1");
            return;
        }
    }
    
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
        case R.id.pressed_view:
            if (pressedView.getTag().equals("0"))// 开始测速
            {
                if (NetWorkUtil.getNetworkStatus(context) != Constants.NETWORK_STATUS_NULL)
                    startTest();
                else{
                    testFailed();
                    pressedView.setTag("1");
                }
                return;
            }

            if (pressedView.getTag().equals("1")) // 网络连接失败，无法测速
            {
                Intent intent = new Intent(SpeedTestActivity_bak.this, NetWorkActivity.class);
                startActivity(intent);
                return;
            }
            break;
        }
    }

    private class ClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.speed_test_btn:
                if (speedTestBtn.getTag().equals("0")) { // 开始测速
                    mHandler.sendEmptyMessage(MainHandler.LATENCY_TEST_TASK_START);
                    MobclickAgent.onEvent(context, "cs");
                    return;
                }
                if (speedTestBtn.getTag().equals("1")) { // 网络连接失败，无法测速
                    Intent intent = new Intent(SpeedTestActivity_bak.this, NetWorkActivity.class);
                    startActivity(intent);
                    return;
                }
                if (speedTestBtn.getTag().equals("2")) { // 停止测速
                    if (mCurrentTestTask != null)
                        mCurrentTestTask.cancel(true);
                    prepareNextTest();
                    MobclickAgent.onEvent(context, "cstop");
                    return;
                }
                break;
            }
        }
    }

    public class MainHandler extends Handler {
        public static final int FIND_FASTEST_SERVER_TASK_START = 0;
        public static final int LATENCY_TEST_TASK_START = 1;
        public static final int DOWNLOAD_TEST_TASK_START = 2;
        public static final int UPDATE_NEEDLE = 3;
        public static final int SKIP_TO_RESULT = 4;
        public static final int UPDATE_INSTANT_SPEED = 5;
        

        @Override
        public void handleMessage(Message msg) {
            final int testStatus = msg.getData().getInt("TEST_STATUS");
            super.handleMessage(msg);
            switch (msg.what) {
            case FIND_FASTEST_SERVER_TASK_START:
                mCurrentTestTask = new LatencyTestTask(new TestTaskCallbacks() {

                    @Override
                    public void onTestUpdate( TestParameters[] paramArrayOfTestParameters) {
                    }

                    @Override
                    public void onTestFailed(
                            SpeedTestError paramSpeedTestError,
                            TestParameters paramTestParameters) {
                        loadingFailed();
                    }
                    @Override
                    public void onTestComplete(TestParameters paramTestParameters) {
                        TestParametersLatency localTestParametersLatency = (TestParametersLatency) paramTestParameters;
                        speedValue.setPing(localTestParametersLatency.getPing());
                        pressedView.setText("开始测速");
                        pressedView.setTextSize(20);
                        pressedView.setClickable(true);
                        pressedView.setTag("0");
                        pressedView.setTextColor(getResources().getColor(R.color.loading_red));
                        pressedView.invalidate();
                        onPrepare = false;
                    }

                    @Override
                    public void onBeginTest() {
                        if (testStatus == 0) {
                            pressedView.setText("正在寻找最近的服务器");
                            pressedView.setTextSize(18);
                            pressedView.setTextColor(getResources().getColor(R.color.loading_gray));
                            pressedView.setClickable(false);
                        }else if(testStatus == 1)
                        {
                            pressedView.setTextColor(getResources().getColor(R.color.loading_gray));
                            pressedView.setText("正在获取网络状态");
                            pressedView.setTextSize(18);
                            pressedView.setClickable(false);
                        }
                    }
                });

                try {
                    mCurrentTestTask.testStart(new URL(Constants.DOWNLOAD2MURL));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                break;
            case LATENCY_TEST_TASK_START:
                mCurrentTestTask = new LatencyTestTask(new TestTaskCallbacks() {

                    @Override
                    public void onTestUpdate( TestParameters[] paramArrayOfTestParameters) {
                        
                    }

                    @Override
                    public void onTestFailed(
                            SpeedTestError paramSpeedTestError,
                            TestParameters paramTestParameters) {
                        testFailed();
                        ToastUtil.showToast(SpeedTestActivity_bak.this, "测速失败!");
                    }
                    
                    @Override
                    public void onTestComplete(
                            TestParameters paramTestParameters) {
                        TestParametersLatency localTestParametersLatency = (TestParametersLatency) paramTestParameters;
                        pingTxt.setVisibility(View.VISIBLE);
                        refreshPingStr(localTestParametersLatency.getPing());
                        pingRotate.clearAnimation();
                        pingRotate.setVisibility(View.GONE);
                        
                        progressBar.setVisibility(View.VISIBLE);
                        progressTxt.setText("正在测试网速");
                        mHandler.sendEmptyMessage(MainHandler.DOWNLOAD_TEST_TASK_START);
                        
                    }

                    @Override
                    public void onBeginTest() {
                        pingTxt.setVisibility(View.GONE);
                        pingRotate.setVisibility(View.VISIBLE);
                        progressTxt.setText("正在测试网络延迟...");
                        
                        progressBar.setVisibility(View.GONE);
                        
                        pingRotate.startAnimation(AnimationUtils.loadAnimation(context, R.anim.data_loading_rotate));
                        speedTestBtn.setTag("2");
                        speedTestBtn.setText("取消测速");
                    }
                });

                try {
                    mCurrentTestTask.testStart(new URL(Constants.DOWNLOAD2MURL));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                break;
            case DOWNLOAD_TEST_TASK_START:
                mCurrentTestTask = new DownloadTestTask(
                        new TestTaskCallbacks() {

                            @Override
                            public void onTestUpdate( TestParameters[] paramArrayOfTestParameters) {
                                TestParametersTransfer localTestParametersTransfer = (TestParametersTransfer) paramArrayOfTestParameters[0];
                                downloadByte = localTestParametersTransfer.getBytes();
                                        
                                float speedValue = localTestParametersTransfer.getSpeed() / 1024f;
                                float angle = localTestParametersTransfer.calculateAngle(speedValue);
                                setSpeedValesPic(speedValue);
                                refreshNeedle(angle,30);
                                refreshProgress((int)(localTestParametersTransfer.getProgress()*100));
                            }
                            
                            @Override
                            public void onTestFailed(
                                    SpeedTestError paramSpeedTestError,
                                    TestParameters paramTestParameters) {
                                testFailed();
                                ToastUtil.showToast(SpeedTestActivity_bak.this, "测速失败!");
                            }

                            @Override
                            public void onTestComplete(
                                    TestParameters paramTestParameters) {
                                TestParametersTransfer localTestParametersTransfer = (TestParametersTransfer) paramTestParameters;
                                // 设置测速开始时间(ms)，开始日期
                                speedValue.setTestTime(System.currentTimeMillis());
                                String ly_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
                                speedValue.setTestDateTime(ly_time);
                                // 设置下载速度、测速消费的时间、测速消费的流量
                                speedValue.setCostTime(System.currentTimeMillis()-speedValue.getTestTime());
                                speedValue.setDownloadByte(localTestParametersTransfer.getBytes());
                                speedValue.setDownloadSpeed(localTestParametersTransfer.getSpeed());
                                // 隐藏呼吸灯动画
                                breatheLed.clearAnimation();
                                breatheLedAbove.clearAnimation();
                                breatheLed.setVisibility(View.GONE);
                                breatheLedAbove.setVisibility(View.GONE);
                               
                                mHandler.sendEmptyMessageDelayed(MainHandler.SKIP_TO_RESULT, 1000);
                                 
                            }

                            @Override
                            public void onBeginTest() {
                                begainTime = System.currentTimeMillis();
                                // 显示呼吸灯动画
                                breatheLed.setVisibility(View.VISIBLE);
                                breatheLedAbove.setVisibility(View.VISIBLE);
                                breatheLedAbove.startAnimation(AnimationUtils.loadAnimation(context,
                                        R.anim.breathled_ani));
                                breatheLed.startAnimation(AnimationUtils.loadAnimation(context,
                                        R.anim.breathled_ani));
                            }

                        }, 1);
                try {
                    mCurrentTestTask.testStart(new URL(Constants.DOWNLOAD2MURL));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                break;
            case SKIP_TO_RESULT:
                Intent intent = new Intent(SpeedTestActivity_bak.this, SpeedResultActivity.class);
                Bundle speedResult = new Bundle();
                speedResult.putString("networkType", speedValue.getNetworkType());
                speedResult.putInt("ping", speedValue.getPing());
                speedResult.putLong("costTime", speedValue.getCostTime());
                speedResult.putInt("downloadByte", speedValue.getDownloadByte());
                speedResult.putLong("testTime", speedValue.getTestTime());
                speedResult.putString("testDateTime", speedValue.getTestDateTime());
                speedResult.putDouble("latitude", speedValue.getLatitude());
                speedResult.putDouble("longitude", speedValue.getLongitude());
                speedResult.putString("locationDesc", speedValue.getLocationDesc());
                speedResult.putInt("downloadSpeed", speedValue.getDownloadSpeed());
                speedResult.putFloat("currentDegree", currentDegree);
                intent.putExtras(speedResult);
                startActivityForResult(intent, 10);
                break;
            case UPDATE_INSTANT_SPEED:
                float speedValue = msg.getData().getFloat("speedValue");
                float angle = calculateAngle(speedValue);
                setSpeedValesPic(speedValue);
                refreshNeedle(angle,30);
                break;
            }
        }
    }
    
    public float calculateAngle(float speed) {
        float angle = 0;
        if (speed <= 20) {
            angle = speed * 30 / 20;
        } else if (speed <= 50) {
            angle = 30 + (speed - 20);
        } else if (speed <= 100) {
            angle = 60 + (speed - 50) * 30 / 50;
        } else if (speed <= 200) {
            angle = 90 + (speed - 100) * 30 / 100;
        } else if (speed <= 500) {
            angle = 120 + (speed - 200) * 30 / 300;
        } else if (speed <= 1024) {
            angle = 150 + (speed - 500) * 30 / 524;
        } else if (speed <= 3072) {
            angle = 180 + (speed - 1024) * 30 / 2048;
        } else {
            angle = 210 + (speed - 3072) * 30 / 10240;
        }
        return angle;
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if(requestCode == 10)
        {
            refreshNeedle(0,1000);
            prepareNextTest();
        }
    }
    
    
    private int getPing(long l) {
        return (int)(l - speedValue.getTestTime());
    }

    /**
     * 更新Ping时间
     */
    private void refreshPingStr(int ping) {
        speedValue.setPing(ping);
        
        pingTxt.setText(ping + "ms");
        pingTxt.invalidate();
    }
    

    /**
     * 更新进度条
     * 
     * @param progress
     */
    private void refreshProgress(int progress) {
        try {
            progressBar.setProgress(progress);
            progressBar.invalidate();
            progressTxt.setText(progress + "%");
            progressTxt.invalidate();
        } catch (Exception e) {
            DebugUtil.d("refreshProgress Exception:" + e.getMessage());
        }
    }

    /**
     * 更新网络类型
     * 
     * @param networkType
     * @param networkStr
     */
    private void refreshNetworkType(int networkType, String networkStr) {
        speedValue.setNetworkType(networkStr);
        networkStatus = networkType;
    }

    /**
     * 指针旋转动画
     * 
     * @param angle
     */
    private void refreshNeedle(float angle,int longth) {
        if (angle != currentDegree) {
            RotateAnimation ra = new RotateAnimation(currentDegree, angle,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            currentDegree = angle;
            ra.setDuration(longth);
            needle.startAnimation(ra);
            ra.setFillAfter(true);
            needle.invalidate();
        }
    }
    
    private void setSpeedValesPic(float speed) {
        String speedValue;
        char[] c;
        java.text.DecimalFormat db = new java.text.DecimalFormat("#000");
        java.text.DecimalFormat dm = new java.text.DecimalFormat("#0.00");
        if (speed >= 0 && speed < 1000.0) {
            speedValue = db.format(speed);
            c = speedValue.toCharArray();
            speed_unit.setText("KB/s");
            speedPoint1.setVisibility(View.GONE);
            for (int i = 0; i < c.length; i++) {
                speedNam[i].setImageDrawable(speedPic.getDrawable(c[i] - '0'));
            }
        } else if (speed >= 1000 && speed < (10 * 1024.0)) {
            speedValue = dm.format(speed / 1024.0);
            c = speedValue.toCharArray();
            speed_unit.setText("MB/s");
            speedPoint1.setVisibility(View.VISIBLE);
            speedPoint2.setVisibility(View.GONE);
            speedNam[0].setImageDrawable(speedPic.getDrawable(c[0] - '0'));
            speedNam[1].setImageDrawable(speedPic.getDrawable(c[2] - '0'));
            speedNam[2].setImageDrawable(speedPic.getDrawable(c[3] - '0'));
        } else if (speed >= (10 * 1024.0) && speed < (100 * 1024.0)) {
            speedValue = dm.format(speed / 1024.0);
            c = speedValue.toCharArray();
            speed_unit.setText("MB/s");
            speedPoint1.setVisibility(View.GONE);
            speedPoint2.setVisibility(View.VISIBLE);
            speedNam[0].setImageDrawable(speedPic.getDrawable(c[0] - '0'));
            speedNam[1].setImageDrawable(speedPic.getDrawable(c[1] - '0'));
            speedNam[2].setImageDrawable(speedPic.getDrawable(c[3] - '0'));
        }
    }

    public boolean enableMyLocation() {
        boolean result = false;
        if (mAMapLocManager.isProviderEnabled(LocationProviderProxy.AMapNetwork)) {
            mAMapLocManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 2000, 10, this);
            result = true;
        }
        return result;
    }

    public void disableMyLocation() {
        mAMapLocManager.removeUpdates(this);
    }

    
    
    @Override
    protected void onPause() {
        disableMyLocation();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAMapLocManager != null) {
            mAMapLocManager.removeUpdates(this);
            mAMapLocManager.destory();
        }
        mAMapLocManager = null;
        unregisterReceiver(mNetworkStateReceiver);
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        if (location != null) {
            Double geoLat = location.getLatitude();
            Double geoLng = location.getLongitude();
            String cityCode = "";
            String desc = "";
            Bundle locBundle = location.getExtras();
            if (locBundle != null) {
                cityCode = locBundle.getString("citycode");
                desc = locBundle.getString("desc");
            }
            String str = ("定位成功:(" + geoLng + "," + geoLat + ")"
                    + "\n精    度    :" + location.getAccuracy() + "米"
                    + "\n城市编码:" + cityCode + "\n位置描述:" + desc);
            Message msg = new Message();
            msg.obj = str;
            // 设置测速的地理位置信息
            speedValue.setLatitude(geoLat);
            speedValue.setLongitude(geoLng);
            speedValue.setLocationDesc(desc);
            if (handler != null) {
                handler.sendMessage(msg);
            }
        }
    }
    
    
    
    
    /**
     * 格式化速度值
     * 
     * @param speed
     *//*
    private void formatSpeedPic(float speed) {
        if (speed < 1000.0) {
            speed_unit.setText("KB/s");
            speed_dot.setVisibility(View.GONE);
            decimal[0].setVisibility(View.GONE);
            decimal[1].setVisibility(View.GONE);
            showValue(speed, kps);
            return;
        }
        speed_unit.setText("MB/s");
        speed_dot.setVisibility(View.VISIBLE);
        showValue(speed / 1024f, mps);
    }

    *//**
     * 显示速度
     * 
     * @param speed
     * @param db
     *//*
    private void showValue(float speed, java.text.DecimalFormat db) {
        String speedValue;
        char[] integerChar, decimalChar = null;
        try {
            speedValue = db.format(speed);
            String[] arr = speedValue.split("\\.");
            integerChar = arr[0].toCharArray();
            if (arr.length > 1)
                decimalChar = arr[1].toCharArray();
            for (int i = 0; i < integerChar.length && i < 3; i++) {
                integer[i].setImageDrawable(speedPic.getDrawable(integerChar[i] - '0'));
                integer[i].setVisibility(View.VISIBLE);
            }
            for (int i = integerChar.length; i < 3; i++) {
                integer[i].setVisibility(View.GONE);
            }
            if (decimalChar == null)
                return;
            for (int i = 0; i < decimalChar.length && i < 2; i++) {
                decimal[i].setImageDrawable(speedPic .getDrawable(decimalChar[i] - '0'));
                decimal[i].setVisibility(View.VISIBLE);
            }
            for (int i = decimalChar.length; i < 2; i++) {
                decimal[i].setVisibility(View.GONE);
            }
        } catch (Exception e) {
            DebugUtil.e("showValue:" + e.getMessage());
        }
    }*/

}
