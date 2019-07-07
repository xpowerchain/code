package com.lunary.owerwallet.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.lunary.owerwallet.R;
import com.lunary.owerwallet.service.MQTTService;
import com.lunary.owerwallet.utils.PreferenceUtil;
import com.lunary.owerwallet.utils.WalletStorage;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.util.Locale;

/**
 * Created by Administrator on 2018/7/31.
 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        try {
            Resources resources = getResources();
            Configuration config = resources.getConfiguration();
            DisplayMetrics dm = resources.getDisplayMetrics();
            String language = (String) PreferenceUtil.getData("language","");
            if (TextUtils.isEmpty(language)){
                String defaultLanguage = Locale.getDefault().getLanguage();
                if (defaultLanguage.contains("zh")){
                    language = "zh";
                }else {
                    language = "en";
                }
            }
            if (language.equals("en")) {
                config.locale = Locale.ENGLISH;
            }
            else {
                // 简体中文
                config.locale = Locale.CHINESE;
            }
            resources.updateConfiguration(config, dm);
            PreferenceUtil.saveData("language",language);
        }catch (Exception e){
            e.printStackTrace();
        }
//        startService(new Intent(SplashActivity.this, MQTTService.class));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (WalletStorage.getInstance(getApplicationContext()).get().size()>0){
                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                }else {
                    startActivity(new Intent(SplashActivity.this,EnterWalletMethodsActivity.class));
                }
                finish();
            }
        },3 * 1000);


    }
}
