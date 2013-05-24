/*******************************************************************
 * Copyright @ 2011 ChenFengYun (BeiJing) Technology LTD
 * <P>
 * ====================================================================
 * <P>
 * Project:　　　　　Speedy
 * <P>
 * FileName:　　　　 SpeedListAdapter.java
 * <P>
 * Description:　　
 * <P>
 * @Author:　　　　　 xd.liu
 * <P>
 * @Create Date:　  2012-7-28  下午1:30:47
 ********************************************************************/
package com.quickbird.speedtest.gui.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.quickbird.speedtest.R;
import com.quickbird.speedtest.gui.activity.SpeedValueDetailActivity;
import com.quickbird.speedtestengine.SpeedValue;
import com.quickbird.speedtestengine.utils.DebugUtil;
import com.quickbird.speedtestengine.utils.SpeedValueUtil;
import com.umeng.analytics.MobclickAgent;

public class SpeedListAdapter extends BaseAdapter implements OnClickListener{

    private Context mContext;
    private List<SpeedValue> speedValues;// 用于保存查询出的测速记录
    private TypedArray netWorkPic, medalPic;

    /**
     * @param applicationContext
     * @param mapList
     */
    public SpeedListAdapter(Context context,
            List<SpeedValue> speedValues,
            TypedArray netWorkPic, TypedArray medalPic) {
        this.mContext = context;
        this.speedValues = speedValues;
        this.netWorkPic = netWorkPic;
        this.medalPic = medalPic;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return speedValues.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return speedValues.get(position);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            SpeedValue speedValue = speedValues.get(position);
            ViewEntry view;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.speed_historyitem, null);
                view = new ViewEntry();
                view.network = (ImageView) convertView.findViewById(R.id.network);
                view.date = (TextView) convertView.findViewById(R.id.date);
                view.ping = (TextView) convertView.findViewById(R.id.ping);
                view.speed = (TextView) convertView.findViewById(R.id.speed);
                view.medal = (ImageView) convertView.findViewById(R.id.medal);
                convertView.setTag(view);
            } else {
                view = (ViewEntry) convertView.getTag();
            }
            view.network.setImageDrawable(netWorkPic.getDrawable(SpeedValueUtil.getNetWorkType(speedValue.getNetworkType())));
            view.date.setText(SpeedValueUtil.getTime(speedValue.getTestTime()));
            view.ping.setText(speedValue.getPing() + "ms");
            view.speed.setText(SpeedValueUtil.getSpeed(speedValue.getDownloadSpeed()));
            view.medal.setImageDrawable(medalPic.getDrawable(SpeedValueUtil.getMetal(speedValue.getRank())));
//            view.medal.setImageDrawable(medalPic.getDrawable(SpeedValueUtil.getSpeedLevel(
//                            SpeedValueUtil.getNetWorkType(speedMap.get("networkType").toString()),
//                            SpeedValueUtil.getSpeedNum(speedMap.get("downloadSpeed").toString()))));
            view.speed.setTag(speedValue.getSpeedId()+"");
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            DebugUtil.e("getView.Exception: " + e.getMessage());
        }
        convertView.setOnClickListener(this);
        return convertView;
    }

    private class ViewEntry {
        ImageView network;
        TextView date;
        TextView ping;
        TextView speed;
        ImageView medal;
    }

    @Override
    public void onClick(View v) {
        Integer speedId = 0;
        try {
            ViewEntry view = (ViewEntry) v.getTag();
            speedId = Integer.parseInt(view.speed.getTag().toString());
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            DebugUtil.e("getView.Exception: " + e.getMessage());
        }
        Intent intent = new Intent();
        Bundle speedResult = new Bundle();
        speedResult.putInt("speedId", speedId);
        intent.putExtras(speedResult);
        intent.setClass(mContext, SpeedValueDetailActivity.class);
        mContext.startActivity(intent);
        MobclickAgent.onEvent(mContext, "lsxq");
    }
    
}
