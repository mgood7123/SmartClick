package screen.utils;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import smallville7123.smartclick.R;

import static android.content.Context.WINDOW_SERVICE;

public class ImageAnalysisFloatingView {
    private WindowManager mWindowManager;
    private ViewGroup mFloatingView;
    private View collapsedView;
    private View expandedView;

    private ImageView imageViewMain;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    View analyzerrootLayout;

    Variables variables;

    public ImageAnalysisFloatingView(Variables variables) {
        this.variables = variables;
    }

    public void runOnUiThread(final Runnable action) {
        variables.runOnUiThread(action);
    }

    public void onCreate(Context context) {
        variables.log.logMethodNameWithClassName(this);

        // get the widget layout from xml using layout inflater
        mFloatingView = (ViewGroup) variables.layoutInflater.inflate(R.layout.layout_floating_image_analysis_widget, null);

        // get all required view's from the inflated layout
        analyzerrootLayout = mFloatingView.findViewById(R.id.analyzerrootLayout);
        imageViewMain = (ImageView) mFloatingView.findViewById(R.id.analyzerSelectedImage);
        recyclerView = (RecyclerView) mFloatingView.findViewById(R.id.analyzerRecyclerView);
        collapsedView = mFloatingView.findViewById(R.id.analyzerLayoutCollapsed);
        expandedView = mFloatingView.findViewById(R.id.analyzerLayoutExpanded);

        // setup our recycler view

        layoutManager = new LinearLayoutManager(variables.context);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new ImageAnalysisRecyclerViewAdapter(variables.bitmapBuffer);
        recyclerView.setAdapter(mAdapter);

        // set default views

        collapsedView.setVisibility(View.GONE);
        expandedView.setVisibility(View.GONE);
        analyzerrootLayout.setVisibility(View.GONE);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        // get window services and adding the floating view to it

        mWindowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);

        //adding an touchlistener to make drag movement of the floating widget

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

                        variables.log.logWithClassName(ImageAnalysisFloatingView.this, "expanding view");
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
                variables.log.logWithClassName(ImageAnalysisFloatingView.this, "collapsing view");
                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);
            }
        });

        mFloatingView.findViewById(R.id.analyserEraseVideoBufferButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                variables.bitmapBuffer.clear();
                mAdapter.notifyDataSetChanged();
            }
        });

        //adding click listener to close button
        mFloatingView.findViewById(R.id.analyzerFinishButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                variables.log.logWithClassName(ImageAnalysisFloatingView.this, "hiding ImageAnalysisFloatingView");
                analyzerrootLayout.setVisibility(View.GONE);
                expandedView.setVisibility(View.VISIBLE);
                collapsedView.setVisibility(View.GONE);
                // should we erase the video buffer on finish?
            }
        });
    }

    public void onStart() {
        variables.log.logMethodNameWithClassName(this);
        expandedView.setVisibility(View.VISIBLE);
        collapsedView.setVisibility(View.GONE);
        analyzerrootLayout.setVisibility(View.VISIBLE);
        mAdapter.notifyDataSetChanged();
    }

    public void onDestroy() {
        variables.log.logMethodNameWithClassName(this);
        if (analyzerrootLayout != null) analyzerrootLayout.setVisibility(View.GONE);
        if (expandedView != null) expandedView.setVisibility(View.GONE);
        if (collapsedView != null) collapsedView.setVisibility(View.GONE);
        if (mWindowManager != null) mWindowManager.removeViewImmediate(analyzerrootLayout);
    }
}
