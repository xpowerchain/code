package com.lunary.owerwallet.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lunary.owerwallet.MyApplication;
import com.lunary.owerwallet.activity.AboutUsActivity;
import com.lunary.owerwallet.model.BaseBean;
import com.lunary.owerwallet.model.HttpCallback;
import com.lunary.owerwallet.model.TokenInfo;
import com.lunary.owerwallet.model.VersionUpdateBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Dispatcher;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/8/3.
 */
public class NetWorkUtil {
    private static String baseUrl = "https://apitoken.world:8082/api/";
//    private static String baseUrl = "http://apitoken.world/api/";
    private static OkHttpClient sOkHttpClient;

    static {
        if (null == sOkHttpClient) {
            sOkHttpClient = new OkHttpClient().newBuilder()
                    .sslSocketFactory(HttpsTrustManager.createSSLSocketFactory())
                    .hostnameVerifier(new HttpsTrustManager.TrustAllHostnameVerifier())
                    .build();
        }
    }

    public static Map<String, String> getMap(){
        Map<String,String> map = new HashMap<>();
        map.put("brand",android.os.Build.BRAND.replace(" ",""));//品牌
        map.put("ptype", Build.MODEL.replace(" ",""));//型号
        map.put("device_id", Utils.getIMEI());
        map.put("sys_version","ANDROID"+Build.VERSION.RELEASE);
        map.put("app_version",Utils.getVersion());
        map.put("token","0c4d0f8bf7427b42058acee64cbfe3a7");
        return map;
    }

    public static void getGasPrice(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                String url = baseUrl+"gas_price";
                Map<String,String> map = getMap();
                String str = "app_version="+map.get("app_version")+"&brand="+map.get("brand")+"&device_id="+map.get("device_id")+"&ptype="+map.get("ptype")+"&sys_version="+map.get("sys_version")+"&token="+map.get("token");
                Log.e("TAD","str:"+str);
                Log.e("TAD","Utils.md5(str):"+Utils.md5(str));
                map.put("sign",Utils.md5(str));

                post(url, map, new HttpCallback() {
                    @Override
                    public void onFailure(Exception e) {

                    }

                    @Override
                    public void onResponse(String response) {
                        if (TextUtils.isEmpty(response)){
                            return;
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("safeLow")){
                                Const.safeLow = Double.valueOf(jsonObject.getString("safeLow"));
                            }
                            if (jsonObject.has("fastest")){
                                Const.fastest = Double.valueOf(jsonObject.getString("fastest"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }
    public static void getTokenList(final HttpCallback callback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = baseUrl+"token_list";
                Map<String,String> map = getMap();
                map.put("addr",Const.mFullWallet.getWalletAdress());
                String str = "addr="+map.get("addr")+"&app_version="+map.get("app_version")+"&brand="+map.get("brand")+"&device_id="+map.get("device_id")+"&ptype="+map.get("ptype")+"&sys_version="+map.get("sys_version")+"&token="+map.get("token");
                Log.e("TAD","str:"+str);
                Log.e("TAD","Utils.md5(str):"+Utils.md5(str));
                map.put("sign",Utils.md5(str));

                post(url,map,callback);
            }
        }).start();
    }

    public static void updateAddr(final String addr, final HttpCallback callback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = baseUrl+"wallet_gen";
                Map<String,String> map = getMap();
                map.put("address", addr);
                String str = "address="+map.get("address")+"&app_version="+map.get("app_version")+"&brand="+map.get("brand")+"&device_id="+map.get("device_id")+"&ptype="+map.get("ptype")+"&sys_version="+map.get("sys_version")+"&token="+map.get("token");
                map.put("sign",Utils.md5(str));

                post(url,map, callback);
            }
        }).start();
    }

    public static void getTransferList(final int page, final String token_symbol, final HttpCallback callback){
        new Thread(new Runnable() {
            @Override
            public void run() {

                String url = baseUrl+"txs";
                Map<String,String> map = getMap();
                map.put("addr", Const.mFullWallet.getWalletAdress());
                map.put("symbol",token_symbol);
                map.put("page",page+"");
                String str;
                if (TextUtils.isEmpty(token_symbol)){
                    str = "addr="+map.get("addr")+"&app_version="+map.get("app_version")+"&brand="+map.get("brand")+"&device_id="+map.get("device_id")+"&page="+map.get("page")+"&ptype="+map.get("ptype")+"&sys_version="+map.get("sys_version")+"&token="+map.get("token");
                }else{
                    str = "addr="+map.get("addr")+"&app_version="+map.get("app_version")+"&brand="+map.get("brand")+"&device_id="+map.get("device_id")+"&page="+map.get("page")+"&ptype="+map.get("ptype")+"&symbol="+map.get("symbol")+"&sys_version="+map.get("sys_version")+"&token="+map.get("token");
                }
                Log.e("TAD","str:"+str);
                Log.e("TAD","Utils.md5(str):"+Utils.md5(str));
                map.put("sign",Utils.md5(str));
                post(url,map,callback);
            }
        }).start();
    }

