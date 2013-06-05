package com.quickbird.speedtest.gui.activity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quickbird.controls.Constants;
import com.quickbird.enums.SpeedTestError;
import com.quickbird.enums.WiteSiteTestStatus;
import com.quickbird.speedtest.R;
import com.quickbird.speedtest.business.WebSite;
import com.quickbird.speedtest.gui.adapter.MyPagerAdapter;
import com.quickbird.speedtest.gui.adapter.WebSiteAdapter;
import com.quickbird.speedtest.onkeyshare.ShareAllGird;
import com.quickbird.speedtestengine.TestParameters;
import com.quickbird.speedtestengine.TestParametersLatency;
import com.quickbird.speedtestengine.TestTaskCallbacks;
import com.quickbird.speedtestengine.tasks.LatencyTestTask;
import com.quickbird.speedtestengine.tasks.TestTask;
import com.quickbird.utils.ScreenShotUtil;

public class WebSiteTestActivity extends BaseActivity {

	private Button webSiteTestBtn;
	private ImageButton buttonShare;
	private LinearLayout websiteTab1, websiteTab2, websiteTab3, websiteTab4;// 页卡
	private LinearLayout buttonShareLayout;
	private ImageView imageView[] = new ImageView[4];
	private TextView textView[] = new TextView[4];
	private TypedArray websiteClassifyArray, websiteClassifyFocusArray;

	private ImageView cursor;// 动画图片
	private List<View> listViews; // Tab页面列表
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度
	private ViewPager mPager;// 页卡内容
	private Context context;

	private ArrayList<WebSite> webSiteList1, webSiteList2, webSiteList3, webSiteList4;
	private ArrayList<ArrayList<WebSite>> webSiteLists;
	private ArrayList<WebSiteAdapter> webSiteAdapters;
	private TestTask mCurrentTestTask = null;

	private Integer[] webSiteIcons1 = { R.drawable.baidu, R.drawable.weibo,
			R.drawable.wangyi_news, R.drawable.youku_video,
			R.drawable.tencent_weibo, R.drawable.taobao, };
	private Integer[] webSiteIcons2 = { R.drawable.weibo,
			R.drawable.tencent_weibo, R.drawable.zone, R.drawable.renren,
			R.drawable.kaixin, R.drawable.douban, };
	private Integer[] webSiteIcons3 = { R.drawable.taobao, R.drawable.jingdong,
			R.drawable.baidu_shopping, R.drawable.meilishuo, R.drawable.jumei,
			R.drawable.yamaxun, };
	private Integer[] webSiteIcons4 = { R.drawable.youku_video,
			R.drawable.tudou_video, R.drawable.souhu_video,
			R.drawable.tecent_video, R.drawable.sina_video, };
	private String[] webSites1 = { "百度", "新浪微博", "网易新闻", "优酷视频", "腾讯微博", "淘宝", };
	private String[] webSites2 = { "新浪微博", "腾讯微博", "QQ空间", "人人网", "开心网", "豆瓣", };
	private String[] webSites3 = { "淘宝", "京东商城", "百度购物", "美丽说", "聚美优品", "亚马逊", };
	private String[] webSites4 = { "优酷网", "土豆网", "搜狐视频", "腾讯视频", "新浪视频", };
	
	private String[] shareStr = {
			"【浏览网页快不快，测试一下就出来】您手机经常访问的网站快不快？用网站测速一目了然！",
			"【浏览网页快不快，测试一下就出来】刷微博，上人人，哪个社交网站更给力？用网站测速一目了然！",
			"【浏览网页快不快，测试一下就出来】网络购物，不快不行，您要上的购物网站快吗？",
			"【浏览网页快不快，测试一下就出来】看视频，上公开课，哪个视频网站更给力？用网站测速一目了然！"
	};

	private String[] websitesAddress1 = { "http://www.baidu.com",
			"http://weibo.cn/pub/", "http://3g.163.com/touch/",
			"http://m.youku.com/smartphone/",
			"http://t.qq.com/",
			"http://m.taobao.com/?sprefer=sypc00" };

