package com.lunary.owerwallet.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lunary.owerwallet.MyApplication;
import com.lunary.owerwallet.R;
import com.lunary.owerwallet.model.FullWallet;
import com.lunary.owerwallet.model.HttpCallback;
import com.lunary.owerwallet.utils.Const;
import com.lunary.owerwallet.utils.NetWorkUtil;
import com.lunary.owerwallet.utils.UpdateManager;
import com.lunary.owerwallet.utils.Utils;
import com.lunary.owerwallet.utils.WalletStorage;

import org.json.JSONObject;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Created by Administrator on 2018/8/4.
 */
public class WalletInfoActivity extends Activity {

    private FullWallet fullWallet;
    private Context mContext;
    private ImageView back;
    private TextView mWalletName;
    private EditText mWalletname;
    private TextView save;
    private TextView asset;
    private TextView walletaddress;
    private TextView updatePwd;
    private TextView importprivatekey;
    private TextView importkeystore;
    private Button deleteWallet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walletinfo);
        MyApplication.addActivity(this);
        mContext = this;
        fullWallet = (FullWallet) getIntent().getSerializableExtra("FullWallet");
        back = (ImageView) findViewById(R.id.back);
        mWalletName = (TextView) findViewById(R.id.walletName);
        mWalletname = (EditText) findViewById(R.id.walletname);
        save = (TextView) findViewById(R.id.save);
        asset = (TextView) findViewById(R.id.asset);
        walletaddress = (TextView) findViewById(R.id.walletaddress);
        updatePwd = (TextView) findViewById(R.id.updatePwd);
        importprivatekey = (TextView) findViewById(R.id.importprivatekey);
        importkeystore = (TextView) findViewById(R.id.importkeystore);
        deleteWallet = (Button) findViewById(R.id.deleteWallet);
        mWalletName.setText(fullWallet.getWalletName());
        mWalletname.setText(fullWallet.getWalletName());
