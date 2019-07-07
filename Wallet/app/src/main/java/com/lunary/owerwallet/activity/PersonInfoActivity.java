package com.lunary.owerwallet.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lunary.owerwallet.MyApplication;
import com.lunary.owerwallet.R;
import com.lunary.owerwallet.utils.PreferenceUtil;

import java.util.Locale;

/**
 * Created by Administrator on 2018/8/7.
 */
public class PersonInfoActivity extends Activity {
    private LinearLayout managerwallet,transferList;
    private TextView aboutUs;
    private TextView mSetting;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Resources resources = getResources();
            Configuration config = resources.getConfiguration();
            DisplayMetrics dm = resources.getDisplayMetrics();
            String language = (String) PreferenceUtil.getData("language","zh");
            if (language.equals("en")) {
                config.locale = Locale.ENGLISH;
            }
            else {
                // 简体中文
                config.locale = Locale.CHINESE;
            }
            resources.updateConfiguration(config, dm);
        }catch (Exception e){
            e.printStackTrace();
        }
        setContentView(R.layout.activity_personinfo);
        MyApplication.addActivity(this);
        mContext = this;
        managerwallet = (LinearLayout) findViewById(R.id.managerwallet);
        transferList = (LinearLayout) findViewById(R.id.transferList);
        aboutUs = (TextView) findViewById(R.id.aboutUs);
        mSetting = (TextView) findViewById(R.id.settings);

        managerwallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext,ManageWalletActivity.class));
            }
        });
        transferList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext,TransferListActivity.class));
            }
        });
        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext,AboutUsActivity.class));
            }
        });

        mSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext,SettingActivity.class));
            }
        });
    }
    long exitTime = 0;

    @Override
    public void onBackPressed() {

        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            super.onBackPressed();
            finish();
            MyApplication.closeAllActivity();
            System.exit(0);
        }
    }
}
