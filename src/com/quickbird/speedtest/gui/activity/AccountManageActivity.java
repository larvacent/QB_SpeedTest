package com.quickbird.speedtest.gui.activity;

import java.util.HashMap;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.widget.CheckedTextView;
import cn.sharesdk.framework.AbstractWeibo;
import cn.sharesdk.framework.WeiboActionListener;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.weibo.TencentWeibo;

import com.quickbird.speedtest.R;

public class AccountManageActivity extends BaseActivity implements Callback,
		WeiboActionListener {

	private Context mContext;
	private Handler handler;
	private CheckedTextView ctvSw,ctvTc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);
		mContext = AccountManageActivity.this;
		handler = new Handler(this);

		ctvSw = (CheckedTextView)findViewById(R.id.ctvSw);
		ctvTc = (CheckedTextView)findViewById(R.id.ctvTc);
		ctvSw.setOnClickListener(this);
		ctvTc.setOnClickListener(this);

		// 获取平台列表
		AbstractWeibo[] weibos = AbstractWeibo.getWeiboList(mContext);
		for (AbstractWeibo weibo : weibos) {
			if (!weibo.isValid()) {
				continue;
			}

			CheckedTextView ctv = getView(weibo);
			if (ctv != null) {
				ctv.setChecked(true);
				String userName = weibo.getDb().get("nickname"); // getAuthedUserName();
				if (userName == null || userName.length() <= 0 || "null".equals(userName)) {
					// 如果平台已经授权却没有拿到帐号名称，则自动获取用户资料，以获取名称
					userName = getWeiboName(weibo);
					weibo.setWeiboActionListener(this);
					weibo.showUser(null);
				}
				ctv.setText(userName);
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	/** 授权和取消授权的逻辑代码 */
	public void onClick(View v) {
		AbstractWeibo weibo = getWeibo(v.getId());
		CheckedTextView ctv = (CheckedTextView) v;
		if (weibo == null) {
			ctv.setChecked(false);
			ctv.setText(R.string.not_yet_authorized);
			return;
		}

		if (weibo.isValid()) {
			weibo.removeAccount();
			ctv.setChecked(false);
			ctv.setText(R.string.not_yet_authorized);
			return;
		}

		weibo.setWeiboActionListener(this);
		weibo.showUser(null);
	}

	private AbstractWeibo getWeibo(int vid) {
		String name = null;
		switch (vid) {
		case R.id.ctvSw:
			name = SinaWeibo.NAME;
			break;
		case R.id.ctvTc:
			name = TencentWeibo.NAME;
			break;
		}

		if (name != null) {
			try {
				return AbstractWeibo.getWeibo(mContext, name);
			} catch (Exception e) {
			}
		}
		return null;
	}

	private CheckedTextView getView(AbstractWeibo weibo) {
		if (weibo == null) {
			return null;
		}

		String name = weibo.getName();
		if (name == null) {
			return null;
		}

		View v = null;
		if (SinaWeibo.NAME.equals(name)) {
			v = (CheckedTextView)findViewById(R.id.ctvSw);
		} else if (TencentWeibo.NAME.equals(name)) {
			v = (CheckedTextView)findViewById(R.id.ctvTc);
		}
		if (v == null) {
			return null;
		}

		if (!(v instanceof CheckedTextView)) {
			return null;
		}

		return (CheckedTextView) v;
	}

	private String getWeiboName(AbstractWeibo weibo) {
		if (weibo == null) {
			return null;
		}

		String name = weibo.getName();
		if (name == null) {
			return null;
		}

		int res = 0;
		if (SinaWeibo.NAME.equals(name)) {
			res = R.string.sinaweibo;
		} else if (TencentWeibo.NAME.equals(name)) {
			res = R.string.tencentweibo;
		}

		if (res == 0) {
			return name;
		}

		return mContext.getResources().getString(res);
	}

	public void onComplete(AbstractWeibo weibo, int action,
			HashMap<String, Object> res) {
		Message msg = new Message();
		msg.arg1 = 1;
		msg.arg2 = action;
		msg.obj = weibo;
		handler.sendMessage(msg);
	}

	public void onError(AbstractWeibo weibo, int action, Throwable t) {
		t.printStackTrace();

		Message msg = new Message();
		msg.arg1 = 2;
		msg.arg2 = action;
		msg.obj = weibo;
		handler.sendMessage(msg);
	}

	public void onCancel(AbstractWeibo weibo, int action) {
		Message msg = new Message();
		msg.arg1 = 3;
		msg.arg2 = action;
		msg.obj = weibo;
		handler.sendMessage(msg);
	}

	/**
	 * 处理操作结果
	 * <p>
	 * 如果获取到用户的名称，则显示名称；否则如果已经授权，则显示 平台名称
	 */
	public boolean handleMessage(Message msg) {
		AbstractWeibo weibo = (AbstractWeibo) msg.obj;
		String text = actionToString(msg.arg2);
		switch (msg.arg1) {
		case 1: { // 成功
			text = weibo.getName() + " completed at " + text;
//			Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
		}
			break;
		case 2: { // 失败
			text = weibo.getName() + " caught error at " + text;
//			Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
			return false;
		}
		case 3: { // 取消
			text = weibo.getName() + " canceled at " + text;
//			Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
			return false;
		}
		}

		CheckedTextView ctv = getView(weibo);
		if (ctv != null) {
			ctv.setChecked(true);
			String userName = weibo.getDb().get("nickname"); // getAuthedUserName();
			if (userName == null || userName.length() <= 0
					|| "null".equals(userName)) {
				userName = getWeiboName(weibo);
			}
			ctv.setText(userName);
		}
		return false;
	}

	public String actionToString(int action) {
		switch (action) {
		case AbstractWeibo.ACTION_AUTHORIZING:
			return "ACTION_AUTHORIZING";
		case AbstractWeibo.ACTION_GETTING_FRIEND_LIST:
			return "ACTION_GETTING_FRIEND_LIST";
		case AbstractWeibo.ACTION_FOLLOWING_USER:
			return "ACTION_FOLLOWING_USER";
		case AbstractWeibo.ACTION_SENDING_DIRECT_MESSAGE:
			return "ACTION_SENDING_DIRECT_MESSAGE";
		case AbstractWeibo.ACTION_TIMELINE:
			return "ACTION_TIMELINE";
		case AbstractWeibo.ACTION_USER_INFOR:
			return "ACTION_USER_INFOR";
		case AbstractWeibo.ACTION_SHARE:
			return "ACTION_SHARE";
		default: {
			return "UNKNOWN";
		}
		}
	}

	// @Override
	// public void onClick(View v) {
	// super.onClick(v);
	// switch (v.getId()) {
	// case R.id.tweibo_account:
	// // tecentAccount();
	// break;
	// }
	// }

	// private void tecentAccount() {
	// if (iftecentAccounttie) {
	// builder = new CustomDialog.Builder(AccountManageActivity.this);
	// builder.setTitle("解除绑定")
	// .setMessage(
	// "是否解除绑定" + "\n\n" + "您的腾讯微博帐号解除绑定后，再次分享时需要重新授权。")
	// .setNegativeButton("取消",
	// new DialogInterface.OnClickListener() {
	// @Override
	// public void onClick(DialogInterface dialog,
	// int which) {
	// dialog.dismiss();
	// }
	// })
	// .setPositiveButton("确定",
	// new DialogInterface.OnClickListener() {
	// @Override
	// public void onClick(DialogInterface dialog,
	// int which) {
	// if (dialog != null)
	// dialog.dismiss();
	// TecentTokenKeeper.clear(mContext);
	// tweiboLogo.setImageResource(R.drawable.umeng_socialize_tx_off);
	// iftecentAccounttie = false;
	// }
	// });
	// dialog = builder.create();
	// dialog.show();
	//
	// } else {
	// oAuth=new OAuthV2(Constants.TECENT_REDIRECT_URL);
	// oAuth.setClientId(Constants.APP_KEY);
	// oAuth.setClientSecret(Constants.APP_SECRET);
	// //关闭OAuthV2Client中的默认开启的QHttpClient。
	// OAuthV2Client.getQHttpClient().shutdownConnection();
	// Intent intent = new Intent(AccountManageActivity.this,
	// OAuthV2AuthorizeWebView.class);//创建Intent，使用WebView让用户授权
	// intent.putExtra("oauth", oAuth);
	// startActivityForResult(intent,2);
	// }
	// }
	//
	//
	//
	// @Override
	// protected void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// super.onActivityResult(requestCode, resultCode, data);
	// if (requestCode==2) {
	// if (resultCode==OAuthV2AuthorizeWebView.RESULT_CODE) {
	// oAuth=(OAuthV2) data.getExtras().getSerializable("oauth");
	// if (oAuth.getStatus() == 0) {
	// Toast.makeText(getApplicationContext(), "登陆成功",
	// Toast.LENGTH_SHORT).show();
	// iftecentAccounttie = true;
	// tweiboLogo.setImageResource(R.drawable.umeng_socialize_tx_on);
	// TecentTokenKeeper.keepOAuthV2(mContext, oAuth);
	// }
	// }
	// }
	// }

}
