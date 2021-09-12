package com.cnl.translate.view;
import android.widget.ImageView;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

public class MovePointImageView extends ImageView {

	private float startX,startY;
	private float viewX,viewY;
	private OnTouchListener touchCallback;

	public MovePointImageView(Context ctx) {
		super(ctx);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				startX = event.getRawX();
				startY = event.getRawY();
				viewX = getX();
				viewY = getY();
				break;
			case MotionEvent.ACTION_MOVE:
				float offsetX = event.getRawX() - startX;
				float offsetY = event.getRawY() - startY;
				setX(viewX + offsetX);
				setY(viewY + offsetY);
				break;
		}
		if(touchCallback != null)
			touchCallback.onTouch(this, event);
		return true;
	}

	@Override
	public void setOnTouchListener(View.OnTouchListener l) {
		touchCallback = l;
	}

}
