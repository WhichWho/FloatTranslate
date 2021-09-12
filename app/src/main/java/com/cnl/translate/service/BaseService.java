package com.cnl.translate.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public abstract class BaseService extends Service {

    protected boolean started = false;
    protected int NOTIFICATION_COUNT = 0x10086;
    protected final int NOTIFICATION_ID = NOTIFICATION_COUNT++;

    protected abstract void onCommand();

    protected abstract PendingIntent getNotificationIntent();

    @Override
    public IBinder onBind(Intent p1) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!started) {
            started = true;
            startForeground(NOTIFICATION_ID, CreateInform());
            onCommand();
        }
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        started = false;
        super.onDestroy();
    }

    protected Notification CreateInform() {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(android.R.drawable.sym_def_app_icon);
        builder.setContentIntent(getNotificationIntent());
        builder.setContentTitle("悬浮窗正在显示");
        builder.setContentText("点击打开");
        builder.setOngoing(true);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("CHANNEL_ONE_ID", "CHANNEL_ONE_NAME",
                    NotificationManager.IMPORTANCE_MIN);
            notificationChannel.enableLights(false);
            notificationChannel.setShowBadge(false);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
            builder.setChannelId("CHANNEL_ONE_ID");
        }
        return builder.build();
    }

}
