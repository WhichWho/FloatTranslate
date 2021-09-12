package com.cnl.translate.window;

import android.os.Build;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.SearchView.OnCloseListener;

import com.cnl.translate.R;
import com.cnl.translate.utils.MainModul;
import com.cnl.translate.utils.OrientationDetection;
import com.cnl.translate.view.FloatMoveImageView;

import android.os.Handler;
import android.os.Message;

public class MainFloatWindow extends BaseWindow {

    private View.OnClickListener cb;
    private OnCloseListener clo;
    private Handler mainHd;
    private int lastRotation;
    private int lastX;
    private int lastY;

    @Override
    protected BaseWindow.Option getOption() {
        int w = MainModul.getScreenW();
        int h = MainModul.getScreenH();
        int x = Math.min(h, w);
        Option opt = new Option();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            opt.type = LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            opt.type = LayoutParams.TYPE_PHONE;
        }
        opt.flags =
                LayoutParams.FLAG_TRANSLUCENT_STATUS |
                        LayoutParams.FLAG_TRANSLUCENT_NAVIGATION |
                        LayoutParams.FLAG_FULLSCREEN |
                        LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                        LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        LayoutParams.FLAG_NOT_FOCUSABLE;
        opt.w = opt.h = x / 10;
        lastX = opt.x = w - opt.w;
        lastY = opt.y = h / 2 - opt.h;
        return opt;
    }

    @Override
    protected View getView() {
        FloatMoveImageView img = new FloatMoveImageView(ctx, lpm);
        img.setImageResource(R.drawable.ic_launcher);
        img.setScaleType(ImageView.ScaleType.FIT_CENTER);
        img.setOnClickListener(cb);
        img.setOnCloseListener(new OnCloseListener() {
            @Override
            public boolean onClose() {
                isShowing = false;
                return clo == null ? true : clo.onClose();
            }
        });
        img.setOnChangeListener(new FloatMoveImageView.OnChangeListener() {
            @Override
            public void onChange(int x, int y) {
                lastX = x;
                lastY = y;
            }
        });
        return img;
    }

    public void setOnClick(View.OnClickListener lis) {
        cb = lis;
    }

    public void setOnClose(OnCloseListener lis) {
        clo = lis;
    }

    public void setAutoFixRotation(OrientationDetection od) {
        mainHd = new Handler(ctx.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    wm.updateViewLayout(self, lpm);
                    ((FloatMoveImageView) self).notificationRotation();
                }
            }
        };
        od.addCallBack(new OrientationDetection.CallBack() {
            @Override
            public void onChange(final int rotation) {
                int mRotation = 0;
                switch (rotation) {
                    case Surface.ROTATION_0:
                    case Surface.ROTATION_180:
                        mRotation = 0;
                        break;
                    case Surface.ROTATION_90:
                    case Surface.ROTATION_270:
                        mRotation = 1;
                        break;
                }
                if (mRotation != lastRotation) {
                    lastRotation = mRotation;
                    onRotationChange();
                }
            }
        });
        od.enable(750);
    }

    private void onRotationChange() {
        float w = MainModul.getScreenH();
        float h = MainModul.getScreenW();
        float pw = (float) (lastX + lpm.width) / w * h - lpm.height;
        float ph = (float) (lastY + lpm.height) / h * w - lpm.width;
        lastX = lpm.x = (int) pw;
        lastY = lpm.y = (int) ph;
        mainHd.sendEmptyMessage(0);
    }

}
