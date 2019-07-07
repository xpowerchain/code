package com.lunary.owerwallet.utils;

import com.lunary.owerwallet.model.FullWallet;
import com.lunary.owerwallet.model.TokenInfo;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/8/3.
 */
public class Const {

    public final static int REQUEST_SCAN = 200;
    public final static int REQUEST_LODE = 100;
    public final static int REQUEST_TRANSFER = 101;
    public static FullWallet mFullWallet;
    public static ArrayList<TokenInfo> tokenInfos;
    public static double safeLow = 40;
    public static double fastest = 60;
    public static int gas = 200000;

}
