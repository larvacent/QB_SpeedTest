package com.quickbird.speedtest.gui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.tencent.weibo.oauthv2.OAuthV2;
import com.umeng.analytics.MobclickAgent;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.sso.SsoHandler;

public class BaseActivity extends Activity implements OnClickListener {

    public static Oauth2AccessToken accessToken;
    public SsoHandler mSsoHandler;
    public static OAuthV2 oAuth;
    public boolean wifiState, mobileState;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
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
