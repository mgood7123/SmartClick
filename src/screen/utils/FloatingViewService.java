package screen.utils;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import smallville7123.smartclick.R;
import smallville7123.widgets.FloatingView;

public class FloatingViewService extends Service {

    private FloatingView mFloatingView;
    private View collapsedView;
    private View expandedView;

    private ScreenUtils SU = new ScreenUtils();

    private Handler mHandler = new Handler();
    private Thread mUiThread = Thread.currentThread();


    // start of android.app.Activity.runOnUiThread

    /**
     * Runs the specified action on the UI thread. If the current thread is the UI
     * thread, then the action is executed immediately. If the current thread is
     * not the UI thread, the action is posted to the event queue of the UI thread.
     *
     * @param action the action to run on the UI thread
     */
    public void runOnUiThread(Runnable action) {
        if (Thread.currentThread() != mUiThread) {
            mHandler.post(action);
        } else {
            action.run();
        }
    }

    // end of android.app.Activity.runOnUiThread

    public FloatingViewService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    ImageAnalysisFloatingView analyzer;

    ImageView cache;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();

        SU.variables.log.logMethodNameWithClassName(this);

        SU.onCreate(this, new Variables.Callback() {
            @Override
            public void run(Object o) {
                runOnUiThread((Runnable) o);
            }
        });

        analyzer = new ImageAnalysisFloatingView(SU.variables);

        //getting the widget layout from xml using layout inflater
        mFloatingView = (FloatingView) SU.variables.layoutInflater.inflate(R.layout.layout_floating_widget, null);
        mFloatingView.attachToWindowManager();

        mFloatingView.setOnSetupExternalViews(new FloatingView.Callback<FloatingView>() {
            @Override
            public void run(FloatingView floatingView) {
                SU.setBitmapView((BitmapView) floatingView.findViewById(R.id.renderedCaptureFloatingWidget));
            }
        });

        mFloatingView.setOnRestoreState(new FloatingView.Callback<Bundle>() {
            @Override
            public void run(Bundle argument) {
                SU.variables.bitmapView.setImageBitmap(SU.variables.lastImageCompressed);
            }
        });

        mFloatingView.setOnSetupExternalListeners(new FloatingView.Callback<FloatingView>() {
            @Override
            public void run(FloatingView floatingView) {
                floatingView.findViewById(R.id.FloatMirrorStart).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SU.variables.log.logWithClassName(FloatingViewService.this, "MIRROR START");
                        SU.startScreenMirror();
                    }
                });

                floatingView.findViewById(R.id.FloatMirrorStop).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SU.variables.log.logWithClassName(FloatingViewService.this, "MIRROR STOP");
                        SU.stopScreenMirror();
                    }
                });

                floatingView.findViewById(R.id.FloatRecordStart).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SU.variables.log.logWithClassName(FloatingViewService.this, "RECORD START");
                        SU.startScreenRecord();
                    }
                });

                floatingView.findViewById(R.id.FloatRecordEnd).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SU.variables.log.logWithClassName(FloatingViewService.this, "RECORD STOP");
                        SU.stopScreenRecord();
                    }
                });

                floatingView.findViewById(R.id.buttonAnalyse).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SU.variables.screenRecord) {
                            SU.variables.log.logWithClassName(FloatingViewService.this, "RECORD STOP");
                            SU.stopScreenRecord();
                        }
                        SU.variables.log.logWithClassName(FloatingViewService.this, "ANALYSE");
                        analyzer.onStart();
                    }
                });

                //adding click listener to close button
                floatingView.findViewById(R.id.buttonClose).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SU.variables.log.logWithClassName(FloatingViewService.this, "killing FLOATING VIEW SERVICE");
                        stopSelf();
                    }
                });
            }
        });

        mFloatingView.reloadResources();

        // the analyzer should appear ON TOP of our floating window, not behind it

        analyzer.onCreate(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDestroy() {
        super.onDestroy();
        SU.variables.log.logMethodNameWithClassName(this);
        SU.variables.log.logWithClassName(this, "RECORD STOP");
        SU.stopScreenRecord();
        mFloatingView.detachFromWindowManager();
        analyzer.onDestroy();
        analyzer = null;
        mFloatingView = null;
    }
}