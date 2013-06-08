package com.quickbird.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;

import com.quickbird.controls.Constants;

public class ScreenShotUtil {

	// 获取指定Activity的截屏，保存到png文件
	public static Bitmap takeScreenShot(Activity activity) {
	    
	    // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        // 获取屏幕长和高
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();

		// View是你需要截图的View
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache(true);
		Bitmap b = view.getDrawingCache();
		
		float scalePercent = getScalePercent(activity);
		
		// 去掉标题栏
		Bitmap b1 = Bitmap.createBitmap(b, 0, statusBarHeight, width, height - statusBarHeight);
		
		Bitmap thumbBmp = Bitmap.createScaledBitmap(b1, (int)(width * scalePercent), (int)((height - statusBarHeight) * scalePercent), true);
		
		// 释放不需要的内存
		b.recycle();
		b1.recycle();
		view.destroyDrawingCache();
		
		return thumbBmp;
	}
	
	// 获取指定View的截屏，保存到png文件
	public static Bitmap takeViewShot(View view) {

		// 获取屏幕长和高
		int width = view.getWidth();
		int height = view.getHeight();

		// View是你需要截图的View
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache(true);
		Bitmap b = view.getDrawingCache();

		Bitmap thumbBmp = Bitmap.createScaledBitmap(b, width, height, true);
		// 释放不需要的内存
		b.recycle();
		view.destroyDrawingCache();
		return thumbBmp;
	}
	
	public static float getScalePercent(Activity activity) {
        // TODO Auto-generated method stub
	    DisplayMetrics metric = new DisplayMetrics();
	    activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int Dpi = metric.densityDpi;
        if(Dpi<=240)
            return 1;
        else if(Dpi<=320)
            return (float) 0.7;
        else 
            return (float) 0.4;
    }

    // Backup 20121219 by jz.lin
	public static Bitmap takeScreenShot_bak(Activity activity) {
	    
	    // View是你需要截图的View
	    View view = activity.getWindow().getDecorView();
	    view.setDrawingCacheEnabled(true);
	    view.buildDrawingCache();
	    Bitmap b1 = view.getDrawingCache();
	    
	    // 获取状态栏高度
	    Rect frame = new Rect();
	    activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
	    int statusBarHeight = frame.top;
	    
	    // 获取屏幕长和高
	    int width = activity.getWindowManager().getDefaultDisplay().getWidth();
	    int height = activity.getWindowManager().getDefaultDisplay()
	            .getHeight();
	    
	    // 去掉标题栏
	    // Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
	    Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight);
	    //Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, b1.getHeight()- statusBarHeight);
	    view.destroyDrawingCache();
	    return b;
	}

	// 保存到sdcard
	public static void savePic(Bitmap b, String strFileName) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(strFileName);
			if (null != fos) {
				b.compress(Bitmap.CompressFormat.PNG, 90, fos);
				fos.flush();
				fos.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 截屏
	public static void shoot(Activity a) {
		ScreenShotUtil.savePic(ScreenShotUtil.takeScreenShot(a), Constants.PIC_PRE_PATH_NAME);
	}
	
	// 截取指定View
	public static void captureView(View view) {
		ScreenShotUtil.savePic(ScreenShotUtil.takeViewShot(view), Constants.PIC_PRE_PATH_NAME);
		Bitmap bmp = BitmapFactory.decodeFile(Constants.PIC_PRE_PATH_NAME);
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 70, 70, true);
        bmp.recycle();
        ScreenShotUtil.savePic(thumbBmp, Constants.PIC_THUMB_PATH_NAME);
	}
}
