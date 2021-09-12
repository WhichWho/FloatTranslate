package com.cnl.translate.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.cnl.translate.core.CaptureCore;
import com.cnl.translate.utils.MainModul;
import com.cnl.translate.utils.PreferenceQuery;

public abstract class BaseActivity extends Activity{
	
	public static final int REQUEST_MEDIA_PROJECTION = 0x10086;

	protected abstract void onCreate();
	protected void afterActivityResult(){}
	
    @Override
    protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		MainModul.initContext(this);
		PreferenceQuery.getInstance(this);
		initCapture();
		setStatusBarTransparent();
		onCreate();
    }

	private void initCapture(){
		MediaProjectionManager mMpMngr = getSystemService(MediaProjectionManager.class);
		startActivityForResult(mMpMngr.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(requestCode == REQUEST_MEDIA_PROJECTION){
			if(resultCode == Activity.RESULT_OK){
				CaptureCore.newInstance(this, data);
			}else{
				finish();
			}
		}
		afterActivityResult();
	}
	
	private void setStatusBarTransparent(){
		Window window = getWindow();
		if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
				View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
				View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
			window.getDecorView().setSystemUiVisibility(flags);
			window.setNavigationBarColor(Color.TRANSPARENT);
			window.setStatusBarColor(Color.TRANSPARENT);
		}
	}
	
}
