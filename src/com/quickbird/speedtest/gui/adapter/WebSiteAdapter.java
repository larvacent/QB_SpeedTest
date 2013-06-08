/*******************************************************************
 * Copyright @ 2013 ChenFengYun (BeiJing) Technology LTD
 * <P>
 * ====================================================================
 * <P>
 * Project:　　　　　GameBooster
 * <P>
 * FileName:　　　　 GameAdapter.java
 * <P>
 * Description:　　
 * <P>
 * @Author:　　　　　 yang.li
 * <P>
 * @Create Date:　 2013-3-12 下午2:49:25
 ********************************************************************/
package com.quickbird.speedtest.gui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.quickbird.enums.WiteSiteTestStatus;
import com.quickbird.speedtest.R;
import com.quickbird.speedtest.business.WebSite;

/**
 * @author liyang
 * 
 */
public class WebSiteAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<WebSite> mWebSiteList;

	private class ViewHolder {
		private ImageView speedTag;
		private ImageView icon;
		private TextView name;
		private ImageView check;
		private ImageView mask;
		private ImageView load;
	}

	public WebSiteAdapter(Context c, ArrayList<WebSite> webSiteList) {
		mContext = c;
		mWebSiteList = webSiteList;
	}

	public int getCount() {
		return mWebSiteList.size();
	}

	public Object getItem(int position) {
		return mWebSiteList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		WebSite webSite = mWebSiteList.get(position);
		if (null == convertView) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.website_gridview_item, null);
			holder = new ViewHolder();
			holder.speedTag = (ImageView) convertView.findViewById(R.id.website_item_tag);
			holder.icon = (ImageView) convertView.findViewById(R.id.website_item_icon);
			holder.name = (TextView) convertView.findViewById(R.id.website_item_name);
			holder.check = (ImageView) convertView.findViewById(R.id.website_item_check);
			holder.mask = (ImageView) convertView.findViewById(R.id.website_item_mask);
			holder.load = (ImageView) convertView.findViewById(R.id.website_item_load);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.icon.setImageResource(webSite.getIcon());
		holder.name.setText(webSite.getName());
		if (webSite.getStatus() == WiteSiteTestStatus.Wait) {
			if (webSite.isChecked())
				holder.check.setImageResource(R.drawable.ic_selected_green);

			if (!webSite.isChecked())
				holder.check.setImageResource(R.drawable.ic_deselected);
			holder.speedTag.setVisibility(View.INVISIBLE);
			holder.mask.setVisibility(View.GONE);
			holder.load.setVisibility(View.GONE);
			return convertView;
		}
		if(webSite.getStatus() == WiteSiteTestStatus.Test)
		{
			holder.check.setVisibility(View.GONE);
			holder.speedTag.setVisibility(View.INVISIBLE);
			holder.mask.setVisibility(View.VISIBLE);
			holder.load.setVisibility(View.VISIBLE);
			holder.load.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.data_loading_rotate));
			return convertView; 
		}
		if (webSite.getStatus() == WiteSiteTestStatus.Finish) {
			holder.speedTag.setVisibility(View.VISIBLE);
			holder.mask.setVisibility(View.GONE);
			holder.load.clearAnimation();
			holder.load.setVisibility(View.GONE);
			holder.check.setVisibility(View.VISIBLE);
			if (webSite.isChecked())
				holder.check.setImageResource(R.drawable.ic_selected_green);

			if (!webSite.isChecked())
				holder.check.setImageResource(R.drawable.ic_deselected);
			switch (webSite.getDegree()) {
			case -1:
				holder.speedTag.setVisibility(View.INVISIBLE);
				break;
			case 0:
				holder.speedTag.setImageResource(R.drawable.ic_mark_fail);
				break;
			case 1:
				holder.speedTag.setImageResource(R.drawable.ic_mark_hyperslow);
				break;
			case 2:
				holder.speedTag.setImageResource(R.drawable.ic_mark_slow);
				break;
			case 3:
				holder.speedTag.setVisibility(View.VISIBLE);
				holder.speedTag.setImageResource(R.drawable.ic_mark_fast);
				break;
			case 4:
				holder.speedTag.setImageResource(R.drawable.ic_mark_furious);
				break;
			default:
				break;
			}
			return convertView;
		}
		if(webSite.getStatus() == WiteSiteTestStatus.Error)
		{
			holder.speedTag.setVisibility(View.VISIBLE);
			holder.mask.setVisibility(View.GONE);
			holder.load.setVisibility(View.GONE);
			holder.load.clearAnimation();
			holder.check.setVisibility(View.VISIBLE);
			if (webSite.isChecked())
				holder.check.setImageResource(R.drawable.ic_selected_green);

			if (!webSite.isChecked())
				holder.check.setImageResource(R.drawable.ic_deselected);
			holder.speedTag.setImageResource(R.drawable.ic_mark_fail);
			return convertView;
		}
		return convertView;
	}

}
