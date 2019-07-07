package com.lunary.owerwallet.fragents;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lunary.owerwallet.R;
import com.lunary.owerwallet.activity.ImportKeystoreActivity;

/**
 * Created by Administrator on 2018/8/13.
 */
public class ImportKeystoreFragment extends Fragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_importkeystore,null);
        final String key = ((ImportKeystoreActivity)getActivity()).keystore;
        TextView keystoreTxt = (TextView) rootView.findViewById(R.id.keystoreTxt);
        keystoreTxt.setText(key);
        TextView copyText = (TextView) rootView.findViewById(R.id.copyText);
        copyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager mClipboardManager = (ClipboardManager) getActivity().getSystemService(getActivity().CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("copy from demo", key);
                mClipboardManager.setPrimaryClip(clipData);
                Toast.makeText(getActivity(),getString(R.string.copied),Toast.LENGTH_LONG).show();
            }
        });
        return rootView;
    }
}
