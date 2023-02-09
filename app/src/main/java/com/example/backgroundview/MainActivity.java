package com.example.backgroundview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.backgroundview.service.BroadcastReceiverService;

public class MainActivity extends AppCompatActivity {

    Button button;
    TextView DrawOverlaysTextView;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        button = (Button) findViewById(R.id.button);
        DrawOverlaysTextView = (TextView) findViewById(R.id.DrawOverlaysTextView);

        if (!Settings.canDrawOverlays(this)) {
            button.setVisibility(View.VISIBLE);
            DrawOverlaysTextView.setVisibility(View.VISIBLE);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!Settings.canDrawOverlays(context)) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:" + getPackageName()));
                            startActivityForResult(intent, 941204);
                        }
                    } else {
                        button.setVisibility(View.INVISIBLE);
                        DrawOverlaysTextView.setText("Start! BackgorundView Service");
                        startBroadcastReceiverService();
                    }
                }
            });
        } else {
            DrawOverlaysTextView.setText("Start! BackgorundView Service");
            startBroadcastReceiverService();
        }
    }

    void startBroadcastReceiverService() {
        try {
            Intent intent = new Intent(this, BroadcastReceiverService.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.startForegroundService(intent);
            } else {
                this.startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

