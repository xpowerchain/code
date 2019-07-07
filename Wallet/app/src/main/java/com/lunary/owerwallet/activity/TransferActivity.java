package com.lunary.owerwallet.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lunary.owerwallet.MyApplication;
import com.lunary.owerwallet.R;
import com.lunary.owerwallet.model.BaseBean;
import com.lunary.owerwallet.model.HttpCallback;
import com.lunary.owerwallet.model.TokenInfo;
import com.lunary.owerwallet.utils.Const;
import com.lunary.owerwallet.utils.NetWorkUtil;
import com.lunary.owerwallet.utils.UpdateManager;
import com.lunary.owerwallet.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.spongycastle.util.BigIntegers;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.TransactionUtils;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/8/6.
 */
public class TransferActivity extends Activity {
    private ImageView back,scan,help;
    private Context mContext;
    private EditText toAddress;
    private EditText transferAmount;
    private EditText note;
    private EditText mGasPrice;
    private EditText mGasLimit;
    private TextView balance;
    private TextView txCost;
    private TextView seekbarPrice;
    private TextView title;
    private SeekBar seekBar;
    private Button next;
    private TokenInfo tokenInfo;
    private ImageView mSwithImg;
    private RelativeLayout no_advancedSettings;
    private LinearLayout advancedSettings;
    private TextView text;
    private boolean mSwitchOn = false;
    private double amount;
    private boolean isRequest = false;
    private double gwei_eth = 1000000000.0;
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
        MyApplication.addActivity(this);
        mContext = this;
        back = (ImageView) findViewById(R.id.back);
        scan = (ImageView) findViewById(R.id.scan);
        toAddress = (EditText) findViewById(R.id.toAddress);
        transferAmount = (EditText) findViewById(R.id.transferAmount);
        note = (EditText) findViewById(R.id.note);
        mGasPrice = (EditText) findViewById(R.id.gasPrice);
        mGasLimit = (EditText) findViewById(R.id.gasLimit);
        balance = (TextView) findViewById(R.id.balance);
        txCost = (TextView) findViewById(R.id.txCost);
        title = (TextView) findViewById(R.id.title);
        seekbarPrice = (TextView) findViewById(R.id.seekbarPrice);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        next = (Button) findViewById(R.id.next);
        mSwithImg = (ImageView) findViewById(R.id.swith);
        help = (ImageView) findViewById(R.id.help);
        no_advancedSettings = (RelativeLayout) findViewById(R.id.no_advancedSettings);
        advancedSettings = (LinearLayout) findViewById(R.id.advancedSettings);
        text = (TextView) findViewById(R.id.text);
        String token_symbol = getIntent().getStringExtra("TokenInfo");
        String scanStr = getIntent().getStringExtra("scan");
        TokenInfo tokenInfo1 = new TokenInfo();
        if (!TextUtils.isEmpty(token_symbol)){
            tokenInfo1.setToken_symbol(token_symbol);
        }else  if (!TextUtils.isEmpty(scanStr)){
            String[] str = scanStr.split(",");
            if (str.length == 4){
                tokenInfo1.setToken_symbol(str[3]);
                toAddress.setText(str[1]);
                transferAmount.setText(str[2]);
            }
        }

