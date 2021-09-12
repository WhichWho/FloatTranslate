package com.cnl.translate.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.SearchView.OnCloseListener;

import com.cnl.translate.window.FloatCloseWindow;

public class FloatMoveImageView extends ImageView{

	private float startX,startY;
	private float viewX,viewY;
	private boolean isMove = false;
	private long previousTime;
	private OnClickListener mClickListener;
	private OnCloseListener mCloseListener;
	private OnChangeListener mChangeListener;
	private WindowManager windowManager;
	private LayoutParams mLayoutParams;
	private ViewGroup shadow;
	private ImageView closeImage;
	private FloatCloseWindow mCloseWindow;
	private Rect otherViewRect;
	private Rect viewRect;

	public FloatMoveImageView(Context context, LayoutParams mLayoutParams){
		super(context);
		this.windowManager = context.getSystemService(WindowManager.class);
		this.mLayoutParams = mLayoutParams;

		mCloseWindow = new FloatCloseWindow();
		mCloseWindow.init(getContext());
		otherViewRect = new Rect();
		viewRect = new Rect();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event){
		switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				previousTime = System.currentTimeMillis();
				startX = event.getRawX();
				startY = event.getRawY();
				viewX = mLayoutParams.x;
				viewY = mLayoutParams.y;
				mCloseWindow.show();
				shadow = mCloseWindow.optView();
				closeImage = (ImageView) shadow.getChildAt(0);
				getRootView().getGlobalVisibleRect(viewRect);
				closeImage.getGlobalVisibleRect(otherViewRect);
				break;
			case MotionEvent.ACTION_MOVE:
				float offsetX = event.getRawX() - startX;
				float offsetY = event.getRawY() - startY;
				if(Math.abs(offsetX) >= 35 || Math.abs(offsetY) >= 35){
					isMove = true;
					mLayoutParams.x = (int)(viewX + offsetX);
					mLayoutParams.y = (int)(viewY + offsetY);
					windowManager.updateViewLayout(getRootView(), mLayoutParams);
					if(mChangeListener != null){
						mChangeListener.onChange(mLayoutParams.x, mLayoutParams.y);
					}
				}else{
					isMove = false;
				}
				if(otherViewRect.isEmpty())
					closeImage.getGlobalVisibleRect(otherViewRect);
				viewRect.offsetTo(mLayoutParams.x, mLayoutParams.y);
				if(Rect.intersects(viewRect, otherViewRect))
					closeImage.setColorFilter(Color.RED);
				else closeImage.clearColorFilter();
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				if(!isMove && System.currentTimeMillis() - previousTime < 750){
					if(mClickListener != null){
						mClickListener.onClick(this);
					}
				}
				mCloseWindow.hide();
				viewRect.offsetTo(mLayoutParams.x, mLayoutParams.y);
				if(Rect.intersects(viewRect, otherViewRect)){
					if(mCloseListener != null){
						if(mCloseListener.onClose()){
							windowManager.removeViewImmediate(getRootView());
						}
					}else{
						windowManager.removeViewImmediate(getRootView());
					}
				}
				otherViewRect.setEmpty();
				isMove = false;
				break;
		}
		return true;
	}
	
	public void notificationRotation(){
		mCloseWindow = new FloatCloseWindow();
		mCloseWindow.init(getContext());
	}

	@Override
	public void setOnClickListener(OnClickListener click){
		super.setOnClickListener(click);
		mClickListener = click;
	}

	public void setOnCloseListener(OnCloseListener close){
		mCloseListener = close;
	}
	
	public void setOnChangeListener(OnChangeListener change){
		mChangeListener = change;
	}
	
	public interface OnChangeListener{
		public abstract void onChange(int x, int y);
	}
	
}
