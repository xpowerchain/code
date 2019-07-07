package com.lunary.owerwallet.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.lunary.owerwallet.MyApplication;
import com.lunary.owerwallet.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.UUID;

/**
 * Created by Administrator on 2018/8/3.
 */
public class Utils {
    static String imei;
    public static String versionName;
    public static String versionCode;
    private static String uuid;

    public static String getFrescoMainCacheDir() {
        String r = String.format("%s%s%s", DirUtil.getAppCacheDir().getPath(), File.separator, "fresco_main");
        new File(r).mkdirs();
        return r;
    }

    public static String getFrescoSmallImageCacheDir() {
        return String.format("%s%s%s", DirUtil.getAppCacheDir().getPath(), File.separator, "fresco_small");
    }
    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }
    public static String getVersion(){
        if (TextUtils.isEmpty(versionCode)) {
            PackageManager pm = MyApplication.getContext().getPackageManager();
            PackageInfo pi = null;
            try {
                pi = pm.getPackageInfo(MyApplication.getContext().getPackageName(), 0);
                if (null != pi) {
                    versionCode = pi.versionCode+"";
                    versionName = pi.versionName;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return versionCode;
    }

    public static String getDecimalFormat(double value){
        DecimalFormat df = new DecimalFormat("0.0000");
        df.setRoundingMode(RoundingMode.DOWN);
        return df.format(value);
    }
    public static String getDecimalFormat(double value,int pos){
        DecimalFormat df = new DecimalFormat("0.00000000");
        df.setRoundingMode(RoundingMode.DOWN);
        return df.format(value);
    }
    public static String getDecimalFormat(String value){
        DecimalFormat df = new DecimalFormat("0.0000");
        df.setRoundingMode(RoundingMode.DOWN);
        return df.format(Long.valueOf(value));
    }
    /**
     * deviceID的组成为：渠道标志+识别符来源标志+hash后的终端识别符
     *
     * 渠道标志为：
     * 1，andriod（a）
     *
     * 识别符来源标志：
     * 1， wifi mac地址（wifi）；
     * 2， IMEI（imei）；
     * 3， 序列号（sn）；
     * 4， id：随机码。若前面的都取不到时，则随机生成一个随机码，需要缓存。
     * @return
     */
    public static String getIMEI() {
        if (!TextUtils.isEmpty(imei)){
            return imei;
        }
        Context context = MyApplication.getContext();
        StringBuilder deviceId = new StringBuilder();
        // 渠道标志
        deviceId.append("a");
        try {
            //IMEI（imei）
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String imei = tm.getDeviceId();
            if(!TextUtils.isEmpty(imei)){
                deviceId.append("imei");
                deviceId.append(imei);
                return deviceId.toString();
            }
            //wifi mac地址
            String wifiMac = getPreferedMac();
            if(!TextUtils.isEmpty(wifiMac)){
                deviceId.append("wifi");
                deviceId.append(wifiMac);
                return deviceId.toString();
            }
            //序列号（sn）
            String sn = tm.getSimSerialNumber();
            if(!TextUtils.isEmpty(sn)){
                deviceId.append("sn");
                deviceId.append(sn);
                return deviceId.toString();
            }
            //如果上面都没有， 则生成一个id：随机码
            String uuid = getUUID(context);
            if(!TextUtils.isEmpty(uuid)){
                deviceId.append("id");
                deviceId.append(uuid);
                return deviceId.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            deviceId.append("id").append(getUUID(context));
        }
        imei = md5(deviceId.toString());
        return imei;
    }
    private static String mac = null;

    public static String getPreferedMac() {
        if (!TextUtils.isEmpty(mac)) {
            return mac;
        }
        String ethernetMac = getEthernetMac();
        if (!TextUtils.isEmpty(ethernetMac)) {
            mac = ethernetMac;
        } else {
            mac = getMacAddress();
        }

        return mac;
    }
    private static String sEthernetMac;

    // 获取ethernet的mac地址(每次应用重启，都获取一次)
    public static String getEthernetMac() {
        if (!TextUtils.isEmpty(sEthernetMac)) {
            return sEthernetMac;
        }

        String mac = "";
        try {
            Process p = Runtime.getRuntime().exec("cat /sys/class/net/eth0/address");
            InputStream is = p.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader bf = new BufferedReader(isr);
            String line = null;
            if ((line = bf.readLine()) != null) {
                mac = line;
            }
            bf.close();
            isr.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mac = TextUtils.isEmpty(mac) ? mac : mac.toUpperCase();
//        if(Const.logEnabled()) Const.logD("getEthernetMac = " + mac);
        sEthernetMac = mac;

        return mac;
    }

    private static String sWifiMac;

    // 获取wifi的mac地址（第一次获取后，会保存到文件中，后续使用，会保存再内存中）
    // 因为有的厂家wifi mac是随机生成的，每次开机会变，所以会在第一次获取到，保存到文件中
    private static String getMacAddress() {
        if (!TextUtils.isEmpty(sWifiMac)) {
            return sWifiMac;
        }
        WifiManager wifiMng = (WifiManager) MyApplication.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfor = wifiMng.getConnectionInfo();
        String mac = wifiInfor.getMacAddress();
        mac = TextUtils.isEmpty(mac) ? mac : mac.toUpperCase();
        sWifiMac = mac;

        return mac;
    }
    /**
     * 得到全局唯一UUID
     */
    public static String getUUID(Context context){
        SharedPreferences mShare = context.getSharedPreferences("data",context.MODE_PRIVATE);
        if(mShare != null){
            uuid = mShare.getString("uuid", "");
        }
        if(TextUtils.isEmpty(uuid)){
            uuid = UUID.randomUUID().toString();
            mShare.edit().putString("uuid",uuid).commit();
        }
        return uuid;
    }
}
