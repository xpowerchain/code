package com.lunary.owerwallet.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.lunary.owerwallet.BaseActivity;
import com.lunary.owerwallet.MyApplication;
import com.lunary.owerwallet.R;
import com.lunary.owerwallet.utils.UpdateManager;
import com.lunary.owerwallet.utils.Utils;
/**
 * Created by Administrator on 2018/8/7.
 */
public class AboutUsActivity extends BaseActivity {
    private ImageView back;
    private TextView update;
    private TextView versionName;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        MyApplication.addActivity(this);
        mContext = this;
        back = (ImageView) findViewById(R.id.back);
        update = (TextView) findViewById(R.id.update);
        versionName = (TextView) findViewById(R.id.versionName);
        versionName.setText(getString(R.string.currentVersion)+"V "+Utils.versionName+" Build: "+Utils.versionCode);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateManager.getInstance().getUpdate(mContext);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UpdateManager.getInstance().progressIsNull();
        MyApplication.removeActivity(this);
    }

}
