package com.cnl.translate.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import java.io.PrintWriter;
import java.io.StringWriter;

public class MainModul{
	
	private static Context ctx;

	public static void initContext(Context context){
		ctx = context;
	}

	public static Bitmap RoughGlass(Context ctx, Bitmap fbmp, int radius){
//		Bitmap bitmap=fbmp.copy(Bitmap.Config.ARGB_8888, true);
//		RenderScript rs = RenderScript.create(ctx);
//		Allocation input = Allocation.createFromBitmap(rs, bitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
//		Allocation output = Allocation.createTyped(rs, input.getType());
//		Allocation overlayAlloc = Allocation.createFromBitmap(rs, bitmap);
//		ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, overlayAlloc.getElement());
//		script.setRadius(radius);
//		script.setInput(input);
//		script.forEach(output);
//		output.copyTo(bitmap);
//		return bitmap;
		StackBlurManager blur = new StackBlurManager(fbmp);
		return blur.process(radius);
	}

	public static Bitmap ReduceLight(Bitmap bitmap){
		return ReduceLight(bitmap, 0x77);
	}
	
	public static Bitmap ReduceLight(Bitmap bitmap, int radius){
		if(!bitmap.isMutable()){
			bitmap = bitmap.copy(bitmap.getConfig(), true);
		}
		Canvas canvas = new Canvas(bitmap);
		canvas.drawARGB(radius, 0, 0, 0);
		return bitmap;
	}

	public static Bitmap bitmap2Gray(Bitmap bmSrc) {
		int width = bmSrc.getWidth();
		int height = bmSrc.getHeight();
		Bitmap bmpGray = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		Canvas c = new Canvas(bmpGray);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmSrc, 0, 0, paint);
		return bmpGray;
	}

	public static Bitmap FitView(int vw, int vh, Bitmap bitmap){
		int pw = bitmap.getWidth();
		int ph = bitmap.getHeight();
		float scale = Math.max((float)vw / pw, (float)vh / ph);
		Matrix mat = new Matrix();
		mat.postScale(scale, scale);
		Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, pw, ph, mat, true);
		pw = bmp.getWidth();
		ph = bmp.getHeight();
		int x = (pw - vw) / 2;
		int y = (ph - vh) / 2;
		Bitmap rBitmap = Bitmap.createBitmap(bmp, x, y, vw, vh);
		bmp.recycle();
		return rBitmap;
	}
	
	public static int getHeight1(){
		int result = 0;
		int resourceId = ctx.getResources().getIdentifier("status_bar_height", "dimen", "android");  
		if(resourceId > 0){  
			result = ctx.getResources().getDimensionPixelSize(resourceId);  
		}  
		return result;  
	}

	public static int getHeight2(){
		int result = 0;
        if(navigationBarExist2()){
			int id = ctx.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            result = ctx.getResources().getDimensionPixelSize(id);
        }
		return result;
    }
	
	public static boolean navigationBarExist2() {
        WindowManager windowManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        Display d = windowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            d.getRealMetrics(realDisplayMetrics);
        }

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }
	
	public static int getScreenW(){
		WindowManager mManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
		return mManager.getDefaultDisplay().getWidth();
	}
	
	public static int getScreenH(){
		WindowManager mManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
		return mManager.getDefaultDisplay().getHeight();
	}
	
	public static String e(Throwable e){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}
	
}
