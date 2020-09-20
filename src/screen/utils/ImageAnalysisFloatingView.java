package screen.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayInputStream;

import smallville7123.smartclick.R;

import static android.content.Context.WINDOW_SERVICE;

public class ImageAnalysisFloatingView {
    private WindowManager mWindowManager;
    private ViewGroup mFloatingView;
    private View collapsedView;
    private View expandedView;

    private TextView textViewMain;
    private ImageView imageViewMain;

    private RecyclerView recyclerView;
    private ImageAnalysisRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    View analyzerrootLayout;

    Variables variables;

    final WindowManager.LayoutParams maximizedLayout = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
    );

    final WindowManager.LayoutParams minimizedLayout = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
    );

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
        textViewMain = (TextView) mFloatingView.findViewById(R.id.analyzerTextView);
        imageViewMain = (ImageView) mFloatingView.findViewById(R.id.analyzerSelectedImage);
        recyclerView = (RecyclerView) mFloatingView.findViewById(R.id.analyzerRecyclerView);
        collapsedView = mFloatingView.findViewById(R.id.analyzerLayoutCollapsed);
        expandedView = mFloatingView.findViewById(R.id.analyzerLayoutExpanded);

        // setup our recycler view

        layoutManager = new LinearLayoutManager(variables.context, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new ImageAnalysisRecyclerViewAdapter();
        mAdapter.setClickListener(new ImageAnalysisRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(byte[] memory, String text) {
                Bitmap image = BitmapFactory.decodeStream(new ByteArrayInputStream(memory));
                // TODO: resize bitmap
                textViewMain.setText(text);
                imageViewMain.setImageBitmap(image);
            }
        });
        recyclerView.setAdapter(mAdapter);

        // set default views

        collapsedView.setVisibility(View.GONE);
        expandedView.setVisibility(View.GONE);
        analyzerrootLayout.setVisibility(View.GONE);

        // get window services and adding the floating view to it

        mWindowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, minimizedLayout);

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
                        initialX = minimizedLayout.x;
                        initialY = minimizedLayout.y;
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
                        mWindowManager.updateViewLayout(mFloatingView, maximizedLayout);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        //this code is helping the widget to move around the screen with fingers
                        minimizedLayout.x = initialX + (int) (event.getRawX() - initialTouchX);
                        minimizedLayout.y = initialY + (int) (event.getRawY() - initialTouchY);
                        mWindowManager.updateViewLayout(mFloatingView, minimizedLayout);
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
                mWindowManager.updateViewLayout(mFloatingView, minimizedLayout);
            }
        });

        mFloatingView.findViewById(R.id.analyzerEraseVideoBufferButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.clearData();
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
                mWindowManager.updateViewLayout(mFloatingView, minimizedLayout);
                // should we erase the video buffer on finish?
            }
        });
    }

    public void onStart() {
        variables.log.logMethodNameWithClassName(this);
        expandedView.setVisibility(View.VISIBLE);
        collapsedView.setVisibility(View.GONE);
        analyzerrootLayout.setVisibility(View.VISIBLE);
        mWindowManager.updateViewLayout(mFloatingView, maximizedLayout);
        // duplicate the video memory
        mAdapter.setData(variables.videoMemory);
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
