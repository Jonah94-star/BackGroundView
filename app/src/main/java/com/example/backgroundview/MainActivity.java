package com.example.backgroundview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.example.backgroundview.service.BroadcastReceiverService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            Intent i = new Intent(this, BroadcastReceiverService.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.startForegroundService(i);
            } else {
                this.startService(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