        int index  = Const.tokenInfos.indexOf(tokenInfo1);
        if (index == -1){
            tokenInfo = Const.tokenInfos.get(0);
        }else{
            tokenInfo = Const.tokenInfos.get(index);
        }
        amount = tokenInfo.getToken_num();
        balance.setText(getString(R.string.balance)+"："+Utils.getDecimalFormat(amount)+" "+tokenInfo.getToken_symbol().toLowerCase());
        seekBar.setMax((int) (Const.fastest-Const.safeLow));
        seekBar.setProgress(0);
        seekBar.getThumb().setColorFilter(Color.parseColor("#ea5299"), PorterDuff.Mode.SRC_ATOP);
        seekBar.getProgressDrawable().setColorFilter(Color.parseColor("#ea5299"), PorterDuff.Mode.SRC_ATOP);
        title.setText(tokenInfo.getToken_symbol().toUpperCase()+getString(R.string.send));
        double price = (seekBar.getProgress()+Const.safeLow)*Const.gas/gwei_eth;
        seekbarPrice.setText(price+" eth");
        transferAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                balance.setVisibility(hasFocus ? View.VISIBLE : View.GONE);
            }
        });
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,WebViewActivity.class);
                intent.putExtra("title",getString(R.string.gas_cost));
                startActivity(intent);
            }
        });
        mGasLimit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                txCost.setVisibility(hasFocus ? View.VISIBLE : View.GONE);
            }
        });
        mGasLimit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String gaslimit = s.toString();
                if (TextUtils.isEmpty(gaslimit)){
                    gaslimit = "0";
                }
                String gas = mGasPrice.getText().toString();
                if (TextUtils.isEmpty(gas)){
                    gas = "0";
                }
                double price = Long.parseLong(gas)*Long.parseLong(gaslimit)/ gwei_eth;
                txCost.setText("矿工费用："+Utils.getDecimalFormat(price,8)+" eth");
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double price = (progress+Const.safeLow) * Const.gas/ gwei_eth;
                seekbarPrice.setText(Utils.getDecimalFormat(price,8)+" eth");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRequest){
                    return;
                }
                final String to = toAddress.getText().toString();
                if (TextUtils.isEmpty(to) || (!to.contains("0x")|| to.length()!=42)){
                    showTextDailog(getString(R.string.fra));
                    return;
                }
                final String tranAmount = transferAmount.getText().toString();
                if (TextUtils.isEmpty(tranAmount)){
                    showTextDailog("无效的金额！");
                    return;
                }
                if (amount<=0 || Double.valueOf(tranAmount) > amount){
                    showTextDailog("余额不足！");
                    return;
                }
                if (mSwitchOn){
                    if (TextUtils.isEmpty(mGasLimit.getText()) || TextUtils.isEmpty(mGasPrice.getText())){
                        showTextDailog(getString(R.string.gog));
                        return;
                    }
                }
                isRequest = true;
                NetWorkUtil.getGasEstimate(Const.mFullWallet.getWalletAdress(), to, tokenInfo.getToken_symbol(), tranAmount, new HttpCallback() {
                    @Override
                    public void onFailure(Exception e) {
                        isRequest = false;
                    }

                    @Override
                    public void onResponse(final String response) {
                        isRequest = false;
                        if (TextUtils.isEmpty(response)){
                            return;
                        }

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("nonce") && jsonObject.has("gas")){
                                final BigInteger nonce = new BigInteger(jsonObject.getString("nonce"));
                                final int gas =jsonObject.getInt("gas");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showCommentDailog(to,Double.valueOf(tranAmount),nonce,gas);
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
                //1 Gwei = 0.000000001 Eth
//                Web3jService web3jService = new HttpService("");
//                Web3j web3j = Web3jFactory.build(web3jService);
//                String hexValue = null;

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(mContext,QRScanActivity.class), Const.REQUEST_TRANSFER);
            }
        });
        mSwithImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSwitchOn){
                    mSwitchOn = false;
                    no_advancedSettings.setVisibility(View.VISIBLE);
                    text.setVisibility(View.GONE);
                    advancedSettings.setVisibility(View.GONE);
                    mSwithImg.setImageResource(R.mipmap.switch_off);
                }else{
                    mSwitchOn = true;
                    advancedSettings.setVisibility(View.VISIBLE);
                    text.setVisibility(View.VISIBLE);
                    no_advancedSettings.setVisibility(View.GONE);
                    mSwithImg.setImageResource(R.mipmap.switch_on);
                }
            }
        });
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,WebViewActivity.class);
                intent.putExtra("title",getString(R.string.relevant_concepts));
                startActivity(intent);
            }
        });


        NetWorkUtil.getGasPrice();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Const.REQUEST_SCAN){
            if (data != null){
                String textStr =  data.getStringExtra("address");
                toAddress.setText(textStr);
            }
        }
    }
    /**
     * 弹出评论框
     */
    private void showTextDailog(String text) {
        //R.style.***一定要写，不然不能充满整个屏宽，引用R.style.AppTheme就可以
        final AlertDialog dialog = new AlertDialog.Builder(mContext, R.style.AppTheme).create();
        final View view = View.inflate(mContext, R.layout.dialog_text, null);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        //设置dialog弹出后会点击屏幕或物理返回键，dialog不消失
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        window.setContentView(view);

        //获得window窗口的属性
        WindowManager.LayoutParams params = window.getAttributes();
        //设置窗口宽度为充满全屏
        params.width = (int) getResources().getDimension(R.dimen.dp_265);//如果不设置,可能部分机型出现左右有空隙,也就是产生margin的感觉
        //设置窗口高度为包裹内容
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;//就是这个属性导致window后所有的东西都成暗淡
        params.dimAmount = 0.5f;//设置对话框的透明程度背景(非布局的透明度)
        //将设置好的属性set回去
        window.setAttributes(params);

        TextView textStr = (TextView) view.findViewById(R.id.text);
        textStr.setText(text);
        TextView close = (TextView) view.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog!=null && dialog.isShowing()){
                    dialog.dismiss();
                }
            }
        });
    }
    /**
     * 弹出评论框
     * @param to
     * @param tranAmount
     * @param nonce
     * @param gas
     */
    private void showCommentDailog(final String to, final double tranAmount, final BigInteger nonce, final int gas) {
        if (gas > Const.gas){
            Const.gas = gas;
        }
        //R.style.***一定要写，不然不能充满整个屏宽，引用R.style.AppTheme就可以
        final AlertDialog dialog = new AlertDialog.Builder(mContext, R.style.AppTheme).create();
        final View view = View.inflate(mContext, R.layout.dialog_transfer, null);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        //设置dialog弹出时的动画效果，从屏幕底部向上弹出
//        window.setWindowAnimations(R.style.dialogStyle);
//        window.getDecorView().setPadding(0,0,0,0);

        //设置dialog弹出后会点击屏幕或物理返回键，dialog不消失
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        window.setContentView(view);

        //获得window窗口的属性
        WindowManager.LayoutParams params = window.getAttributes();
        //设置窗口宽度为充满全屏
        params.width = WindowManager.LayoutParams.MATCH_PARENT;//如果不设置,可能部分机型出现左右有空隙,也就是产生margin的感觉
        //设置窗口高度为包裹内容
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;//就是这个属性导致window后所有的东西都成暗淡
        params.dimAmount = 0.5f;//设置对话框的透明程度背景(非布局的透明度)
        //将设置好的属性set回去
        window.setAttributes(params);

        TextView otherWalletAsress = (TextView) view.findViewById(R.id.otherWalletAdress);
        TextView myWalletAsress = (TextView) view.findViewById(R.id.myWalletAdress);
        TextView minersFee = (TextView) view.findViewById(R.id.minersFee);
        TextView minersFeeMethods = (TextView) view.findViewById(R.id.minersFeeMethods);
        TextView amount = (TextView) view.findViewById(R.id.amount);
        final EditText walletpwd = (EditText) view.findViewById(R.id.walletpwd);
        ImageView close = (ImageView) view.findViewById(R.id.close);
        ImageView back = (ImageView) view.findViewById(R.id.back);
        final LinearLayout transferInfo = (LinearLayout) view.findViewById(R.id.tranferinfo);
        final LinearLayout walletPwd = (LinearLayout) view.findViewById(R.id.walletPwd);
        Button confirInfo = (Button) view.findViewById(R.id.confirInfo);
        myWalletAsress.setText(Const.mFullWallet.getWalletAdress());
        otherWalletAsress.setText(to);
        amount.setText(Utils.getDecimalFormat(tranAmount,8)+" "+tokenInfo.getToken_symbol().toUpperCase());
        if (mSwitchOn){
            double price = Long.parseLong(mGasPrice.getText().toString())*Long.parseLong(mGasLimit.getText().toString())/ gwei_eth;;
            minersFee.setText(Utils.getDecimalFormat(price,8)+" ETH");
            minersFeeMethods.setText("≈Gas Price("+Long.parseLong(mGasPrice.getText().toString())+")*Gas("+Long.parseLong(mGasLimit.getText().toString())+"gwei)");
        }else{
            double price = (seekBar.getProgress()+Const.safeLow)*Const.gas/ gwei_eth;;
            minersFee.setText(Utils.getDecimalFormat(price,8)+" ETH");
            minersFeeMethods.setText("≈Gas Price("+(seekBar.getProgress()+Const.safeLow)+")*Gas("+Const.gas+"gwei)");
        }

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog!=null && dialog.isShowing()){
                    dialog.dismiss();
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transferInfo.setVisibility(View.VISIBLE);
                walletPwd.setVisibility(View.GONE);
            }
        });
        confirInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (transferInfo.getVisibility() == View.VISIBLE){
                    transferInfo.setVisibility(View.INVISIBLE);
                    walletPwd.setVisibility(View.VISIBLE);
                }else{
                    if (dialog!=null && dialog.isShowing()){
                        dialog.dismiss();
                    }
                    if(!Const.mFullWallet.getPassword().equals(walletpwd.getText().toString())){
                        Toast.makeText(mContext,"密码不正确！",Toast.LENGTH_LONG).show();
                        return;
                    }
                    //ETH转帐
                    UpdateManager.getInstance().showProgress(mContext);
                BigInteger value = BigInteger.valueOf((long) (tranAmount * Math.pow(10,tokenInfo.getDecimals())));

                Credentials credentials = Credentials.create(Const.mFullWallet.getPrivateKey());
                Function function = new Function("transfer",
                         Arrays.<Type>asList(new Address(to),new Uint256(value)),
                         Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
                String encodedFunction = FunctionEncoder.encode(function);
                    RawTransaction rawTransaction;
                    BigInteger gasPrice = null,gasLimit = null;
                    if (mSwitchOn){
                        gasPrice = BigInteger.valueOf((long) (Long.parseLong(mGasPrice.getText().toString())* gwei_eth));
                        gasLimit = BigInteger.valueOf(Long.parseLong(mGasLimit.getText().toString()));
                    }else{
                        gasPrice = BigInteger.valueOf((long) ((long) (seekBar.getProgress()+Const.safeLow)* gwei_eth));
                        gasLimit = BigInteger.valueOf(Const.gas);
                    }

                    Log.e("TAD","gasPrice:"+gasPrice+" gasLimit:"+gasLimit);
                    if (tokenInfo.getToken_symbol().toUpperCase().equals("ETH")){
                        rawTransaction = RawTransaction.createTransaction(nonce,gasPrice ,gasLimit , to,value,encodedFunction);
                    }else {
                        rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit,tokenInfo.getToken_addr(),BigInteger.ZERO,encodedFunction);
                    }
                    //签名Transaction，这里要对交易做签名
                    byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
                    String hexValue = Numeric.toHexString(signedMessage);
                    NetWorkUtil.getTranfer(hexValue, new HttpCallback() {
                        @Override
                        public void onFailure(Exception e) {
                            UpdateManager.getInstance().progressIsNull();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext,"转账失败！",Toast.LENGTH_LONG).show();
                                }
                            });


                        }

                        @Override
                        public void onResponse(String response) {
                            UpdateManager.getInstance().progressIsNull();
                            if (TextUtils.isEmpty(response)){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mContext,"转账失败！",Toast.LENGTH_LONG).show();
                                    }
                                });
                                return;
                            }
                            final BaseBean ret = new Gson().fromJson(response, BaseBean.class);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (ret.getCode().equals("200")){
                                        Toast.makeText(mContext,"发送中...",Toast.LENGTH_LONG).show();
                                        finish();
                                    }else {
                                        Toast.makeText(mContext,"转账失败！",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

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
