package com.cnl.translate.window;

import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.WindowManager.LayoutParams;

import com.cnl.translate.view.OCRTranslateView;

public class RecordWindow extends BaseWindow{

	private Rect r;
	private Point p;
	
	public RecordWindow(Rect rect, Point point){
		r = rect;
		p = point;
	}
	
	@Override
	protected BaseWindow.Option getOption(){
		Option opt = new Option();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			opt.type = LayoutParams.TYPE_APPLICATION_OVERLAY;
		} else {
			opt.type = LayoutParams.TYPE_SYSTEM_OVERLAY;
		}
		opt.flags = 
			LayoutParams.FLAG_TRANSLUCENT_STATUS |
			LayoutParams.FLAG_TRANSLUCENT_NAVIGATION |
			LayoutParams.FLAG_FULLSCREEN |
			LayoutParams.FLAG_LAYOUT_NO_LIMITS |
			LayoutParams.FLAG_NOT_TOUCHABLE |
			LayoutParams.FLAG_NOT_FOCUSABLE;
		opt.w = p.x;
		opt.h = p.y;
		return opt;
	}

	@Override
	protected View getView(){
		return new OCRTranslateView(ctx, r, p);
	}

}
