package com.lunary.owerwallet.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lunary.owerwallet.MyApplication;
import com.lunary.owerwallet.R;
import com.lunary.owerwallet.adapter.PinAdapter;
import com.lunary.owerwallet.model.BaseBean;
import com.lunary.owerwallet.model.HttpCallback;
import com.lunary.owerwallet.model.TokentransferBean;
import com.lunary.owerwallet.utils.Const;
import com.lunary.owerwallet.utils.NetWorkUtil;
import com.lunary.owerwallet.utils.Utils;
import com.lunary.owerwallet.weight.SectionPinListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;

/**
 * Created by Administrator on 2018/8/11.
 */
public class TransferListActivity extends Activity {
    private ArrayList<TokentransferBean> tokenInfos = new ArrayList<>();
    ListView listView;
    PinAdapter mAdapter;
    private BGARefreshLayout mRefreshLayout;
    private Context mContext;
    private ImageView back;
    private TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transferlist);
        MyApplication.addActivity(this);
        mContext = this;
        listView = (ListView) findViewById(R.id.list);
        mRefreshLayout = (BGARefreshLayout) findViewById(R.id.refreshLayout);
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        mAdapter = new PinAdapter(this,tokenInfos);
        listView.setAdapter(mAdapter);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title.setText(Const.mFullWallet.getWalletName());
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

        mRefreshLayout.beginRefreshing();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TokentransferBean tokentransferBean = tokenInfos.get(position);
                Intent intent = new Intent(mContext,TransferInfoActivity.class);
                intent.putExtra("Tokentransfer",tokentransferBean);
                startActivity(intent);
            }
        });
    }
    private boolean isRequest = false;

    private void getTransferList(final int page){
        if (isRequest){
            return;
        }
        isRequest = true;
        NetWorkUtil.getTransferList(page, "", new HttpCallback() {
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
                        ArrayList<TokentransferBean> tokentransferBeen = ret.getData();
                        if (tokentransferBeen != null && tokentransferBeen.size()>0){
                            tokenInfos.addAll(ret.getData());
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
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