    public static void getGasEstimate(final String from, final String to, final String symbol, final String amount, final HttpCallback callback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = baseUrl+"gas_estimate";
                Map<String,String> map = getMap();
                map.put("from",from);
                map.put("to",to);
                map.put("symbol",symbol);
                map.put("amount",amount);
                String str = "amount="+map.get("amount")+"&app_version="+map.get("app_version")+"&brand="+map.get("brand")+"&device_id="+map.get("device_id")+"&from="+map.get("from")+"&ptype="+map.get("ptype")+"&symbol="+map.get("symbol")+"&sys_version="+map.get("sys_version")+"&to="+map.get("to")+"&token="+map.get("token");
                Log.e("TAD","str:"+str);
                Log.e("TAD","Utils.md5(str):"+Utils.md5(str));
                map.put("sign",Utils.md5(str));

                NetWorkUtil.post(url,map,callback);

            }
        }).start();
    }

    public static void getTranfer(final String tx, final HttpCallback callback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = baseUrl+"transfer";
                Map<String,String> map = getMap();
                map.put("tx",tx);
                String str = "app_version="+map.get("app_version")+"&brand="+map.get("brand")+"&device_id="+map.get("device_id")+"&ptype="+map.get("ptype")+"&sys_version="+map.get("sys_version")+"&token="+map.get("token")+"&tx="+map.get("tx");
                Log.e("TAD","str:"+str);
                Log.e("TAD","Utils.md5(str):"+Utils.md5(str));
                map.put("sign",Utils.md5(str));
                NetWorkUtil.post(url,map,callback);

            }
        }).start();
    }

    public static void getUpdate(final HttpCallback callback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = baseUrl+"update";
                Map<String,String> map =NetWorkUtil.getMap();
                String str = "app_version="+map.get("app_version")+"&brand="+map.get("brand")+"&device_id="+map.get("device_id")+"&ptype="+map.get("ptype")+"&sys_version="+map.get("sys_version")+"&token="+map.get("token");
                Log.e("TAD","str:"+str);
                Log.e("TAD","Utils.md5(str):"+Utils.md5(str));
                map.put("sign",Utils.md5(str));
                NetWorkUtil.post(url,map,callback);

            }
        }).start();
    }
    public static void post(String url, Map<String, String> maps, final HttpCallback callback) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        Log.e("TAD","url:"+url);
        FormBody.Builder builder = new FormBody.Builder();
        if (null != maps && maps.size() > 0) {
            for (Map.Entry<String, String> entry : maps.entrySet()) {
                if (TextUtils.isEmpty(entry.getValue()) || TextUtils.isEmpty(entry.getKey())) {
                    continue;
                }

                builder.add(entry.getKey(), entry.getValue());
            }
        }

        Request request = new Request.Builder()
                .url(url)
                .post(builder.build())
                .build();

        sOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (null != callback) {
                    callback.onFailure(e);
                    e.printStackTrace();
                    Log.e("TAD","onFailure:");
                }
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    if (null != callback) {
                        String res = getBodyString(response);
                        Log.e("TAD","response:"+res);
                        callback.onResponse(res);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (null != callback) {
                        callback.onFailure(e);
                        e.printStackTrace();
                        Log.e("TAD","onFailure:");
                    }
                } finally {
                    if (null != response && null != response.body()) {
                        response.body().close();
                    }
                }
            }
        });
    }
    public static void get(String url, final HttpCallback callback){
        try {

            Log.e("TAD","url:"+url);

            Request.Builder builder = new Request.Builder().url(url);

            Request request = builder.build();
            sOkHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (null != callback) {
                        callback.onFailure(e);
                    }
                }

                @Override
                public void onResponse(Call call, Response response) {
                    try {
                        if (null != callback) {
                            String res = getBodyString(response);
                            callback.onResponse(res);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } catch (Exception ex) {
            if (null != callback) {
                callback.onFailure(null);
            }
            ex.printStackTrace();
        }
    }


    public static String getBodyString(Response response) {
        if (null == response || !response.isSuccessful()) {
            return "";
        }
        try {
            ResponseBody body = response.body();
            if (null == body) {
                return "";
            }
            byte[] bytes = body.bytes();
            if (null == bytes || 0 == bytes.length) {
                return "";
            }
            Charset UTF_8 = Charset.forName("UTF-8");
            MediaType mediaType = body.contentType();
            Charset charset = mediaType != null ? mediaType.charset(UTF_8) : UTF_8;
            String dataBody = new String(bytes, charset.name()).trim();
            //remove unused string
            int head = dataBody.indexOf("{");
            int tail = dataBody.lastIndexOf("}");
            if (head >= 0 && tail >= 0) {
                dataBody = dataBody.substring(head, tail + 1);
            }


//            Log.e("TTT",str2HexStr(dataBody));
             /* if (5 <= Integer.parseInt(Const.PROTOCOL_VERSION) &&!(dataBody.indexOf("{") == 0 && dataBody.lastIndexOf("}") == dataBody.length()-1 && dataBody.contains("\":"))){
                if (MessyCodeCheck.isMessyCode(dataBody)){
                    byte[] bytes1 = new byte[bytes.length];
                    System.arraycopy(bytes,0,bytes1,0,bytes.length);
                    String str = new String(mCrypt.decrypt(bytes1));
                    dataBody = str.trim();
                }
            }*/
            return dataBody;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }


}
