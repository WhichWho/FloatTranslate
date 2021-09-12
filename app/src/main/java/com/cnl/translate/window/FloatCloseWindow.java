package com.cnl.translate.window;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.cnl.translate.utils.MainModul;

import android.view.ViewGroup;

public class FloatCloseWindow extends BaseWindow {

    @Override
    protected BaseWindow.Option getOption() {
        Option opt = new Option();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            opt.type = LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            opt.type = LayoutParams.TYPE_PHONE;
        }
        opt.flags =
                LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        LayoutParams.FLAG_NOT_FOCUSABLE;
        opt.w = MainModul.getScreenW();
        opt.h = MainModul.getScreenH();
        return opt;
    }

    @Override
    protected View getView() {
        int w = MainModul.getScreenW();
        int h = MainModul.getScreenH();
        int min = Math.min(w, h);
        RelativeLayout layout = new RelativeLayout(ctx);
        layout.setBackground(new BitmapDrawable(drawShadow(w, h)));

        ImageView img = new ImageView(ctx);
        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(min / 7, min / 7);
        lps.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.setMargins(0, 0, 0, h / 5);
        img.setImageBitmap(drawClose(lps.width, lps.height));
        layout.addView(img, lps);

        return layout;
    }

    public ViewGroup optView() {
        return (ViewGroup) self;
    }

    private Bitmap drawShadow(int w, int h) {
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(bmp);
        Paint pa = new Paint();
        int[] colors = {0, 0, Color.argb(175, 0, 0, 0)};
        float[] pos = {0, 0.6f, 1};
        Shader sh1 = new LinearGradient(0, 0, 0, h, colors, pos, Shader.TileMode.REPEAT);
        pa.setShader(sh1);
        pa.setStyle(Paint.Style.FILL);
        pa.setAntiAlias(true);

        Rect rect = new Rect(0, 0, w, h);
        cv.drawRect(rect, pa);

        return bmp;
    }

    private Bitmap drawClose(int w, int h) {
        float r = Math.min(w, h) * .49f;
        double p = Math.PI;

        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(bmp);
        Paint pa = new Paint();
        pa.setColor(Color.WHITE);
        pa.setStyle(Paint.Style.STROKE);
        pa.setStrokeWidth(r * .56f / 5);
        pa.setAntiAlias(true);

        Path path = new Path();
        float rs = r - r * .56f / 5;
        path.addCircle(w / 2f, h / 2f, rs, Path.Direction.CW);
        path.moveTo((float) Math.cos(p / 4) * rs + w / 2, (float) Math.sin(p / 4) * rs + h / 2);
        path.lineTo((float) Math.cos(p / 4 * 5) * rs + w / 2, (float) Math.sin(p / 4 * 5) * rs + h / 2);
        path.moveTo((float) Math.cos(p / 4 * 3) * rs + w / 2, (float) Math.sin(p / 4 * 3) * rs + h / 2);
        path.lineTo((float) Math.cos(p / 4 * 7) * rs + w / 2, (float) Math.sin(p / 4 * 7) * rs + h / 2);
        cv.drawPath(path, pa);

        return bmp;
    }

}
