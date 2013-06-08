package com.quickbird.speedtest.gui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.quickbird.speedtest.R;
import com.quickbird.speedtest.gui.view.CustomDialog;
import com.quickbird.speedtestengine.SpeedValue;
import com.quickbird.speedtestengine.storage.database.SpeedDBManager;
import com.quickbird.speedtestengine.utils.SpeedValueUtil;
import com.quickbird.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

public class SpeedValueDetailActivity extends BaseActivity {

    private Context mContext = SpeedValueDetailActivity.this;
    private TextView networkDetail, dateDetail, pingDetail, speedDetail,
            locationDetail, rankDetail;
    private ImageView medalDetail;
    private SpeedValue speedValue;
    private SpeedDBManager speedDBManager = new SpeedDBManager(mContext);
    private TypedArray speedMetalPic;
    private ImageView deleteValue;
    int speedId = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);
        networkDetail = (TextView) findViewById(R.id.network_detail);
        dateDetail = (TextView) findViewById(R.id.date_detail);
        pingDetail = (TextView) findViewById(R.id.ping_detail);
        speedDetail = (TextView) findViewById(R.id.speed_detail);
        locationDetail = (TextView) findViewById(R.id.location_detail);
        rankDetail = (TextView) findViewById(R.id.rank_detail);
        medalDetail = (ImageView) findViewById(R.id.medal_detail);
        deleteValue = (ImageView) findViewById(R.id.delete_value);
        deleteValue.setOnClickListener(this);
        speedValue = new SpeedValue();
        speedMetalPic = getResources().obtainTypedArray( R.array.speedmetal_array);
        try {
            Bundle resultBundle = new Bundle();
            Intent getResult = getIntent();
            resultBundle = getResult.getExtras();
            speedId = resultBundle.getInt("speedId");
            speedValue = speedDBManager.getSpeedValueById(speedId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        networkDetail.setText(speedValue.getNetworkType());
        dateDetail.setText(SpeedValueUtil.getTime(speedValue.getTestTime()));
        pingDetail.setText(speedValue.getPing()+"ms");
        speedDetail.setText(SpeedValueUtil.getSpeed(speedValue.getDownloadSpeed()));
        locationDetail.setText(speedValue.getLocationDesc());
        rankDetail.setText(String.format(getResources().getString(R.string.detail_rank_str), 1000-speedValue.getRank()));
        medalDetail.setImageDrawable(speedMetalPic.getDrawable(SpeedValueUtil.getMetal(speedValue.getRank())));
    }
    
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch(v.getId())
        {
        case R.id.delete_value:
            CustomDialog dialog = new CustomDialog.Builder(mContext)
            .setTitle("删除历史记录")
            .setMessage("您确定要删除该历史记录吗？")
            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    boolean deleteSucess = speedDBManager.deleteBySpeedId(speedId);
                    if(deleteSucess)
                        ToastUtil.showToast(SpeedValueDetailActivity.this, "删除成功");
                    dialog.dismiss();
                    finish();
                }
            })
            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create();
            dialog.show();
            MobclickAgent.onEvent(mContext, "xqsc");
            break;
        }
    }
}
