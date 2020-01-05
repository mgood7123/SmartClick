package com.mtsahakis.mediaprojectiondemo.screen.utils;

import android.app.Activity;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Handler;
import android.view.Display;
import android.widget.ImageView;

import com.mtsahakis.mediaprojectiondemo.LogUtils;

public class Variables {

    public final String TAG = getClass().getName();

    public ImageView imageView;

    public final int REQUEST_CODE = 100;

    public final String SCREENCAP_NAME = "screencap";

    public final int VIRTUAL_DISPLAY_FLAGS =
            DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY |
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;

    public MediaProjection sMediaProjection;

    public MediaProjectionManager mProjectionManager;

    public ImageReader mImageReader;
    public Handler mHandler;
    public Display mDisplay;
    public VirtualDisplay mVirtualDisplay;

    public int mDensity;

    public OrientationChangeCallback mOrientationChangeCallback;

    public Activity activity;

    public final LogUtils log = new LogUtils(
            TAG, "a bug has occurred, this should not happen"
    );

    public Thread looper;
}
