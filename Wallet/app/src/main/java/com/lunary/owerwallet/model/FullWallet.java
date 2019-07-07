package com.lunary.owerwallet.model;

import android.text.TextUtils;

import java.io.Serializable;

public class FullWallet implements Serializable {

    private static final long serialVersionUID = 2622313531196422839L;
    private String keystorePath;
    private String password;
    private String walletAdress;
    private String walletName;
    private String walletImg;
    private String mnemonic;
    private String privateKey;
    private String asset;
    private long dateAdded;

    public FullWallet() {
        this.dateAdded = System.currentTimeMillis();
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getKeystorePath() {
        return keystorePath;
    }

    public void setKeystorePath(String keystorePath) {
        this.keystorePath = keystorePath;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getWalletAdress() {
        return walletAdress;
    }

    public void setWalletAdress(String walletAdress) {
        this.walletAdress = walletAdress;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getWalletName() {
        if (TextUtils.isEmpty(walletName)){
            walletName = "FS新钱包";
        }
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getWalletImg() {
        return walletImg;
    }

    public void setWalletImg(String walletImg) {
        this.walletImg = walletImg;
    }

    public String getAsset() {
        if (TextUtils.isEmpty(asset)){
            asset = "0";
        }
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    @Override
    public boolean equals(Object obj) {
        return getWalletAdress().equals(((FullWallet)obj).getWalletAdress());
    }
}
