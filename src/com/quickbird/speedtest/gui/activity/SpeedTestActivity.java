package com.quickbird.speedtest.gui.activity;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.quickbird.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

public class SpeedTestActivity extends BaseActivity{
    
	private Button pressedView;
	private Button speedTestBtn;
	private TextView pingTxt, networkTxt, progressTxt;
	private TextView speed_unit;
	private ProgressBar progressBar;
	private ImageView pingRotate;
	private ImageView lightBreatheView;
	private ImageView lightRotateView;
	private ImageView lightRotateFullView;
	private ImageView picTextView;
	private LinearLayout promptView1,promptView2;
	
	private ImageView needle, breatheLed, breatheLedAbove;
	private ImageView[] speedNam = new ImageView[3];
	private ImageView speedPoint1, speedPoint2;
	private LinearLayout buttonHistoryLayout;
	private ImageButton buttonHistory;
	
	private LinearLayout buttonMapLayout;
	private ImageButton buttonMap;
	
    private int networkStatus;
    private float currentDegree = 0l;
    private ClickListener clickListener;
    private MainHandler mHandler = new MainHandler();
    private TestTask mCurrentTestTask = null;
    private Context context;
    java.text.DecimalFormat kps = new java.text.DecimalFormat("#0");
    java.text.DecimalFormat mps = new java.text.DecimalFormat("#0.00");
    private TypedArray speedPic;

    private SpeedValue speedValue;
    private boolean onPrepare;
    public static boolean onTesting;
    private boolean inActivity;
    private boolean ifSetPing = false;
    private int viewSwitch = 0;
    
    public  final int TIME_INTERVAL = 200;
    private long downloadByte;
    private long downloadTime;
    private int readLength;
    private long begainTime;
    private float instantSpeed;
    private CalculateSpeedThread calculateSpeedThread;
    
    private class CalculateSpeedThread extends Thread {
        @Override
        public void run() {
            int count = 0;
            instantSpeed = 0;
            while (!Thread.currentThread().isInterrupted()&& onTesting && inActivity) {
                try {
                    calaulateSpeed();
					updateText(instantSpeed / 1024, count, MainHandler.UPDATE_INSTANT_SPEED);
					DebugUtil.d("instantSpeed:"+instantSpeed/1024);
					if (count > 100)
						Thread.interrupted();
					if (count++ == 100) {
					  onTesting = false;
					  updateText(instantSpeed/1024, 100,MainHandler.UPDATE_INSTANT_SPEED);
                      speedValue.setCostTime(System.currentTimeMillis() - speedValue.getTestTime());
					  speedValue.setDownloadSpeed((int) instantSpeed);
					  mHandler.sendEmptyMessageDelayed(MainHandler.SKIP_TO_RESULT, 1000);
					  Thread.interrupted();
                    }
                    Thread.sleep(TIME_INTERVAL);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    DebugUtil.e("calculateSpeedThread InterruptedException:" + e.getMessage());
                }
            }
        }
    };
    
    private void calaulateSpeed() {
        downloadTime = SystemClock.elapsedRealtime() - begainTime;
        if (readLength != -1 && readLength != 0) {
            instantSpeed = testSpeed(downloadTime,downloadByte);
        } else {
            instantSpeed = getRandom(instantSpeed);
            downloadByte += instantSpeed * TIME_INTERVAL;
        }
    };
    
    
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
		float testSpeed = (float) (1000.0 * resource / time);
		return testSpeed;
	}
    
    /*
     * 获取随机数
     */
    private float getRandom(float temp_speed) {
        double temp_random = 0;
        double temp = temp_speed / 20;
        temp_random = (Math.random() * temp);
        if (temp_random > temp / 2) {
            temp_speed += temp_random;
        } else if (temp_random < temp / 2) {
            temp_speed -= temp_random;
        }
        temp_speed = Math.round(temp_speed);
        return temp_speed;
    }
    
    
	private void updateText(float speed, int progress, int what) {
		Message msg = new Message();
		msg.what = what;
		msg.getData().putFloat("speedValue", speed);
		msg.getData().putInt("progress", progress);
		mHandler.sendMessage(msg);
	}
    
