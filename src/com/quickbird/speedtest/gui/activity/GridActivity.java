package com.quickbird.speedtest.gui.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.widget.GridView;

import com.quickbird.speedtest.R;
import com.quickbird.speedtest.business.WebSite;
import com.quickbird.speedtest.gui.adapter.WebSiteAdapter;

public class GridActivity extends BaseActivity{
	
	private GridView gridView;
	private ArrayList<WebSite> webSiteList1,webSiteList2,webSiteList3,webSiteList4;
	private Integer[] wenSiteIcons = {
	            R.drawable.icon_launcher, R.drawable.icon_launcher,
	            R.drawable.icon_launcher, R.drawable.icon_launcher,
	            R.drawable.icon_launcher, R.drawable.icon_launcher,
	    };
	private String[] wenSites = {
			"网易新闻", "百度新闻",
			"凤凰新闻", "搜狐新闻",
			"腾讯新闻", "新浪新闻",
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.website_gridview);
		gridView = (GridView) findViewById(R.id.gridview);
		webSiteList1 = new ArrayList<WebSite>();
		
		for (int index = 0; index < wenSiteIcons.length; index++) {
			WebSite site = new WebSite();
			site.setChecked(false);
			site.setIcon(wenSiteIcons[index]);
			site.setName(wenSites[index]);
			webSiteList1.add(site);
		}
		
		gridView.setAdapter(new WebSiteAdapter(GridActivity.this, webSiteList1));
		
	}

}
