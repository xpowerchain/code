# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\tool\eclipse-android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-optimizationpasses 9                                                           # 指定代码的压缩级别
-dontusemixedcaseclassnames                                                     # 是否使用大小写混合
-dontskipnonpubliclibraryclasses                                                # 是否混淆第三方jar
-dontpreverify                                                                  # 混淆时是否做预校验
-keepattributes SourceFile,LineNumberTable										# 混淆号错误信息里带上代码行
-verbose                                                                        # 混淆时是否记录日志
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*        # 混淆时所采用的算法

-repackageclasses ''
-allowaccessmodification
-dontwarn


# keep 4大组件， application
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# 自定义的view类
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}
#To maintain custom components names that are used on layouts XML:
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
}
-keep public class * extends android.view.View {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keep public class * extends android.view.View {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Serializables类
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# support v7类
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }

#Compatibility library
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Fragment

# 第三方包类
#-libraryjars libs/jackson-all-1.9.11.jar
#-keep class org.codehaus.jackson.**{*;}

# keep 类成员
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# keep parcelable
-keepclassmembers class * implements android.os.Parcelable {
    static android.os.Parcelable$Creator CREATOR;
}

#Keep the R
-keepclassmembers class **.R$* {
    public static <fields>;
}

-keepclassmembers public class * extends android.view.View {
  void set*(***);
  *** get*();
}

#Maintain java native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

#Maintain enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Remove Logging
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** w(...);
    public static *** v(...);
    public static *** i(...);
}

-ignorewarnings



# others =============================================================================== 参考

# Android Support Library类
-keep class android.** {*;}

# 不混淆的第三方包
#-libraryjars libs/juniversalchardet-1.0.3.jar
#-libraryjars libs/jaudiotagger-2.0.3.jar
#-libraryjars libs/jackson-all-1.9.11.jar
#-keep class com.xui.launcher.icarmusic.view.**{*; }
#-keep class org.mozilla.universalchardet.**{*;}
#-keep class org.jaudiotagger.**{*;}
#-keep class org.codehaus.jackson.**{*;}
#
#
#-libraryjars libs/android_api.jar
#-libraryjars libs/baidumapapi_v2_2_0.jar
#-libraryjars libs/armeabi.jar
#-libraryjars libs/locSDK_3.1.jar
#-libraryjars libs/pushservice-2.4.0.jar
#
## Baidu Map
#-keep class com.baidu.mapapi.** {*; }
#-keep class com.baidu.location.** {*; }
#-keep class com.baidu.platform.** {*; }
#-keep class com.baidu.vi.** {*; }
#-keep class vi.com.gdi.bgl.android.java.** {*; }
#
## Baidu Push
#-keep class com.baidu.android.** {*; }
#-keep class com.baidu.loctp.** {*; }
#
#-keep class com.nineoldandroids.** {*; }
#
## Baidu Tongji
#-keep class com.baidu.a.a.a.** {*; }
#-keep class com.baidu.mobstat.** {*; }
#
## slidingmenu
#-keep class com.jeremyfeinstein.slidingmenu.lib.** {*; }
#
## listviewAnimations
#-keep class com.haarman.listviewanimations.** {*; }
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

 -keep class com.nostra13.universalimageloader.** { *; }

 -keep class com.google.zxing.** { *; }
 -keep class com.baidu.** { *; }
 -keep class com.open.androidtvwidget.** { *; }
 -keep class master.flame.danmaku.** { *; }
 -keep class io.github.clendy.leanback.** { *; }
 -keep class org.videolan.libvlc.** { *; }
 -keep class com.umeng.message.lib.** { *; }
 -keep class demo.playerlib.** { *; }
-dontwarn com.zhy.autolayout.**
-keep class com.zhy.autolayout.** { *; }
-keep class com.zhy.autolayout.widget.** { *; }
-keep class org.json.simple.** { *; }
-keep class com.liulishuo.filedownloader.** { *; }
-keep class com.bumptech.glide.** { *; }
-keep class com.umeng.** { *; }
-dontwarn com.taobao.**
-keep class com.taobao.** { *; }
-dontwarn com.alibaba.**
-keep class com.alibaba.** { *; }
-keep class com.ut.device.** { *; }
-keep class com.ta.utdid2.** { *; }

-dontwarn java.nio.file.**
-keep class java.nio.file.** { *; }
-dontwarn org.android.spdy.**
-keep class org.android.spdy.** { *; }

-dontwarn sun.misc.**
-keep class sun.misc.** { *; }
-dontwarn rx.**
-keep class rx.** { *; }
-dontwarn java.lang.invoke.**
-keep class java.lang.invoke.** { *; }
-dontwarn org.codehaus.mojo.**
-keep class org.codehaus.mojo.** { *; }

-keep class org.json.** { *; }

-dontobfuscate
-dontoptimize
-dontwarn com.taobao.**
-dontwarn anet.channel.**
-dontwarn anetwork.channel.**
-dontwarn org.android.**
-dontwarn org.apache.thrift.**
-dontwarn com.xiaomi.**
-dontwarn com.huawei.**

-keepattributes *Annotation*

-keep class com.taobao.** {*;}
-keep class org.android.** {*;}
-keep class anet.channel.** {*;}
-keep class com.umeng.** {*;}
-keep class com.xiaomi.** {*;}
-keep class com.huawei.** {*;}
-keep class org.apache.thrift.** {*;}

-keep class com.alibaba.sdk.android.**{*;}
-keep class com.ut.**{*;}
-keep class com.ta.**{*;}

-keep public class **.R$*{
   public static final int *;
}

#（可选）避免Log打印输出
-assumenosideeffects class android.util.Log {
   public static *** v(...);
   public static *** d(...);
   public static *** i(...);
   public static *** w(...);
 }

-keep class com.baidu.kirin.** { *; }
-keep class com.baidu.mobstat.** { *; }
 -keep class com.baidu.bottom.** { *; }
 -dontwarn com.igexin.**
 -keep class com.igexin.**{*;}
 -keep class com.flurry.** { *; }
-dontwarn com.flurry.**
-keepattributes *Annotation*,EnclosingMethod
-keepclasseswithmembers class * {
public <init>(android.content.Context, android.util.AttributeSet, int);
}
# Google Play Services library
-keep class * extends java.util.ListResourceBundle {
   protected Object[][] getContents();
}
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
   public static final *** NULL;
}
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
   @com.google.android.gms.common.annotation.KeepName *;
}
-keepnames class * implements android.os.Parcelable {
   public static final ** CREATOR;
}
-keep class com.ksyun.media.player.**{ *; }
-keep class com.ksy.statlibrary.**{ *;}
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
#ACRA specifics
# we need line numbers in our stack traces otherwise they are pretty useless
-renamesourcefileattribute SourceFile

# ACRA needs "annotations" so add this...
-keepattributes *Annotation*
-keep class com.tencent.** { *; }
-keep class com.google.gson.** { *; }
