package com.cnl.translate.window;

import android.content.Context;

public interface BaseWindowImp{
	
	public abstract void init(Context ctx);
	
	public abstract void show();
	
	public abstract void hide();
	
	public abstract boolean isShowing();
}
