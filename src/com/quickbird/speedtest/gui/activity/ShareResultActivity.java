package com.quickbird.speedtest.gui.activity;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.sharesdk.framework.AbstractWeibo;

import com.quickbird.controls.Config;
import com.quickbird.controls.Constants;
import com.quickbird.speedtest.R;
import com.quickbird.speedtest.onkeyshare.ShareAllGird;
import com.quickbird.speedtestengine.SpeedValue;
import com.quickbird.speedtestengine.storage.database.SpeedDBManager;
import com.quickbird.speedtestengine.utils.DebugUtil;
import com.quickbird.speedtestengine.utils.InputStreamUtil;
import com.quickbird.speedtestengine.utils.NetWorkUtil;
import com.quickbird.speedtestengine.utils.ProtocolUtil;
import com.quickbird.speedtestengine.utils.SpeedValueUtil;
import com.quickbird.speedtestengine.utils.StringUtil;
import com.quickbird.utils.ScreenShotUtil;
import com.umeng.analytics.MobclickAgent;

public class ShareResultActivity extends BaseActivity {

    private TextView speedResultTxt, peopleNumsTxt, rankTxt;
    private ImageView wideBroadImgeView;
    private ImageView speedLevel, medal;
    private Button returnBtn, shareBtn;
    private long totalRecorks = 0;
    private long breakRecorks = 0;
    private int speedMetal = 0;
    private TypedArray speedMetalPic, speedGradePic;
    private SpeedValue speedValue;
    private Context context;
    private int networkStatus;
    private boolean ifWeixin = false;
    private TypedArray speedWideBroadPic;
    
