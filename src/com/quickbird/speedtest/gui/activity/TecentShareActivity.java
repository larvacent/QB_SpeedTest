package com.quickbird.speedtest.gui.activity;

import java.io.File;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.quickbird.speedtest.R;
import com.quickbird.speedtestengine.utils.DebugUtil;
import com.tencent.weibo.api.FriendsAPI;
import com.tencent.weibo.api.TAPI;
import com.tencent.weibo.constants.OAuthConstants;
import com.tencent.weibo.oauthv2.OAuthV2;
import com.umeng.analytics.MobclickAgent;

public class TecentShareActivity extends BaseActivity {

    private OAuthV2 oAuthV2;
    private TextView mTextNum;
    private ImageButton mSend, mPhoto;
    private ImageButton attiton;
    private EditText mEdit;
    private FrameLayout mPiclayout;
    private ImageView mImage;
    private String mPicPath = "";
    private String mContent = "";
    private String fromWhere = "";
    public static final int WEIBO_MAX_LENGTH = 140;
    private MainHandler mHandler = new MainHandler();
    private boolean ifAttionQuickbird = true;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        context = this;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_share_mblog);
        setTitle(getString(R.string.umeng_share_shareto_tenc));
        Intent intent = this.getIntent();
        // 接收用Intent传来的App信息及之前通过了Oauth鉴权的信息
        oAuthV2 = (OAuthV2) intent.getExtras().getSerializable("oauth");
        mPicPath = intent.getStringExtra("picPath");
        mContent = intent.getStringExtra("content");
        fromWhere = intent.getStringExtra("fromWhere");

        mSend = (ImageButton) this.findViewById(R.id.share_btnSend);
        mSend.setOnClickListener(this);
        LinearLayout total = (LinearLayout) this
                .findViewById(R.id.share_ll_text_limit_unit);
        total.setOnClickListener(this);
        mTextNum = (TextView) this.findViewById(R.id.share_tv_text_limit);

        mEdit = (EditText) this.findViewById(R.id.share_etEdit);
        mEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
                String mText = mEdit.getText().toString();
                int len = mText.length();
                if (len <= WEIBO_MAX_LENGTH) {
                    len = WEIBO_MAX_LENGTH - len;
                    // mTextNum.setTextColor(R.color.weibosdk_text_num_gray);
                    if (!mSend.isEnabled())
                        mSend.setEnabled(true);
                } else {
                    len = len - WEIBO_MAX_LENGTH;

                    mTextNum.setTextColor(Color.RED);
                    if (mSend.isEnabled())
                        mSend.setEnabled(false);
                }
                mTextNum.setText(String.valueOf(len));
            }
        });
        mEdit.setText(mContent);
        mPiclayout = (FrameLayout) TecentShareActivity.this.findViewById(R.id.share_flPic);
        attiton = (ImageButton) TecentShareActivity.this.findViewById(R.id.attention_quickbird);
        attiton.setOnClickListener(this);

        if (TextUtils.isEmpty(this.mPicPath)) {
            mPiclayout.setVisibility(View.GONE);
        } else {
            mPiclayout.setVisibility(View.VISIBLE);
            File file = new File(mPicPath);
            if (file.exists()) {
                DebugUtil.d("mPicPath" + mPicPath);
                Bitmap pic = BitmapFactory.decodeFile(this.mPicPath);
                ImageView image = (ImageView) this
                        .findViewById(R.id.share_ivImage);
                image.setImageBitmap(pic);
            } else {
                mPiclayout.setVisibility(View.GONE);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        String mPicPath = this.mPicPath;
        String mContent = this.mContent;
        switch (v.getId()) {
        case R.id.share_btnSend:
            if (fromWhere.equals("TestSpeedResult"))
                MobclickAgent.onEvent(context, "speedtest_tweibo_send");
            else
                MobclickAgent.onEvent(context, "savetraffic_tweibo_send");
            try {
                if (!TextUtils.isEmpty(oAuthV2.getAccessToken())) {
                    this.mContent = mEdit.getText().toString();
                    if (TextUtils.isEmpty(mContent)) {
                        Toast.makeText(this, "请输入内容!", Toast.LENGTH_LONG)
                                .show();
                        return;
                    }
                    if (!TextUtils.isEmpty(mPicPath)) {
                        mHandler.sendEmptyMessage(MainHandler.SHARE_TEXT_PIC);
                        mHandler.sendEmptyMessage(MainHandler.FINISH);
                    } else {
                        // Just update a text weibo!
                        mHandler.sendEmptyMessage(MainHandler.SHARE_TEXT);
                        mHandler.sendEmptyMessage(MainHandler.FINISH);
                    }
                } else {
                    Toast.makeText(this,
                            this.getString(R.string.weibosdk_please_login),
                            Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            break;
        case R.id.share_ll_text_limit_unit:
            Dialog dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.weibosdk_attention)
                    .setMessage(R.string.weibosdk_delete_all)
                    .setPositiveButton(R.string.weibosdk_ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    mEdit.setText("");
                                }
                            })
                    .setNegativeButton(R.string.weibosdk_cancel, null).create();
            dialog.show();
            break;
        case R.id.share_ivDelPic:
            Dialog delDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.weibosdk_attention)
                    .setMessage(R.string.weibosdk_del_pic)
                    .setPositiveButton(R.string.weibosdk_ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    mPiclayout.setVisibility(View.GONE);
                                }
                            })
                    .setNegativeButton(R.string.weibosdk_cancel, null).create();
            delDialog.show();
            break;
        case R.id.attention_quickbird:
            ifAttionQuickbird = !ifAttionQuickbird;
            if (ifAttionQuickbird) {
                attiton.setBackgroundResource(R.drawable.share_box_off);
            } else {
                attiton.setBackgroundResource(R.drawable.share_box_on);
            }
            break;
        }

    }

    public class MainHandler extends Handler {
        public static final int SHARE_TEXT = 1;
        public static final int SHARE_TEXT_PIC = 2;
        public static final int FINISH = 3;
        public static final int TOAST_SUCCESS = 4;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case SHARE_TEXT_PIC:
                new Thread() {
                    TAPI tAPI = new TAPI(OAuthConstants.OAUTH_VERSION_2_A);
                    FriendsAPI friendsAPI = new FriendsAPI(
                            OAuthConstants.OAUTH_VERSION_2_A);

                    @Override
                    public void run() {
                        try {
                            String response = tAPI.addPic(oAuthV2, "json",
                                    mContent, "127.0.0.1", mPicPath);
                            // if (ifAttionQuickbird)
                            // friendsAPI.add(oAuthV2, "json", "上网快鸟", null);
                            if (response != null)
                                mHandler.sendEmptyMessage(MainHandler.TOAST_SUCCESS);
                            DebugUtil.d("response:" + response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            tAPI.shutdownConnection();
                            friendsAPI.shutdownConnection();
                        }
                    };
                }.start();
                break;
            case SHARE_TEXT:
                new Thread() {
                    TAPI tAPI = new TAPI(OAuthConstants.OAUTH_VERSION_2_A);

                    @Override
                    public void run() {
                        try {
                            String response = tAPI.add(oAuthV2, "json",
                                    mContent, "127.0.0.1");
                            DebugUtil.d("response:" + response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            tAPI.shutdownConnection();
                        }
                    };
                }.start();
                break;
            case FINISH:
                finish();
                break;
            case TOAST_SUCCESS:
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(TecentShareActivity.this,
                                R.string.weibosdk_send_sucess,
                                Toast.LENGTH_LONG).show();
                    }
                });
                break;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}
