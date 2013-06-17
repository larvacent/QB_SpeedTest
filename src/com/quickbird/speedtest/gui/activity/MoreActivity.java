package com.quickbird.speedtest.gui.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.quickbird.speedtest.R;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.NotificationType;
import com.umeng.fb.UMFeedbackService;
import com.umeng.fb.util.FeedBackListener;
import com.umeng.update.UmengDownloadListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

public class MoreActivity extends BaseActivity {
	private Context mContext = this;
	private RelativeLayout networksetLayout, historyLayout, accountLayout,
			versionLayout, feedbackLayout, aboutLayout;
	public final static String formMore = "formMore";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_more);

        networksetLayout = (RelativeLayout) findViewById(R.id.networkset_layout);
        historyLayout = (RelativeLayout) findViewById(R.id.history_layout);
        accountLayout = (RelativeLayout) findViewById(R.id.account_layout);
        versionLayout = (RelativeLayout) findViewById(R.id.version_layout);
        feedbackLayout = (RelativeLayout) findViewById(R.id.feedback_layout);
        aboutLayout = (RelativeLayout) findViewById(R.id.about_layout);

        networksetLayout.setOnClickListener(this);
        historyLayout.setOnClickListener(this);
        accountLayout.setOnClickListener(this);
        versionLayout.setOnClickListener(this);
        feedbackLayout.setOnClickListener(this);
        aboutLayout.setOnClickListener(this);

        UmengUpdateAgent.setOnDownloadListener(null);
        // 使用友盟用户反馈功能
        UMFeedbackService.enableNewReplyNotification(this, NotificationType.AlertDialog);

        MobclickAgent.onEvent(this, "cst");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
        case R.id.networkset_layout:
            startActivity(new Intent(MoreActivity.this, NetWorkActivity.class));
            break;
        case R.id.history_layout:
        	Intent intentToHistory = new Intent(MoreActivity.this, SpeedHistoryActivity.class);
        	Bundle bundleToHistory = new Bundle();
        	bundleToHistory.putString("formMore", formMore);
        	intentToHistory.putExtras(bundleToHistory);
        	startActivity(intentToHistory);
            break;
        case R.id.version_layout:
            // 如果想程序启动时自动检查是否需要更新， 把下面两行代码加在Activity 的onCreate()函数里。
            com.umeng.common.Log.LOG = true;
            UmengUpdateAgent.setUpdateOnlyWifi(false); // 目前我们默认在Wi-Fi接入情况下才进行自动提醒。如需要在其他网络环境下进行更新自动提醒，则请添加该行代码
            UmengUpdateAgent.setUpdateAutoPopup(false);
            UmengUpdateAgent.setUpdateListener(updateListener);
            UmengUpdateAgent.setOnDownloadListener(new UmengDownloadListener() {

                @Override
                public void OnDownloadEnd(int result) {
                }

            });
            UmengUpdateAgent.update(mContext);
            break;
        case R.id.about_layout:
            Intent intent = new Intent(MoreActivity.this, AboutActivity.class);
            startActivity(intent);
//            Activity activity = MoreActivity.this;
//            while (activity.getParent() != null) {
//                activity = activity.getParent();
//            }
//            try {
//                final CustomDialog dialog = new CustomDialog(activity, R.style.Dialog);
//                View view = LayoutInflater.from(activity).inflate(R.layout.alert_about, null);
//                Button button = (Button) view.findViewById(R.id.alert_btn);
//                dialog.setContentView(view);
//                dialog.show();
//                button.setOnClickListener(new OnClickListener() {
//                    
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                        
//                    }
//                });
//            } catch (Exception e) {
//                DebugUtil.d(e.getMessage());
//            }
            break;
        case R.id.feedback_layout:
            // “友盟反馈”还支持反馈信息的定制化，以便在反馈页面中收集额外信息。例如，开发者想进行有奖反馈，他可能需要收集用户的QQ、手机号等联系方式用于确认，另外还可能需要用户姓名、奖品寄送地址等信息。
            FeedBackListener listener = new FeedBackListener() {
                @Override
                public void onSubmitFB(Activity activity) {
                    EditText qqText = (EditText) activity.findViewById(R.id.feedback_qq);
                    Map<String, String> contactMap = new HashMap<String, String>();
                    contactMap.put("contact", qqText.getText().toString());
                    UMFeedbackService.setContactMap(contactMap);
                }

                @Override
                public void onResetFB(Activity activity,
                        Map<String, String> contactMap,
                        Map<String, String> remarkMap) {
                    // FB initialize itself,load other attribute
                    // from local storage and set them
                    EditText qqText = (EditText) activity
                            .findViewById(R.id.feedback_qq);
                    if (contactMap != null)
                        qqText.setText(contactMap.get("contact"));
                }
            };
            UMFeedbackService.setFeedBackListener(listener);
            // 如果您程序界面是iOS风格，我们还提供了左上角的“返回”按钮，用于退出友盟反馈模块。启动友盟反馈模块前，您需要增加如下语句来设置“返回”按钮可见：
            UMFeedbackService.setGoBackButtonVisible();
            UMFeedbackService.openUmengFeedbackSDK(mContext);
            break;
        case R.id.account_layout:
            startActivity(new Intent(MoreActivity.this, AccountManageActivity.class));
            break;
        }
    }

    // 友盟手动更新功能
    UmengUpdateListener updateListener = new UmengUpdateListener() {
        @Override
        public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
            switch (updateStatus) {
            case 0: // has update
                Log.i("--->", "callback result");
                UmengUpdateAgent.showUpdateDialog(mContext, updateInfo);
                break;
            case 1: // has no update
                Toast.makeText(mContext, "没有更新", Toast.LENGTH_SHORT).show();
                break;
            case 2: // none wifi
                Toast.makeText(mContext, "没有wifi连接， 只在wifi下更新",
                        Toast.LENGTH_SHORT).show();
                break;
            case 3: // time out
                Toast.makeText(mContext, "网络联接不成功，请确认联网成功后再试",
                        Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };
}