    public static final int GET_RANK = 0;
    
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == GET_RANK)
            {
                DebugUtil.d("GET_RANK:");
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            HttpURLConnection connection = null;
                            connection = ProtocolUtil.prepareConnection(ShareResultActivity.this,Config.SERVER_URL);
                            ProtocolUtil.WriteRequest2Remote(ProtocolUtil.prepareRequestBody(ShareResultActivity.this, speedValue),connection);
                            DebugUtil.d("ResponseCode:" + connection.getResponseCode());
                            DebugUtil.d("ResponseMessage:" + connection.getResponseMessage());
                            try {
                                String response = InputStreamUtil.InputStreamTOString(connection.getInputStream());
                                if(!StringUtil.isNull(response)){
                                    JSONObject object = new JSONObject(response);
                                    totalRecorks = object.optInt("total");
                                    breakRecorks = object.optInt("broke");
                                    DebugUtil.d("totalRecorks:" + totalRecorks);
                                    DebugUtil.d("breakRecorks:" + breakRecorks);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (ProtocolException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    };
                }.start();
            }
        }
    };
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_rankresult);
        context = ShareResultActivity.this;
        speedResultTxt = (TextView) findViewById(R.id.speed_result);
        peopleNumsTxt = (TextView) findViewById(R.id.people_nums);
        rankTxt = (TextView) findViewById(R.id.rank);
        speedLevel = (ImageView) findViewById(R.id.speed_level);
        medal = (ImageView) findViewById(R.id.medal);
        returnBtn = (Button) findViewById(R.id.return_btn);
        shareBtn = (Button) findViewById(R.id.share_btn);

        wideBroadImgeView = (ImageView) findViewById(R.id.widebroad_img);
        
        returnBtn.setOnClickListener(this);
        shareBtn.setOnClickListener(this);

        speedMetalPic = getResources().obtainTypedArray( R.array.speedmetal_array);
        speedGradePic = getResources().obtainTypedArray( R.array.speedgrade_array);
        speedWideBroadPic = getResources().obtainTypedArray(R.array.speed_widebroad_array);
        speedValue = new SpeedValue();
        
        networkStatus = NetWorkUtil.getNetworkStatus(context);

        Bundle resultBundle = new Bundle();
        Intent getResult = getIntent();
        resultBundle = getResult.getExtras();
        try {
            speedValue.setNetworkType(resultBundle.getString("networkType"));
            speedValue.setPing(resultBundle.getInt("ping",0));
            speedValue.setCostTime(resultBundle.getLong("costTime",0));
            speedValue.setDownloadByte(resultBundle.getInt("downloadByte"));
            speedValue.setTestTime(resultBundle.getLong("testTime",0));
            speedValue.setTestDateTime(resultBundle.getString("testDateTime"));
            speedValue.setLatitude(resultBundle.getDouble("latitude",0));
            speedValue.setLongitude(resultBundle.getDouble("longitude",0));
            speedValue.setLocationDesc(resultBundle.getString("locationDesc"));
            speedValue.setDownloadSpeed(resultBundle.getInt("downloadSpeed", 0));
        } catch (Exception e) {
            e.printStackTrace();
        }
        wideBroadImgeView.setImageDrawable(speedWideBroadPic.getDrawable(SpeedValueUtil.getSpeedWideBroadLevel(speedValue.getDownloadSpeed())));

        handler.sendEmptyMessage(GET_RANK);
        String speedResultStr = getResources().getString( R.string.speedresult_speed);
        speedResultStr = String.format(speedResultStr, StringUtil.formatSpeed(speedValue.getDownloadSpeed()/1024.0f));
        speedResultTxt.setText(speedResultStr);

        breakRecorks = getBrokeRecords(speedValue.getDownloadSpeed()/1024.0f);

        String rankStr = getResources().getString(R.string.speedresult_beat);
        rankStr = String.format(rankStr, breakRecorks);
        rankTxt.setText(rankStr);

        speedMetal = SpeedValueUtil.getMetal(breakRecorks);
        medal.setImageDrawable(speedMetalPic.getDrawable(speedMetal));
        speedLevel.setImageDrawable(speedGradePic.getDrawable(speedMetal));
        
        speedValue.setRank((int)breakRecorks);
        
        if (!ifWeixin) {
            SpeedDBManager dbManager = new SpeedDBManager( ShareResultActivity.this);
            dbManager.insertSpeedValue(speedValue);
        }
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		AbstractWeibo.initSDK(this);// 初始化分享SDK
	}

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
        case R.id.return_btn:
            this.finish();
            break;
        case R.id.share_btn:
            shareSpeedResult();
            MobclickAgent.onEvent(context, "cshare");
            break;
        }
    }
    
    /**
     * 设置分享按钮状态
     * @param status
     */
    private void avertShareButton(boolean status) {
        shareBtn.setClickable(status);
    }

    private long getBrokeRecords(float speedValue) {
        // JAVA产生指定范围的随机数 math.round(Math.random()*(Max-Min)+Min)
        if (speedValue > 500)
            return Math.round(Math.random() * (1000 - 800) + 800);
        if (speedValue > 250)
            return Math.round(Math.random() * (800 - 500) + 500);
        if (speedValue > 120)
            return Math.round(Math.random() * (500 - 200) + 200);
        if (speedValue > 50)
            return Math.round(Math.random() * (200 - 100) + 200);
		if (speedValue < 10)
			return 0;
        return Math.round(Math.random() * (100 - 0) + 0);
    }
    
    public  void shareSpeedResult() {
        getCaptureBitmap();
		showGrid(false);
    }

    private String prepareWeiboContent() {
        if (networkStatus == Constants.NETWORK_STATUS_MOBILE)
            return "亲，你有你的概念，我有我的标准；你只看到标榜的“3G”，但是否真的体会到上网的快感？请用网速测试！详情请看http://www.speedtest.quickbird.com/landing/";
        if (networkStatus == Constants.NETWORK_STATUS_WIFI)
            return "您的网速是2M、3M、还是10M？快用网速测试，一目了然；详情请看http://www.speedtest.quickbird.com/landing/";
        return "";
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private  void getCaptureBitmap() {
        FrameLayout captureLayout = (FrameLayout) findViewById(R.id.capture_layout);
        ScreenShotUtil.captureView(captureLayout);
    }
    
	// 使用快捷分享完成图文分享
	private void showGrid(boolean silent) {
		Intent i = new Intent(context, ShareAllGird.class);
		// 分享时Notification的图标
		i.putExtra("notif_icon", R.drawable.ic_launcher);
		// 分享时Notification的标题
		i.putExtra("notif_title", context.getString(R.string.app_name));

		// address是接收人地址，仅在信息和邮件使用，否则可以不提供
		i.putExtra("address", "support@quickbird.com");
		// title标题，在印象笔记、邮箱、信息、微信（包括好友和朋友圈）、人人网和QQ空间使用，否则可以不提供
		i.putExtra("title", context.getString(R.string.share));
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用，否则可以不提供
		i.putExtra("titleUrl", "www.speedtest.quickbird.com");
		// text是分享文本，所有平台都需要这个字段
		i.putExtra("text", prepareWeiboContent());
		// imagePath是本地的图片路径，所有平台都支持这个字段，不提供，则表示不分享图片
		i.putExtra("imagePath", Constants.PIC_PRE_PATH_NAME);
		// url仅在微信（包括好友和朋友圈）中使用，否则可以不提供
//		 i.putExtra("url", "www.speedtest.quickbird.com");
		// thumbPath是缩略图的本地路径，仅在微信（包括好友和朋友圈）中使用，否则可以不提供
		 i.putExtra("thumbPath", Constants.PIC_PRE_PATH_NAME);
		// appPath是待分享应用程序的本地路劲，仅在微信（包括好友和朋友圈）中使用，否则可以不提供
		i.putExtra("appPath", Constants.PIC_PRE_PATH_NAME);
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用，否则可以不提供
		i.putExtra("comment", context.getString(R.string.share));
		// site是分享此内容的网站名称，仅在QQ空间使用，否则可以不提供
		i.putExtra("site", context.getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用，否则可以不提供
		i.putExtra("siteUrl", "www.www.speedtest.quickbird.com");
		// 是否直接分享
		i.putExtra("silent", silent);
		context.startActivity(i);
	}
}
