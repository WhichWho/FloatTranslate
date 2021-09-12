package com.cnl.translate.window;

import com.cnl.translate.window.BaseWindow.Option;

import android.os.Build;
import android.view.View;
import android.view.WindowManager.LayoutParams;

import android.graphics.Bitmap;

import com.cnl.translate.view.EditImageView;

import android.view.View.OnClickListener;
import android.graphics.Rect;
import android.graphics.Point;

public class SelectWindow extends BaseWindow {

    private Bitmap bmp;
    private BaseWindow last;
    private BaseWindow next;

    public SelectWindow(Bitmap bitmap, BaseWindow lastWindow) {
        bmp = bitmap;
        last = lastWindow;
    }

    @Override
    protected BaseWindow.Option getOption() {
        Option opt = new Option();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            opt.type = LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            opt.type = LayoutParams.TYPE_SYSTEM_ERROR;
        }
        opt.flags =
                LayoutParams.FLAG_TRANSLUCENT_STATUS |
                        LayoutParams.FLAG_TRANSLUCENT_NAVIGATION |
                        LayoutParams.FLAG_FULLSCREEN |
                        LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                        LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        LayoutParams.FLAG_NOT_FOCUSABLE;
        opt.w = bmp.getWidth();
        opt.h = bmp.getHeight();
        return opt;
    }

    @Override
    protected View getView() {
        EditImageView editImageView = new EditImageView(ctx, 700, 300, bmp);
        editImageView.setCallBack(new EditImageView.CallBack() {
            @Override
            public void onResult(Rect rect) {
                Point p = new Point(bmp.getWidth(), bmp.getHeight());
                next = new RecordWindow(rect, p);
                next.init(ctx);
                next.show();
                hide();
                last.show();
            }
        });
        editImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View p1) {
                hide();
                last.show();
            }
        });
        return editImageView;
    }

    public BaseWindow getFrameWindow() {
        return next;
    }

}
