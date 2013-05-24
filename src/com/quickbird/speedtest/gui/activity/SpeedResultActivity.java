package com.quickbird.speedtest.gui.activity;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.quickbird.controls.Config;
import com.quickbird.controls.Constants;
import com.quickbird.speedtest.R;
import com.quickbird.speedtest.business.WeiboBusiness;
import com.quickbird.speedtest.gui.view.CustomDialog;
import com.quickbird.speedtestengine.SpeedValue;
import com.quickbird.speedtestengine.storage.database.SpeedDBManager;
import com.quickbird.speedtestengine.storage.xml.TecentTokenKeeper;
import com.quickbird.speedtestengine.storage.xml.WeiboAccessTokenKeeper;
import com.quickbird.speedtestengine.utils.DebugUtil;
import com.quickbird.speedtestengine.utils.InputStreamUtil;
import com.quickbird.speedtestengine.utils.NetWorkUtil;
import com.quickbird.speedtestengine.utils.ProtocolUtil;
import com.quickbird.speedtestengine.utils.SpeedValueUtil;
import com.quickbird.speedtestengine.utils.StringUtil;
import com.quickbird.utils.ScreenShotUtil;
import com.quickbird.utils.ToastUtil;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.weibo.oauthv2.OAuthV2;
import com.tencent.weibo.oauthv2.OAuthV2Client;
import com.tencent.weibo.webview.OAuthV2AuthorizeWebView;
import com.umeng.analytics.MobclickAgent;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.sso.SsoHandler;
import com.weibo.sdk.android.util.Utility;

public class SpeedResultActivity extends BaseActivity {

    private TextView speedResultTxt, peopleNumsTxt, rankTxt;
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
                            connection = ProtocolUtil.prepareConnection(SpeedResultActivity.this,Config.SERVER_URL);
                            ProtocolUtil.WriteRequest2Remote(ProtocolUtil.prepareRequestBody(SpeedResultActivity.this, speedValue),connection);
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
        
        setContentView(R.layout.activity_speedresult);
        context = SpeedResultActivity.this;
        speedResultTxt = (TextView) findViewById(R.id.speed_result);
        peopleNumsTxt = (TextView) findViewById(R.id.people_nums);
        rankTxt = (TextView) findViewById(R.id.rank);
        speedLevel = (ImageView) findViewById(R.id.speed_level);
        medal = (ImageView) findViewById(R.id.medal);
        returnBtn = (Button) findViewById(R.id.return_btn);
        shareBtn = (Button) findViewById(R.id.share_btn);

        returnBtn.setOnClickListener(this);
        shareBtn.setOnClickListener(this);

