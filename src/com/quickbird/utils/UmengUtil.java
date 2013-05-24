package com.quickbird.utils;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.NotificationType;
import com.umeng.fb.UMFeedbackService;
import com.umeng.update.UmengUpdateAgent;

/***
 * 友盟统计工具
 * 
 * @author XueDong
 * 
 */
public class UmengUtil {
    public static void onCreate(Context context) {
        MobclickAgent.setDebugMode(false);
        MobclickAgent.setSessionContinueMillis(60000);// 60秒之内返回应用可视为同一次启动
        // 使用友盟错误报告
        MobclickAgent.onError(context);
        // 使用友盟在线配置功能
        MobclickAgent.updateOnlineConfig(context);
        MobclickAgent.setAutoLocation(false);
        // 使用友盟用户反馈功能
        UMFeedbackService.enableNewReplyNotification(context,
                NotificationType.AlertDialog);
        // 如果想程序启动时自动检查是否需要更新， 把下面两行代码加在Activity 的onCreate()函数里。
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        // 如果您同时使用了手动更新和自动检查更新，为了避免更新回调被多次调用，请加上下面这句代码
        UmengUpdateAgent.setOnDownloadListener(null);
        // 使用友盟用户自动更新功能
        // UmengUpdateAgent.update(context);
    }
}
