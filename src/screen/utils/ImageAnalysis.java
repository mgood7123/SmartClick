package screen.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;

import smallville7123.smartclick.R;

import static android.content.Context.WINDOW_SERVICE;

public class ImageAnalysis {
    private WindowManager mWindowManager;
    private ViewGroup mFloatingView;
    private View collapsedView;
    private View expandedView;

    View analyzerrootLayout;

    Variables variables;

    public ImageAnalysis(Variables variables) {
        this.variables = variables;
    }

    public void runOnUiThread(final Runnable action) {
        variables.runOnUiThread(action);
    }

    public void onCreate(Context context) {
        Log.e("ImageAnalysis", "onCreate");

        //getting the widget layout from xml using layout inflater
        mFloatingView = (ViewGroup) variables.layoutInflater.inflate(R.layout.layout_floating_image_analysis_widget, null);

        //setting the layout parameters
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        //getting windows services and adding the floating view to it
        mWindowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);

        //getting the collapsed and expanded view from the floating view
        collapsedView = mFloatingView.findViewById(R.id.analyzerLayoutCollapsed);
        expandedView = mFloatingView.findViewById(R.id.analyzerLayoutExpanded);
        // and set default views
        collapsedView.setVisibility(View.GONE);
        expandedView.setVisibility(View.VISIBLE);

        //adding an touchlistener to make drag movement of the floating widget
        analyzerrootLayout = mFloatingView.findViewById(R.id.analyzerrootLayout);
        analyzerrootLayout.setVisibility(View.GONE);
        analyzerrootLayout.setOnTouchListener(new View.OnTouchListener() {
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

                        Log.e("ImageAnalysis", "expanding view");
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
                Log.e("ImageAnalysis", "collapsing view");
                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);
            }
        });
        //adding click listener to close button
        mFloatingView.findViewById(R.id.analyzerFinishButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("ImageAnalysis", "hiding ImageAnalysis");
                analyzerrootLayout.setVisibility(View.GONE);
                expandedView.setVisibility(View.VISIBLE);
                collapsedView.setVisibility(View.GONE);
            }
        });
    }

    public void onDestroy() {
        variables.log.logMethodName();
        if (analyzerrootLayout != null) analyzerrootLayout.setVisibility(View.GONE);
        if (expandedView != null) expandedView.setVisibility(View.GONE);
        if (collapsedView != null) collapsedView.setVisibility(View.GONE);
        if (mWindowManager != null) mWindowManager.removeViewImmediate(analyzerrootLayout);
    }

    public void start() {
        variables.log.logMethodName();
        expandedView.setVisibility(View.VISIBLE);
        collapsedView.setVisibility(View.GONE);
        analyzerrootLayout.setVisibility(View.VISIBLE);

        if (variables.bitmapBuffer.size() != 0) {
            ImageView im = (ImageView)analyzerrootLayout.findViewById(R.id.analyserRenderedCaptureFloatingWidget);
            // decompress memory to bitmap
            Bitmap image = BitmapFactory.decodeStream(new ByteArrayInputStream(variables.bitmapBuffer.get(0).toByteArray()));
            if (image != null) im.setImageBitmap(image);
        }
    }
}
