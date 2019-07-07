package com.lunary.owerwallet.fragents;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lunary.owerwallet.R;
import com.lunary.owerwallet.activity.MainActivity;
import com.lunary.owerwallet.model.FullWallet;
import com.lunary.owerwallet.model.HttpCallback;
import com.lunary.owerwallet.utils.Const;
import com.lunary.owerwallet.utils.DateUtil;
import com.lunary.owerwallet.utils.NetWorkUtil;
import com.lunary.owerwallet.utils.OwerWalletUtils;
import com.lunary.owerwallet.utils.UpdateManager;
import com.lunary.owerwallet.utils.Utils;
import com.lunary.owerwallet.utils.WalletStorage;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;
import org.web3j.abi.datatypes.Address;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.MnemonicUtils;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.WalletUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.web3j.crypto.Hash.sha256;

/**
 * Created by Administrator on 2018/8/2.
 */
public class OfficialWalletFragment extends Fragment {
    private EditText mneminic;
    private EditText keystorePwd;
    private TextView loadwallet;
    private boolean isRequest;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_officialwallet,null);
        isRequest = false;
        mneminic = (EditText) rootView.findViewById(R.id.mneminic);
        keystorePwd = (EditText) rootView.findViewById(R.id.keystorePwd);
        loadwallet = (TextView) rootView.findViewById(R.id.loadwallet);
//        Credentials credentials = OwerWalletUtils.loadCredentials("yourpassword", keyStoreDir + "/UTC--2018-05-22T02-46-57.932000000Z--ae45f5aec6e6e7c0780a2a09dc830a9c3cb5b16b.json" );
        loadwallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mneminicStr = mneminic.getText().toString();
                if (TextUtils.isEmpty(mneminicStr)){
                    Toast.makeText(getActivity(),"keystore 文本内容为空",Toast.LENGTH_LONG).show();
                    return;
                }
                final String pwd = keystorePwd.getText().toString();
                if (TextUtils.isEmpty(pwd) || pwd.length()<8){
                    Toast.makeText(getActivity(),"密码不正确",Toast.LENGTH_LONG).show();
                    return;
                }
                if (isRequest){
                    return;
                }


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Credentials credentials = null;
                        try {

                            JSONObject jsonObject = new JSONObject(mneminicStr);

                            if (!jsonObject.has("address")){
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(),"keystore 文本内容不正确",Toast.LENGTH_LONG).show();
                                    }
                                });

                                return;
                            }

//                            String fileName = DateUtil.dateToSSSXXXLong(System.currentTimeMillis()) + jsonObject.getString(Address.TYPE_NAME) + ".json";
//                            File file = new File(OfficialWalletFragment.this.getActivity().getFilesDir(), fileName);
//                            if (!file.exists()) {
//                                try {
//                                    file.createNewFile();
//                                    FileOutputStream fout = new FileOutputStream(file.getAbsoluteFile(), false);
//                                    fout.write(mneminicStr.getBytes());
//                                    fout.close();
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
                            UpdateManager.getInstance().showProgress(getActivity());
                            isRequest = true;
                            WalletFile walletFile = new WalletFile();
                            walletFile.setVersion(jsonObject.getInt("version"));
                            walletFile.setId(jsonObject.getString("id"));
                            walletFile.setAddress(jsonObject.getString("address"));
                            WalletFile.Crypto crypto = new WalletFile.Crypto();
                            walletFile.setCrypto(crypto);
                            JSONObject cryptoJsonObject = jsonObject.getJSONObject("crypto");
                            crypto.setKdf(cryptoJsonObject.getString("kdf"));
                            crypto.setCipher(cryptoJsonObject.getString("cipher"));
                            crypto.setCiphertext(cryptoJsonObject.getString("ciphertext"));
                            crypto.setMac(cryptoJsonObject.getString("mac"));
                            WalletFile.ScryptKdfParams params = new WalletFile.ScryptKdfParams();
                            JSONObject KdfJsonObject = cryptoJsonObject.getJSONObject("kdfparams");
                            params.setR(KdfJsonObject.getInt("r"));
                            params.setN(KdfJsonObject.getInt("n"));
                            params.setP(KdfJsonObject.getInt("p"));
                            params.setDklen(KdfJsonObject.getInt("dklen"));
                            params.setSalt(KdfJsonObject.getString("salt"));
                            crypto.setKdfparams(params);
                            WalletFile.CipherParams cipherParams = new WalletFile.CipherParams();
                            cipherParams.setIv(cryptoJsonObject.getJSONObject("cipherparams").getString("iv"));
                            crypto.setCipherparams(cipherParams);
//                            credentials = Credentials.create(Wallet.decrypt(pwd, walletFile));
                            credentials = OwerWalletUtils.loadCredentials(pwd,walletFile);
                            FullWallet fullWallet = new FullWallet();
                            fullWallet.setPassword(pwd);
                            fullWallet.setWalletAdress(Keys.toChecksumAddress(credentials.getAddress()));
                            fullWallet.setPrivateKey(credentials.getEcKeyPair().getPrivateKey().toString(16));
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
                            updateAddr(credentials.getAddress());
                        }catch (final CipherException e) {
                            isRequest = false;
                            UpdateManager.getInstance().progressIsNull();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (e.getMessage().contains("Invalid password provided")){
                                        Toast.makeText(getActivity(),"密码无效",Toast.LENGTH_LONG).show();
                                    }else{
                                        Toast.makeText(getActivity(),"钱包导入失败,请使用其他方式",Toast.LENGTH_LONG).show();
                                    }

                                }
                            });
                        }catch (Exception e) {
                            e.printStackTrace();
                            isRequest = false;
                            UpdateManager.getInstance().progressIsNull();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(),"钱包导入失败,请使用其他方式",Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                    }
                }).start();



            }
        });
        return rootView;
    }

    public void setKeystore(String keystore) {
        mneminic.setText(keystore);
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
