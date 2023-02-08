/**
 * 필요한 권한
 * android.permission.FOREGROUND_SERVICE
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
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.backgroundview.MainActivity;
import com.example.backgroundview.R;

import java.util.ArrayList;

public class BroadcastReceiverService extends Service {
    private final String TAG = this.getClass().getSimpleName();

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                switch (intent.getAction()) {
                    case Intent.ACTION_SCREEN_OFF:      // 화면 꺼짐
                        Log.d(TAG, "ACTION_SCREEN_OFF");
                        break;
                    case Intent.ACTION_USER_PRESENT:    // 화면 잠금해제
                        Log.d(TAG, "ACTION_USER_PRESENT");
                        break;
                    default:
                        break;
                }
            }
        }
    };

    private ArrayList<String> packageNames;

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

}