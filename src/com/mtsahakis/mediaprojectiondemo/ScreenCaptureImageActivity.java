package com.mtsahakis.mediaprojectiondemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import screen.utils.ScreenUtils;

public class ScreenCaptureImageActivity extends Activity {

    ScreenUtils SU = new ScreenUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SU.onCreate(this, (ImageView) findViewById(R.id.renderedCapture));

        findViewById(R.id.StartFloatingServiceButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SU.createFloatingWidget();
            }
        });

        findViewById(R.id.screenshotButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SU.takeScreenShot();
            }
        });

        // start projection
        findViewById(R.id.startButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SU.startScreenMirror();
            }
        });

        // stop projection
        findViewById(R.id.stopButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SU.stopScreenMirror();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SU.createFloatingWidget();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        SU.onActivityResult(requestCode, resultCode, data);
    }
}