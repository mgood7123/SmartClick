package com.mtsahakis.mediaprojectiondemo.screen.utils.media.projection.helpers;

import android.hardware.display.VirtualDisplay;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.os.Handler;
import android.util.Log;

import com.mtsahakis.mediaprojectiondemo.screen.utils.OrientationChangeCallback;
import com.mtsahakis.mediaprojectiondemo.screen.utils.ScreenUtils;
import com.mtsahakis.mediaprojectiondemo.screen.utils.Variables;

public class MediaProjectionStopCallback extends MediaProjection.Callback {
    private final Variables variables;
    private final MediaProjectionHelper mediaProjectionHelper;

    public MediaProjectionStopCallback(Variables variables, MediaProjectionHelper mediaProjectionHelper) {
        this.variables = variables;
        this.mediaProjectionHelper = mediaProjectionHelper;
    }

    @Override
    public void onStop() {
        Log.e("ScreenCapture", "stopping projection.");
        variables.mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (variables.mVirtualDisplay != null) variables.mVirtualDisplay.release();
                if (variables.mImageReader != null) variables.mImageReader.setOnImageAvailableListener(null, null);
                if (variables.mOrientationChangeCallback != null) variables.mOrientationChangeCallback.disable();
                variables.sMediaProjection.unregisterCallback(MediaProjectionStopCallback.this);
                variables.sMediaProjection = null;

                // we quit from here as it seems to be the last message posted to the handler
                variables.mHandler.getLooper().quitSafely();
            }
        });
    }
}