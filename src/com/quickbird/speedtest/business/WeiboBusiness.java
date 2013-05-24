package com.quickbird.speedtest.business;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.quickbird.speedtest.gui.activity.ShareActivity;
import com.weibo.sdk.android.WeiboException;

public class WeiboBusiness {
    
    private Context context;
    public WeiboBusiness(Context context) {
        this.context = context;
    }
    
    public void goToWeiboAct(Activity activity, String accessToken,
            String content, String picPath)
            throws WeiboException {
        if (picPath != null) {
            File picFile = new File(picPath);
            if (!picFile.exists()) {
                picPath = null;
            }
        }
        if (TextUtils.isEmpty(accessToken)) {
            throw new WeiboException("token can not be null!");
        }
        if (TextUtils.isEmpty(content) && TextUtils.isEmpty(picPath)) {
            throw new WeiboException("weibo content can not be null!");
        }
        Intent i = new Intent(activity, ShareActivity.class);
        i.putExtra(ShareActivity.EXTRA_ACCESS_TOKEN, accessToken);
        i.putExtra(ShareActivity.EXTRA_WEIBO_CONTENT, content);
        i.putExtra(ShareActivity.EXTRA_PIC_URI, picPath);
        i.putExtra(ShareActivity.EXTRA_WHERE, "TestSpeedResult");
        activity.startActivity(i);
    }

}
