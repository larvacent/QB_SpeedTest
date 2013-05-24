package com.quickbird.speedtest.gui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.quickbird.controls.Constants;
import com.quickbird.speedtest.R;
import com.quickbird.speedtest.gui.view.CustomDialog;
import com.quickbird.speedtestengine.storage.xml.TecentTokenKeeper;
import com.quickbird.speedtestengine.storage.xml.WeiboAccessTokenKeeper;
import com.quickbird.utils.ToastUtil;
import com.tencent.weibo.oauthv2.OAuthV2;
import com.tencent.weibo.oauthv2.OAuthV2Client;
import com.tencent.weibo.webview.OAuthV2AuthorizeWebView;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.sso.SsoHandler;
import com.weibo.sdk.android.util.Utility;

public class AccountManageActivity extends BaseActivity {

    private RelativeLayout weiboAccount, tweiboAccount;
    private ImageView weiboLogo, tweiboLogo;
    private boolean ifsinaAccounttie, iftecentAccounttie;
    private Weibo mWeibo;
    private CustomDialog dialog = null;
    private CustomDialog.Builder builder = null;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        mContext = AccountManageActivity.this;
        weiboAccount = (RelativeLayout) findViewById(R.id.weibo_account);
        tweiboAccount = (RelativeLayout) findViewById(R.id.tweibo_account);
        weiboLogo = (ImageView) findViewById(R.id.weibo_logo);
        tweiboLogo = (ImageView) findViewById(R.id.tweibo_logo);
        weiboAccount.setOnClickListener(this);
        tweiboAccount.setOnClickListener(this);
        mWeibo = Weibo.getInstance(Constants.CONSUMER_KEY, Constants.REDIRECT_URL);
        oAuth = new OAuthV2(Constants.APP_ID, Constants.APP_SECRET, Constants.TECENT_REDIRECT_URL);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 新浪微博账号是否绑定
        BaseActivity.accessToken = WeiboAccessTokenKeeper.readAccessToken(this);
        if (BaseActivity.accessToken.isSessionValid()) {
            ifsinaAccounttie = true;
            Weibo.isWifi = Utility.isWifi(this);
            weiboLogo.setImageResource(R.drawable.umeng_socialize_sina_on);
        } else {
            ifsinaAccounttie = false;
            weiboLogo.setImageResource(R.drawable.umeng_socialize_sina_off);
        }
        // 腾讯微博账号是否绑定
        if (TecentTokenKeeper.isSessionValid(mContext)) {
            iftecentAccounttie = true;
            tweiboLogo.setImageResource(R.drawable.umeng_socialize_tx_on);
        } else {
            iftecentAccounttie = false;
            tweiboLogo.setImageResource(R.drawable.umeng_socialize_tx_off);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
        case R.id.weibo_account:
            sinaAccount();
            break;
        case R.id.tweibo_account:
            tecentAccount();
            break;
        }
    }
    
    
    class AuthDialogListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            String token = values.getString("access_token");
            String expires_in = values.getString("expires_in");
            BaseActivity.accessToken = new Oauth2AccessToken(token, expires_in);
            if (BaseActivity.accessToken.isSessionValid()) {
                ifsinaAccounttie = true;
                WeiboAccessTokenKeeper.keepAccessToken(AccountManageActivity.this, accessToken);
                weiboLogo.setImageResource(R.drawable.umeng_socialize_sina_on);
                ToastUtil.showToast(AccountManageActivity.this, "认证成功");
            }
        }

        @Override
        public void onError(WeiboDialogError e) {
            ToastUtil.showToast(AccountManageActivity.this, "登陆失败");
        }

        @Override
        public void onCancel() {
        }

        @Override
        public void onWeiboException(WeiboException e) {
            ToastUtil.showToast(AccountManageActivity.this, "登陆失败");
        }

    }
   

    private void sinaAccount() {
        if (ifsinaAccounttie) {
            builder = new CustomDialog.Builder(AccountManageActivity.this);
            builder.setTitle("解除绑定")
                    .setMessage( "是否解除绑定" + "\n\n" + "您的新浪微博帐号解除绑定后，再次分享时需要重新授权。")
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    dialog.dismiss();
                                }
                            })
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    if (dialog != null)
                                        dialog.dismiss();
                                    WeiboAccessTokenKeeper.clear(mContext);
                                    weiboLogo.setImageResource(R.drawable.umeng_socialize_sina_off);
                                    ifsinaAccounttie = false;
                                }
                            });
            dialog = builder.create();
            dialog.show();

        } else {
            mSsoHandler = new SsoHandler(AccountManageActivity.this, mWeibo);
            mSsoHandler.authorize(new AuthDialogListener());
        }
    }
    
    private void tecentAccount() {
        if (iftecentAccounttie) {
            builder = new CustomDialog.Builder(AccountManageActivity.this);
            builder.setTitle("解除绑定")
                    .setMessage(
                            "是否解除绑定" + "\n\n" + "您的腾讯微博帐号解除绑定后，再次分享时需要重新授权。")
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    dialog.dismiss();
                                }
                            })
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    if (dialog != null)
                                        dialog.dismiss();
                                    TecentTokenKeeper.clear(mContext);
                                    tweiboLogo.setImageResource(R.drawable.umeng_socialize_tx_off);
                                    iftecentAccounttie = false;
                                }
                            });
            dialog = builder.create();
            dialog.show();

        } else {
            oAuth=new OAuthV2(Constants.TECENT_REDIRECT_URL);
            oAuth.setClientId(Constants.APP_KEY);
            oAuth.setClientSecret(Constants.APP_SECRET);
            //关闭OAuthV2Client中的默认开启的QHttpClient。
            OAuthV2Client.getQHttpClient().shutdownConnection();
            Intent intent = new Intent(AccountManageActivity.this, OAuthV2AuthorizeWebView.class);//创建Intent，使用WebView让用户授权
            intent.putExtra("oauth", oAuth);
            startActivityForResult(intent,2); 
        }
    }
    
    

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(mSsoHandler!=null){
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        if (requestCode==2) {
            if (resultCode==OAuthV2AuthorizeWebView.RESULT_CODE)    {
                oAuth=(OAuthV2) data.getExtras().getSerializable("oauth");
                if (oAuth.getStatus() == 0) {
                    Toast.makeText(getApplicationContext(), "登陆成功", Toast.LENGTH_SHORT).show();
                    iftecentAccounttie = true;
                    tweiboLogo.setImageResource(R.drawable.umeng_socialize_tx_on);
                    TecentTokenKeeper.keepOAuthV2(mContext, oAuth);
                }
            }
        }
    }

}
