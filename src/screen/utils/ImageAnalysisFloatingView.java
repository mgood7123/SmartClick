package screen.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayInputStream;

import smallville7123.smartclick.R;
import smallville7123.widgets.FloatingView;

import static android.content.Context.WINDOW_SERVICE;

public class ImageAnalysisFloatingView {
    private WindowManager mWindowManager;
    private FloatingView mFloatingView;
    private View collapsedView;
    private View expandedView;

    private TextView textViewMain;
    private ImageView imageViewMain;
    
    private String cachedText;
    private Bitmap cachedBitmap;

    private RecyclerView recyclerView;
    private ImageAnalysisRecyclerViewAdapter mAdapter;
    private ImageAnalysisRecyclerViewAdapter cachedAdapter;
    private RecyclerView.LayoutManager layoutManager;

    View analyzerRootLayout;

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

    WindowManager.LayoutParams cachedLayout;
    

    public ImageAnalysisFloatingView(Variables variables) {
        this.variables = variables;
    }

    public void runOnUiThread(final Runnable action) {
        variables.runOnUiThread(action);
    }

    public void onCreate(Context context) {
        variables.log.logMethodNameWithClassName(this);

        refreshUI();

        // hide by default

        analyzerRootLayout.setVisibility(View.GONE);
    }

    public void onStart() {
        variables.log.logMethodNameWithClassName(this);
        mFloatingView.expand();
        analyzerRootLayout.setVisibility(View.VISIBLE);
        cachedLayout = maximizedLayout;
        mWindowManager.updateViewLayout(mFloatingView, cachedLayout);
        // duplicate the video memory
        mAdapter.setData(variables.videoMemory);
        mAdapter.notifyDataSetChanged();
    }

    public void onDestroy() {
        variables.log.logMethodNameWithClassName(this);
        analyzerRootLayout.setVisibility(View.GONE);
        mWindowManager.removeViewImmediate(analyzerRootLayout);
        mWindowManager = null;
        analyzerRootLayout = null;
        mFloatingView.collapse();
        mFloatingView = null;
    }

    public void refreshUI() {
        // in the Analyser, we need to refresh our UI

        // only problem now, is how tf do we refresh a view
        if (mWindowManager != null) mWindowManager.removeViewImmediate(analyzerRootLayout);


        // cache all view visibilities so we can restore them

        boolean expanded = false;
        Integer cachedAnalyzerRootLayoutVisibility = View.VISIBLE;
        Integer cachedTextViewMainVisibility = View.VISIBLE;
        Integer cachedImageViewMainVisibility = View.VISIBLE;
        Integer cachedRecyclerViewVisibility = View.VISIBLE;


        if (mFloatingView != null) expanded = mFloatingView.expanded;
        if (analyzerRootLayout != null)
            cachedAnalyzerRootLayoutVisibility = analyzerRootLayout.getVisibility();
        if (textViewMain != null)
            cachedTextViewMainVisibility = textViewMain.getVisibility();
        if (imageViewMain != null)
            cachedImageViewMainVisibility = imageViewMain.getVisibility();
        if (recyclerView != null)
            cachedRecyclerViewVisibility = recyclerView.getVisibility();

        // get all required view's
        mFloatingView = (FloatingView) variables.layoutInflater.inflate(R.layout.layout_floating_image_analysis_widget, null);
        analyzerRootLayout = variables.log.errorAndThrowIfNull(mFloatingView.findViewById(R.id.analyzerRootLayout));
        collapsedView = variables.log.errorAndThrowIfNull(mFloatingView.findViewById(R.id.analyzerLayoutCollapsed));
        expandedView = variables.log.errorAndThrowIfNull(mFloatingView.findViewById(R.id.analyzerLayoutExpanded));

        textViewMain = (TextView) mFloatingView.findViewById(R.id.analyzerTextView);
        imageViewMain = (ImageView) mFloatingView.findViewById(R.id.analyzerSelectedImage);
        recyclerView = (RecyclerView) mFloatingView.findViewById(R.id.analyzerRecyclerView);

        // set up our RecyclerView
        layoutManager = new LinearLayoutManager(variables.context, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);


        mAdapter = new ImageAnalysisRecyclerViewAdapter();
        recyclerView.setAdapter(mAdapter);
        // the cached adapter must be updated after the new adapter's state has been restored

        // restore view states
        if (expanded) mFloatingView.expand(); else mFloatingView.collapse();
        analyzerRootLayout.setVisibility(cachedAnalyzerRootLayoutVisibility);
        textViewMain.setVisibility(cachedTextViewMainVisibility);
        imageViewMain.setVisibility(cachedImageViewMainVisibility);
        recyclerView.setVisibility(cachedRecyclerViewVisibility);

        if (cachedText != null) textViewMain.setText(cachedText);
        if (cachedBitmap != null) imageViewMain.setImageBitmap(cachedBitmap);
        if (cachedAdapter != null) {
            mAdapter.setData(cachedAdapter);
            mAdapter.notifyDataSetChanged();
        }

        // the new adapter's state has been restored, the cached adapter can now be updated
        cachedAdapter = mAdapter;

        // set up on-click listeners
        mAdapter.setClickListener(new ImageAnalysisRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(byte[] memory, String text) {
                cachedText = text;
                cachedBitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(memory));
                // TODO: resize bitmap
                textViewMain.setText(cachedText);
                imageViewMain.setImageBitmap(cachedBitmap);
            }
        });
        analyzerRootLayout.setOnTouchListener(new View.OnTouchListener() {
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
                        cachedLayout = maximizedLayout;
                        mWindowManager.updateViewLayout(mFloatingView, cachedLayout);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        //this code is helping the widget to move around the screen with fingers
                        minimizedLayout.x = initialX + (int) (event.getRawX() - initialTouchX);
                        minimizedLayout.y = initialY + (int) (event.getRawY() - initialTouchY);
                        cachedLayout = minimizedLayout;
                        mWindowManager.updateViewLayout(mFloatingView, cachedLayout);
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
                cachedLayout = minimizedLayout;
                mWindowManager.updateViewLayout(mFloatingView, cachedLayout);
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
                analyzerRootLayout.setVisibility(View.GONE);
                expandedView.setVisibility(View.VISIBLE);
                collapsedView.setVisibility(View.GONE);
                cachedLayout = minimizedLayout;
                mWindowManager.updateViewLayout(mFloatingView, cachedLayout);
                // should we erase the video buffer on finish?
            }
        });

        // apply our updated UI

        mWindowManager = (WindowManager) variables.context.getSystemService(WINDOW_SERVICE);
        if (cachedLayout == null) cachedLayout = minimizedLayout;
        mWindowManager.addView(mFloatingView, cachedLayout);
    }
}
