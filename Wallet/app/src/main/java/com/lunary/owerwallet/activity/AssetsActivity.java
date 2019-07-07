package com.lunary.owerwallet.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lunary.owerwallet.MyApplication;
import com.lunary.owerwallet.R;
import com.lunary.owerwallet.adapter.TokenInfoAdapter;
import com.lunary.owerwallet.model.FullWallet;
import com.lunary.owerwallet.model.BaseBean;
import com.lunary.owerwallet.model.HttpCallback;
import com.lunary.owerwallet.model.TokenInfo;
import com.lunary.owerwallet.service.MQTTService;
import com.lunary.owerwallet.utils.Const;
import com.lunary.owerwallet.utils.NetWorkUtil;
import com.lunary.owerwallet.utils.PreferenceUtil;
import com.lunary.owerwallet.utils.UpdateManager;
import com.lunary.owerwallet.utils.Utils;
import com.lunary.owerwallet.utils.WalletStorage;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;

/**
 * Created by Administrator on 2018/8/2.
 */
public class AssetsActivity extends Activity implements BGARefreshLayout.BGARefreshLayoutDelegate{

    private ImageView asset_menu;
    private Toolbar toolbar;
    private TextView mWalletname;
    private LinearLayout mInfoLayout;
    private TextView mWalletaddress;
    private TextView assetCount;
    private ListView mTokenListView;
    private Context mContext;
    ArrayList<FullWallet> fullWallets;
    Drawer result;
    private ArrayList<TokenInfo> tokenInfos = new ArrayList<>();
    private ArrayList<PrimaryDrawerItem> primaryDrawerItems = new ArrayList<>();
    private TokenInfoAdapter tokenInfoAdapter;
    private BGARefreshLayout mRefreshLayout;
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
        setContentView(R.layout.fragment_assets);
        MyApplication.addActivity(this);
        mContext = this;
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        mWalletname = (TextView) findViewById(R.id.walletname);
        mInfoLayout = (LinearLayout) findViewById(R.id.infoLayout);
        mWalletaddress = (TextView) findViewById(R.id.walletaddress);
        mTokenListView = (ListView) findViewById(R.id.list);
        mRefreshLayout = (BGARefreshLayout) findViewById(R.id.refreshLayout);
        assetCount = (TextView) findViewById(R.id.aseetCount);
        fullWallets = WalletStorage.getInstance(getApplicationContext()).get();
        if (fullWallets.size()<=0){
            startActivity(new Intent(mContext,CreateWalletActivity.class));
            finish();
            return;
        }
        tokenInfoAdapter = new TokenInfoAdapter(mContext,tokenInfos);
        mTokenListView.setAdapter(tokenInfoAdapter);
        Const.mFullWallet = fullWallets.get(0);
        createDrawer(toolbar);
        toolbar.setNavigationIcon(R.mipmap.js_images_asset_menu);
        mWalletname.setText(Const.mFullWallet.getWalletName());
        mWalletaddress.setText(Const.mFullWallet.getWalletAdress());
        initAction();
        // 为BGARefreshLayout 设置代理
        mRefreshLayout.setDelegate(this);
        BGARefreshViewHolder mDefineBAGRefreshWithLoadView = new BGANormalRefreshViewHolder(mContext , true);
        //设置刷新样式
        mRefreshLayout.setRefreshViewHolder(mDefineBAGRefreshWithLoadView);
        mRefreshLayout.beginRefreshing();
        NetWorkUtil.getGasPrice();
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public String getModel() {
        File file = new File(Const.mFullWallet.getKeystorePath());
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while ((s = br.readLine()) != null) {//使用readLine方法，一次读一行
                result.append(System.lineSeparator() + s);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString().trim();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            SharedPreferences preferences = getSharedPreferences("data",MODE_PRIVATE);
            FullWallet fullWallet1 = new FullWallet();
            fullWallet1.setWalletAdress(preferences.getString("address",""));
            if (!fullWallet1.getWalletAdress().equals(Const.mFullWallet.getWalletAdress())){
                int index = fullWallets.indexOf(fullWallet1);
                Const.mFullWallet = fullWallets.get(index);
            }
            fullWallets = WalletStorage.getInstance(getApplicationContext()).get();
            int has = fullWallets.indexOf(Const.mFullWallet);
            if (has == -1){
                Const.mFullWallet = fullWallets.get(0);
                preferences.edit().putString("address",Const.mFullWallet.getWalletAdress()).commit();
            }else{
                Const.mFullWallet = fullWallets.get(has);
            }
            if (primaryDrawerItems.size()>1){
                for (int i=0;i<primaryDrawerItems.size();i++){
                    FullWallet fullWallet = new FullWallet();
                    fullWallet.setWalletAdress(primaryDrawerItems.get(i).getTag().toString());
                    int index =fullWallets.indexOf(fullWallet);
                    if (index == -1){
                        result.removeItemByPosition(i);
                        primaryDrawerItems.remove(i);
                        i--;
                    }else{
                        if (Const.mFullWallet.getWalletAdress().equals(fullWallet.getWalletAdress())){
                            PrimaryDrawerItem item =  primaryDrawerItems.get(i);
                            item.withName(Const.mFullWallet.getWalletName()).withIcon(R.mipmap.js_components_images_avatar6);
                            result.updateItem(item);
                            result.setSelection(item);
                        }
                    }
                }
            }else{
                PrimaryDrawerItem item =  primaryDrawerItems.get(0);
                item.withName(Const.mFullWallet.getWalletName()).withIcon(R.mipmap.js_components_images_avatar6);
                result.updateItem(item);
                result.setSelection(item);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        mRefreshLayout.beginRefreshing();
    }

    private void initAction(){
        mInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,WalletInfoActivity.class);
                intent.putExtra("FullWallet", Const.mFullWallet);
                startActivity(intent);
            }
        });
        mWalletaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,PaymentCodeActivity.class);
                intent.putExtra("Token_symbol","ETH");
                startActivity(intent);
            }
        });
        mTokenListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TokenInfo tokenInfo = tokenInfos.get(position);
                Intent intent = new Intent(mContext,TokenInfoActivity.class);
                intent.putExtra("TokenInfo",tokenInfo);
                startActivity(intent);
