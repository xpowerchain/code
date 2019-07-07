package com.lunary.owerwallet.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.lunary.owerwallet.model.FullWallet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by Administrator on 2018/8/2.
 */
public class WalletStorage {
    private static WalletStorage instance;
    private Context mContext;

    private ArrayList<FullWallet> wallets = new ArrayList<>();

    public static WalletStorage getInstance(Context context) {
        if (instance == null)
            instance = new WalletStorage(context);
        return instance;
    }

    public WalletStorage(Context context) {
        mContext = context;
        try {
            load();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (wallets == null)
                wallets = new ArrayList<FullWallet>();
        }
    }
    public synchronized boolean add(FullWallet addresse) {

        for (int i = 0; i < wallets.size(); i++)
            if (wallets.get(i).getWalletAdress().equalsIgnoreCase(addresse.getWalletAdress())) return false;
        wallets.add(addresse);
        save();
        return true;
    }

    public synchronized ArrayList<FullWallet> get() {
        return wallets;
    }

    public void removeWallet(String address) {
        int position = -1;
        for (int i = 0; i < wallets.size(); i++) {
            if (wallets.get(i).getWalletAdress().equalsIgnoreCase(address)) {
                position = i;
                break;
            }
        }
        if (position >= 0) {
            new File(wallets.get(position).getKeystorePath()).delete();
            wallets.remove(position);
        }
        save();
    }
    public synchronized void save() {
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(new File(mContext.getFilesDir(), "wallets.dat"));
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(wallets);
            oos.close();
            fout.close();
        } catch (Exception e) {
        }
    }

    @SuppressWarnings("unchecked")
    public synchronized void load() throws IOException, ClassNotFoundException {
        FileInputStream fout = new FileInputStream(new File(mContext.getFilesDir(), "wallets.dat"));
        ObjectInputStream oos = new ObjectInputStream(new BufferedInputStream(fout));
        wallets = (ArrayList<FullWallet>) oos.readObject();
        oos.close();
        fout.close();
    }
}
