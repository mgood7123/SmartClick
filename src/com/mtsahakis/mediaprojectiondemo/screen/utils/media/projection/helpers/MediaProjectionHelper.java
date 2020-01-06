package com.mtsahakis.mediaprojectiondemo.screen.utils.media.projection.helpers;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.media.ImageReader;
import android.media.projection.MediaProjectionManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.mtsahakis.mediaprojectiondemo.screen.utils.ImageAvailableListener;
import com.mtsahakis.mediaprojectiondemo.screen.utils.OrientationChangeCallback;
import com.mtsahakis.mediaprojectiondemo.screen.utils.Variables;

public class MediaProjectionHelper {
    private Variables variables;

    public MediaProjectionHelper(final Variables variables) {
        this.variables = variables;
    }

    public MediaProjectionManager getMediaProjectionManager() {
        if (variables.activity == null)
            variables.log.errorAndThrow(
                    "please set activity before calling " + variables.log.getMethodName()
            );
        return (MediaProjectionManager)
                variables.activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    }

    public void requestCapturePermission() {
        variables.activity.startActivityForResult(
                variables.mProjectionManager.createScreenCaptureIntent(), variables.REQUEST_CODE
        );
    }

    public void stopCapture() {
        variables.mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (variables.sMediaProjection != null) {
                    variables.sMediaProjection.stop();
                }
            }
        });
    }

    public void createVirtualDisplay() {
        // get width and height
        Point size = new Point();
        variables.mDisplay.getSize(size);
        int mWidth = size.x;
        int mHeight = size.y;

        // start capture reader
        variables.mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
        variables.mVirtualDisplay = variables.sMediaProjection.createVirtualDisplay(variables.SCREENCAP_NAME, mWidth, mHeight, variables.mDensity, variables.VIRTUAL_DISPLAY_FLAGS, variables.mImageReader.getSurface(), null, variables.mHandler);
        variables.mImageReader.setOnImageAvailableListener(new ImageAvailableListener(variables, mWidth, mHeight), variables.mHandler);
    }


    public void startCapture(int resultCode, Intent data) {
        if (variables.sMediaProjection != null) return;
        Log.e("ScreenCapture", "starting projection.");
        variables.sMediaProjection = variables.mProjectionManager.getMediaProjection(resultCode, data);

        if (variables.sMediaProjection != null) {
            // display metrics
            DisplayMetrics metrics = variables.activity.getResources().getDisplayMetrics();
            variables.mDensity = metrics.densityDpi;
            variables.mDisplay = variables.activity.getWindowManager().getDefaultDisplay();

            // create virtual display depending on device width / height
            createVirtualDisplay();

            // register orientation change callback
            variables.mOrientationChangeCallback = new OrientationChangeCallback(variables, this);
            if (variables.mOrientationChangeCallback.canDetectOrientation()) {
                variables.mOrientationChangeCallback.enable();
            }

            // register media projection stop callback
            variables.sMediaProjection.registerCallback(new MediaProjectionStopCallback(variables, this), variables.mHandler);
            Log.e("ScreenCapture", "started projection.");
        }
    }
}