        speedMetalPic = getResources().obtainTypedArray( R.array.speedmetal_array);
        speedGradePic = getResources().obtainTypedArray( R.array.speedgrade_array);
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
            SpeedDBManager dbManager = new SpeedDBManager( SpeedResultActivity.this);
            dbManager.insertSpeedValue(speedValue);
        }
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
            avertShareButton(false);
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
        return Math.round(Math.random() * (100 - 0) + 0);
    }
    
    public  void shareSpeedResult() {
        getCaptureBitmap();
        final CustomDialog dialog = new CustomDialog(context, R.style.Dialog);
        View v = LayoutInflater.from(context).inflate(R.layout.share_shareto, null);
        RelativeLayout btnToSina = (RelativeLayout) v.findViewById(R.id.btnToSina);
        RelativeLayout btnToTenc = (RelativeLayout) v.findViewById(R.id.btnToTenc);
        RelativeLayout btnToWeiXin = (RelativeLayout) v.findViewById(R.id.btnToWeiXin);
        RelativeLayout btnToWinXinSession = (RelativeLayout) v.findViewById(R.id.btnToWinXinSession);
        dialog.setContentView(v);
        dialog.show(); 
        // 如果没有安装微信客户端则不显示
        IWXAPI api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
        if(api.isWXAppInstalled() == false)
        {
            btnToWeiXin.setVisibility(View.GONE);
            btnToWinXinSession.setVisibility(View.GONE);
        }
        dialog.setOnCancelListener(new OnCancelListener() {
            
            @Override
            public void onCancel(DialogInterface dialog) {
                avertShareButton(true);
            }
        });
        android.view.View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                case R.id.btnToSina:
                    shareToWeibo();
                    dialog.dismiss();
                    avertShareButton(true);
                    break;
                case R.id.btnToTenc:
                    shareToTecentWeibo();
                    dialog.dismiss();
                    avertShareButton(true);
                    break;
                case R.id.btnToWeiXin:
                    ifWeixin = !ifWeixin;
                    shareToWeixin(false);
                    dialog.dismiss();
                    avertShareButton(true);
                    break;
                case R.id.btnToWinXinSession:
                    ifWeixin = !ifWeixin;
                    shareToWeixin(true);
                    dialog.dismiss();
                    avertShareButton(true);
                    break;
                }
            };
        };
        btnToSina.setOnClickListener(listener);
        btnToTenc.setOnClickListener(listener);
        btnToWeiXin.setOnClickListener(listener);
        btnToWinXinSession.setOnClickListener(listener);
    }

    private String prepareWeiboContent() {
        if (networkStatus == Constants.NETWORK_STATUS_MOBILE)
            return "亲，你有你的概念，我有我的标准；你只看到标榜的“3G”，但是否真的体会到上网的快感？请用快鸟测速！猛击下载http://dl.quickbird.com/android/QB_Speed.apk";
        if (networkStatus == Constants.NETWORK_STATUS_WIFI)
            return "您的带宽是2M,3M还是10M，用快鸟测速一目了然；猛击下载http://dl.quickbird.com/android/QB_Speed.apk";
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

    private  void shareToWeibo() {
        Weibo mWeibo = Weibo.getInstance(Constants.CONSUMER_KEY, Constants.REDIRECT_URL);
        accessToken = WeiboAccessTokenKeeper.readAccessToken(this);
        if (accessToken.isSessionValid()) {
            Weibo.isWifi = Utility.isWifi(this);
            try {
                WeiboBusiness weiboBusiness = new WeiboBusiness( SpeedResultActivity.this);
                weiboBusiness.goToWeiboAct(SpeedResultActivity.this,
                        accessToken.getToken(), prepareWeiboContent(),
                        Constants.PIC_PRE_PATH_NAME);
            } catch (WeiboException e) {
                DebugUtil.e("WeiboException :" + e.getMessage());
            }
        } else {
            mSsoHandler = new SsoHandler(SpeedResultActivity.this, mWeibo);
            mSsoHandler.authorize(new AuthDialogListener());
        }
    }
    
    private void shareToTecentWeibo() {

        if (TecentTokenKeeper.isSessionValid(context)) {
            oAuth = TecentTokenKeeper.readOAuthV2(context);
            try {
                gotoTecentAct(this,
                            oAuth,
                            prepareWeiboContent(),
                            Constants.PIC_PRE_PATH_NAME);
            } catch (WeiboException e) {
                e.printStackTrace();
            }
            
        } else {
            oAuth = new OAuthV2(Constants.TECENT_REDIRECT_URL);
            oAuth.setClientId(Constants.APP_KEY);
            oAuth.setClientSecret(Constants.APP_SECRET);
            // 关闭OAuthV2Client中的默认开启的QHttpClient。
            OAuthV2Client.getQHttpClient().shutdownConnection();
            DebugUtil.i("-------------Step1: Implicit Grant--------------");
            Intent intent = new Intent(this, OAuthV2AuthorizeWebView.class);// 创建Intent，使用WebView让用户授权
            intent.putExtra("oauth", oAuth);
            startActivityForResult(intent, 2);
        }
    }
    
    private void gotoTecentAct(Activity activity, OAuthV2 oAuth,
            String content, String picPath)
                    throws WeiboException {
        if (picPath != null) {
            File picFile = new File(picPath);
            if (!picFile.exists()) {
                picPath = null;
            }
        }
        if (TextUtils.isEmpty(content) && TextUtils.isEmpty(picPath)) {
            throw new WeiboException("weibo content can not be null!");
        }
        
        Intent intent = new Intent(activity, TecentShareActivity.class);
        intent.putExtra("oauth", oAuth);
        intent.putExtra("content", content);
        intent.putExtra("picPath", picPath);
        intent.putExtra("fromWhere", "TestSpeedResult");
        activity.startActivity(intent);
    }
    
    private void shareToWeixin(boolean ifShareToTrends) {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        try {
            IWXAPI api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
            api.registerApp(Constants.APP_ID);
            if (api.isWXAppInstalled() == false) {
                Toast.makeText(this, "请安装微信客户端！", Toast.LENGTH_LONG).show();
                return;
            }
            Bitmap bmp = BitmapFactory.decodeFile(Constants.PIC_PRE_PATH_NAME);
            Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 70, 70, true);
            bmp.recycle();

            WXWebpageObject webpage = new WXWebpageObject();
            webpage.webpageUrl = "http://www.shangwangkuainiao.com/";
            WXMediaMessage msg = new WXMediaMessage(webpage);
            msg.title = "快鸟测速";
            msg.description = prepareWeiboContent();
            msg.thumbData = ToastUtil.bmpToByteArray(thumbBmp, true);

            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = buildTransaction("webpage");
            req.message = msg;
            req.scene = ifShareToTrends ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
            api.sendReq(req);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "请选择其它方式分享，无法启动微信。", Toast.LENGTH_SHORT).show();
        }
    }

    class AuthDialogListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            String token = values.getString("access_token");
            String expires_in = values.getString("expires_in");
            accessToken = new Oauth2AccessToken(token, expires_in);
            if (accessToken.isSessionValid()) {
                WeiboAccessTokenKeeper.keepAccessToken(SpeedResultActivity.this, accessToken);
                ToastUtil.showToast(SpeedResultActivity.this, "认证成功");
                try {
                    WeiboBusiness weiboBusiness = new WeiboBusiness( SpeedResultActivity.this);
                    weiboBusiness.goToWeiboAct(SpeedResultActivity.this,
                            accessToken.getToken(), prepareWeiboContent(),
                            Constants.PIC_PRE_PATH_NAME);
                } catch (WeiboException e) {
                    DebugUtil.e("WeiboException :" + e.getMessage());
                }
            }
        }

        @Override
        public void onError(WeiboDialogError e) {
            ToastUtil.showToast(SpeedResultActivity.this, "认证失败");
        }

        @Override
        public void onCancel() {
            ToastUtil.showToast(SpeedResultActivity.this, "取消认证");
        }

        @Override
        public void onWeiboException(WeiboException e) {
            ToastUtil.showToast(SpeedResultActivity.this, "认证失败");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        if (requestCode == 2) {
            if (resultCode == OAuthV2AuthorizeWebView.RESULT_CODE) {
                oAuth = (OAuthV2) data.getExtras().getSerializable("oauth");
                if (oAuth.getStatus() == 0) {
                    Toast.makeText(getApplicationContext(), "登陆成功",Toast.LENGTH_SHORT).show();
                    TecentTokenKeeper.keepOAuthV2(context, oAuth);
                    MobclickAgent.onEvent(context, "speedtest_tweibo_oth_suc");
                    try {
                        gotoTecentAct(this,
                                    oAuth,
                                    prepareWeiboContent(),
                                    Constants.PIC_PRE_PATH_NAME);
                    } catch (WeiboException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

}
