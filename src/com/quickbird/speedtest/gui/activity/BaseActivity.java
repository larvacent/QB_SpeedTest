package com.quickbird.speedtest.gui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import cn.sharesdk.framework.AbstractWeibo;

import com.tencent.weibo.oauthv2.OAuthV2;
import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends Activity implements OnClickListener {

    public static OAuthV2 oAuth;
    public boolean wifiState, mobileState;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        AbstractWeibo.initSDK(this);// 初始化分享SDK
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
