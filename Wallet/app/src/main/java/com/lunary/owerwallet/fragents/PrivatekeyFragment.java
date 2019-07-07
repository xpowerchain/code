package com.lunary.owerwallet.fragents;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lunary.owerwallet.R;
import com.lunary.owerwallet.activity.MainActivity;
import com.lunary.owerwallet.model.FullWallet;
import com.lunary.owerwallet.model.HttpCallback;
import com.lunary.owerwallet.utils.Const;
import com.lunary.owerwallet.utils.NetWorkUtil;
import com.lunary.owerwallet.utils.OwerWalletUtils;
import com.lunary.owerwallet.utils.UpdateManager;
import com.lunary.owerwallet.utils.Utils;
import com.lunary.owerwallet.utils.WalletStorage;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/8/4.
 */
public class PrivatekeyFragment extends Fragment {
    private EditText mneminic;
    private EditText mWalletpassword,mConfirmpassword;
    private TextView loadwallet;
    private boolean isRequest;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_privatekey, null);
        isRequest = false;
        mWalletpassword = (EditText) rootView.findViewById(R.id.walletpassword);
        mConfirmpassword = (EditText) rootView.findViewById(R.id.confirmpassword);
        mneminic = (EditText) rootView.findViewById(R.id.mneminic);
        loadwallet = (TextView) rootView.findViewById(R.id.loadwallet);
        loadwallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mneminicStr = mneminic.getText().toString();
                if (TextUtils.isEmpty(mneminicStr)){
                    Toast.makeText(getActivity(),"私钥内容为空",Toast.LENGTH_LONG).show();
                    return;
                }
                final String pwd = mWalletpassword.getText().toString();
                if (TextUtils.isEmpty(pwd) || pwd.length()<8){
                    Toast.makeText(getActivity(),"密码不正确",Toast.LENGTH_LONG).show();
                    return;
                }
                String confirpwd = mConfirmpassword.getText().toString();
                if (!TextUtils.equals(pwd,confirpwd)){
                    Toast.makeText(getActivity(),"密码不正确",Toast.LENGTH_LONG).show();
                    return;
                }
                if (isRequest){
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            UpdateManager.getInstance().showProgress(getActivity());
                            ECKeyPair keyPair =  ECKeyPair.create(new BigInteger(mneminicStr,16));
                            WalletFile walletFile = Wallet.createLight(pwd,keyPair);
                            FullWallet fullWallet = new FullWallet();
                            fullWallet.setPassword(pwd);
                            fullWallet.setWalletAdress(Keys.toChecksumAddress("0x"+walletFile.getAddress()));
                            fullWallet.setPrivateKey(mneminicStr);
                            int index = WalletStorage.getInstance(getActivity().getApplicationContext()).get().indexOf(fullWallet);
                            if (index == -1){
                                WalletStorage.getInstance(getActivity().getApplicationContext()).add(fullWallet);
                            }else{
                                FullWallet fullWallet1 = WalletStorage.getInstance(getActivity().getApplicationContext()).get().get(index);
                                fullWallet1.setPassword(pwd);
                                WalletStorage.getInstance(getActivity().getApplicationContext()).save();
                            }
                            SharedPreferences preferences = getActivity().getSharedPreferences("data",getActivity().MODE_PRIVATE);
                            preferences.edit().putString("address",fullWallet.getWalletAdress()).commit();
                            updateAddr(walletFile.getAddress());
                        } catch (final CipherException e) {

                            isRequest = false;
                            UpdateManager.getInstance().progressIsNull();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (e.getMessage().contains("Invalid password provided")){
                                        Toast.makeText(getActivity(),"密码无效",Toast.LENGTH_LONG).show();
                                    }else{
                                        Toast.makeText(getActivity(),"钱包导入失败",Toast.LENGTH_LONG).show();
                                    }

                                }
                            });
                        }
                    }
                }).start();
            }
        });
        return rootView;
    }

    public void setPrivatekey(String privatekey) {
        mneminic.setText(privatekey);
    }


    private void updateAddr(String addr){
        NetWorkUtil.updateAddr(addr, new HttpCallback() {
            @Override
            public void onFailure(Exception e) {
                UpdateManager.getInstance().progressIsNull();
                isRequest = false;
                startActivity(new Intent(getActivity(),MainActivity.class));
                getActivity().finish();
            }

            @Override
            public void onResponse(String response) {
                isRequest = false;
                UpdateManager.getInstance().progressIsNull();
                startActivity(new Intent(getActivity(),MainActivity.class));
                getActivity().finish();
            }
        });
    }
}
