package com.quickbird.speedtest.gui.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.quickbird.speedtest.R;
import com.quickbird.speedtest.gui.adapter.SpeedListAdapter;
import com.quickbird.speedtest.gui.view.CustomDialog;
import com.quickbird.speedtestengine.SpeedValue;
import com.quickbird.speedtestengine.storage.database.SpeedDBManager;
import com.quickbird.speedtestengine.utils.DebugUtil;
import com.umeng.analytics.MobclickAgent;

public class SpeedHistoryActivity extends BaseActivity {

    private Context mContext = SpeedHistoryActivity.this;
    private ListView speedValuesListview;
    private TypedArray netWorkPic, medalPic;
    private SpeedListAdapter speedListAdapter;
    private SpeedDBManager speedDBManager = new SpeedDBManager(mContext);
    private List<SpeedValue> speedValues;// 用于保存查询出的测速记录
    private Button speedtestButton;
    private TextView historyNum;
    private ImageView deleteAll;
    private int refreshTest = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        netWorkPic = getResources().obtainTypedArray(R.array.network_pic_array);
        medalPic = getResources().obtainTypedArray(R.array.smallmetal_array);
        MobclickAgent.onEvent(this, "cht");
        try {
        	Bundle resultBundle = new Bundle();
        	Intent getResult = getIntent();
        	resultBundle = getResult.getExtras();
        	if(resultBundle.getString(MoreActivity.formMore).equals(MoreActivity.formMore))
        		refreshTest = 1;
        	else 
        		refreshTest = 0;
		} catch (Exception e) {
			refreshTest = 0;
		}
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }

    private void populateFields() {
        speedValues = new ArrayList<SpeedValue>();
        speedValues = speedDBManager.getAllSpeedValues();
        if (speedValues.size() <= 0) {
            setContentView(R.layout.activity_nospeedhistory);
            speedtestButton = (Button) findViewById(R.id.speed_test_btn);
            speedtestButton.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                	if (!SpeedTestActivity.onTesting&&refreshTest == 0) {
						Base.startTest = true;
						finish();
					} else if (refreshTest == 1) {
						finish();
						Base.mTabHost.setCurrentTab(1);
					}
                }
            });
            return;
        }
        setContentView(R.layout.activity_history);
        historyNum = (TextView) findViewById(R.id.history_num);
        historyNum.setText(String.format(getResources().getString(R.string.history_num), speedValues.size()));
        deleteAll = (ImageView) findViewById(R.id.delete_all);
        deleteAll.setOnClickListener(this);
        
        speedValuesListview = (ListView) findViewById(R.id.speedValues_listview);
        speedListAdapter = new SpeedListAdapter(mContext, speedValues, netWorkPic, medalPic);
        speedValuesListview.setAdapter(speedListAdapter);
        speedListAdapter.notifyDataSetChanged();
    }
    
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
        case R.id.delete_all:
            Activity activity = SpeedHistoryActivity.this;
            while (activity.getParent() != null) {
                activity = activity.getParent();
            }
            try {
                CustomDialog dialog = new CustomDialog.Builder(activity)
                        .setTitle("删除历史记录")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        speedDBManager.deleteAllSpeedValues();
                                        speedValues = speedDBManager.getAllSpeedValues();
                                        historyNum.setText(String.format(getResources().getString( R.string.history_num), speedValues.size()));
                                        speedListAdapter = new SpeedListAdapter( mContext, speedValues, netWorkPic, medalPic);
                                        speedValuesListview.setAdapter(speedListAdapter);
                                        speedListAdapter.notifyDataSetChanged();
                                        dialog.dismiss();
											setContentView(R.layout.activity_nospeedhistory);
											speedtestButton = (Button) findViewById(R.id.speed_test_btn);
											speedtestButton.setOnClickListener(new OnClickListener() {
														@Override
													public void onClick(View v) {
														if (!SpeedTestActivity.onTesting&&refreshTest == 0) {
															Base.startTest = true;
															finish();
														} else if (refreshTest == 0) {
															finish();
															Base.mTabHost.setCurrentTab(1);
														}
													}
												});
                                    }
                                })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create();
                dialog.show();
            } catch (Exception e) {
                DebugUtil.d("AlertDialog Exception:" + e.getMessage());
            }
            MobclickAgent.onEvent(this, "shls");
            break;
        }
    }

}