//                MQTTService.publish("sssssss");
            }
        });
    }
    public void createDrawer(Toolbar toolbar){

        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withName(getResources().getString(R.string.scan)).withIcon(R.mipmap.js_images_common_scan);
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withName(getResources().getString(R.string.enterwalletmethods_createwallet)).withIcon(R.mipmap.js_images_common_createwallet);
        DrawerBuilder builder = new DrawerBuilder();
        for (int i = 0 ;i< fullWallets.size();i++){
            PrimaryDrawerItem item = new PrimaryDrawerItem().withName(fullWallets.get(i).getWalletName()).withIcon(R.mipmap.js_components_images_avatar6);
            primaryDrawerItems.add(item);
            builder.addDrawerItems(item.withTag(fullWallets.get(i).getWalletAdress()));
        }
        builder.addDrawerItems(item1.withTag("scan"));
        builder.addDrawerItems(item2.withTag("create"));
        result = builder .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true) //启用toolbar的ActionBarDrawerToggle动画
                .withShowDrawerOnFirstLaunch(false) //默认开启抽屉
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        String tag = drawerItem.getTag().toString();
                        if (drawerItem.getTag().toString().contains("scan")){
                            startActivity(new Intent(mContext,QRScanActivity.class));
                        }else if (drawerItem.getTag().toString().contains("create")){
                            startActivity(new Intent(mContext,CreateWalletActivity.class));
                        }else{
                            SharedPreferences preferences = getSharedPreferences("data",MODE_PRIVATE);
                            preferences.edit().putString("address",tag).commit();
                            mRefreshLayout.beginRefreshing();
                        }
                        //监听方法实现
                        return false;
                    }
                }) //抽屉中item的监听事件
                .withDrawerGravity(Gravity.END) //设置抽屉打开方向默认从左，end从右侧打开
