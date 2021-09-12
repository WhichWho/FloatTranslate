package com.cnl.translate.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import java.nio.ByteBuffer;

public class CaptureCore{
	
	private final static int MIN_CAPTURE_TIME = 1000;
	private static CaptureCore self;
	
	private Context ctx;
	private Intent key;
	private MediaProjection mpj;
	private ImageReader reader;
	private VirtualDisplay mVirtualDisplay;
	private Display dd;
	private DisplayMetrics dm;

	private Handler mHandler;
	private CaptureCallBack cb;

	private long lastCaptureTime = 0;
	private long realCaptureTime = 0;
	private Point lastRotation;

	private CaptureCore(Context ctx, Intent key){
		this.ctx = ctx;
		this.key = key;
		creatProject();
	}
	
	public static CaptureCore newInstance(){
		return self;
	}

	public static CaptureCore newInstance(Context ctx, Intent key){
		if(self == null){
			self = new CaptureCore(ctx, key);
		}
		return self;
	}

	public void capture(CaptureCallBack call){
		if((SystemClock.uptimeMillis() - lastCaptureTime) > MIN_CAPTURE_TIME){
			Point p = new Point();
			switch(dd.getRotation()){
				case Surface.ROTATION_0:
				case Surface.ROTATION_180:
					p.x = dm.widthPixels;
					p.y = dm.heightPixels;
					break;
				case Surface.ROTATION_90:
				case Surface.ROTATION_270:
					p.x = dm.heightPixels;
					p.y = dm.widthPixels;
					break;
			}
			cb = call;
			if(!p.equals(lastRotation)){
				lastRotation = p;
				initImageReader(p);
			}
			startVirtual(p);
		}
		lastCaptureTime = SystemClock.uptimeMillis();
	}

	private void creatProject(){
		MediaProjectionManager mpm = ctx.getSystemService(MediaProjectionManager.class);
		WindowManager wm = ctx.getSystemService(WindowManager.class);
		dd = wm.getDefaultDisplay();
		dm = new DisplayMetrics();
		dd.getRealMetrics(dm);
		mHandler = new Handler(ctx.getMainLooper());
		mpj = mpm.getMediaProjection(Activity.RESULT_OK, key);
	}
	
	private void initImageReader(Point p){
		if(reader != null){
			reader.close();
			reader = null;
		}
		reader = ImageReader.newInstance(p.x, p.y, PixelFormat.RGBA_8888, 1);
		reader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener(){
				@Override
				public void onImageAvailable(ImageReader p1){
					if((SystemClock.uptimeMillis() - realCaptureTime) > MIN_CAPTURE_TIME){
						Bitmap bmp = startCapture();
						cb.onCapture(bmp);
						cb = null;
					}else{
						reader.acquireLatestImage().close();
					}
					realCaptureTime = SystemClock.uptimeMillis();
				}
			}, mHandler);
	}

	private void startVirtual(final Point p){
		mHandler.post(new Runnable(){
				@Override
				public void run(){
					try{
						mVirtualDisplay = mpj.createVirtualDisplay(
							"capture_screen", p.x, p.y, dm.densityDpi, 
							DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, reader.getSurface(), null, null);
					}catch(Exception e){
						if(cb.onDefeated(e)){
							creatProject();
							capture(cb);
						}
					}
				}
			});
	}

	private Bitmap startCapture(){
		Image image = reader.acquireLatestImage();
		int width = image.getWidth();
		int height = image.getHeight();
		Image.Plane[] planes = image.getPlanes();
		ByteBuffer buffer = planes[0].getBuffer();
		int pixelStride = planes[0].getPixelStride();
		int rowStride = planes[0].getRowStride();
		int rowPadding = rowStride - pixelStride * width;
		Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
		bitmap.copyPixelsFromBuffer(buffer);
		Bitmap bitmapout = Bitmap.createBitmap(bitmap, 0, 0, width, height);
		image.close();
		bitmap.recycle();
		if(mVirtualDisplay != null){
			mVirtualDisplay.release();
			mVirtualDisplay = null;
		}
		return bitmapout;
	}

	public interface CaptureCallBack{
		public void onCapture(Bitmap bmp);
		public boolean onDefeated(Exception e);
	}
	
	public static abstract class SampleCallBack implements CaptureCallBack{

		@Override
		public boolean onDefeated(Exception e){
			return true;
		}

	}
}
