package screen.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayInputStream;

import smallville7123.smartclick.R;
import smallville7123.widgets.FloatingView;

public class ImageAnalysisFloatingView {
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
        variables.log.log("fullscreenWhenExpanded is " + mFloatingView.fullscreenWhenExpanded);
        mFloatingView.expand();

        analyzerRootLayout.setVisibility(View.VISIBLE);
        // duplicate the video memory
        mAdapter.setData(variables.videoMemory);
        mAdapter.notifyDataSetChanged();
    }

    public void onDestroy() {
        variables.log.logMethodNameWithClassName(this);
        mFloatingView.detachFromWindowManager();
        analyzerRootLayout.setVisibility(View.GONE);
        mFloatingView = null;
        analyzerRootLayout = null;
    }

    public void refreshUI() {
        variables.log.logMethodName();
        // in the Analyser, we need to refresh our UI

        // remove existing view

        if (mFloatingView != null) mFloatingView.detachFromWindowManager();

        // cache all view visibilities so we can restore them

        Boolean expanded = null;
        Boolean fullscreenWhenExpanded = null;
        WindowManager.LayoutParams layout = null;
        Integer cachedAnalyzerRootLayoutVisibility = View.VISIBLE;
        Integer cachedTextViewMainVisibility = View.VISIBLE;
        Integer cachedImageViewMainVisibility = View.VISIBLE;
        Integer cachedRecyclerViewVisibility = View.VISIBLE;

        if (mFloatingView != null) {
            expanded = mFloatingView.expanded;
            fullscreenWhenExpanded = mFloatingView.fullscreenWhenExpanded;
            layout = mFloatingView.getLayout();
        }
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
        if (fullscreenWhenExpanded != null)
            mFloatingView.fullscreenWhenExpanded = fullscreenWhenExpanded;
        if (expanded != null) {
            if (expanded.booleanValue())
                mFloatingView.expand();
            else
                mFloatingView.collapse();
        }
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
                mFloatingView.collapse();
                mFloatingView.updateWindowManagerLayout(mFloatingView.minimizedLayout);
                // should we erase the video buffer on finish?
            }
        });

        // apply our updated UI
        mFloatingView.attachToWindowManager(layout);
    }
}
