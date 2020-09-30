package screen.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.media.ImageReader;
import android.media.projection.MediaProjectionManager;
import android.os.Looper;
import android.util.DisplayMetrics;

public class MediaProjectionHelper {
    Variables variables;

    public MediaProjectionHelper(final Variables variables) {
        this.variables = variables;
    }

    public void startScreenRecord() {
        variables.log.logWithClassName(this, "recording screen");
        variables.screenRecord = true;
        startScreenMirror();
    }

    public void stopScreenRecord() {
        variables.log.logWithClassName(this, "recorded screen");
        stopScreenMirror();
    }

    public void takeScreenShot() {
        variables.log.logWithClassName(this, "taking screenshot");
        variables.screenshot = true;
        startScreenMirror();
    }

    public void startScreenMirror() {
        variables.stop = false;
        variables.log.logWithClassName(this, "looper is " + variables.looper);
        if (variables.looper == null) {
            variables.log.logWithClassName(this, "startLooper");
            variables.looperHelper.startLooper();
            variables.log.logWithClassName(this, "requestCapturePermission");
            requestCapturePermission();
            variables.log.logWithClassName(this, "requested");
        }
    }

    public void stopScreenMirror() {
        variables.stop = true;
        variables.log.logWithClassName(this, "looper is " + variables.looper);
        if (variables.looper != null) {
            variables.log.logWithClassName(this, "stopCapture");
            stopCapture();
            variables.log.logWithClassName(this, "stopLooper");
            variables.looperHelper.stopLooper();
            variables.log.logWithClassName(this, "stopped");
        }
    }

    public MediaProjectionManager getMediaProjectionManager() {
        if (variables.context == null) {
            variables.log.logWithClassName(this,
                    "please set activity or service before calling " + variables.log.getMethodName()
            );
        }
        return (MediaProjectionManager)
                variables.context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    }

    public void requestCapturePermission(Activity activity) {
        variables.projectionActivity = activity;
        requestCapturePermission();
    }

    public void requestCapturePermission() {
        if (variables.sMediaProjection != null) {
            variables.log.logWithClassName(this, "sMediaProjection already obtained");
            return;
        }
        variables.log.logWithClassName(this, "obtaining sMediaProjection");
        if (variables.grantedPermission) {
            variables.log.logWithClassName(this, "permission already granted");
            variables.log.logWithClassName(this, "using cached permission");
            startCapture(variables.resultCodeSaved, variables.dataSaved);
        } else {
            if (null != variables.projectionActivity) {
                variables.projectionActivity.startActivityForResult(
                        variables.mProjectionManager.createScreenCaptureIntent(), variables.REQUEST_CODE
                );
            } else {
                if (null == variables.context) {
                    variables.log.errorAndThrowWithClass(this, "error: a service or activity must be passed");
                }
                ProjectionActivity.requestProjectionIntentActivity(variables.context, this);
            }
        }
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
        if (variables.sMediaProjection != null) {
            variables.log.logWithClassName(this, "sMediaProjection already obtained");
            return;
        }
        variables.log.logWithClassName(this, "obtaining sMediaProjection");
        if (resultCode == Activity.RESULT_OK) {
            if (!variables.grantedPermission) {
                variables.log.logWithClassName(this, "caching permission");
                variables.grantedPermission = true;
                variables.resultCodeSaved = resultCode;
                variables.dataSaved = data;
                variables.log.logWithClassName(this, "cached permission");
            }
            variables.log.logWithClassName(this, "starting projection.");
            variables.sMediaProjection = variables.mProjectionManager.getMediaProjection(resultCode, data);

            if (variables.sMediaProjection != null) {
                // display metrics
                if (variables.projectionActivity != null) {
                    variables.mDisplay = variables.projectionActivity.getWindowManager().getDefaultDisplay();
                }
                if (variables.context != null) {
                    DisplayMetrics metrics = variables.context.getResources().getDisplayMetrics();
                    variables.mDensity = metrics.densityDpi;
                }

                // create virtual display depending on device width / height
                createVirtualDisplay();

                // register orientation change callback
                if (variables.context != null) {
                    variables.mOrientationChangeCallback = new OrientationChangeCallback(variables.context, variables, this);
                }

                if (variables.mOrientationChangeCallback.canDetectOrientation()) {
                    variables.mOrientationChangeCallback.enable();
                }

                // register media projection stop callback
                variables.sMediaProjection.registerCallback(new MediaProjectionStopCallback(variables, this), variables.mHandler);
                variables.log.logWithClassName(this, "started projection.");
            }
        } else {
            stopCapture();
        }
    }

    public void stopCapture() {
        variables.mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (variables.sMediaProjection != null) {
                    variables.sMediaProjection.stop();
                } else {
                    Looper l = variables.mHandler.getLooper();
                    if (l != null) {
                        variables.log.logWithClassName(this, "quiting looper");
                        l.quitSafely();
                        variables.log.logWithClassName(this, "quit looper");
                        new Thread() {
                            @Override
                            public void run() {
                                variables.looperHelper.stopLooper();
                            }
                        }.start();
                    }
                }
            }
        });
    }
}
