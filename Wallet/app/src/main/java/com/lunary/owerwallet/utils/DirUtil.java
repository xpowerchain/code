package com.lunary.owerwallet.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.StatFs;

import com.lunary.owerwallet.MyApplication;

import java.io.File;
import java.io.IOException;

/**
 * Created by wxliao on 2015/11/9.
 */
public class DirUtil {
    private static final long MIN_SDCARD_SIZE = 10 * 1024 * 1024;  //10M
    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";
    public static Context mContext = MyApplication.getContext();

    public static void init(Context context) {
        mContext = context;
    }

    public static boolean isSDCardAvailable() {
        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException e) { // (sh)it happens (Issue #660)
            externalStorageState = "";
        } catch (IncompatibleClassChangeError e) { // (sh)it happens too (Issue #989)
            externalStorageState = "";
        }
        return Environment.MEDIA_MOUNTED.equals(externalStorageState) && hasExternalStoragePermission();
    }

    /**
     * 获取存储卡的剩余容量，单位为字节
     *
     * @return availableSpare
     */
    private static long getSDCardAvailableStore() {
        String path = Environment.getExternalStorageDirectory().getPath();
        // 取得sdcard文件路径
        StatFs statFs = new StatFs(path);
        // 获取block的SIZE
        long blocSize = statFs.getBlockSize();
        // 可使用的Block的数量
        long availableBlock = statFs.getAvailableBlocks();
        return availableBlock * blocSize;
    }

    public static File appCacheDir = null;

    /**
     * 获取图片缓存目录
     *
     * @return
     */
    public static File getAppCacheDir() {
        if (null != appCacheDir) {
            return appCacheDir;
        }
        if (isSDCardAvailable()) {
            File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
            appCacheDir = new File(new File(dataDir, mContext.getPackageName()), "cache");
            if (!appCacheDir.exists()) {
                appCacheDir.mkdirs();
                try {
                    new File(appCacheDir, ".noMedia").createNewFile();
                } catch (IOException e) {
                }
            }
        }
        if (appCacheDir == null) {
            appCacheDir = mContext.getCacheDir();
        }
        if (appCacheDir == null) {
            String cacheDirPath = "/data/data/" + mContext.getPackageName() + "/cache/";
            appCacheDir = new File(cacheDirPath);
        }

        setRoot(appCacheDir);
        String address = appCacheDir.getAbsolutePath();
        String[] all = address.split("/");
        ///data/data/com.ifensi.tv/Plugin/com.ifensi.tvapp/data/com.ifensi.tvapp/cache/apks/com.tencent.qqmusictv.apk
        if (all.length > 0) {
            File fatherDir = appCacheDir;
            for (int i = 2; i < all.length; i++) {
                fatherDir = fatherDir.getParentFile();
                setRoot(fatherDir);
//                if(Const.logEnabled()) Const.logD("TAG", "输出来的父类-->" + fatherDir);
            }
        }
        return appCacheDir;
    }

    /**
     * 删除单个文件
     *
     * @param filePath 被删除文件的文件名
     * @return 文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 获取视频缓存目录根据文件名
     *
     * @return
     */
    public static File getAppCacheGifAdDir() {
        File appCacheDir = null;
        if (isSDCardAvailable()) {
            File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
            appCacheDir = new File(new File(dataDir, mContext.getPackageName()), "cache/gif_ad/");
            if (!appCacheDir.exists()) {
                appCacheDir.mkdirs();
                try {
                    new File(appCacheDir, ".noMedia").createNewFile();
                } catch (IOException e) {
                }
            }
        }
        if (appCacheDir == null) {
            appCacheDir = mContext.getCacheDir();
        }
        if (appCacheDir == null) {
            String cacheDirPath = "/data/data/" + mContext.getPackageName() + "/cache/gif_ad/";
            appCacheDir = new File(cacheDirPath);
        }
        return appCacheDir;
    }

    private static boolean hasExternalStoragePermission() {
        int perm = mContext.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
        return perm == PackageManager.PERMISSION_GRANTED;
    }

    public static void setRoot(File file) {
        if (file.exists()) {
//            if(Const.logEnabled()) Const.logD("TAG","父类文件file路径--》"+file.getAbsolutePath());
            file.setReadable(true, false);
            file.setWritable(true, false);
            file.setExecutable(true, false);
        } else {
//            if(Const.logEnabled()) Const.logD("TAG","父类文件不存在");
        }

    }
}
