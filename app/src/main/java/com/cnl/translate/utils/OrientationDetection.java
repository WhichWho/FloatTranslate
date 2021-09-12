package com.cnl.translate.utils;

import android.content.Context;
import android.view.Display;
import android.view.WindowManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class OrientationDetection{
	
	private Display dd;
	private int rotation;
	private List<CallBack> cb;
	private Timer timer;

	public OrientationDetection(Context ctx){
		WindowManager wm = ctx.getSystemService(WindowManager.class);
		dd = wm.getDefaultDisplay();
		timer = new Timer();
		cb = new ArrayList<>();
	}
	
	public void enable(int period){
		timer.schedule(new MainTask(), 0, period);
	}
	
	public void disable(){
		timer.cancel();
	}

	private class MainTask extends TimerTask{
		@Override
		public void run(){
			int r = dd.getRotation();
			if(r != rotation){
				rotation = r;
				if(cb.size() != 0){
					for(CallBack cbi: cb){
						cbi.onChange(rotation);
					}
				}
			}
		}
	}
	
	public void addCallBack(CallBack call){
		cb.add(call);
	}
	
	public interface CallBack{
		public abstract void onChange(int rotation);
	}
	
}
