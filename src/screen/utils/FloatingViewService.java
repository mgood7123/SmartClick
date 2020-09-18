package screen.utils;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import smallville7123.smartclick.R;

public class FloatingViewService extends Service {

    private WindowManager mWindowManager;
    private ViewGroup mFloatingView;
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

    final ImageAnalysis analyser = new ImageAnalysis(SU.variables);

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("FLOATING VIEW SERVICE", "onCreate");

        SU.onCreate(this, new Variables.Callback() {
            @Override
            public void run(Object o) {
                runOnUiThread((Runnable) o);
            }
        });

        //getting the widget layout from xml using layout inflater
        mFloatingView = (ViewGroup) SU.variables.layoutInflater.inflate(R.layout.layout_floating_widget, null);
        SU.setImageView((ImageView) mFloatingView.findViewById(R.id.renderedCaptureFloatingWidget));

        //setting the layout parameters
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                PixelFormat.TRANSLUCENT
        );

        //getting windows services and adding the floating view to it
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);

        //getting the collapsed and expanded view from the floating view
        collapsedView = mFloatingView.findViewById(R.id.layoutCollapsed);
        expandedView = mFloatingView.findViewById(R.id.layoutExpanded);
        //and set default views
        collapsedView.setVisibility(View.VISIBLE);
        expandedView.setVisibility(View.GONE);

        //adding an touchlistener to make drag movement of the floating widget
        mFloatingView.findViewById(R.id.relativeLayoutParent).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_UP:
                        //when the drag is ended switching the state of the widget
                        //
                        // TODO: do not change visibility when location has changed significantly
                        //  such as a drag from one area of the screen to another area of the screen
                        //

                        Log.e("FLOATING VIEW SERVICE", "expanding view");
                        collapsedView.setVisibility(View.GONE);
                        expandedView.setVisibility(View.VISIBLE);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        //this code is helping the widget to move around the screen with fingers
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });

        expandedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("FLOATING VIEW SERVICE", "collapsing view");
                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);
            }
        });

        mFloatingView.findViewById(R.id.FloatMirrorStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("FLOATING VIEW SERVICE", "RECORD START");
                SU.startScreenMirror();
            }
        });

        mFloatingView.findViewById(R.id.FloatMirrorStop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("FLOATING VIEW SERVICE", "RECORD STOP");
                SU.stopScreenMirror();
            }
        });

        mFloatingView.findViewById(R.id.FloatRecordStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("FLOATING VIEW SERVICE", "RECORD START");
                SU.startScreenRecord();
            }
        });

        mFloatingView.findViewById(R.id.FloatRecordEnd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("FLOATING VIEW SERVICE", "RECORD STOP");
                SU.stopScreenRecord();
            }
        });

        mFloatingView.findViewById(R.id.buttonAnalyse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("FLOATING VIEW SERVICE", "ANALYSE");
                analyser.start();
            }
        });

        //adding click listener to close button
        mFloatingView.findViewById(R.id.buttonClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("FLOATING VIEW SERVICE", "killing FLOATING VIEW SERVICE");
                stopSelf();
            }
        });

        analyser.onCreate(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }
}