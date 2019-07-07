package com.lunary.owerwallet.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.lunary.owerwallet.MyApplication;
import com.lunary.owerwallet.R;
import com.lunary.owerwallet.model.TokentransferBean;
import com.lunary.owerwallet.utils.DateUtil;
import com.lunary.owerwallet.utils.QRCodeUtil;
import com.lunary.owerwallet.utils.Utils;

/**
 * Created by Administrator on 2018/8/7.
 */
public class TransferInfoActivity extends Activity {

    private Context mContext;
    private TokentransferBean tokentransferBean;
    ClipboardManager mClipboardManager;
    private ImageView back;
    private ImageView headImg;
    private ImageView qrcode;
    private TextView from_addr;
    private TextView to_addr;
    private TextView note;
    private TextView cost;
    private TextView transfer_amount;
    private TextView transfer_symbol;
    private TextView trans_hash;
    private TextView block_number;
    private TextView trans_time;
    private Button copytext;
    private TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transferinfo);
        MyApplication.addActivity(this);
        mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        mContext = this;
        tokentransferBean = (TokentransferBean) getIntent().getSerializableExtra("Tokentransfer");
        final String url = "https://etherscan.io/tx/"+tokentransferBean.getTrans_hash();
        back = (ImageView) findViewById(R.id.back);
        headImg = (ImageView) findViewById(R.id.headImg);
        qrcode = (ImageView) findViewById(R.id.qrcode);
        from_addr = (TextView) findViewById(R.id.from_addr);
        to_addr = (TextView) findViewById(R.id.to_addr);
        note = (TextView) findViewById(R.id.note);
        cost = (TextView) findViewById(R.id.cost);
        title = (TextView) findViewById(R.id.title);
        transfer_amount = (TextView) findViewById(R.id.transfer_amount);
        transfer_symbol = (TextView) findViewById(R.id.transfer_symbol);
        trans_hash = (TextView) findViewById(R.id.trans_hash);
        block_number = (TextView) findViewById(R.id.block_number);
        trans_time = (TextView) findViewById(R.id.trans_time);
        copytext = (Button) findViewById(R.id.copytext);
        from_addr.setText(tokentransferBean.getFrom_addr());
        to_addr.setText(tokentransferBean.getTo_addr());
        title.setText(getString(R.string.transrecords));
        try{
            String str = Utils.getDecimalFormat((Long.valueOf(tokentransferBean.getGas_used())*Long.valueOf(tokentransferBean.getGas_price())/Math.pow(10,tokentransferBean.getToken_decimal())),6);
            cost.setText(str+" ether");
        }catch (Exception e){
            cost.setText("0 ether");
        }
        transfer_amount.setText(tokentransferBean.getAmount());
        transfer_symbol.setText(tokentransferBean.getToken_symbol().toLowerCase());
        trans_hash.setText(tokentransferBean.getTrans_hash());
        block_number.setText(tokentransferBean.getBlock_number());
        trans_time.setText(DateUtil.dateToStrLong(tokentransferBean.getTrans_time()));
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
                    qrcode.setImageBitmap(QRCodeUtil.createQRCode(url,qrcode.getMeasuredWidth()));
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });
        copytext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipData clipData = ClipData.newPlainText("copy from demo", url);
                mClipboardManager.setPrimaryClip(clipData);
                Toast.makeText(mContext,getString(R.string.copied),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.removeActivity(this);
    }
}
