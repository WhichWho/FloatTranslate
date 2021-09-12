package com.cnl.translate.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cnl.translate.core.CaptureCore;
import com.cnl.translate.core.OCRCore;
import com.cnl.translate.core.TranslateUtil;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import com.cnl.translate.core.TranslateCore;
import com.cnl.translate.utils.PreferenceQuery;

public class OCRTranslateView extends RelativeLayout {

    private TextView tv;
    private Handler mHandler;
    private Timer timer;
    private Rect rect;
    private OcrThread mThread;

    public OCRTranslateView(Context ctx, Rect rect, Point size) {
        super(ctx);
        this.rect = rect;
        initViews(ctx, size, rect);
        initWorks(ctx);
    }

    private void initViews(Context ctx, Point size, Rect rect) {
        ImageView img = new ImageView(ctx);
        img.setImageBitmap(drawRedRect(size, rect));
        addView(img, new LayoutParams(-1, -1));

        tv = new TextView(ctx);
        tv.setText("翻译的文字将会显示在这里");
        tv.setBackgroundColor(0x66000000);
        LayoutParams lpm = new LayoutParams(-2, -2);
        lpm.addRule(ALIGN_PARENT_TOP);
        lpm.addRule(CENTER_HORIZONTAL);
        addView(tv, lpm);
    }

    private void initWorks(final Context ctx) {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
//				ClipboardManager cm = ctx.getSystemService(ClipboardManager.class);
//				cm.setText(msg.obj.toString());
                if (msg.what == 0) {
                    tv.setTextColor(Color.WHITE);
                    tv.setText(String.valueOf(msg.obj));
                } else {
                    tv.setTextColor(Color.RED);
                    tv.setText("失败！code=" + msg.what + "\n" + msg.obj);
                }
            }
        };
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                CaptureCore.newInstance().capture(new CaptureCore.SampleCallBack() {
                    @Override
                    public void onCapture(Bitmap bmp) {
                        Bitmap bitmap = Bitmap.createBitmap(bmp, rect.left, rect.top, rect.width(), rect.height());
                        bmp.recycle();
                        if (!mThread.unlockAndPost(bitmap)) {
                            Message msg = Message.obtain();
                            msg.obj = "Thread is busy! Please reduce the invoke frequence.\n";
                            msg.what = -1;
                            mHandler.sendMessage(msg);
                        }
                    }
                });
            }
        };
        mThread = new OcrThread(mHandler);
        mThread.start();
        timer = new Timer();
        timer.schedule(task, 0, 2000);
    }

    private Bitmap drawRedRect(Point size, Rect rect) {
        Bitmap bmp = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(bmp);
        Paint pa = new Paint();
        pa.setColor(Color.RED);
        pa.setStyle(Paint.Style.STROKE);
        pa.setStrokeWidth(5);
        cv.drawRect(rect, pa);
        return bmp;
    }

    @Override
    protected void onDetachedFromWindow() {
        timer.cancel();
        mThread.flagToStop();
        super.onDetachedFromWindow();
    }

    private class OcrThread extends Thread {

        private Handler mainHandler;
        private final Object syncObj;
        private Bitmap bmp;
        private OCRCore ocr;
        private HashMap<String, String> opt;
        private TranslateUtil traUtil;

        private boolean userTra = false;

        private volatile AtomicBoolean isIdle;
        private volatile AtomicBoolean runFlag;

        public OcrThread(Handler handler) {
            isIdle = new AtomicBoolean(true);
            runFlag = new AtomicBoolean(true);
            syncObj = new Object();
            ocr = OCRCore.getInstance();
            opt = new HashMap<>();
            opt.put("language_type", "JAP");
            traUtil = TranslateUtil.getInstance();
            mainHandler = handler;

            PreferenceQuery pq = PreferenceQuery.getInstance();
            userTra = pq.getBoolean("user_tra");
            if (userTra) {
                String traAK = pq.getString("user_tra_ak");
                String traSK = pq.getString("user_tra_sk");
                TranslateCore.oauth(traAK, traSK);
            }
        }

        public boolean unlockAndPost(Bitmap bitmap) {
            boolean idle = isIdle.get();
            if (idle) {
                synchronized (syncObj) {
                    bmp = bitmap;
                    syncObj.notifyAll();
                }
            }
            return idle;
        }

        public void flagToStop() {
            runFlag.set(false);
        }

        @Override
        public void run() {
            while (runFlag.get()) {
                try {
                    synchronized (syncObj) {
                        isIdle.set(true);
                        syncObj.wait();
                    }
                    isIdle.set(false);
                    String text = OCRCore.parse(ocr.ocr(bmp, opt));
                    String tra;
                    if (userTra) {
                        tra = TranslateCore.parse(TranslateCore.getTransResult(text, "jp", "zh"));
                    } else {
                        tra = traUtil.translateDirect("ja", "zh", text);
                    }
                    Message msg = Message.obtain();
                    msg.obj = tra;
                    msg.what = 0;
                    mainHandler.sendMessage(msg);
                } catch (Exception e) {
                    Message msg = Message.obtain();
                    msg.obj = e.getMessage();
                    msg.what = -1;
                    mainHandler.sendMessage(msg);
                }
            }
        }
    }
}
