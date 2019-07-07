package com.lunary.owerwallet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.lunary.owerwallet.MyApplication;
import com.lunary.owerwallet.R;
import com.lunary.owerwallet.fragents.MnemonicFragment;
import com.lunary.owerwallet.fragents.OfficialWalletFragment;
import com.lunary.owerwallet.fragents.PrivatekeyFragment;
import com.lunary.owerwallet.utils.Const;

/**
 * Created by Administrator on 2018/8/1.
 */
public class LoadWalletActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    public Fragment[] fragments;
    private TabLayout tabLayout;
    SectionsPagerAdapter mSectionsPagerAdapter;
    private ImageView back,scan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loadwallet);
        MyApplication.addActivity(this);
        back = (ImageView) findViewById(R.id.back);
        scan = (ImageView) findViewById(R.id.scan);
        fragments = new Fragment[3];
        fragments[0] = new MnemonicFragment();
        fragments[1] = new OfficialWalletFragment();
        fragments[2] = new PrivatekeyFragment();


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabTextColors(getResources().getColor(R.color.color_9DA1A4), getResources().getColor(R.color.color_fa83bb));

        tabLayout.getTabAt(0).setText(getString(R.string.mnemonic));
        tabLayout.getTabAt(1).setText(getString(R.string.official));
        tabLayout.getTabAt(2).setText(getString(R.string.privateKey));

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(LoadWalletActivity.this,QRScanActivity.class),Const.REQUEST_LODE);
            }
        });

    }
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Const.REQUEST_SCAN){
            if (data != null){
                String textStr =  data.getStringExtra("address");
                int current = mViewPager.getCurrentItem();
                if (current == 0){
                    MnemonicFragment mnemonicFragment = (MnemonicFragment) fragments[0];
                    if (mnemonicFragment.isAdded()){
                        mnemonicFragment.setMnemonic(textStr);
                    }
                }else if (current == 1){
                    OfficialWalletFragment officialWalletFragment = (OfficialWalletFragment) fragments[1];
                    if (officialWalletFragment.isAdded()){
                        officialWalletFragment.setKeystore(textStr);
                    }
                }else if (current == 2){
                    PrivatekeyFragment privatekeyFragment = (PrivatekeyFragment) fragments[2];
                    if (privatekeyFragment.isAdded()){
                        privatekeyFragment.setPrivatekey(textStr);
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.removeActivity(this);
    }
}
