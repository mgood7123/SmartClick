package screen.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import smallville7123.floatingview.FloatingView;
import smallville7123.libparcelablebundle.ParcelableBundle;
import smallville7123.smartclick.R;

public class FloatingViewService extends Service {
    private FloatingView mFloatingView;
    private View collapsedView;
    private View expandedView;

    private ScreenUtils SU = new ScreenUtils();

    private Handler mHandler = new Handler();
    private Thread mUiThread = Thread.currentThread();
    private BitmapView bitmapView;


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
        return new MediaProjectionBinder(this);
    }

    ImageAnalysisFloatingView analyzer;

    ImageView cache;

    View mirrorStartButton;
    String mirrorStartButtonKey = "a";
    View mirrorStopButton;
    String mirrorStopButtonKey = "b";
    View recordStartButton;
    String recordStartButtonKey = "c";
    View recordStopButton;
    String recordStopButtonKey = "d";
    View analyseButton;
    String analyseButtonKey = "e";
    View closeButton;
    String closeButtonKey = "f";

    private final void saveViewColor(ParcelableBundle bundle, String key, View view) {
        if (view != null) {
            bundle.putParcelable(key, view.getBackgroundTintList());
        }
    }

    private final void restoreViewColor(ParcelableBundle bundle, String key, View view) {
        if (view != null) {
            ColorStateList colorStateList = bundle.getParcelable(key);
            if (colorStateList != null) view.setBackgroundTintList(colorStateList);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();

        new smallville7123.smartclick.Notification(this, "Floating View").show(1);

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
                mirrorStartButton = floatingView.findViewById(R.id.FloatMirrorStart);
                mirrorStopButton = floatingView.findViewById(R.id.FloatMirrorStop);
                recordStartButton = floatingView.findViewById(R.id.FloatRecordStart);
                recordStopButton = floatingView.findViewById(R.id.FloatRecordEnd);
                analyseButton = floatingView.findViewById(R.id.buttonAnalyse);
                closeButton = floatingView.findViewById(R.id.buttonClose);
                bitmapView = (BitmapView) floatingView.findViewById(R.id.renderedCaptureFloatingWidget);
                SU.setBitmapView(bitmapView);

                mirrorStartButton.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                mirrorStopButton.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                recordStartButton.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                recordStopButton.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                analyseButton.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                closeButton.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            }
        });
        
        mFloatingView.setOnSaveState(new FloatingView.Callback<ParcelableBundle>() {
            @Override
            public void run(ParcelableBundle state) {
                saveViewColor(state, mirrorStartButtonKey, mirrorStartButton);
                saveViewColor(state, mirrorStopButtonKey, mirrorStopButton);
                saveViewColor(state, recordStartButtonKey, recordStartButton);
                saveViewColor(state, recordStopButtonKey, recordStopButton);
                saveViewColor(state, analyseButtonKey, analyseButton);
                saveViewColor(state, closeButtonKey, closeButton);
                BitmapView.saveState(bitmapView, state, "bitmapView");
            }
        });

        mFloatingView.setOnRestoreState(new FloatingView.Callback<ParcelableBundle>() {
            @Override
            public void run(ParcelableBundle state) {
                restoreViewColor(state, mirrorStartButtonKey, mirrorStartButton);
                restoreViewColor(state, mirrorStopButtonKey, mirrorStopButton);
                restoreViewColor(state, recordStartButtonKey, recordStartButton);
                restoreViewColor(state, recordStopButtonKey, recordStopButton);
                restoreViewColor(state, analyseButtonKey, analyseButton);
                restoreViewColor(state, closeButtonKey, closeButton);
                BitmapView.restoreState(bitmapView, state, "bitmapView");
            }
        });

        mFloatingView.setOnSetupExternalListeners(new FloatingView.Callback<FloatingView>() {
            @Override
            public void run(FloatingView floatingView) {
                mirrorStartButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mirrorStartButton.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                        mirrorStopButton.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                        recordStartButton.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                        recordStopButton.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                        SU.variables.log.logWithClassName(FloatingViewService.this, "MIRROR START");
                        SU.startScreenMirror();
                    }
                });

                mirrorStopButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mirrorStartButton.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                        mirrorStopButton.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                        recordStartButton.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                        recordStopButton.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                        SU.variables.log.logWithClassName(FloatingViewService.this, "MIRROR STOP");
                        SU.stopScreenMirror();
                    }
                });

                recordStartButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mirrorStartButton.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                        mirrorStopButton.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                        recordStartButton.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                        recordStopButton.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                        SU.variables.log.logWithClassName(FloatingViewService.this, "RECORD START");
                        SU.startScreenRecord();
                    }
                });

                recordStopButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mirrorStartButton.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                        mirrorStopButton.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                        recordStartButton.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                        recordStopButton.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                        SU.variables.log.logWithClassName(FloatingViewService.this, "RECORD STOP");
                        SU.stopScreenRecord();
                    }
                });

                analyseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SU.variables.screenRecord) {
                            mirrorStartButton.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                            mirrorStopButton.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                            recordStartButton.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                            recordStopButton.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                            SU.variables.log.logWithClassName(FloatingViewService.this, "RECORD STOP");
                            SU.stopScreenRecord();
                        }
                        analyseButton.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                        SU.variables.log.logWithClassName(FloatingViewService.this, "ANALYSE");
                        analyzer.onStart(analyseButton);
                    }
                });

                closeButton.setOnClickListener(new View.OnClickListener() {
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
        stopForeground(true);
    }
}