	private String[] websitesAddress2 = {
			"http://weibo.cn/pub/",
			"http://t.qq.com/",
			"http://pt.3g.qq.com/s?aid=nLoginqz&sid=AYqtsxg-ykt0WrZ2kiUXfnyn&KqqWap_Act=3&g_ut=2&go_url=http%3A%2F%2Fqzone.z.qq.com%2Findex.jsp",
			"http://mt.renren.com/login?redirect=http%3A%2F%2Fmt.renren.com%2Fhome%3Fcp_config%3D0%26from%3D8000103&from=8000103",
			"http://iphone.kaixin001.com/?flag=0&from=plat&noredirect=1",
			"http://m.douban.com/login?redir=%2F%3Fsession%3Db24a8570&session=d4495868", };

	private String[] websitesAddress3 = {
			"http://m.taobao.com/?sprefer=sypc00", "http://m.jd.com/",
			"http://gouwu.baidu.com/wise/", "http://wap.meilishuo.com/",
			"http://m.jumei.com/?fr=baidu_wap_ppzq_bt",
			"http://www.amazon.cn/gp/aw/h.html" };

	private String[] websitesAddress4 = { "http://m.youku.com/smartphone/",
			"http://m.tudou.com/touch/index", "http://m.tv.sohu.com/",
			"http://v.qq.com/", "http://video.sina.cn/" };
	
	private final int WEBSITE_TEST = 0;
	
