package com.lunary.owerwallet.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.lunary.owerwallet.MyApplication;
import com.lunary.owerwallet.R;
import com.lunary.owerwallet.utils.Const;
import com.lunary.owerwallet.utils.QRCodeUtil;

/**
 * Created by Administrator on 2018/8/3.
 */
public class PaymentCodeActivity extends Activity {
    ClipboardManager mClipboardManager;
    private TextView mWalletAdress;
    private TextView copytext;
    private Context mContext;
    private EditText amountcount;
    private ImageView qrcode;
    ImageView back;
    TextView share;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paymentcode);
        MyApplication.addActivity(this);
        mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        mContext = this;
        final String token_symbol = getIntent().getStringExtra("Token_symbol");
        mWalletAdress = (TextView) findViewById(R.id.walletaddress);
        mWalletAdress.setText(Const.mFullWallet.getWalletAdress());
        copytext = (TextView) findViewById(R.id.copytext);
        amountcount = (EditText) findViewById(R.id.amountcount);
        qrcode = (ImageView) findViewById(R.id.qrcode);
        back = (ImageView) findViewById(R.id.back);
        share = (TextView) findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent textIntent = new Intent(Intent.ACTION_SEND);
                textIntent.setType("text/plain");
                textIntent.putExtra(Intent.EXTRA_TEXT, Const.mFullWallet.getWalletAdress());
                startActivity(Intent.createChooser(textIntent, "分享"));
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        qrcode.post(new Runnable() {
            @Override
            public void run() {
                try {
                    qrcode.setImageBitmap(QRCodeUtil.createQRCode("transfer,"+Const.mFullWallet.getWalletAdress()+",0,"+token_symbol,qrcode.getMeasuredWidth()));
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });
        amountcount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                qrcode.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String str = s.toString();
                            if (TextUtils.isEmpty(str)){
                                str = "0";
                            }
                            qrcode.setImageBitmap(QRCodeUtil.createQRCode("transfer,"+Const.mFullWallet.getWalletAdress()+","+str+","+token_symbol,qrcode.getMeasuredWidth()));
                        } catch (WriterException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        copytext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipData clipData = ClipData.newPlainText("copy from demo", Const.mFullWallet.getWalletAdress());
                mClipboardManager.setPrimaryClip(clipData);
                Toast.makeText(mContext,"已复制",Toast.LENGTH_LONG).show();
            }
        });

    }

}