//    private BroadcastReceiver mNetworkStateReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if(onTesting)
//                if(!NetWorkUtil.getWifiState(context)||!NetWorkUtil.getMobileStatus(context))
//                    if(mCurrentTestTask!=null){
//                        if (mCurrentTestTask != null)
//                            mCurrentTestTask.cancel(true);
//                        try {
//                            onTesting = false;
//                            prepareNextTest();
//                        } catch (Exception e) {
//                            DebugUtil.d("BroadcastReceiver Exception:" + e.getMessage());
//                        }
//                    }
//        }
//    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = SpeedTestActivity.this;
        inActivity = true;
        try {
            onPrepare = true;
            onTesting = false;
            prepareTest();
//            IntentFilter filter = new IntentFilter();        
//            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//            registerReceiver(mNetworkStateReceiver, filter);
        } catch (Exception e) {
            DebugUtil.d("onCreate:"+e.getMessage());
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (onPrepare) {
            refreshNetworkType(
                    NetWorkUtil.getNetworkStatus(SpeedTestActivity.this),
                    NetWorkUtil.getNetworkStatusStr(SpeedTestActivity.this));
            if (networkStatus == Constants.NETWORK_STATUS_NULL) {
                loadingFailed();
            } else {
                Message msg = mHandler.obtainMessage(MainHandler.FIND_FASTEST_SERVER_TASK_START);
                msg.getData().putInt("TEST_STATUS", 0);
                mHandler.sendMessage(msg);
            }
        }else{
            if (!onTesting&&viewSwitch==1)
                prepareNextTest();
        }
		if (Base.startTest && !onTesting) {
			Base.startTest = false;
			onTesting = true;
			mHandler.sendEmptyMessage(MainHandler.DOWNLOAD_TEST_TASK_START);
			MobclickAgent.onEvent(context, "cs");
		}
    }

    private void prepareTest() {
		setContentView(R.layout.activity_loading);
		viewSwitch = 0;
		pressedView = (Button) findViewById(R.id.pressed_view);
		pressedView.setOnClickListener(this);
		lightBreatheView = (ImageView) findViewById(R.id.light_breathe_view);
		lightRotateView = (ImageView) findViewById(R.id.light_rotate_view);
		lightRotateFullView = (ImageView) findViewById(R.id.light_rotate_full);
		picTextView = (ImageView) findViewById(R.id.pic_text_view);
		promptView1 = (LinearLayout) findViewById(R.id.prompt_view_1);
		promptView2 = (LinearLayout) findViewById(R.id.prompt_view_2);
		picTextView.setVisibility(View.GONE);
		lightRotateFullView.setVisibility(View.GONE);
        
        lightRotateView.startAnimation(AnimationUtils.loadAnimation(SpeedTestActivity.this, R.anim.data_loading_rotate));
        lightBreatheView.startAnimation(AnimationUtils.loadAnimation(SpeedTestActivity.this, R.anim.breathled_ani));
        speedValue = new SpeedValue();
    }

    private void initTestView() {
        setContentView(R.layout.activity_testing);
        viewSwitch = 1;
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
        
        buttonHistoryLayout = (LinearLayout) findViewById(R.id.button_history_layout);
        buttonHistory = (ImageButton) findViewById(R.id.button_history);
        clickListener = new ClickListener();
        
        buttonMapLayout = (LinearLayout) findViewById(R.id.button_map_layout);
        buttonMap = (ImageButton) findViewById(R.id.button_map);

        refreshPingStr(speedValue.getPing());
        networkTxt.setText(speedValue.getNetworkType());
        
        buttonMapLayout.setOnClickListener(clickListener);
        buttonMap.setOnClickListener(clickListener);
        
        buttonHistoryLayout.setOnClickListener(clickListener);
        buttonHistory.setOnClickListener(clickListener);
        speedTestBtn.setOnClickListener(clickListener);
    }
    
    private void prepareNextTest() {
        refreshNetworkType(
                NetWorkUtil.getNetworkStatus(SpeedTestActivity.this),
                NetWorkUtil.getNetworkStatusStr(SpeedTestActivity.this));
        networkTxt.setText(speedValue.getNetworkType());
        refreshProgress(0);
        
        speedNam[0].setImageResource(R.drawable.speed_number_background);
        speedNam[1].setImageResource(R.drawable.speed_number_background);
        speedNam[2].setImageResource(R.drawable.speed_number_background);
        speedPoint1.setVisibility(View.GONE);
        speedPoint2.setVisibility(View.GONE);
        speed_unit.setText("");
        if (networkStatus == Constants.NETWORK_STATUS_MOBILE
                || networkStatus == Constants.NETWORK_STATUS_WIFI) {
            speedTestBtn.setText("重新测速");
            speedTestBtn.setTag("0");
            progressTxt.setText("网络正常，开始测速吧！");
            return;
        }
        if (networkStatus == Constants.NETWORK_STATUS_NULL) {
            speedTestBtn.setText("开启网络");
            speedTestBtn.setTag("1");
            return;
        }
    }
    
    private void loadingFailed() {
    	promptView1.setVisibility(View.GONE);
    	promptView2.setVisibility(View.VISIBLE);
        pressedView.setTag("1");
    }
    
    private void testFailed() {
        Base.mTabHost.setCurrentTab(1);
        onPrepare = true;
        onTesting = false;
        prepareTest();
        refreshNetworkType(
                NetWorkUtil.getNetworkStatus(SpeedTestActivity.this),
                NetWorkUtil.getNetworkStatusStr(SpeedTestActivity.this));
        if (networkStatus == Constants.NETWORK_STATUS_NULL) {
            loadingFailed();
        } else {
        	promptView1.setVisibility(View.GONE);
        	promptView2.setVisibility(View.GONE);
            pressedView.setText("");
            pressedView.setClickable(true);
            pressedView.setTag("0");
            picTextView.setVisibility(View.VISIBLE);
            pressedView.invalidate();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
        case R.id.pressed_view:
            if (pressedView.getTag().equals("0"))// 开始测速
            {
                if (NetWorkUtil.getNetworkStatus(context) != Constants.NETWORK_STATUS_NULL) {
                    initTestView();
                    mHandler.sendEmptyMessage(MainHandler.DOWNLOAD_TEST_TASK_START);
                } else {
                    testFailed();
                    pressedView.setTag("1");
                }
                return;
            }

            if (pressedView.getTag().equals("1")) // 网络连接失败，无法测速
            {
                Intent intent = new Intent(SpeedTestActivity.this, NetWorkActivity.class);
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
                    if (!onTesting) {
                        onTesting = true;
                        mHandler.sendEmptyMessage(MainHandler.DOWNLOAD_TEST_TASK_START);
                        MobclickAgent.onEvent(context, "cs");
                    }
                    return;
                }
                if (speedTestBtn.getTag().equals("1")) { // 网络连接失败，无法测速
                    Intent intent = new Intent(SpeedTestActivity.this, NetWorkActivity.class);
                    startActivity(intent);
                    return;
                }
                if (speedTestBtn.getTag().equals("2")) { // 取消测速
					if (mCurrentTestTask != null && onTesting == true) {
						mCurrentTestTask.cancel(true);
					}
                    return;
                }
                MobclickAgent.onEvent(context, "cstop");
                break;
            case R.id.button_history_layout:
            case R.id.button_history:
                startActivity(new Intent(SpeedTestActivity.this, SpeedHistoryActivity.class));
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
        public static final int FIND_FASTEST_SERVER_TASK_COMPLATE = 5;
        public static final int UPDATE_INSTANT_SPEED = 6;
        public static final int START_CALCULATE_THREAD = 7;
        public static final int SHUTDOWN_CALCULATE_THREAD = 8;
        public static final int START_DOWNLOAD_THREAD = 9;
        public static final int SHUTDOWN_DOWNLOAD_THREAD = 10;
        public static final int START_TEST = 11;
        public static final int UPDATE_TEST = 12;
        public static final int TEST_FAILED = 13;
        public static final int TEST_CANCEL = 14;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case FIND_FASTEST_SERVER_TASK_START:
            	promptView1.setVisibility(View.VISIBLE);
            	promptView2.setVisibility(View.GONE);
				pressedView.setTextSize(12);
				pressedView.setTextColor(getResources().getColor(R.color.loading_gray));
				pressedView.setClickable(false);
				mHandler.sendEmptyMessageDelayed(MainHandler.FIND_FASTEST_SERVER_TASK_COMPLATE, 3000);
                break;
            case FIND_FASTEST_SERVER_TASK_COMPLATE:
            	promptView1.setVisibility(View.GONE);
            	promptView2.setVisibility(View.GONE);
				pressedView.setClickable(true);
				pressedView.setTag("0");
				pressedView.invalidate();
				picTextView.setVisibility(View.VISIBLE);
				lightRotateView.clearAnimation();
				lightRotateFullView.setVisibility(View.INVISIBLE);
				lightRotateFullView.startAnimation(AnimationUtils.loadAnimation(SpeedTestActivity.this, R.anim.data_loading_rotate));
				onPrepare = false;
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
                        ToastUtil.showToast(SpeedTestActivity.this, "测速失败!");
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
                                if (!ifSetPing && localTestParametersTransfer.getBytes()>0) {
                                    pingTxt.setVisibility(View.VISIBLE);
                                    refreshPingStr(getPing(System .currentTimeMillis()));
                                    pingRotate.clearAnimation();
                                    pingRotate.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.VISIBLE);
                                    progressTxt.setText("正在测试网速");
                                    begainTime = SystemClock.elapsedRealtime();
                                    mHandler.sendEmptyMessage(MainHandler.START_CALCULATE_THREAD);
                                }
                                onTesting = true;
                                downloadByte = localTestParametersTransfer.getBytes();
                                readLength = localTestParametersTransfer.getReadLength();
                            }
                            
                            @Override
							public void onTestFailed(SpeedTestError paramSpeedTestError, TestParameters paramTestParameters) {
								if (paramSpeedTestError == SpeedTestError.TEST_CANCELLED) {
									
									onTesting = false;
									pingRotate.clearAnimation();
                                    pingRotate.setVisibility(View.GONE);
			                        refreshNeedle(0, 500);
			                        prepareNextTest();
								}else{
									testFailed();
									ToastUtil.showToast(SpeedTestActivity.this,"测速失败!");
								}
							}

                            @Override
                            public void onTestComplete(TestParameters paramTestParameters) {
                            	readLength = -1;
                            }

                            @Override
                            public void onBeginTest() {
                                ifSetPing = false;
                                onTesting = true;
                                try {
                                	// 显示呼吸灯动画
                                	breatheLed.setVisibility(View.VISIBLE);
                                	breatheLedAbove.setVisibility(View.VISIBLE);
                                	breatheLedAbove.startAnimation(AnimationUtils.loadAnimation(context,
                                			R.anim.breathled_ani));
                                	breatheLed.startAnimation(AnimationUtils.loadAnimation(context,
                                			R.anim.breathled_ani));
                                	
                                	pingTxt.setVisibility(View.GONE);
                                	pingRotate.setVisibility(View.VISIBLE);
                                	progressTxt.setText("正在测试网络延迟...");
                                	progressBar.setVisibility(View.GONE);
                                	pingRotate.startAnimation(AnimationUtils.loadAnimation(context, R.anim.data_loading_rotate));
                                	
                                	speedTestBtn.setTag("2");
                                	speedTestBtn.setText("取消测速");
                                	
                                	// 设置测速开始时间(ms)，开始日期
                                	speedValue.setTestTime(System.currentTimeMillis());
                                	String ly_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
                                	speedValue.setTestDateTime(ly_time);
								} catch (Exception e) {
									DebugUtil.d("onBeginTest:"+e.getMessage());
								}
                            }

                        }, 1);
                try {
                    mCurrentTestTask.testStart(new URL(Constants.DOWNLOAD2MURL));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                break;
            case SKIP_TO_RESULT:
                Intent intent = new Intent(SpeedTestActivity.this, ShareResultActivity.class);
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
                
                inActivity = false;
                
                startActivityForResult(intent, 10);
                break;
            case UPDATE_INSTANT_SPEED:
            	float speedFloat = msg.getData().getFloat("speedValue");
            	float angle = calculateAngle(speedFloat);
            	setSpeedValesPic(speedFloat);
                refreshNeedle(angle,30);
                int progress = msg.getData().getInt("progress");
				refreshProgress(progress);
            	break;
            case START_CALCULATE_THREAD:
            	calculateSpeedThread = new CalculateSpeedThread();
                calculateSpeedThread.start();
            	break;
            case SHUTDOWN_CALCULATE_THREAD:
            	if (calculateSpeedThread != null && calculateSpeedThread.isAlive())
                    calculateSpeedThread.interrupt();
            	break;
            case START_TEST:
            	ifSetPing = false;
                onTesting = true;
                // 显示呼吸灯动画
                breatheLed.setVisibility(View.VISIBLE);
                breatheLedAbove.setVisibility(View.VISIBLE);
                breatheLedAbove.startAnimation(AnimationUtils.loadAnimation(context,
                        R.anim.breathled_ani));
                breatheLed.startAnimation(AnimationUtils.loadAnimation(context,
                        R.anim.breathled_ani));
                
                pingTxt.setVisibility(View.GONE);
                pingRotate.setVisibility(View.VISIBLE);
                progressTxt.setText("正在测试网络延迟...");
                progressBar.setVisibility(View.GONE);
                pingRotate.startAnimation(AnimationUtils.loadAnimation(context, R.anim.data_loading_rotate));
                
                speedTestBtn.setTag("2");
                speedTestBtn.setText("取消测速");
                
                // 设置测速开始时间(ms)，开始日期
                speedValue.setTestTime(System.currentTimeMillis());
                String ly_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
                speedValue.setTestDateTime(ly_time);
                
            	break;
            case UPDATE_TEST:
            	onTesting = true;
				pingTxt.setVisibility(View.VISIBLE);
				refreshPingStr(getPing(System.currentTimeMillis()));
				pingRotate.clearAnimation();
				pingRotate.setVisibility(View.GONE);
				progressBar.setVisibility(View.VISIBLE);
				progressTxt.setText("正在测试网速");
				begainTime = SystemClock.elapsedRealtime();
				mHandler.sendEmptyMessage(MainHandler.START_CALCULATE_THREAD);
            	break;
            case TEST_FAILED:
            	testFailed();
				ToastUtil.showToast(SpeedTestActivity.this,"测速失败!");
            	break;
            case TEST_CANCEL:
            	mHandler.sendEmptyMessage(MainHandler.SHUTDOWN_DOWNLOAD_THREAD);
            	mHandler.sendEmptyMessage(MainHandler.SHUTDOWN_CALCULATE_THREAD);
            	onTesting = false;
                refreshNeedle(0, 500);
                prepareNextTest();
            	break;
            }
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10)
        {
        	DebugUtil.d("onActivityResult");
            initTestView();
            refreshNeedle(0,1000);
            prepareNextTest();
            inActivity = true;
        }
    }
    
    private int getPing(long l) {
        ifSetPing = !ifSetPing;
        return (int)(l - speedValue.getTestTime());
    }

    /**
     * 更新Ping时间
     */
    private void refreshPingStr(int ping) {
        if (ping < 0)
            ping = 0;
		ping = ping % 300;
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
            progressTxt.setText("测速已完成"+progress + "%");
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
        try {
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
        } catch (Exception e) {
            DebugUtil.d("setSpeedValesPic Exception:"+e.getMessage());
        }
    }

    
    private void uploadAveSpeedLevel(float aveSpeed) {

        if (aveSpeed > 1024 * 5) {
            MobclickAgent.onEvent(context, "sdis", "5120KB+");
            return;
        }
        if (aveSpeed > 2048) {
            MobclickAgent.onEvent(context, "sdis", "2048_5120KB");
            return;
        }
        if (aveSpeed > 1000) {
            MobclickAgent.onEvent(context, "sdis", "1000_2048KB");
            return;
        }
        if (aveSpeed > 500) {
            MobclickAgent.onEvent(context, "sdis", "500_1000KB");
            return;
        }
        if (aveSpeed > 200) {
            MobclickAgent.onEvent(context, "sdis", "200_500KB");
            return;
        }
        if (aveSpeed > 100) {
            MobclickAgent.onEvent(context, "sdis", "100_200KB");
            return;
        }
        if (aveSpeed > 50) {
            MobclickAgent.onEvent(context, "sdis", "50_100KB");
            return;
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
        } else if (speed <= 1000) {
            angle = 150 + (speed - 500) * 30 / 524;
        } else if (speed <= 3072) {
            angle = 180 + (speed - 1000) * 30 / 2048;
        } else {
            angle = 210 + (speed - 3072) * 30 / 10000;
        }
        return angle;
    }
    
}