//        asset.setText(fullWallet.getAsset()+"Ether");
        walletaddress.setText(fullWallet.getWalletAdress());
        initAction();
    }

    private void initAction(){

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mWalletname.getText().toString();
                if (TextUtils.isEmpty(name)){
                    Toast.makeText(mContext,"钱包名不能为空！",Toast.LENGTH_LONG).show();
                    return;
                }
                fullWallet.setWalletName(name);
                int index = WalletStorage.getInstance(getApplicationContext()).get().indexOf(fullWallet);
                if (index != -1){
                    WalletStorage.getInstance(getApplicationContext()).get().set(index,fullWallet);
                    WalletStorage.getInstance(getApplicationContext()).save();
                }
                startActivity(new Intent(mContext,ManageWalletActivity.class));
                finish();
            }
        });
        updatePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext,UpdatePwdActivity.class));
            }
        });
        importprivatekey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputPwd();
            }
        });
        importkeystore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputPwd2();
            }
        });
        deleteWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder =
                        new AlertDialog.Builder(mContext);
                builder.setMessage("删除钱包")
                        .setCancelable(false)
                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                WalletStorage.getInstance(getApplicationContext()).removeWallet(fullWallet.getWalletAdress());
                                if (WalletStorage.getInstance(getApplicationContext()).get().size() <= 0){
                                    startActivity(new Intent(mContext,CreateWalletActivity.class));
                                }
                                finish();
                            }

                        })
                        .setPositiveButton("取消", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {

                                dialog.dismiss();
                            }

                        });
                builder.create().show();
            }
        });
    }

    private void importprivatekey(){
            final AlertDialog dialog = new AlertDialog.Builder(mContext, R.style.AppTheme).create();
            final View view = View.inflate(mContext, R.layout.dialog_importprivatekey, null);
            Window window = dialog.getWindow();
            window.setGravity(Gravity.CENTER);
            //设置dialog弹出后会点击屏幕或物理返回键，dialog不消失
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
            window.setContentView(view);

            //获得window窗口的属性
            WindowManager.LayoutParams params = window.getAttributes();
            //设置窗口宽度为充满全屏
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;//如果不设置,可能部分机型出现左右有空隙,也就是产生margin的感觉
            //设置窗口高度为包裹内容
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;//就是这个属性导致window后所有的东西都成暗淡
            params.dimAmount = 0.5f;//设置对话框的透明程度背景(非布局的透明度)
            //将设置好的属性set回去
            window.setAttributes(params);
            TextView privateKey = (TextView) view.findViewById(R.id.privateKey);
            privateKey.setText(fullWallet.getPrivateKey());
            Button copytext = (Button) view.findViewById(R.id.copytext);
            ImageView back = (ImageView) view.findViewById(R.id.back);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            copytext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("copy from demo", fullWallet.getPrivateKey());
                    mClipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(mContext,"已复制",Toast.LENGTH_LONG).show();
                }
            });
    }

    private void inputPwd(){
        final AlertDialog dialog = new AlertDialog.Builder(mContext, R.style.AppTheme).create();
        final View view = View.inflate(mContext, R.layout.dialog_pwd, null);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        //设置dialog弹出后会点击屏幕或物理返回键，dialog不消失
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        window.setContentView(view);

        //获得window窗口的属性
        WindowManager.LayoutParams params = window.getAttributes();
        //设置窗口宽度为充满全屏
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;//如果不设置,可能部分机型出现左右有空隙,也就是产生margin的感觉
        //设置窗口高度为包裹内容
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;//就是这个属性导致window后所有的东西都成暗淡
        params.dimAmount = 0.5f;//设置对话框的透明程度背景(非布局的透明度)
        //将设置好的属性set回去
        window.setAttributes(params);
        final EditText walletpwd = (EditText) view.findViewById(R.id.walletpwd);
        Button copytext = (Button) view.findViewById(R.id.copytext);
        ImageView back = (ImageView) view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        copytext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(!Const.mFullWallet.getPassword().equals(walletpwd.getText().toString())){
                    Toast.makeText(mContext,"密码不正确！",Toast.LENGTH_LONG).show();
                    return;
                }
                importprivatekey();
            }
        });
    }
    private void inputPwd2(){
        final AlertDialog dialog = new AlertDialog.Builder(mContext, R.style.AppTheme).create();
        final View view = View.inflate(mContext, R.layout.dialog_pwd, null);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        //设置dialog弹出后会点击屏幕或物理返回键，dialog不消失
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        window.setContentView(view);

        //获得window窗口的属性
        WindowManager.LayoutParams params = window.getAttributes();
        //设置窗口宽度为充满全屏
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;//如果不设置,可能部分机型出现左右有空隙,也就是产生margin的感觉
        //设置窗口高度为包裹内容
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;//就是这个属性导致window后所有的东西都成暗淡
        params.dimAmount = 0.5f;//设置对话框的透明程度背景(非布局的透明度)
        //将设置好的属性set回去
        window.setAttributes(params);
        final EditText walletpwd = (EditText) view.findViewById(R.id.walletpwd);
        Button copytext = (Button) view.findViewById(R.id.copytext);
        ImageView back = (ImageView) view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        copytext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(!Const.mFullWallet.getPassword().equals(walletpwd.getText().toString())){
                    Toast.makeText(mContext,"密码不正确！",Toast.LENGTH_LONG).show();
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            UpdateManager.getInstance().showProgress(mContext);
                            ECKeyPair ecKeyPair = ECKeyPair.create(new BigInteger(fullWallet.getPrivateKey(),16));
                            WalletFile walletFile = Wallet.createLight(walletpwd.getText().toString(),ecKeyPair);
                            String str = new Gson().toJson(walletFile);
                            UpdateManager.getInstance().progressIsNull();
                            Intent intent = new Intent(mContext,ImportKeystoreActivity.class);
                            intent.putExtra("keystore",str);
                            startActivity(intent);
                        } catch (CipherException e) {
                            UpdateManager.getInstance().progressIsNull();
                            if (e.getMessage().contains("Invalid password provided")){
                                Toast.makeText(mContext,"密码无效",Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(mContext,"钱包导入失败",Toast.LENGTH_LONG).show();
                            }
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
