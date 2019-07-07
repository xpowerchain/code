package com.lunary.owerwallet.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.lunary.owerwallet.MyApplication;
import com.lunary.owerwallet.R;
import com.lunary.owerwallet.utils.Const;

import java.io.IOException;
import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class QRScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    public static final int REQUEST_CODE = 100;
    public static final int REQUEST_CAMERA_PERMISSION = 106;

    public static final byte SCAN_ONLY = 0;
    public static final byte ADD_TO_WALLETS = 1;
    public static final byte REQUEST_PAYMENT = 2;
    public static final byte PRIVATE_KEY = 3;

    private byte type;

    private ZXingScannerView mScannerView;
    private FrameLayout barCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);
        MyApplication.addActivity(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView title = (TextView) findViewById(R.id.toolbar_title);

        title.setText(getString(R.string.scan));

        barCode = (FrameLayout) findViewById(R.id.barcode);
        // BarcodeCapture barcodeCapture = (BarcodeCapture) getSupportFragmentManager().findFragmentById(R.id.barcode);
        // barcodeCapture.setRetrieval(this);

        if (hasPermission(this))
            initQRScan(barCode);
        else
            askForPermissionRead(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void initQRScan(FrameLayout frame) {
        mScannerView = new ZXingScannerView(this);
        frame.addView(mScannerView);
        mScannerView.setResultHandler(this);
        ArrayList<BarcodeFormat> supported = new ArrayList<BarcodeFormat>();
        supported.add(BarcodeFormat.QR_CODE);
        mScannerView.setFormats(supported);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mScannerView != null)
            mScannerView.stopCamera();
    }

    public boolean hasPermission(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (c.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        } else {
            return true;
        }
        return false;
    }

    public static void askForPermissionRead(Activity c) {
        if (Build.VERSION.SDK_INT < 23) return;
        ActivityCompat.requestPermissions(c, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initQRScan(barCode);
                } else {
                    Toast.makeText(this, "Please grant camera permission in order to read QR codes", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    public void handleResult(Result result) {
        if (result == null){
            finish();
            return;
        }
        String address = result.getText();
        if (address.contains("transfer")){
            Intent intent = new Intent(QRScanActivity.this,TransferActivity.class);
            intent.putExtra("scan",address);
            startActivity(intent);
        }else{
            Intent intent = new Intent();
            intent.putExtra("address",address);
            Log.e("TAD","address:"+address);
            setResult(Const.REQUEST_SCAN,intent);
        }

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.removeActivity(this);
    }
}