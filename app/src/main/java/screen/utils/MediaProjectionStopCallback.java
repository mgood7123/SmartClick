package screen.utils;

import android.media.projection.MediaProjection;
import android.os.Looper;

public class MediaProjectionStopCallback extends MediaProjection.Callback {
    private final Variables variables;
    private final MediaProjectionHelper mediaProjectionHelper;

    public MediaProjectionStopCallback(Variables variables, MediaProjectionHelper mediaProjectionHelper) {
        this.variables = variables;
        this.mediaProjectionHelper = mediaProjectionHelper;
    }

    @Override
    public void onStop() {
        variables.log.logWithClassName(this, "posting stop projection.");
        variables.mHandler.post(new Runnable() {
            @Override
            public void run() {
                variables.log.logWithClassName(MediaProjectionStopCallback.this, "stopping projection.");

                if (variables.mVirtualDisplay != null) variables.mVirtualDisplay.release();
                if (variables.mImageReader != null) variables.mImageReader.setOnImageAvailableListener(null, null);
                if (variables.mOrientationChangeCallback != null)
                    variables.mOrientationChangeCallback.disable();
                variables.sMediaProjection.unregisterCallback(MediaProjectionStopCallback.this);
                variables.sMediaProjection = null;
                if (variables.screenRecord) variables.screenRecord = false;
                variables.log.logWithClassName(MediaProjectionStopCallback.this, "stopped projection.");

                // we quit from here as it seems to be the last message posted to the handler
                variables.log.logWithClassName(MediaProjectionStopCallback.this, "quiting looper");
                variables.mHandler.getLooper().quitSafely();
                variables.log.logWithClassName(MediaProjectionStopCallback.this, "quit looper");
            }
        });
        if (variables.looper != null) {
            variables.log.logWithClassName(this, "stopCapture");
            Looper l = variables.mHandler.getLooper();
            if (l != null) {
                variables.log.logWithClassName(this, "quiting looper");
                l.quitSafely();
                variables.log.logWithClassName(this, "quit looper");
            }
            variables.log.logWithClassName(this, "stopLooper");
            new Thread() {
                @Override
                public void run() {
                    variables.looperHelper.stopLooper();
                }
            }.start();
            variables.log.logWithClassName(this, "stopped");
        }
        variables.log.logWithClassName(this, "posted stop projection.");
    }
}