package com.quickbird.speedtest.gui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.quickbird.speedtest.R;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        TextView command = (TextView) findViewById(R.id.speedtest_command);
        command.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://dl.quickbird.com/android/latest.apk");
                startActivity(new Intent(Intent.ACTION_VIEW,uri));
            }
        });

    }

}
