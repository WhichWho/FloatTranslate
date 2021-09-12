package com.cnl.translate.service;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SearchView.OnCloseListener;

import com.cnl.translate.activity.MainActivity;
import com.cnl.translate.core.CaptureCore;
import com.cnl.translate.utils.MainModul;
import com.cnl.translate.window.BaseWindow;
import com.cnl.translate.window.MainFloatWindow;
import com.cnl.translate.window.SelectWindow;
import com.cnl.translate.utils.OrientationDetection;
import com.cnl.translate.utils.PreferenceAnalyse;
import com.cnl.translate.window.InputWindow;

public class MainService extends BaseService {

    private MainFloatWindow mainWindow;
    private SelectWindow selectWindow;
    private InputWindow inputWindow;
    private OrientationDetection od;

    @Override
    protected void onCommand() {
        od = new OrientationDetection(this);
        mainWindow = new MainFloatWindow();
        mainWindow.setOnClick(new OnClickListener() {
            @Override
            public void onClick(View img) {
                PreferenceAnalyse.MODE mode = PreferenceAnalyse.getMode();
                switch (mode) {
                    case TAP:
                    case AUTO:
                        autoAndTap(mode);
                        break;
                    case INPUT:
                        input();
                        break;
                }
            }

            private void input() {
                if (inputWindow == null) {
                    inputWindow = new InputWindow();
                    inputWindow.init(MainService.this);
                    mainWindow.setAutoFixRotation(od);
                }
                if (inputWindow.isShowing()) {
                    inputWindow.hide();
                } else {
                    inputWindow.show();
                }
            }

            private void autoAndTap(PreferenceAnalyse.MODE mode) {
                if (selectWindow != null) {
                    if (mode == PreferenceAnalyse.MODE.TAP) {

                    } else {
                        BaseWindow frameWindow = selectWindow.getFrameWindow();
                        if (frameWindow != null && frameWindow.isShowing()) {
                            frameWindow.hide();
                            return;
                        }
                    }
                }
                CaptureCore cap = CaptureCore.newInstance();
                cap.capture(new CaptureCore.SampleCallBack() {
                    @Override
                    public void onCapture(Bitmap bmp) {
                        int w = bmp.getWidth();
                        int h = bmp.getHeight();
                        int pad = MainModul.getHeight2();
                        if (w > h) {
                            w -= pad;
                        } else {
                            h -= pad;
                        }
                        bmp = Bitmap.createBitmap(bmp, 0, 0, w, h);
                        selectWindow = new SelectWindow(bmp, mainWindow);
                        selectWindow.init(MainService.this);
                        selectWindow.show();
                        mainWindow.hide();
                    }
                });
            }
        });
        mainWindow.setOnClose(new OnCloseListener() {
            @Override
            public boolean onClose() {
                if (selectWindow != null) {
                    BaseWindow frameWindow = selectWindow.getFrameWindow();
                    if (frameWindow != null && frameWindow.isShowing()) {
                        frameWindow.hide();
                    }
                }
                stopSelf();
                return true;
            }
        });
        mainWindow.init(this);
        mainWindow.setAutoFixRotation(od);
        mainWindow.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        od.disable();
    }

    @Override
    protected PendingIntent getNotificationIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        return PendingIntent.getActivity(this, 0, intent, 0);
    }

}