//                .append(result);
                .build();
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        SharedPreferences preferences = getSharedPreferences("data",MODE_PRIVATE);
        FullWallet fullWallet = new FullWallet();
        fullWallet.setWalletAdress(preferences.getString("address",""));
        if (!fullWallet.getWalletAdress().equals(Const.mFullWallet.getWalletAdress())){
            int index = fullWallets.indexOf(fullWallet);
            if (index == -1){
                Const.mFullWallet = fullWallets.get(0);
                preferences.edit().putString("address",Const.mFullWallet.getWalletAdress()).commit();
            }else{
                Const.mFullWallet = fullWallets.get(index);
            }

        }

        mWalletname.setText(Const.mFullWallet.getWalletName());
        mWalletaddress.setText(Const.mFullWallet.getWalletAdress());
        NetWorkUtil.getTokenList(new HttpCallback() {
            @Override
            public void onFailure(Exception e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.endRefreshing();
                    }
                });
            }

            @Override
            public void onResponse(String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.endRefreshing();
                    }
                });
                if (TextUtils.isEmpty(response)){
                    return;
                }
//                response = "{\"code\":\"200\",\"msg\":\"成功\",\"data\":[{\"id\":15131,\"address_id\":69,\"token_name\":\"ETH\",\"token_num\":\"1.00011036416171200000\",\"token_addr\":\"0x2ea5b10867b3f9f126774f532418bd5de6fc22e4\",\"token_symbol\":\"ETH\",\"create_time\":\"2018-08-16T07:53:47.000Z\",\"decimals\":18,\"rate\":\"1.0000000000\",\"token_ico_url\":\"http://ad.fshd.com/uploads/image/2018/08/10/default.png\"},{\"id\":15132,\"address_id\":69,\"token_name\":\"Universal Group Token\",\"token_num\":\"0.00000000000000000000\",\"token_addr\":\"0xc7e17320d36170dc6eacf1f068efc5152a141fb9\",\"token_symbol\":\"GROUP\",\"create_time\":\"2018-08-16T07:53:47.000Z\",\"decimals\":18,\"rate\":null,\"token_ico_url\":\"http://ad.fshd.com/uploads/image/2018/08/10/default.png\"}]}";
                tokenInfos.clear();
                final BaseBean<ArrayList<TokenInfo>> ret = new Gson().fromJson(response, new TypeToken<BaseBean<ArrayList<TokenInfo>>>() {}.getType());
                if (ret.getCode().contains("101")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext,"请求失败请刷新！",Toast.LENGTH_LONG).show();
                            mRefreshLayout.endRefreshing();
                        }
                    });

                    NetWorkUtil.updateAddr(Const.mFullWallet.getWalletAdress(),null);
                }else if (ret.getCode().contains("200")){
                    if (ret.getData()!=null){
                        tokenInfos.addAll(ret.getData());
                    }
                    Const.tokenInfos = tokenInfos;
                    double price = 0;
                    for (int i=0;i<tokenInfos.size();i++){
                        if (!TextUtils.isEmpty(tokenInfos.get(i).getRate())){
                            price = price+Float.parseFloat(tokenInfos.get(i).getRate())*tokenInfos.get(i).getToken_num();
                        }
//                        if (tokenInfos.get(i).getToken_symbol().toUpperCase().equals("ETH")){
//                            ethNum = tokenInfos.get(i).getToken_num();
//                        }
                    }

                    final double finalPrice = price;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            assetCount.setText("≈"+Utils.getDecimalFormat(finalPrice));
                            tokenInfoAdapter.notifyDataSetChanged();
                        }
                    });
                }


            }
        });
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.removeActivity(this);
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
