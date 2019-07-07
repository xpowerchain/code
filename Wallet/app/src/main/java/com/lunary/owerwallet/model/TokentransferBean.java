package com.lunary.owerwallet.model;

import android.text.TextUtils;

import com.lunary.owerwallet.utils.DateUtil;
import com.lunary.owerwallet.utils.Utils;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 2018/8/11.
 */
public class TokentransferBean implements Serializable {

    private String from_addr;
    private String to_addr;
    private String token_addr;
    private String trans_time;
    private String block_number;
    private String block_hash;
    private String token_symbol;
    private String token_name;
    private String amount;
    private int token_decimal;
    private String gas;
    private String gas_price;
    private String gas_used;
    private String trans_hash;
    private String transTime;

    public String getFrom_addr() {
        return from_addr;
    }

    public void setFrom_addr(String from_addr) {
        this.from_addr = from_addr;
    }

    public String getTo_addr() {
        return to_addr;
    }

    public void setTo_addr(String to_addr) {
        this.to_addr = to_addr;
    }

    public String getToken_addr() {
        return token_addr;
    }

    public void setToken_addr(String token_addr) {
        this.token_addr = token_addr;
    }

    public long getTrans_time() {
        try {//2018-08-17T05:53:05.000Z
            trans_time = trans_time.replace("Z", " UTC");//注意是空格+UTC
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");//注意格式化的表达式
            return format.parse(trans_time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setTrans_time(String trans_time) {
        this.trans_time = trans_time;
    }

    public String getBlock_number() {
        return block_number;
    }

    public void setBlock_number(String block_number) {
        this.block_number = block_number;
    }

    public String getBlock_hash() {
        return block_hash;
    }

    public void setBlock_hash(String block_hash) {
        this.block_hash = block_hash;
    }

    public String getToken_symbol() {
        if (TextUtils.isEmpty(token_symbol)){
            token_symbol = "";
        }
        return token_symbol;
    }

    public void setToken_symbol(String token_symbol) {
        this.token_symbol = token_symbol;
    }

    public String getToken_name() {
        return token_name;
    }

    public void setToken_name(String token_name) {
        this.token_name = token_name;
    }

    public String getAmount() {
        if (TextUtils.equals("0",amount)){
            return "";
        }else {
            return Utils.getDecimalFormat(Double.valueOf(amount)/Math.pow(10, getToken_decimal()));
        }

    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getToken_decimal() {
        return token_decimal;
    }

    public void setToken_decimal(int token_decimal) {
        this.token_decimal = token_decimal;
    }

    public String getGas() {
        return gas;
    }

    public void setGas(String gas) {
        this.gas = gas;
    }

    public String getGas_price() {
        return gas_price;
    }

    public void setGas_price(String gas_price) {
        this.gas_price = gas_price;
    }

    public String getGas_used() {
        return gas_used;
    }

    public void setGas_used(String gas_used) {
        this.gas_used = gas_used;
    }

    public String getTrans_hash() {
        return trans_hash;
    }

    public void setTrans_hash(String trans_hash) {
        this.trans_hash = trans_hash;
    }

    public String getTransTime() {
        this.transTime = DateUtil.dateToStrLong2(getTrans_time());
        return transTime;
    }
}
