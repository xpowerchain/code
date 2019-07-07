package com.lunary.owerwallet.fragents;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.zxing.WriterException;
import com.lunary.owerwallet.R;
import com.lunary.owerwallet.activity.ImportKeystoreActivity;
import com.lunary.owerwallet.utils.Const;
import com.lunary.owerwallet.utils.QRCodeUtil;

/**
 * Created by Administrator on 2018/8/13.
 */
public class ImportKeystoreScanFragment extends Fragment{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_importkeystoresan,null);
        final ImageView qrcode = (ImageView) rootView.findViewById(R.id.scan);
        qrcode.post(new Runnable() {
            @Override
            public void run() {
                try {
                    qrcode.setImageBitmap(QRCodeUtil.createQRCode(((ImportKeystoreActivity)getActivity()).keystore,qrcode.getMeasuredWidth()));
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });
        return rootView;
    }
}
