package com.lunary.owerwallet.activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lunary.owerwallet.MyApplication;
import com.lunary.owerwallet.R;
import com.lunary.owerwallet.utils.UpdateManager;

public class MainActivity extends AppCompatActivity {

    //walletAddress:UTC--2018-08-02T14-43-12.229--8350612a02ca5d682053e251da337212e3968f8a.json====phone tone museum there common moral uncle oblige hip bomb brain invest
    //walletAddress:0x8350612a02ca5d682053e251da337212e3968f8a====c310845f33d345b7d323133978d0462d3dbcbe33bd537b0f88cff7e68fae630e

    private Context mContext;
    private ViewPager mViewPager;
    public View[] views;
    SectionsPagerAdapter mSectionsPagerAdapter;
    LocalActivityManager manager;
    private LinearLayout asset,my;
    private ImageView asset_img,my_img;
    private TextView asset_txt,my_txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyApplication.addActivity(this);
        MyApplication.onlyMainActivity();
        asset = (LinearLayout) findViewById(R.id.asset);
        my = (LinearLayout) findViewById(R.id.my);
        asset_img = (ImageView) findViewById(R.id.asset_img);
        my_img = (ImageView) findViewById(R.id.my_img);
        asset_txt = (TextView) findViewById(R.id.asset_txt);
        my_txt = (TextView) findViewById(R.id.my_txt);
        manager = new LocalActivityManager(this, true);
        manager.dispatchCreate(savedInstanceState);
        mContext = this;
//        FullWallet fullWallet = WalletStorage.getInstance(getApplicationContext()).get().get(0);
        views = new View[2];
        Intent intentMain = new Intent(mContext, AssetsActivity.class);
        views[0] = manager.startActivity("AssetsActivity", intentMain).getDecorView();
        intentMain = new Intent(mContext, PersonInfoActivity.class);
        views[1] = manager.startActivity("PersonInfoActivity", intentMain).getDecorView();

        mSectionsPagerAdapter = new SectionsPagerAdapter();
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        asset_img.setImageResource(R.mipmap.js_images_tab_assetsselected);
        asset_txt.setTextColor(getResources().getColor(R.color.color_fa83bb));
        asset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0);
            }
        });
        my.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(1);
            }
        });
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0){
                    AssetsActivity assetsActivity = (AssetsActivity) manager.getActivity("AssetsActivity");
                    assetsActivity.onResume();
                    asset_img.setImageResource(R.mipmap.js_images_tab_assetsselected);
                    asset_txt.setTextColor(getResources().getColor(R.color.color_fa83bb));
                    my_img.setImageResource(R.mipmap.js_images_tab_my);
                    my_txt.setTextColor(getResources().getColor(R.color.black));
                }else  if (position == 1){
                    asset_img.setImageResource(R.mipmap.js_images_tab_assets);
                    asset_txt.setTextColor(getResources().getColor(R.color.black));
                    my_img.setImageResource(R.mipmap.js_images_tab_myselected);
                    my_txt.setTextColor(getResources().getColor(R.color.color_fa83bb));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        UpdateManager.getInstance().getUpdate(mContext);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mViewPager.getCurrentItem() == 0){
            AssetsActivity assetsActivity = (AssetsActivity) manager.getActivity("AssetsActivity");
            assetsActivity.onResume();
        }

    }

    public class SectionsPagerAdapter extends PagerAdapter{
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return views.length;
        }

        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            // TODO Auto-generated method stub
            container.removeView(views[position]);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // TODO Auto-generated method stub
            container.addView(views[position]);


            return views[position];
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.removeActivity(this);
    }
}
