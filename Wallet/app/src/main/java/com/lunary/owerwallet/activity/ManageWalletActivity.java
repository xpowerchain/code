package com.lunary.owerwallet.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.lunary.owerwallet.MyApplication;
import com.lunary.owerwallet.R;
import com.lunary.owerwallet.adapter.ManageWalletAdapter;
import com.lunary.owerwallet.model.FullWallet;
import com.lunary.owerwallet.utils.WalletStorage;

import java.util.ArrayList;

import jnr.ffi.annotations.In;

/**
 * Created by Administrator on 2018/8/4.
 */
public class ManageWalletActivity extends Activity {
    private LinearLayout mCreateWallet,mLoadWallet;
    private Context mContext;
    private ListView mWalletListView;
    ImageView back;
    private ManageWalletAdapter manageWalletAdapter;
    private ArrayList<FullWallet> wallets = new ArrayList<>();
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managewallet);
        MyApplication.addActivity(this);
        mContext = this;
        mCreateWallet = (LinearLayout) findViewById(R.id.createwallet);
        mLoadWallet  = (LinearLayout) findViewById(R.id.loadwallet);
        mWalletListView = (ListView) findViewById(R.id.list);
        back = (ImageView) findViewById(R.id.back);
        manageWalletAdapter = new ManageWalletAdapter(mContext,wallets);
        mWalletListView.setAdapter(manageWalletAdapter);
        mCreateWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext,CreateWalletActivity.class));
            }
        });
        mLoadWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext,LoadWalletActivity.class));
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mWalletListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FullWallet fullWallet = (FullWallet) manageWalletAdapter.getItem(position);
                Intent intent = new Intent(mContext,WalletInfoActivity.class);
                intent.putExtra("FullWallet",fullWallet);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        manageWalletAdapter = null;
        wallets = WalletStorage.getInstance(getApplicationContext()).get();
        manageWalletAdapter = new ManageWalletAdapter(mContext,wallets);
        mWalletListView.setAdapter(manageWalletAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.removeActivity(this);
    }
}
