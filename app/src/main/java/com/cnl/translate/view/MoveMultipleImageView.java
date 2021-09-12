package com.cnl.translate.view;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class MoveMultipleImageView extends ImageView {

	private ImageView[] views;
	private float[] startXs,startYs;
	private float startX,startY;
	private boolean isMove;
	private long previousTime;
	private OnTouchListener touchCallback;
	private OnClickListener clickCallback;

	public MoveMultipleImageView(Context ctx, ImageView... unites) {
		super(ctx);
		views = unites;
		startXs = new float[unites.length];
		startYs = new float[unites.length];
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				startX = event.getRawX();
				startY = event.getRawY();
				int i = 0;
				for (ImageView img : views) {
					startXs[i] = img.getX();
					startYs[i++] = img.getY();
				}
				previousTime = System.currentTimeMillis();
				break;
			case MotionEvent.ACTION_MOVE:
				float offsetX = event.getRawX() - startX;
				float offsetY = event.getRawY() - startY;
				int j = 0;
				for (ImageView img : views) {
					img.setX(startXs[j] + offsetX);
					img.setY(startYs[j++] + offsetY);
				}
				if (Math.abs(offsetX) >= 35 || Math.abs(offsetY) >= 35) {
					isMove = true;
				} else {
					isMove = false;
				}
				break;
			case MotionEvent.ACTION_UP:
				if(!isMove && System.currentTimeMillis() - previousTime < 750){
					if(clickCallback != null){
						clickCallback.onClick(this);
					}
				}
				isMove = false;
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

	@Override
	public void setOnClickListener(View.OnClickListener l) {
		clickCallback = l;
	}

}
