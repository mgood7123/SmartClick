package screen.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import smallville7123.libparcelablebundle.ParcelableBundle;
import smallville7123.smartclick.R;
import smallville7123.floatingview.FloatingView;

public class ImageAnalysisFloatingView {
    private FloatingView mFloatingView;

    private TextView textViewMain;
    private BitmapView bitmapViewMain;
    
    private String cachedText;
    private byte[] cachedCompressedBitmap;

    private RecyclerView recyclerView;
    private ImageAnalysisRecyclerViewAdapter currentAdapter;
    private ImageAnalysisRecyclerViewAdapter previousAdapter;
    private RecyclerView.LayoutManager layoutManager;

    View analyzerRootLayout;

    Variables variables;
    private View sourceButton;

    public ImageAnalysisFloatingView(Variables variables) {
        this.variables = variables;
    }

    public void runOnUiThread(final Runnable action) {
        variables.runOnUiThread(action);
    }

    final String analyzerRootLayoutKey = "1";
    final String textViewMainKey = "2";
    final String bitmapViewMainKey = "3";
    final String recyclerViewKey = "4";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onCreate(Context context) {
        variables.log.logMethodNameWithClassName(this);

        mFloatingView = (FloatingView) variables.layoutInflater.inflate(R.layout.layout_floating_image_analysis_widget, null);
        mFloatingView.attachToWindowManager();

        mFloatingView.setOnSetupExternalListeners(new FloatingView.Callback<FloatingView>() {
            @Override
            public void run(final FloatingView floatingView) {
                // set up on-click listeners
                currentAdapter.setClickListener(new ImageAnalysisRecyclerViewAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(byte[] memory, String text) {
                        cachedText = text;
                        cachedCompressedBitmap = memory;
                        textViewMain.setText(cachedText);
                        bitmapViewMain.setImageBitmap(memory, BitmapView.ScaleMode.SCALE_WIDTH_HEIGHT);
                    }
                });

                floatingView.findViewById(R.id.analyzerEraseVideoBufferButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentAdapter.clearData();
                        currentAdapter.notifyDataSetChanged();
                    }
                });

                //adding click listener to close button
                floatingView.findViewById(R.id.analyzerFinishButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (sourceButton != null) sourceButton.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                        variables.log.logWithClassName(ImageAnalysisFloatingView.this, "hiding ImageAnalysisFloatingView");
                        analyzerRootLayout.setVisibility(View.GONE);
                        floatingView.collapse();
                        floatingView.updateWindowManagerLayout(floatingView.minimizedLayout);
                    }
                });
            }
        });

        mFloatingView.setOnSaveState(new FloatingView.Callback<ParcelableBundle>() {
            @Override
            public void run(ParcelableBundle state) {
                // cache all view visibilities so we can restore them

                if (analyzerRootLayout != null)
                    state.putInt(analyzerRootLayoutKey, analyzerRootLayout.getVisibility());
                if (textViewMain != null)
                    state.putInt(textViewMainKey, textViewMain.getVisibility());
                if (bitmapViewMain != null)
                    state.putInt(bitmapViewMainKey, bitmapViewMain.getVisibility());
                if (recyclerView != null)
                    state.putInt(recyclerViewKey, recyclerView.getVisibility());
            }
        });

        mFloatingView.setOnSetupExternalViews(new FloatingView.Callback<FloatingView>() {
            @Override
            public void run(FloatingView floatingView) {
                // get all required view's
                analyzerRootLayout = variables.log.errorAndThrowIfNull(floatingView.findViewById(R.id.analyzerRootLayout));

                textViewMain = (TextView) floatingView.findViewById(R.id.analyzerTextView);
                bitmapViewMain = (BitmapView) floatingView.findViewById(R.id.analyzerSelectedImage);
                recyclerView = (RecyclerView) floatingView.findViewById(R.id.analyzerRecyclerView);

                // set up our RecyclerView
                layoutManager = new LinearLayoutManager(variables.context, LinearLayoutManager.HORIZONTAL, false);
                recyclerView.setLayoutManager(layoutManager);

                currentAdapter = new ImageAnalysisRecyclerViewAdapter();
                recyclerView.setAdapter(currentAdapter);
                //
                // the state of our adapter cannot be saved easily into Bundle due to it using Views
                // because of this, the adapter must be saved such that it outlives this scope
                // and then used to restore the new adapter's state
                //
            }
        });

        mFloatingView.setOnRestoreState(new FloatingView.Callback<ParcelableBundle>() {
            @Override
            public void run(ParcelableBundle state) {
                // restore view states
                int visibility = state.getInt(analyzerRootLayoutKey, View.VISIBLE);
                analyzerRootLayout.setVisibility(visibility);
                visibility = state.getInt(textViewMainKey, View.VISIBLE);
                textViewMain.setVisibility(visibility);
                visibility = state.getInt(bitmapViewMainKey, View.VISIBLE);
                bitmapViewMain.setVisibility(visibility);
                visibility = state.getInt(recyclerViewKey, View.VISIBLE);
                recyclerView.setVisibility(visibility);

                if (cachedText != null) textViewMain.setText(cachedText);
                if (cachedCompressedBitmap != null) bitmapViewMain.setImageBitmap(cachedCompressedBitmap);
                
                // if we previously had an adapter
                // then we use the previous adapter to restore our current adapter
                if (previousAdapter != null) {
                    currentAdapter.setData(previousAdapter);
                    currentAdapter.notifyDataSetChanged();
                }

                // store the new adapter as the previous adapter
                previousAdapter = currentAdapter;
            }
        });

        mFloatingView.reloadResources();

        // hide by default
        analyzerRootLayout.setVisibility(View.GONE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onStart() {
        onStart(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onStart(View analyseButton) {
        if (analyseButton != null) sourceButton = analyseButton;
        variables.log.logMethodNameWithClassName(this);
        mFloatingView.expand();

        analyzerRootLayout.setVisibility(View.VISIBLE);
        // duplicate the video memory
        currentAdapter.setData(variables.bitmapView.getRecordedData());
        currentAdapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onDestroy() {
        variables.log.logMethodNameWithClassName(this);

        if (mFloatingView != null) {
            mFloatingView.detachFromWindowManager();

            // destroy references
            analyzerRootLayout = null;
            mFloatingView = null;
        }
    }
}