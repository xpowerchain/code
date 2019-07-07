package com.lunary.owerwallet.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lunary.owerwallet.MyApplication;
import com.lunary.owerwallet.R;
import com.lunary.owerwallet.utils.PreferenceUtil;
import com.lunary.owerwallet.utils.UpdateManager;
import com.lunary.owerwallet.utils.Utils;

import java.util.Locale;

/**
 * Created by Administrator on 2018/8/7.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class LanguageActivity extends Activity {
    private ImageView back;
    private TextView simapleChinese;
    private TextView english;
    private Context mContext;
    private String language;
    private TextView save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutlanguage);
        MyApplication.addActivity(this);
        mContext = this;
        back = (ImageView) findViewById(R.id.back);
        simapleChinese = (TextView) findViewById(R.id.simapleChinese);
        english = (TextView) findViewById(R.id.english);
        save = (TextView) findViewById(R.id.save);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        String languaget = (String) PreferenceUtil.getData("language","");
        if (languaget.equals("en")) {
            english.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.select),null);
            simapleChinese.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
        }
        else {
            // 简体中文
            english.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
            simapleChinese.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.select),null);
        }
        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                language = "en";
                english.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.select),null);
                simapleChinese.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
            }
        });
        simapleChinese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                language = "zh";
                english.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
                simapleChinese.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.select),null);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Resources resources = getResources();
                    Configuration config = resources.getConfiguration();
                    DisplayMetrics dm = resources.getDisplayMetrics();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        if (language.equals("en"))
                        {
                            config.setLocale(Locale.ENGLISH);
                        }
                        else
                        {
                            // 简体中文
                            config.setLocale(Locale.CHINESE);
                        }
                        mContext.createConfigurationContext(config);
                    }else {
                        if (language.equals("en"))
                        {
                            config.locale = Locale.ENGLISH;
                        }
                        else
                        {
                            // 简体中文
                            config.locale = Locale.CHINESE;
                        }
                        resources.updateConfiguration(config, dm);
                    }
                    // 保存设置语言的类型
                    PreferenceUtil.saveData("language", language);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    Intent intent = new Intent(mContext, SplashActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(0);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.removeActivity(this);
    }

}
