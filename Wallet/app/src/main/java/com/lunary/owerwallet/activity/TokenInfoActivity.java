package com.lunary.owerwallet.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lunary.owerwallet.MyApplication;
import com.lunary.owerwallet.R;
import com.lunary.owerwallet.adapter.TokenTransactionAdapter;
import com.lunary.owerwallet.model.BaseBean;
import com.lunary.owerwallet.model.HttpCallback;
import com.lunary.owerwallet.model.TokenInfo;
import com.lunary.owerwallet.model.TokentransferBean;
import com.lunary.owerwallet.utils.Const;
import com.lunary.owerwallet.utils.NetWorkUtil;
import com.lunary.owerwallet.utils.Utils;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;

/**
 * Created by Administrator on 2018/8/4.
 */
public class TokenInfoActivity extends AppCompatActivity {
    private Context mContext;
    private ImageView back;
    private TextView tokenName;
    private TextView asset;
    private TextView equivalent;
    private ListView list;
    private LinearLayout transfer;
    private LinearLayout collection;
    private BGARefreshLayout mRefreshLayout;
    private ArrayList<TokentransferBean> tokenInfos = new ArrayList<>();
    private TokenTransactionAdapter tokenTransactionAdapter;
    TokenInfo tokenInfo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tokeninfo);
        MyApplication.addActivity(this);
        mContext = this;
        tokenInfo = (TokenInfo) getIntent().getSerializableExtra("TokenInfo");
        back = (ImageView) findViewById(R.id.back);
        tokenName = (TextView) findViewById(R.id.tokenName);
        asset = (TextView) findViewById(R.id.asset);
        equivalent = (TextView) findViewById(R.id.equivalent);
        list = (ListView) findViewById(R.id.list);
        transfer = (LinearLayout) findViewById(R.id.transfer);
        collection = (LinearLayout) findViewById(R.id.collection);
        mRefreshLayout = (BGARefreshLayout) findViewById(R.id.refreshLayout);
        tokenTransactionAdapter = new TokenTransactionAdapter(mContext,tokenInfos,tokenInfo.getToken_symbol());
        list.setAdapter(tokenTransactionAdapter);
        tokenName.setText(tokenInfo.getToken_symbol());
        asset.setText(Utils.getDecimalFormat(tokenInfo.getToken_num()));
        if (TextUtils.isEmpty(tokenInfo.getRate())){
            equivalent.setText("-");
        }else{
            equivalent.setText("≈ $ "+Utils.getDecimalFormat(Double.parseDouble(tokenInfo.getRate())*tokenInfo.getToken_num()));
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });
        collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,PaymentCodeActivity.class);
                intent.putExtra("Token_symbol",tokenInfo.getToken_symbol());
                startActivity(intent);
            }
        });
        transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,TransferActivity.class);
                intent.putExtra("TokenInfo",tokenInfo.getToken_symbol());
                startActivity(intent);
            }
        });
        BGARefreshViewHolder mDefineBAGRefreshWithLoadView = new BGANormalRefreshViewHolder(mContext , true);
        //设置刷新样式
        mRefreshLayout.setRefreshViewHolder(mDefineBAGRefreshWithLoadView);
        // 为BGARefreshLayout 设置代理
        mRefreshLayout.setDelegate(new BGARefreshLayout.BGARefreshLayoutDelegate() {
            @Override
            public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
                getTransferList(1);
            }

            @Override
            public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
                if (tokenInfos.size()%10 == 0){
                    getTransferList(tokenInfos.size()/10+1);
                    return true;
                }
                mRefreshLayout.endLoadingMore();
                return false;
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TokentransferBean tokentransferBean = tokenInfos.get(position);
                Intent intent = new Intent(mContext,TransferInfoActivity.class);
                intent.putExtra("Tokentransfer",tokentransferBean);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRefreshLayout.beginRefreshing();
    }

    private boolean isRequest = false;

    private void getTransferList(final int page){
        if (isRequest){
            return;
        }
        isRequest = true;
        NetWorkUtil.getTransferList(page, tokenInfo.getToken_symbol(), new HttpCallback() {
            @Override
            public void onFailure(Exception e) {
                isRequest = false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.endRefreshing();
                        mRefreshLayout.endLoadingMore();
                    }
                });
            }

            @Override
            public void onResponse(String response) {
                isRequest = false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.endRefreshing();
                        mRefreshLayout.endLoadingMore();
                    }
                });

                if (TextUtils.isEmpty(response)){
                    return;
                }
                try {
                    final BaseBean<ArrayList<TokentransferBean>> ret = new Gson().fromJson(response, new TypeToken<BaseBean<ArrayList<TokentransferBean>>>() {}.getType());
                    if (ret.getData()!=null){
                        if (page == 1){
                            tokenInfos.clear();
                        }
                        tokenInfos.addAll(ret.getData());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }catch (OutOfMemoryError e){
                    e.printStackTrace();
                }finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tokenTransactionAdapter.notifyDataSetChanged();
                        }
                    });
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
