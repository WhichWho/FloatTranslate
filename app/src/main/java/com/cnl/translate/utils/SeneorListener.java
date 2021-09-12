package com.cnl.translate.utils;

import android.content.Context;
import android.hardware.SensorManager;
import android.view.OrientationEventListener;

public class SeneorListener{

	private OrientationEventListener mOrientationListener;
	private int mOrientation;
	private CallBack cb;
	
	public SeneorListener(Context ctx){
		mOrientationListener = new OrientationEventListener(ctx, SensorManager.SENSOR_ORIENTATION){
			@Override
			public void onOrientationChanged(int orientation){
				if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
					return;
				}
				int newOrientation = ((orientation + 45) / 90 * 90) % 360;
				if (newOrientation != mOrientation) {
					mOrientation = newOrientation;
					if(cb != null){
						cb.onOrientationChange(mOrientation);
					}
				}
			}
		};
		if(mOrientationListener.canDetectOrientation()){
			mOrientationListener.enable();
		}else{
			mOrientationListener.disable();
		}
	}
	
	public void disable(){
		mOrientationListener.disable();
	}
	
	public void setCallBack(CallBack call){
		cb = call;
	}
	
	public interface CallBack{
		public void onOrientationChange(int orientation);
	}
	
}
