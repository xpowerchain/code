package com.open.androidtvwidget.bridge;

import com.open.androidtvwidget.view.MainUpView;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Anim Bridge
 * 
 *
 */
public abstract class BaseEffectBridge {

	public abstract void onInitBridge(MainUpView view);

	/**
	 */
	public abstract boolean onDrawMainUpView(Canvas canvas);

	/**
	 */
	public abstract void onOldFocusView(View oldFocusView, float scaleX, float scaleY);

	/**
	 */
	public abstract void onFocusView(View focusView, float scaleX, float scaleY);

	/**
	 */
	public void setMainUpView(MainUpView view) {
		
	}

	public MainUpView getMainUpView() {
		return null;
	}
	
	/**
	 */
	
	public void setUpRectResource(int resId) {
		
	}
	
	public void setUpRectDrawable(Drawable upRectDrawable) {
		
	}
	
	public Drawable getUpRectDrawable() {
		return null;
	}
	
	public void setDrawUpRectPadding(Rect rect) {
		
	}

	public void setDrawUpRectPadding(RectF rect) {

	}

	public RectF getDrawUpRect() {
		return null;
	}

	/**
	 */
	
	public void setShadowResource(int resId) {
	}

	public Drawable getShadowDrawable() {
		return null;
	}

	public void setShadowDrawable(Drawable shadowDrawable) {
	}
	
	public void setDrawShadowRectPadding(Rect rect) {
	}

	public void setDrawShadowRectPadding(RectF rect) {
	}

	public RectF getDrawShadowRect() {
		return null;
	}
	
}
