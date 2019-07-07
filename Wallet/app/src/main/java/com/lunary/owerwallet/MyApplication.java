package com.lunary.owerwallet;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.multidex.MultiDex;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Supplier;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.DefaultExecutorSupplier;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.liulishuo.filedownloader.FileDownloader;
import com.lunary.owerwallet.activity.CreateWalletActivity;
import com.lunary.owerwallet.activity.MainActivity;
import com.lunary.owerwallet.utils.Utils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Administrator on 2018/8/2.
 */
public class MyApplication extends Application {
    static Context context;
    public static ArrayList<Activity> list = new ArrayList<Activity>();

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        FileDownloader.init(this);
        Fresco.initialize(MyApplication.this, getConfigureCaches(MyApplication.this));
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    public static void addActivity(Activity activity){

        list.add(activity);
    }
    public static void removeActivity(Activity activity){

        list.add(activity);
    }
    public static void closeAllActivity(){

        for(int i=0;i<list.size();i++){
            Activity activity = list.get(i);
            activity.finish();
        }
    }

    public static void onlyMainActivity(){

        for(int i=0;i<list.size();i++){
            Activity activity = list.get(i);
            if(!activity.getComponentName().toString().contains(MainActivity.class.getSimpleName())){
                activity.finish();
            }
        }
    }

    public static final int MAX_DISK_CACHE_SIZE = 100 * ByteConstants.MB;
    public static final int MAX_SMALL_IMAGE_DISK_CACHE_SIZE = 100 * ByteConstants.MB;
    public static final int MAX_MEMORY_CACHE_SIZE = 15 * ByteConstants.MB;

    private ImagePipelineConfig getConfigureCaches(Context context) {
        final MemoryCacheParams bitmapCacheParams = new MemoryCacheParams(
                MAX_MEMORY_CACHE_SIZE,// 内存缓存中总图片的最大大小,以字节为单位。
                Integer.MAX_VALUE,// 内存缓存中图片的最大数量。
                MAX_MEMORY_CACHE_SIZE,// 内存缓存中准备清除但尚未被删除的总图片的最大大小,以字节为单位。
                Integer.MAX_VALUE,// 内存缓存中准备清除的总图片的最大数量。
                Integer.MAX_VALUE);// 内存缓存中单个图片的最大大小。

        Supplier<MemoryCacheParams> mSupplierMemoryCacheParams = new Supplier<MemoryCacheParams>() {
            @Override
            public MemoryCacheParams get() {
                return bitmapCacheParams;
            }
        };
        ImagePipelineConfig.Builder builder = ImagePipelineConfig.newBuilder(context);
        builder.setBitmapMemoryCacheParamsSupplier(mSupplierMemoryCacheParams);
        builder.setBitmapsConfig(Bitmap.Config.RGB_565);
        builder.setDownsampleEnabled(true);
        //默认图片的磁盘配置
        String temp = Utils.getFrescoMainCacheDir();
        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(context)
                .setBaseDirectoryPath(new File(temp))
                .setMaxCacheSize(MAX_DISK_CACHE_SIZE)
                .build();
        builder.setMainDiskCacheConfig(diskCacheConfig);

        //小图片的磁盘配置（用于广告等不经常发生变化的图片的缓存）
        temp = Utils.getFrescoSmallImageCacheDir();
        DiskCacheConfig diskSmallCacheConfig = DiskCacheConfig.newBuilder(context)
                .setBaseDirectoryPath(new File(temp))
                .setMaxCacheSize(MAX_SMALL_IMAGE_DISK_CACHE_SIZE)
                .build();
        builder.setSmallImageDiskCacheConfig(diskSmallCacheConfig);
        return builder.build();
    }
}
