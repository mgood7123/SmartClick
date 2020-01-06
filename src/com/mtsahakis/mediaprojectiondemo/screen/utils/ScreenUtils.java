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


    public Variables variables = new Variables();

    public void onCreate(Activity activity, ImageView imageView) {
        variables.activity = activity;
        variables.imageView = imageView;
        variables.mProjectionManager = variables.mediaProjectionHelper.getMediaProjectionManager();
    }

    public void takeScreenShot() {
        variables.log.errorNoStackTrace("taking screenshot");
        variables.screenshot = true;
        startScreenMirror();
    }

    public void startScreenMirror() {
        variables.log.errorNoStackTrace("looper is " + variables.looper);
        if (variables.looper == null) {
            variables.log.errorNoStackTrace("startLooper");
            variables.looperHelper.startLooper();
            variables.log.errorNoStackTrace("requestCapturePermission");
            variables.mediaProjectionHelper.requestCapturePermission();
            variables.log.errorNoStackTrace("requested");
        }
    }

    public void stopScreenMirror() {
        variables.log.errorNoStackTrace("looper is " + variables.looper);
        if (variables.looper != null) {
            variables.log.errorNoStackTrace("stopCapture");
            variables.mediaProjectionHelper.stopCapture();
            variables.log.errorNoStackTrace("stopLooper");
            variables.looperHelper.stopLooper();
            variables.log.errorNoStackTrace("stopped");
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == variables.REQUEST_CODE) {
            variables.mediaProjectionHelper.startCapture(resultCode, data);
        }
    }
}
