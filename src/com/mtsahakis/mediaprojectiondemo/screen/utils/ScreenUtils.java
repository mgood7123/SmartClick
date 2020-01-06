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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == variables.REQUEST_CODE) {
            variables.mediaProjectionHelper.startCapture(resultCode, data);
        }
    }

    public void takeScreenShot() {
        variables.mediaProjectionHelper.takeScreenShot();
    }

    public void startScreenMirror() {
        variables.mediaProjectionHelper.startScreenMirror();
    }

    public void stopScreenMirror() {
        variables.mediaProjectionHelper.stopScreenMirror();
    }
}
