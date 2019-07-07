package com.lunary.owerwallet.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloader;
import com.lunary.owerwallet.R;
import com.lunary.owerwallet.activity.AboutUsActivity;
import com.lunary.owerwallet.model.HttpCallback;
import com.lunary.owerwallet.model.VersionUpdateBean;
import java.io.File;
import java.io.IOException;

public class UpdateManager {
	private static class LazyHolder {
		private static final UpdateManager INSTANCE = new UpdateManager();
	}

	public static final UpdateManager getInstance() {
		return LazyHolder.INSTANCE;
	}
	private boolean isDown = false;
	private AlertDialog dialog;

	public void getUpdate(final Context mContext){
		if (mContext.getClass().getSimpleName().contains(AboutUsActivity.class.getSimpleName())){
			showProgress(mContext);
		}

		NetWorkUtil.getUpdate(new HttpCallback() {
			@Override
			public void onFailure(Exception e) {
				if (mContext.getClass().getSimpleName().contains(AboutUsActivity.class.getSimpleName())){
					toast(mContext,"更新失败！请重试！");
					dismissProgress();
				}
			}

			@Override
			public void onResponse(String response) {
				dismissProgress();
				if (TextUtils.isEmpty(response)){
					if (mContext.getClass().getSimpleName().contains(AboutUsActivity.class.getSimpleName()))
						toast(mContext,"更新失败！请重试！");
					return;
				}
				final VersionUpdateBean ret = new Gson().fromJson(response, VersionUpdateBean.class);
				((Activity)mContext).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (!ret.isSuggestUpdate()){
							if (mContext.getClass().getSimpleName().contains(AboutUsActivity.class.getSimpleName()))
								toast(mContext,mContext.getString(R.string.noUpdates));
						}else{
							if (ret.isForceUpdate()){
								updateMustDialog(mContext,ret.getLastestVersionDesc(),ret.getLastestVersion(),ret.getLastestVersionUrl());
							}else {
								updateDialog(mContext,ret.getLastestVersionDesc(),ret.getLastestVersion(),ret.getLastestVersionUrl());
							}

						}
					}
				});

			}
		});
	}
	public void updateMustDialog(final Context mContext,String content,String version, final String url){
		final AlertDialog builder = new AlertDialog.Builder(mContext,R.style.AppTheme).create();
		final View view = View.inflate(mContext, R.layout.dialog_update, null);
		Window window = builder.getWindow();
		window.setGravity(Gravity.CENTER);
		//设置dialog弹出后会点击屏幕或物理返回键，dialog不消失
		builder.setCanceledOnTouchOutside(true);
		builder.show();
		builder.setCancelable(false);
		window.setContentView(view);

		//获得window窗口的属性
		WindowManager.LayoutParams params = window.getAttributes();
		//设置窗口宽度为充满全屏
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;//如果不设置,可能部分机型出现左右有空隙,也就是产生margin的感觉
		//设置窗口高度为包裹内容
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;//就是这个属性导致window后所有的东西都成暗淡
		params.dimAmount = 0.5f;//设置对话框的透明程度背景(非布局的透明度)
		//将设置好的属性set回去
		window.setAttributes(params);
		TextView txt = (TextView) view.findViewById(R.id.content);
		TextView update = (TextView) view.findViewById(R.id.update);
		TextView dismiss = (TextView) view.findViewById(R.id.dismiss);
		dismiss.setVisibility(View.GONE);
		txt.setText("前方发现新版本: V" + version+"\n\n"+content);
		update.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				builder.dismiss();
				downLoadApk(url,mContext,"com.lunary.owerwallet.apk");
			}
		});
	}
	public void updateDialog(final Context mContext,String content,String version, final String url){
		final AlertDialog builder = new AlertDialog.Builder(mContext,R.style.AppTheme).create();
		final View view = View.inflate(mContext, R.layout.dialog_update, null);
		Window window = builder.getWindow();
		window.setGravity(Gravity.CENTER);
		//设置dialog弹出后会点击屏幕或物理返回键，dialog不消失
		builder.setCanceledOnTouchOutside(true);
		builder.show();
		window.setContentView(view);

		//获得window窗口的属性
		WindowManager.LayoutParams params = window.getAttributes();
		//设置窗口宽度为充满全屏
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;//如果不设置,可能部分机型出现左右有空隙,也就是产生margin的感觉
		//设置窗口高度为包裹内容
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;//就是这个属性导致window后所有的东西都成暗淡
		params.dimAmount = 0.5f;//设置对话框的透明程度背景(非布局的透明度)
		//将设置好的属性set回去
		window.setAttributes(params);
		TextView txt = (TextView) view.findViewById(R.id.content);
		TextView update = (TextView) view.findViewById(R.id.update);
		TextView dismiss = (TextView) view.findViewById(R.id.dismiss);
		txt.setText("前方发现新版本: V" + version+"\n\n"+content);
		update.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				builder.dismiss();
				downLoadApk(url,mContext,"com.lunary.owerwallet.apk");
			}
		});
		dismiss.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				builder.dismiss();
			}
		});
	}
	/*
	 * 从服务器中下载APK 
	 */  
	public void downLoadApk(final String url,final Context mContext,final String fileName) {
		if (isDown){
			return;
		}
		final ProgressDialog pd;    //进度条对话框   
		pd = new ProgressDialog(mContext);  
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("正在下载更新");
		pd.setProgressNumberFormat(null);
		pd.show();
		pd.setCancelable(false);
		pd.setMax(100);
		new Thread(){  
			@Override  
			public void run() {  
				try {  
					getFileFromServer(url, pd,mContext,fileName);
				} catch (Exception e) {
					e.printStackTrace();  
				}  
			}}.start();  
	}

	private void getFileFromServer(String path, final ProgressDialog pd, final Context mContext, String fileName){
		isDown = true;
		final File file = new File(mContext.getFilesDir(),fileName);
		if(!file.exists()){
			file.mkdirs();
		}else{
			file.delete();
		}
		Log.e("TAD","file.getAbsolutePath():"+file.getAbsolutePath());
		final BaseDownloadTask task = FileDownloader.getImpl().create(path)
				.setPath(file.getAbsolutePath())
				.setListener(new SimpleDownloadListener() {
					private int mLastProgress;

					@Override
					protected void completed(BaseDownloadTask task) {

						pd.dismiss(); //结束掉进度条对话框
						isDown = false;
						toast(mContext,"下载完成了....");
						Log.e("TAD",file.length()/1024+" ==== ");
						String[] command = {"chmod", "777", file.getPath() };
						ProcessBuilder builder = new ProcessBuilder(command);
						try {
							builder.start();
						} catch (IOException e) {
							e.printStackTrace();
						}
						if(Build.VERSION.SDK_INT >= 24) {//判读版本是否在7.0以上
							Uri apkUri = FileProvider.getUriForFile(mContext, "com.dafangya.app.pro.fileprovider", file);
							Intent install = new Intent(Intent.ACTION_VIEW);
							install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
							install.setDataAndType(apkUri, "application/vnd.android.package-archive");
							mContext.startActivity(install);
						}else {
							installApk(file, mContext);
						}

					}

					@Override
					protected void error(BaseDownloadTask task, Throwable e) {
						pd.dismiss(); //结束掉进度条对话框
						isDown = false;
						toast(mContext,"更新失败！请重试！");
						if(file.exists()){
							file.delete();
						}
					}

					@Override
					protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
						super.progress(task, soFarBytes, totalBytes);
						try {
							if (0 == totalBytes || 0 == soFarBytes) {
								return;
							}
							int progress = (int) (((float) soFarBytes / totalBytes) * 100);
							if (mLastProgress == progress) {
								return;
							}
							pd.setProgress(progress);
							mLastProgress = progress;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
		task.start();
	}
	//安装apk    
	protected void installApk(File file,final Context mContext) {
		Intent intent = new Intent(); 
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		//执行动作   
		intent.setAction(Intent.ACTION_VIEW);  
		//执行的数据类型   
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");//编者按：此处Android应为android，否则造成安装不了    
		mContext.startActivity(intent);
	}


	public void showProgress(final Context mContext) {
		//R.style.***一定要写，不然不能充满整个屏宽，引用R.style.AppTheme就可以
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				dialog = new AlertDialog.Builder(mContext, R.style.AppTheme).create();
				final View view = View.inflate(mContext, R.layout.dialog_progress, null);
				Window window = dialog.getWindow();
				window.setGravity(Gravity.CENTER);
				//设置dialog弹出后会点击屏幕或物理返回键，dialog不消失
				dialog.setCanceledOnTouchOutside(true);
				dialog.show();
				window.setContentView(view);

				//获得window窗口的属性
				WindowManager.LayoutParams params = window.getAttributes();
				//设置窗口宽度为充满全屏
				params.width = (int) mContext.getResources().getDimension(R.dimen.dp_165);//如果不设置,可能部分机型出现左右有空隙,也就是产生margin的感觉
				//设置窗口高度为包裹内容
				params.height = WindowManager.LayoutParams.WRAP_CONTENT;
				params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;//就是这个属性导致window后所有的东西都成暗淡
				params.dimAmount = 0.5f;//设置对话框的透明程度背景(非布局的透明度)
				//将设置好的属性set回去
				window.setAttributes(params);
			}
		});
	}
	private void dismissProgress() {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				if (dialog!=null && dialog.isShowing()){
					dialog.dismiss();
				}
				dialog = null;
			}
		});
	}
	public void progressIsNull(){
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				if (dialog!=null && dialog.isShowing()){
					dialog.dismiss();
				}
				dialog = null;
			}
		});
	}

	private void toast(final Context mContext, final String message){
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
			}
		});

	}
}
