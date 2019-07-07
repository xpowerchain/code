package com.lunary.owerwallet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.lunary.owerwallet.MyApplication;
import com.lunary.owerwallet.R;

/**
 * Created by Administrator on 2018/7/31.
 */
public class EnterWalletMethodsActivity extends Activity {

    private Button mCreateWallet,mLoadWallet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enterwalletmethods);
        MyApplication.addActivity(this);
        mCreateWallet = (Button) findViewById(R.id.createwallet);
        mLoadWallet  = (Button) findViewById(R.id.loadwallet);
        mCreateWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EnterWalletMethodsActivity.this,CreateWalletActivity.class));
            }
        });
        mLoadWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EnterWalletMethodsActivity.this,LoadWalletActivity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.removeActivity(this);
    }
}
