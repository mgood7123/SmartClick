package com.mtsahakis.mediaprojectiondemo.screen.utils.media.projection.helpers;

import android.media.projection.MediaProjection;
import android.util.Log;

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
        Log.e("ScreenCapture", "posting stop projection.");
        variables.mHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.e("ScreenCapture", "stopping projection.");
                if (variables.mVirtualDisplay != null) variables.mVirtualDisplay.release();
                if (variables.mImageReader != null) variables.mImageReader.setOnImageAvailableListener(null, null);
                if (variables.mOrientationChangeCallback != null) variables.mOrientationChangeCallback.disable();
                variables.sMediaProjection.unregisterCallback(MediaProjectionStopCallback.this);
                variables.sMediaProjection = null;
                Log.e("ScreenCapture", "stopped projection.");

                // we quit from here as it seems to be the last message posted to the handler
                Log.e("ScreenCapture", "quiting looper");
                variables.mHandler.getLooper().quitSafely();
                Log.e("ScreenCapture", "quit looper");
            }
        });
        Log.e("ScreenCapture", "posted stop projection.");
    }
}