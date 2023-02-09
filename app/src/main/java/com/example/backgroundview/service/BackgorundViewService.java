package com.example.backgroundview.service;

import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.backgroundview.R;

public class BackgorundViewService {
    LayoutInflater inflate;
    WindowManager windowManager;
    View mView;

    BackgorundViewService(LayoutInflater inflate, WindowManager windowManager, View mView) {
        this.inflate = inflate;
        this.windowManager = windowManager;
        this.mView = mView;

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(200, 200,
                Build.VERSION.SDK_INT < Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_PHONE : WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.TYPE_APPLICATION_PANEL | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSPARENT);
        params.gravity = Gravity.END | Gravity.BOTTOM;
        params.x = 10;
        params.y = 100;

        this.mView = inflate.inflate(R.layout.view_background, null);

        setupBackgroundLayout();

        this.windowManager.addView(this.mView, params);
    }

    void setupBackgroundLayout() {
        final TextView view_background_textview = (TextView) mView.findViewById(R.id.view_background_textview);
        view_background_textview.setText("BackGroundView");
    }

}