	private ArrayList<WebSite> testWebSite;
	private int testId =  0;   // 正在测速中的页卡编号
	private int testIndex = 0; // 正在测速中的页卡编号

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch(msg.what)
			{
			case WEBSITE_TEST:
				mCurrentTestTask = new LatencyTestTask(new TestTaskCallbacks() {
					
					@Override
					public void onTestUpdate(TestParameters[] paramArrayOfTestParameters) {
						
					}
					
					@Override
					public void onTestFailed(SpeedTestError paramSpeedTestError,
							TestParameters paramTestParameters) {
						testWebSite.get(testId).setStatus(WiteSiteTestStatus.Error);
						webSiteAdapters.get(testIndex).notifyDataSetChanged();
						if (testId++ < testWebSite.size() - 1) {
							handler.sendEmptyMessage(WEBSITE_TEST);
						} else {
							testId = 0;
							webSiteTestBtn.setVisibility(View.VISIBLE);
						}
					}

					@Override
					public void onTestComplete(TestParameters paramTestParameters) {
						TestParametersLatency localTestParametersLatency = (TestParametersLatency) paramTestParameters;
						testWebSite.get(testId).setPing(localTestParametersLatency.getPing());
						testWebSite.get(testId).setDegree(getDegree(localTestParametersLatency.getPing()));
						testWebSite.get(testId).setStatus(WiteSiteTestStatus.Finish);
						webSiteAdapters.get(testIndex).notifyDataSetChanged();
						if (testId++ < testWebSite.size() - 1) {
							handler.sendEmptyMessage(WEBSITE_TEST);
						} else {
							testId = 0;
							webSiteTestBtn.setVisibility(View.VISIBLE);
							buttonShare.setVisibility(View.VISIBLE);
						}
					}
					
					@Override
					public void onBeginTest() {
						testWebSite.get(testId).setStatus(WiteSiteTestStatus.Test);
						webSiteAdapters.get(testIndex).notifyDataSetChanged();
						webSiteTestBtn.setVisibility(View.GONE);
					}
				});
				try {
					mCurrentTestTask.testStart(new URL(testWebSite.get(testId).getAddress()));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				break;
			}
		};
	};
	
	private int getDegree(int ping) {
		if (ping > 500)
			return 1;
		if (ping > 300)
			return 2;
		if (ping > 200)
			return 3;
		if (ping > 100)
			return 4;
		if (ping > 0)
			return 4;
		return 4;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webtest);
		context = WebSiteTestActivity.this;
		webSiteTestBtn = (Button) findViewById(R.id.website_test);
		webSiteTestBtn.setOnClickListener(this);
		InitWebSite();
		InitImageView();
		InitCursorView();
		InitViewPager();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.website_test:
			startTest();
			break;
		case R.id.button_share_layout:
		case R.id.button_share_website:
			getCaptureBitmap();
			showGrid(false);
			break;
		}
	}
	
	private  void getCaptureBitmap() {
		LinearLayout captureLayout = (LinearLayout) findViewById(R.id.capture_layout);
        ScreenShotUtil.captureView(captureLayout);
    }
	

	private void startTest() {
		testWebSite = new ArrayList<WebSite>();
		testWebSite.clear();
		
		testIndex = currIndex;
		for (int i = 0; i < webSiteLists.get(testIndex).size(); i++) {
			if (webSiteLists.get(testIndex).get(i).isChecked())
				testWebSite.add(webSiteLists.get(testIndex).get(i));
		}
		if (testWebSite.size() > 0)
			handler.sendEmptyMessage(WEBSITE_TEST);
	}

	private void InitWebSite() {
		webSiteLists = new ArrayList<ArrayList<WebSite>>();
		webSiteList1 = new ArrayList<WebSite>();
		webSiteList2 = new ArrayList<WebSite>();
		webSiteList3 = new ArrayList<WebSite>();
		webSiteList4 = new ArrayList<WebSite>();
		webSiteAdapters = new ArrayList<WebSiteAdapter>();
		for (int index = 0; index < webSiteIcons1.length; index++) {
			WebSite site = new WebSite();
			site.setId(index);
			site.setChecked(true);
			site.setIcon(webSiteIcons1[index]);
			site.setName(webSites1[index]);
			site.setAddress(websitesAddress1[index]);
			site.setStatus(WiteSiteTestStatus.Wait);
			webSiteList1.add(site);
		}
		for (int index = 0; index < webSiteIcons2.length; index++) {
			WebSite site = new WebSite();
			site.setId(index);
			site.setChecked(true);
			site.setIcon(webSiteIcons2[index]);
			site.setName(webSites2[index]);
			site.setAddress(websitesAddress2[index]);
			site.setStatus(WiteSiteTestStatus.Wait);
			webSiteList2.add(site);
		}
		for (int index = 0; index < webSiteIcons3.length; index++) {
			WebSite site = new WebSite();
			site.setId(index);
			site.setChecked(true);
			site.setIcon(webSiteIcons3[index]);
			site.setName(webSites3[index]);
			site.setAddress(websitesAddress3[index]);
			site.setStatus(WiteSiteTestStatus.Wait);
			webSiteList3.add(site);
		}
		for (int index = 0; index < webSiteIcons4.length; index++) {
			WebSite site = new WebSite();
			site.setId(index);
			site.setChecked(true);
			site.setIcon(webSiteIcons4[index]);
			site.setName(webSites4[index]);
			site.setAddress(websitesAddress4[index]);
			site.setStatus(WiteSiteTestStatus.Wait);
			webSiteList4.add(site);
		}
		webSiteLists.add(webSiteList1);
		webSiteLists.add(webSiteList2);
		webSiteLists.add(webSiteList3);
		webSiteLists.add(webSiteList4);
		webSiteAdapters.add(new WebSiteAdapter(context, webSiteList1));
		webSiteAdapters.add(new WebSiteAdapter(context, webSiteList2));
		webSiteAdapters.add(new WebSiteAdapter(context, webSiteList3));
		webSiteAdapters.add(new WebSiteAdapter(context, webSiteList4));
	}

	/**
	 * 初始化动画
	 */
	private void InitCursorView() {
		cursor = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(),
				R.drawable.savetraffic_cur).getWidth();// 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		offset = (screenW / 4 - bmpW) / 2;// 计算偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);// 设置动画初始位置
	}

	/**
	 * 初始化ViewPager
	 */
	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.vPager);
		listViews = new ArrayList<View>();
		LayoutInflater mInflater = getLayoutInflater();
		listViews.add(mInflater.inflate(R.layout.website_gridview, null));
		listViews.add(mInflater.inflate(R.layout.website_gridview, null));
		listViews.add(mInflater.inflate(R.layout.website_gridview, null));
		listViews.add(mInflater.inflate(R.layout.website_gridview, null));

		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());

		textView[currIndex].setTextColor(getResources().getColor(
				R.color.text_red));
		imageView[currIndex].setImageDrawable(websiteClassifyFocusArray
				.getDrawable(currIndex));
		setGridView(currIndex);
	}

	/**
	 * 初始化头标
	 */
	private void InitImageView() {
		websiteTab1 = (LinearLayout) findViewById(R.id.website_tab1);
		websiteTab2 = (LinearLayout) findViewById(R.id.website_tab2);
		websiteTab3 = (LinearLayout) findViewById(R.id.website_tab3);
		websiteTab4 = (LinearLayout) findViewById(R.id.website_tab4);

		imageView[0] = (ImageView) findViewById(R.id.image_view1);
		imageView[1] = (ImageView) findViewById(R.id.image_view2);
		imageView[2] = (ImageView) findViewById(R.id.image_view3);
		imageView[3] = (ImageView) findViewById(R.id.image_view4);

		textView[0] = (TextView) findViewById(R.id.text_view1);
		textView[1] = (TextView) findViewById(R.id.text_view2);
		textView[2] = (TextView) findViewById(R.id.text_view3);
		textView[3] = (TextView) findViewById(R.id.text_view4);

		websiteTab1.setOnClickListener(new MyOnClickListener(0));
		websiteTab2.setOnClickListener(new MyOnClickListener(1));
		websiteTab3.setOnClickListener(new MyOnClickListener(2));
		websiteTab4.setOnClickListener(new MyOnClickListener(3));
		
		buttonShareLayout = (LinearLayout) findViewById(R.id.button_share_layout);
		buttonShare = (ImageButton) findViewById(R.id.button_share_website);
		buttonShare.setOnClickListener(this);
		buttonShareLayout.setOnClickListener(this);
		buttonShare.setVisibility(View.GONE);

		websiteClassifyArray = getResources().obtainTypedArray(R.array.website_classify_array);
		websiteClassifyFocusArray = getResources().obtainTypedArray(R.array.website_classify_focus_array);
	}

	/**
	 * 头标点击监听
	 */
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}
	};

	/**
	 * 页卡切换监听
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
		int two = one * 2;// 页卡1 -> 页卡3 偏移量
		int three = one * 3;

		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, 0, 0, 0);
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(three, 0, 0, 0);
				}
				break;
			case 1:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, one, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(three, one, 0, 0);
				}
				break;
			case 2:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, two, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(three, two, 0, 0);
				}
				break;
			case 3:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, three, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, three, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, three, 0, 0);
				}
				break;
			}
			imageView[currIndex].setImageDrawable(websiteClassifyArray.getDrawable(currIndex));
			imageView[arg0].setImageDrawable(websiteClassifyFocusArray.getDrawable(arg0));
			textView[currIndex].setTextColor(getResources().getColor(R.color.text_black));
			textView[arg0].setTextColor(getResources().getColor(R.color.text_red));

			currIndex = arg0;
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(100);
			cursor.startAnimation(animation);

			setGridView(currIndex);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	private void setGridView(final int index) {
		GridView gridView = (GridView) listViews.get(index).findViewById(
				R.id.gridview);
		gridView.setAdapter(webSiteAdapters.get(index));
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int positon, long arg3) {
				if (!webSiteLists.get(index).get(positon).isChecked())
					webSiteLists.get(index).get(positon).setChecked(true);
				else
					webSiteLists.get(index).get(positon).setChecked(false);
				webSiteAdapters.get(index).notifyDataSetChanged();
			}
		});
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
			i.putExtra("titleUrl", "www.quickbird.com");
			// text是分享文本，所有平台都需要这个字段
			i.putExtra("text", shareStr[currIndex]);
			// imagePath是本地的图片路径，所有平台都支持这个字段，不提供，则表示不分享图片
			i.putExtra("imagePath", Constants.PIC_PRE_PATH_NAME);
			// url仅在微信（包括好友和朋友圈）中使用，否则可以不提供
			i.putExtra("url", "www.quickbird.com");
			// thumbPath是缩略图的本地路径，仅在微信（包括好友和朋友圈）中使用，否则可以不提供
			i.putExtra("thumbPath", Constants.PIC_PRE_PATH_NAME);
			// appPath是待分享应用程序的本地路劲，仅在微信（包括好友和朋友圈）中使用，否则可以不提供
			i.putExtra("appPath", Constants.PIC_PRE_PATH_NAME);
			// comment是我对这条分享的评论，仅在人人网和QQ空间使用，否则可以不提供
			i.putExtra("comment", context.getString(R.string.share));
			// site是分享此内容的网站名称，仅在QQ空间使用，否则可以不提供
			i.putExtra("site", context.getString(R.string.app_name));
			// siteUrl是分享此内容的网站地址，仅在QQ空间使用，否则可以不提供
			i.putExtra("siteUrl", "www.quickbird.com");
			
			// 是否直接分享
			i.putExtra("silent", silent);
			context.startActivity(i);
		}

}
