package screen.utils;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Handler;
import android.view.Display;
import android.view.LayoutInflater;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.util.Vector;

public class Variables {

    public final String TAG = getClass().getName();

    public ImageView imageView;

    public final int REQUEST_CODE = 100;
    public final int REQUEST_CODE_FLOATING_WINDOW = 101;

    public final String SCREENCAP_NAME = "screencap";

    public final int VIRTUAL_DISPLAY_FLAGS =
            DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY |
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;

    public MediaProjection sMediaProjection;

    public MediaProjectionManager mProjectionManager;

    public MediaProjectionHelper mediaProjectionHelper = new MediaProjectionHelper(this);

    public ImageReader mImageReader;

    public Thread looper;
    public Looper looperHelper = new Looper(this);

    public Handler mHandler;

    public Display mDisplay;
    public VirtualDisplay mVirtualDisplay;

    public int mDensity;

    public OrientationChangeCallback mOrientationChangeCallback;

    public Activity activity;
    public Activity projectionActivity;
    public Service service;

    public LogUtils log = new LogUtils(
            TAG, "a bug has occurred, this should not happen"
    );

    public boolean screenshot;
    public boolean grantedPermission;
    public int resultCodeSaved;
    public Intent dataSaved;
    public boolean screenRecord;
    public boolean stop;
    public String cacheDir;
    public LayoutInflater layoutInflater;
    public int max_bitmaps = 20;
    // use a ByteArrayOutputStream to eliminate disk io and keep compressed bitmaps in memory
    public Vector<ByteArrayOutputStream> bitmapBuffer = new Vector<>();

    public interface Callback<Runnable> {
        /**
         * When an object implementing interface <code>Runnable</code> is used
         * to create a thread, starting the thread causes the object's
         * <code>run</code> method to be called in that separately executing
         * thread.
         * <p>
         * The general contract of the method <code>run</code> is that it may
         * take any action whatsoever.
         *
         * @see     java.lang.Thread#run()
         */
        public abstract void run(Runnable runnable);
    }

    Callback runOnUiThread;

    public void setRunOnUIThread(Callback runnable) {
        runOnUiThread = runnable;
    }

    public void runOnUiThread(Runnable action) {
        runOnUiThread.run(action);
    }
}
