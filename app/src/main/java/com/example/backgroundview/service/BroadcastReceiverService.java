/**
 * 필요한 권한
 * android.permission.FOREGROUND_SERVICE
 * android.permission.SYSTEM_ALERT_WINDOW
 */
package com.example.backgroundview.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.backgroundview.MainActivity;
import com.example.backgroundview.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class BroadcastReceiverService extends Service {
    private final String TAG = this.getClass().getSimpleName();

    Timer timer = null;
    int count = 0;

    WindowManager windowManager;
    View mView;
    BackgorundViewService backgorundViewService;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                switch (intent.getAction()) {
                    case Intent.ACTION_SCREEN_OFF:      // 화면 꺼짐
                        Log.d(TAG, "ACTION_SCREEN_OFF");
                        TimerStopService();
                        break;
                    case Intent.ACTION_USER_PRESENT:    // 화면 잠금해제
                        Log.d(TAG, "ACTION_USER_PRESENT");
                        TimerStopService();
                        TimerStartService();
                        break;
                    default:
                        break;
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "LifecycleActivity init");

        if (setBroadcastReceiverService()) {
            startForegroundService();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mReceiver != null) {
            try {
                unregisterReceiver(mReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        TimerStopService();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Nullable
    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startIs) {
        super.onStartCommand(intent, flag, startIs);
        return START_STICKY;
    }

    public boolean setBroadcastReceiverService() {
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_USER_PRESENT);
            registerReceiver(mReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    void startForegroundService() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_foreground);

        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "service_channel";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "smart_channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .createNotificationChannel(channel);

            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(this);
        }
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContent(remoteViews)
                .setContentIntent(pendingIntent);

        startForeground(1, builder.build());
    }

    void TimerStartService() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                count++;
                Log.e(TAG, "count: " + count);
            }
        };
        timer.schedule(task, 100, 1000); // 1초 뒤 1분마다 반복실행
        setupBackgorundView();
    }

    void TimerStopService() {
        if (timer != null) {
            try {
                timer.cancel();
                timer = null;
            } catch (Exception e) {
                e.getMessage();
            }
        }
    }

    void setupBackgorundView() {
        LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        backgorundViewService = new BackgorundViewService(inflate, windowManager, mView);
    }
}