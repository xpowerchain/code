package com.lunary.owerwallet.model;

import android.text.TextUtils;
import android.util.Log;

import com.lunary.owerwallet.utils.Utils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by Administrator on 2018/8/3.
 */
public class TokenInfo implements Serializable{
    private String token_name;
    private String token_symbol;
    private String token_ico_url;
    private String create_time;
    private String token_addr;
    private String rate;
    private double token_num;
    private int decimals;


    public String getToken_name() {
        return token_name;
    }

    public void setToken_name(String token_name) {
        this.token_name = token_name;
    }

    public String getToken_symbol() {
        return token_symbol;
    }

    public void setToken_symbol(String token_symbol) {
        this.token_symbol = token_symbol;
    }

    public String getToken_ico_url() {
        return token_ico_url;
    }

    public void setToken_ico_url(String token_ico_url) {
        this.token_ico_url = token_ico_url;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getToken_addr() {
        return token_addr;
    }

    public void setToken_addr(String token_addr) {
        this.token_addr = token_addr;
    }

    public String getRate() {
        return null;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public double getToken_num() {
        BigDecimal b1 = new BigDecimal(Double.toString(token_num));
        BigDecimal b2 = new BigDecimal(Double.toString(Math.pow(10,getDecimals())));
        return b1.divide(b2, 4, BigDecimal.ROUND_HALF_DOWN).doubleValue();
    }

    public void setToken_num(double token_num) {
        this.token_num = token_num;
    }

    public int getDecimals() {
        return decimals;
    }

    public void setDecimals(int decimals) {
        this.decimals = decimals;
    }

    @Override
    public boolean equals(Object obj) {
        return getToken_symbol().toUpperCase().equals(((TokenInfo)obj).getToken_symbol().toUpperCase());
    }
}

