package com.cnl.translate.view;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class MoveLineImageView extends ImageView {

	private float startX,startY;
	private float viewX,viewY;
	private boolean lockX;
	private OnTouchListener touchCallback;

	public MoveLineImageView(Context ctx, boolean lockX) {
		super(ctx);
		this.lockX = lockX;
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
				if (lockX) setY(viewY + offsetY);
				else setX(viewX + offsetX);
				break;
		}
		if (touchCallback != null)
			touchCallback.onTouch(this, event);
		return true;
	}

	@Override
	public void setOnTouchListener(View.OnTouchListener l) {
		touchCallback = l;
	}

}
