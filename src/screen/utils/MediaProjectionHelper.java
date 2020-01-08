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
import android.util.Log;

public class MediaProjectionHelper {
    private Variables variables;

    public MediaProjectionHelper(final Variables variables) {
        this.variables = variables;
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
            requestCapturePermission();
            variables.log.errorNoStackTrace("requested");
        }
    }

    public void stopScreenMirror() {
        variables.log.errorNoStackTrace("looper is " + variables.looper);
        if (variables.looper != null) {
            variables.log.errorNoStackTrace("stopCapture");
            stopCapture();
            variables.log.errorNoStackTrace("stopLooper");
            variables.looperHelper.stopLooper();
            variables.log.errorNoStackTrace("stopped");
        }
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
        if (variables.sMediaProjection != null) return;
//        if (variables.grantedPermission) {
//            Log.e("ScreenCapture", "permission already granted");
//            Log.e("ScreenCapture", "using cached permission");
//            startCapture(variables.resultCodeSaved, variables.dataSaved);
//        } else {
            Intent intent = new Intent(variables.activity, DetectMediaProjectionPromptService.class);
            intent.putExtra("data",1);
            variables.activity.startService(intent);
            variables.activity.startActivityForResult(
                    variables.mProjectionManager.createScreenCaptureIntent(), variables.REQUEST_CODE
            );
//        }
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
        if (resultCode == Activity.RESULT_OK) {
            if (!variables.grantedPermission) {
                Log.e("ScreenCapture", "caching permission");
                variables.grantedPermission = true; // comment this out to disable caching
                variables.resultCodeSaved = resultCode;
                variables.dataSaved = data;
                Log.e("ScreenCapture", "cached permission");
            }
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
                        Log.e("ScreenCapture", "quiting looper");
                        l.quitSafely();
                        Log.e("ScreenCapture", "quit looper");
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
