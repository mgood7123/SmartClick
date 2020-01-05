package com.mtsahakis.mediaprojectiondemo.screen.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.media.ImageReader;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.mtsahakis.mediaprojectiondemo.screen.utils.media.projection.helpers.MediaProjectionHelper;
import com.mtsahakis.mediaprojectiondemo.screen.utils.media.projection.helpers.MediaProjectionStopCallback;

public class ScreenUtils {


    Variables variables = new Variables();
    MediaProjectionHelper mediaProjectionHelper = new MediaProjectionHelper(variables);
    Looper looper = new Looper(variables);

    public void onCreate(Activity activity, ImageView imageView) {
        variables.activity = activity;
        variables.imageView = imageView;
        variables.mProjectionManager = mediaProjectionHelper.getMediaProjectionManager();
    }

    public void startScreenMirror() {
        looper.startLooper();
        mediaProjectionHelper.requestCapturePermission();
    }

    public void stopScreenMirror() {
        mediaProjectionHelper.stopCapture();
        looper.stopLooper();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == variables.REQUEST_CODE) {
            mediaProjectionHelper.startCapture(resultCode, data);
        }
    }
}
