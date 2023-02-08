/**
 * 필요한 권한
 * android.permission.FOREGROUND_SERVICE
 */
package com.example.backgroundview.service;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;


public class RebootReceiveService extends BroadcastReceiver {
    private final String TAG = this.getClass().getSimpleName();

    private Context context;


    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Log.d(TAG, "onReceive");

        if (!isLaunchingMainBackGroundService()) {
            try {
                /**
                 * 서비스 죽일때 알람으로 다시 서비스 등록
                 */
                if (intent.getAction().equals("ACTION.RESTART.PersistentService")) {
                    Intent i = new Intent(context, BroadcastReceiverService.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(i);
                    } else {
                        context.startService(i);
                    }

                }

                /**
                 * 폰 재시작 할때 서비스 등록
                 */
                if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
                    Intent i = new Intent(context, BroadcastReceiverService.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(i);
                    } else {
                        context.startService(i);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Boolean isLaunchingMainBackGroundService() {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (PreferenceManager.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
