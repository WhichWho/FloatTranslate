package com.cnl.translate.window;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.graphics.PixelFormat;
import android.view.Gravity;

public abstract class BaseWindow implements BaseWindowImp{
	
	protected Context ctx;
	protected View self;
	protected LayoutParams lpm;
	protected WindowManager wm;
	protected boolean isShowing;
	
	protected abstract Option getOption();
	protected abstract View getView();
	
	@Override
	public void init(Context context){
		ctx = context;
		wm = ctx.getSystemService(WindowManager.class);
		Option opt = getOption();
		lpm = new WindowManager.LayoutParams();
		lpm.type = opt.type;
		lpm.format = PixelFormat.RGBA_8888;
		lpm.flags = opt.flags;
		lpm.gravity = Gravity.LEFT | Gravity.TOP;
		lpm.width = opt.w;
		lpm.height = opt.h;
		lpm.x = opt.x;
		lpm.y = opt.y;
		self = getView();
	}

	@Override
	public void show(){
		if(isShowing) return;
		wm.addView(self, lpm);
		isShowing = true;
	}
	
	@Override
	public void hide(){
		if(!isShowing) return;
		wm.removeViewImmediate(self);
		isShowing = false;
	}

	@Override
	public boolean isShowing(){
		return isShowing;
	}

	public static class Option{
		public int type;
		public int flags;
		public int w;
		public int h;
		public int x;
		public int y;
	}

}
