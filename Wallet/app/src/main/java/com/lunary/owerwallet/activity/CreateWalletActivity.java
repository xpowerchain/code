package com.lunary.owerwallet.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lunary.owerwallet.MyApplication;
import com.lunary.owerwallet.R;
import com.lunary.owerwallet.model.FullWallet;
import com.lunary.owerwallet.model.HttpCallback;
import com.lunary.owerwallet.utils.Const;
import com.lunary.owerwallet.utils.NetWorkUtil;
import com.lunary.owerwallet.utils.OwerWalletUtils;
import com.lunary.owerwallet.utils.UpdateManager;
import com.lunary.owerwallet.utils.Utils;
import com.lunary.owerwallet.utils.WalletStorage;

import org.spongycastle.jcajce.provider.digest.SHA3;
import org.web3j.crypto.Bip39Wallet;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.methods.response.EthAccounts;

import java.io.IOException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/7/31.
 */
public class CreateWalletActivity extends Activity {
    private EditText mWalletname,mWalletpassword,mConfirmpassword;
    private EditText passwordinfo;
    private TextView mCreateWallet,mLoadWallet;
    private Context mContxt;
    private ImageView mBack;
    private boolean isRequest = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createwallet);
        MyApplication.addActivity(this);
        mContxt = this;
        isRequest = false;
        mWalletname = (EditText) findViewById(R.id.walletname);
        mWalletpassword = (EditText) findViewById(R.id.walletpassword);
        mConfirmpassword = (EditText) findViewById(R.id.confirmpassword);
        mCreateWallet = (TextView) findViewById(R.id.createwallet);
        mLoadWallet = (TextView) findViewById(R.id.loadwallet);
        mBack = (ImageView) findViewById(R.id.back);
        passwordinfo = (EditText) findViewById(R.id.passwordinfo);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mLoadWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContxt,LoadWalletActivity.class));
            }
        });
        mCreateWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = mWalletname.getText().toString();
                if (TextUtils.isEmpty(name) || name.length()>12){
                    Toast.makeText(mContxt,"姓名不为空且不大于12位",Toast.LENGTH_LONG).show();
                    return;
                }
                final String pwd = mWalletpassword.getText().toString();
                if (TextUtils.isEmpty(pwd) || pwd.length()<8){
                    Toast.makeText(mContxt,"密码不可为空或密码不少于8位",Toast.LENGTH_LONG).show();
                    return;
                }
                String confirpwd = mConfirmpassword.getText().toString();
                if (!TextUtils.equals(pwd,confirpwd)){
                    Toast.makeText(mContxt,"两次密码输入不一致",Toast.LENGTH_LONG).show();
                    return;
                }

                if (isRequest){
                    return;
                }

                isRequest = true;
                UpdateManager.getInstance().showProgress(mContxt);
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            Bip39Wallet walletAddress = OwerWalletUtils.generateBip39Wallet(pwd, mContxt.getFilesDir());
                            Credentials credentials = OwerWalletUtils.loadBip39Credentials(pwd, walletAddress.getMnemonic());
                            FullWallet fullWallet = new FullWallet();
                            fullWallet.setKeystorePath(mContxt.getFilesDir()+"/"+walletAddress.getFilename());
                            fullWallet.setMnemonic(walletAddress.getMnemonic());
                            fullWallet.setPassword(pwd);
                            fullWallet.setWalletName(name);
                            fullWallet.setWalletAdress(Keys.toChecksumAddress(credentials.getAddress()));
                            fullWallet.setPrivateKey(credentials.getEcKeyPair().getPrivateKey().toString(16));
                            int index = WalletStorage.getInstance(getApplicationContext()).get().indexOf(fullWallet);
                            if (index == -1){
                                WalletStorage.getInstance(getApplicationContext()).add(fullWallet);
                            }else{
                                WalletStorage.getInstance(getApplicationContext()).get().set(index,fullWallet);
                                WalletStorage.getInstance(getApplicationContext()).save();
                            }
                            SharedPreferences preferences = getSharedPreferences("data",MODE_PRIVATE);
                            preferences.edit().putString("address",fullWallet.getWalletAdress()).commit();
                            UpdateManager.getInstance().progressIsNull();
                            NetWorkUtil.updateAddr(fullWallet.getWalletAdress(),new HttpCallback() {
                                @Override
                                public void onFailure(Exception e) {
                                    isRequest = false;
                                    UpdateManager.getInstance().progressIsNull();
                                    startActivity(new Intent(mContxt,MainActivity.class));
                                    finish();
                                }

                                @Override
                                public void onResponse(String response) {
                                    isRequest = false;
                                    UpdateManager.getInstance().progressIsNull();
                                    startActivity(new Intent(mContxt,MainActivity.class));
                                    finish();
                                }
                            });
                        } catch (CipherException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.removeActivity(this);
    }
}
