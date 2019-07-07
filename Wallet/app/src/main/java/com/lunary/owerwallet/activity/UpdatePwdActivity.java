package com.lunary.owerwallet.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lunary.owerwallet.R;
import com.lunary.owerwallet.model.FullWallet;
import com.lunary.owerwallet.utils.Const;
import com.lunary.owerwallet.utils.UpdateManager;
import com.lunary.owerwallet.utils.WalletStorage;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;

import java.math.BigInteger;

/**
 * Created by Administrator on 2018/8/15.
 */
public class UpdatePwdActivity extends Activity {

    private Context mContext;
    private TextView loadwallet;
    private TextView done;
    private EditText mOldWalletpassword,mWalletpassword,mConfirmpassword;
    private ImageView mBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatepwd);
        mContext = this;
        mOldWalletpassword = (EditText) findViewById(R.id.oldwalletpassword);
        mWalletpassword = (EditText) findViewById(R.id.walletpassword);
        mConfirmpassword = (EditText) findViewById(R.id.confirmpassword);
        loadwallet = (TextView) findViewById(R.id.loadwallet);
        done = (TextView) findViewById(R.id.done);
        mBack = (ImageView) findViewById(R.id.back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldpwd = mOldWalletpassword.getText().toString();
                if (!TextUtils.equals(oldpwd, Const.mFullWallet.getPassword())){
                    Toast.makeText(mContext,"当前密码不正确",Toast.LENGTH_LONG).show();
                    return;
                }
                final String pwd = mWalletpassword.getText().toString();
                if (TextUtils.isEmpty(pwd) || pwd.length()<8){
                    Toast.makeText(mContext,"密码不可为空或密码不少于8位",Toast.LENGTH_LONG).show();
                    return;
                }
                String confirpwd = mConfirmpassword.getText().toString();
                if (!TextUtils.equals(pwd,confirpwd)){
                    Toast.makeText(mContext,"两次密码输入不一致",Toast.LENGTH_LONG).show();
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            UpdateManager.getInstance().showProgress(mContext);
                            ECKeyPair keyPair =  ECKeyPair.create(new BigInteger(Const.mFullWallet.getPrivateKey(),16));
                            WalletFile walletFile = Wallet.createLight(pwd,keyPair);
                            FullWallet fullWallet = new FullWallet();
                            fullWallet.setPassword(pwd);
                            fullWallet.setWalletAdress("0x"+walletFile.getAddress());
                            Log.e("TAD","walletFile.getAddress():"+walletFile.getAddress());
                            int index = WalletStorage.getInstance(getApplicationContext()).get().indexOf(fullWallet);
                            if (index == -1){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mContext,"当前地址不存在！",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }else{
                                FullWallet fullWallet1 = WalletStorage.getInstance(getApplicationContext()).get().get(index);
                                fullWallet1.setPassword(pwd);
                                WalletStorage.getInstance(getApplicationContext()).save();
                                Log.e("TAD","pwd:"+WalletStorage.getInstance(getApplicationContext()).get().get(index).getPassword());
                                Const.mFullWallet = fullWallet1;
                                finish();
                            }

                        } catch (final CipherException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                Toast.makeText(mContext,"密码修改失败！",Toast.LENGTH_LONG).show();

                                }
                            });
                        }finally {

                            UpdateManager.getInstance().progressIsNull();
                        }
                    }
                }).start();
            }
        });
        loadwallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext,LoadWalletActivity.class));
                finish();
            }
        });
    }
}
