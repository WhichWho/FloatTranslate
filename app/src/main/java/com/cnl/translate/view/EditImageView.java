package com.cnl.translate.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.RegionIterator;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.RectF;

public class EditImageView extends FrameLayout {

	private static final int BUTTON_SIZE = 50;

	private TextView debuger;
	private ImageView realImg;
	private ImageView moveImg;
	private ImageView backImg;
	private Bitmap srcBmp;
	private MovePointImageView[] pointImg = new MovePointImageView[4];
	private MoveLineImageView[] lineImg = new MoveLineImageView[4];
	private float tanImg;
	private CallBack cb;

	public EditImageView(Context ctx, int tagW, int tagH, Bitmap src) {
		super(ctx);
		initData(ctx, tagW, tagH, src);
		initView(ctx, tagW, tagH, src);
	}
	
	public void setCallBack(CallBack call){
		cb = call;
	}
	
	

	private void initData(Context ctx, int tagW, int tagH, Bitmap src) {
		tanImg = tagH / tagW;
		srcBmp = src;
	}

	private void initView(Context ctx, int tagW, int tagH, Bitmap src) {
		setLayoutParams(new LayoutParams(-1, -1));
		setBackgroundColor(Color.BLACK);

		realImg = new ImageView(ctx);
		realImg.setImageBitmap(src);
		realImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		realImg.setAdjustViewBounds(true);
		realImg.setLayoutParams(new LayoutParams(-1, -1));
		addView(realImg);

		for (int i=0;i < pointImg.length;i++) {
			pointImg[i] = new MovePointImageView(ctx);
			lineImg[i] = new MoveLineImageView(ctx, !isDouble(i));
		}

		moveImg = new MoveMultipleImageView(ctx, pointImg);
		moveImg.setOnTouchListener(new OnTouchListener(){
				@Override
				public boolean onTouch(View p1, MotionEvent event) {
					lineAll();
					updataMoveView();
					return false;
				}
			});
		moveImg.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1) {
					getResult();
				}
			});
		LayoutParams mlpm = new LayoutParams(tagW, tagH);
		moveImg.setLayoutParams(mlpm);
		addView(moveImg);

		backImg = new ImageView(ctx);
		backImg.setLayoutParams(new LayoutParams(-1, -1));
		backImg.setAlpha(0.7f);
		addView(backImg);


		OnTouchListener touch1 = new PointOnTouch();
		OnTouchListener touch2 = new LineOnTouch();

		int[] testColors = new int[]{
			Color.RED, Color.GREEN,
			Color.BLUE, Color.YELLOW
		};
		int[] positions = new int[]{
			-1,-1,
			1,-1,
			1,1,
			-1,1
		};

		for (int i=0;i < pointImg.length;i++) {

			pointImg[i].setOnTouchListener(touch1);
			lineImg[i].setOnTouchListener(touch2);

			pointImg[i].setBackgroundColor(testColors[i] | ~0x666666);
			lineImg[i].setBackgroundColor(testColors[i] & ~0x666666);

			pointImg[i].setTag(i);
			lineImg[i].setTag(i);

			pointImg[i].setAlpha(0.7f);
			lineImg[i].setAlpha(0.5f);

			LayoutParams lpm = new LayoutParams(BUTTON_SIZE, BUTTON_SIZE);
			lpm.gravity = Gravity.CENTER;
			lpm.leftMargin = tagW / 2 * positions[i * 2];
			lpm.topMargin = tagH / 2 * positions[i * 2 + 1];
			pointImg[i].setLayoutParams(lpm);

			int w = 0, h = 0;
			if (isDouble(i)) {
				w = BUTTON_SIZE;
				h = tagH - BUTTON_SIZE;
			} else {
				w = tagW - BUTTON_SIZE;
				h = BUTTON_SIZE;
			}
			lineImg[i].setLayoutParams(new LayoutParams(w, h));

			addView(pointImg[i]);
			addView(lineImg[i]);
		}
		post(new Runnable(){
				@Override
				public void run() {
					lineAll();
					updataMoveView();
				}
			});

		debuger = new TextView(ctx);
		debuger.setTextColor(Color.GRAY);
		addView(debuger);

	}
	
	private void getResult() {

		int w0 = srcBmp.getWidth();
		int h0 = srcBmp.getHeight();
		int w1 = fullRect.width();
		int h1 = fullRect.height();

		float sx = (float) w1 / w0;
		float sy = (float) h1 / h0;
		float ss = Math.min(sx, sy);
		float sw = w0 * ss;
		float sh = h0 * ss;

		float offx = (w1 - sw) / 2 + fullRect.left;
		float offy = (h1 - sh) / 2 + fullRect.top;

		RectF src = new RectF(offx, offy, offx + sw, offy + sh);
		RectF lim = new RectF(currRect);
		lim.offsetTo(lim.left + fullRect.left, lim.top + fullRect.top);
		src.intersect(lim);
		src.offsetTo(src.left - offx, src.top - offy);

		float scale = 1 / ss;
		src.left = (float) Math.ceil(src.left * scale);  
        src.top = (float) Math.ceil(src.top * scale);  
        src.right = (float) Math.floor(src.right * scale);  
        src.bottom = (float) Math.floor(src.bottom * scale);  

		Rect srcR = new Rect((int) src.left, (int) src.top, (int) src.right, (int) src.bottom);
//		RectF dst = new RectF(0, 0, src.width(), src.height());

//		Bitmap outbmp = Bitmap.createBitmap((int) src.width(), (int) src.height(), Bitmap.Config.ARGB_8888);
//		Canvas cv = new Canvas(outbmp);
//		cv.drawBitmap(srcBmp, srcR, dst, null);

//		ImageView img = new ImageView(getContext());
//		img.setImageBitmap(outbmp);
//		new AlertDialog.Builder(getContext()).setView(img).show();

		if(cb != null){
			cb.onResult(srcR);
		}
	}

	private int getOpposite(int x) {
		return x > 1 ? x - 2 : x + 2;
	}

	private int getDown(int x) {
		return x == 3 ? 0 : x + 1;
	}

	private int getUp(int x) {
		return x == 0 ? 3 : x - 1;
	}

	private boolean isDouble(int x) {
		return x % 2 == 0;
	}

	private void lineAll() {
		for (int i=0;i < lineImg.length;i++) {
			line(i);
		}
	}

	private void line(int index) {
		View s = lineImg[index];
		View v1 = pointImg[index];
		View v2 = pointImg[getUp(index)];
		LayoutParams lpm = (FrameLayout.LayoutParams) s.getLayoutParams();
		if (isDouble(index)) {
			int h = (int) Math.abs(v1.getY() - v2.getY()) - BUTTON_SIZE;
			lpm.height = h > 0 ? h : 0;
			s.setX(v1.getX());
			s.setY(Math.min(v1.getY(), v2.getY()) + BUTTON_SIZE);
		} else {
			int w = (int) Math.abs(v1.getX() - v2.getX()) - BUTTON_SIZE;
			lpm.width = w > 0 ? w : 0;
			s.setY(v2.getY());
			s.setX(Math.min(v1.getX(), v2.getX()) + BUTTON_SIZE);
		}
		s.setLayoutParams(lpm);
	}

	private void updataMoveView() {
		float x0 = Integer.MAX_VALUE;
		float y0 = Integer.MAX_VALUE;
		float x1 = Integer.MIN_VALUE;
		float y1 = Integer.MIN_VALUE;
		for (View v:pointImg) {
			x0 = Math.min(x0, v.getX());
			y0 = Math.min(y0, v.getY());
			x1 = Math.max(x1, v.getX());
			y1 = Math.max(y1, v.getY());
		}
		int w = (int) (x1 - x0);
		int h = (int) (y1 - y0);
		LayoutParams lpm = (FrameLayout.LayoutParams) moveImg.getLayoutParams();
		lpm.width = w;
		lpm.height = h;
		lpm.leftMargin = (int) x0 + BUTTON_SIZE / 2 - getPaddingLeft();
		lpm.topMargin = (int) y0 + BUTTON_SIZE / 2 - getPaddingTop();
		moveImg.setLayoutParams(lpm);
		debuger.setText(String.format("x %.2f\ny %.2f\nx %.2f\ny %.2f\nw %d\nh %d", x0, y0, x1, y1, w, h));
		updataBackImg();
	}

	private Bitmap backBmp;
	private Paint backPaint;
	private Canvas backCanvas;
	private Region outRegion;
	private Rect fullRect;
	private Rect currRect;
	private Rect iterRect;

	private void updataBackImg() {
		if (backBmp == null) {
			iterRect = new Rect();
			fullRect = new Rect();
			realImg.getGlobalVisibleRect(fullRect);
			fullRect.offsetTo(0, 0);
			currRect = new Rect();
			outRegion = new Region(fullRect);
			moveImg.getGlobalVisibleRect(currRect);
			backBmp = Bitmap.createBitmap(fullRect.width(), fullRect.height(), Bitmap.Config.ARGB_8888);
			backCanvas = new Canvas(backBmp);
			backPaint = new Paint();
			backPaint.setColor(Color.BLACK);
			backImg.setImageBitmap(backBmp);
		}

		long t0 = System.nanoTime();
		backBmp.eraseColor(Color.TRANSPARENT);
		long t1 = System.nanoTime();

		LayoutParams lpm = (FrameLayout.LayoutParams) moveImg.getLayoutParams();
		currRect.left = lpm.leftMargin;
		currRect.top = lpm.topMargin;
		currRect.right = currRect.left + lpm.width;
		currRect.bottom = currRect.top + lpm.height;

		outRegion.set(fullRect);
		outRegion.op(currRect, Region.Op.DIFFERENCE);
		RegionIterator ri = new RegionIterator(outRegion);
		while (ri.next(iterRect)) backCanvas.drawRect(iterRect, backPaint);
		long t2 = System.nanoTime();
		debuger.append("\n\n\n" + (t1 - t0) / 1000000f + "\n" + (t2 - t1) / 1000000f
					+ "\nrect" + fullRect.toShortString() + "\nrect2" + currRect.toShortString());
	}

	private class PointOnTouch implements View.OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent p2) {
			View downV = null;
			View upV = null;
			int index = (int) v.getTag();
			View opp = pointImg[getOpposite(index)];
			float x0 = opp.getX();
			float y0 = opp.getY();
			float x = v.getX();
			float y = v.getY();
			if (isDouble(index)) {
				downV = pointImg[getDown(index)];
				upV = pointImg[getUp(index)];
			} else {
				upV = pointImg[getDown(index)];
				downV = pointImg[getUp(index)];
			}
			downV.setX(x0);
			downV.setY(y);
			upV.setX(x);
			upV.setY(y0);
			lineAll();
			updataMoveView();
			return false;
		}
	}

	private class LineOnTouch implements View.OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent p2) {
			int index = (int) v.getTag();
			View downV = pointImg[index];
			View upV = pointImg[getUp(index)];
			if (isDouble(index)) {
				float x = v.getX();
				upV.setX(x);
				downV.setX(x);
			} else {
				float y = v.getY();
				upV.setY(y);
				downV.setY(y);
			}
			line(getUp(index));
			line(getDown(index));
			updataMoveView();
			return false;
		}
	}

	public interface CallBack{
		public void onResult(Rect rect);
	}

}
