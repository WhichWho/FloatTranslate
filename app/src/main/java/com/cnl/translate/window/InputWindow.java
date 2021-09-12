package com.cnl.translate.window;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import com.cnl.translate.R;
import com.cnl.translate.utils.MainModul;
import com.cnl.translate.view.FloatMoveImageView;

public class InputWindow extends MainFloatWindow{

	@Override
	protected BaseWindow.Option getOption(){
		int w = MainModul.getScreenW();
		int h = MainModul.getScreenH();
		int x = Math.min(h, w);
		Option opt = super.getOption();
		opt.w = opt.h = x / 3;
		opt.x = 0;
		opt.flags &= ~
		(LayoutParams.FLAG_NOT_FOCUSABLE | 
		LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		return opt;
	}

	@Override
	protected View getView(){
		View root = LayoutInflater.from(ctx).inflate(R.layout.float_input, null, false);
		FrameLayout fl = (FrameLayout) root.findViewById(R.id.img);
		FloatMoveImageView view = (FloatMoveImageView) super.getView();
		view.setImageResource(R.drawable.foreg);
		fl.addView(view);
		ViewGroup.LayoutParams rllp = fl.getLayoutParams();
		rllp.width = lpm.height / 5;
		fl.setLayoutParams(rllp);
		return root;
	}
	
}
