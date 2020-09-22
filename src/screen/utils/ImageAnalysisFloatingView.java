package screen.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
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
    private ImageAnalysisRecyclerViewAdapter currentAdapter;
    private ImageAnalysisRecyclerViewAdapter previousAdapter;
    private RecyclerView.LayoutManager layoutManager;

    View analyzerRootLayout;

    Variables variables;

    public ImageAnalysisFloatingView(Variables variables) {
        this.variables = variables;
    }

    public void runOnUiThread(final Runnable action) {
        variables.runOnUiThread(action);
    }

    final String analyzerRootLayoutKey = "1";
    final String textViewMainKey = "2";
    final String imageViewMainKey = "3";
    final String recyclerViewKey = "4";

    public void onCreate(Context context) {
        variables.log.logMethodNameWithClassName(this);

        mFloatingView = (FloatingView) variables.layoutInflater.inflate(R.layout.layout_floating_image_analysis_widget, null);
        mFloatingView.attachToWindowManager();

        mFloatingView.setOnSetupExternalOnClickListeners(new FloatingView.Callback<FloatingView>() {
            @Override
            public void run(final FloatingView view) {
                // set up on-click listeners
                currentAdapter.setClickListener(new ImageAnalysisRecyclerViewAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(byte[] memory, String text) {
                        cachedText = text;
                        cachedBitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(memory));
                        // TODO: resize bitmap
                        textViewMain.setText(cachedText);
                        imageViewMain.setImageBitmap(cachedBitmap);
                    }
                });

                view.findViewById(R.id.analyzerEraseVideoBufferButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentAdapter.clearData();
                        currentAdapter.notifyDataSetChanged();
                    }
                });

                //adding click listener to close button
                view.findViewById(R.id.analyzerFinishButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        variables.log.logWithClassName(ImageAnalysisFloatingView.this, "hiding ImageAnalysisFloatingView");
                        analyzerRootLayout.setVisibility(View.GONE);
                        view.collapse();
                        view.updateWindowManagerLayout(view.minimizedLayout);
                        // should we erase the video buffer on finish?
                    }
                });
            }
        });

        mFloatingView.setOnSaveState(new FloatingView.Callback<Bundle>() {
            @Override
            public void run(Bundle state) {
                // cache all view visibilities so we can restore them

                if (analyzerRootLayout != null)
                    state.putInt(analyzerRootLayoutKey, analyzerRootLayout.getVisibility());
                if (textViewMain != null)
                    state.putInt(textViewMainKey, textViewMain.getVisibility());
                if (imageViewMain != null)
                    state.putInt(imageViewMainKey, imageViewMain.getVisibility());
                if (recyclerView != null)
                    state.putInt(recyclerViewKey, recyclerView.getVisibility());
            }
        });

        mFloatingView.setOnSetupExternalViews(new FloatingView.Callback<FloatingView>() {
            @Override
            public void run(FloatingView view) {
                // get all required view's
                analyzerRootLayout = variables.log.errorAndThrowIfNull(view.findViewById(R.id.analyzerRootLayout));
                collapsedView = variables.log.errorAndThrowIfNull(view.findViewById(R.id.analyzerLayoutCollapsed));
                expandedView = variables.log.errorAndThrowIfNull(view.findViewById(R.id.analyzerLayoutExpanded));

                textViewMain = (TextView) view.findViewById(R.id.analyzerTextView);
                imageViewMain = (ImageView) view.findViewById(R.id.analyzerSelectedImage);
                recyclerView = (RecyclerView) view.findViewById(R.id.analyzerRecyclerView);

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

        mFloatingView.setOnRestoreState(new FloatingView.Callback<Bundle>() {
            @Override
            public void run(Bundle state) {
                // restore view states
                int visibility = state.getInt(analyzerRootLayoutKey, View.VISIBLE);
                analyzerRootLayout.setVisibility(visibility);
                visibility = state.getInt(textViewMainKey, View.VISIBLE);
                textViewMain.setVisibility(visibility);
                visibility = state.getInt(imageViewMainKey, View.VISIBLE);
                imageViewMain.setVisibility(visibility);
                visibility = state.getInt(recyclerViewKey, View.VISIBLE);
                recyclerView.setVisibility(visibility);

                if (cachedText != null) textViewMain.setText(cachedText);
                if (cachedBitmap != null) imageViewMain.setImageBitmap(cachedBitmap);
                
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

    public void onStart() {
        variables.log.logMethodNameWithClassName(this);
        mFloatingView.expand();

        analyzerRootLayout.setVisibility(View.VISIBLE);
        // duplicate the video memory
        currentAdapter.setData(variables.videoMemory);
        currentAdapter.notifyDataSetChanged();
    }

    public void onDestroy() {
        variables.log.logMethodNameWithClassName(this);
        mFloatingView.detachFromWindowManager();
        analyzerRootLayout.setVisibility(View.GONE);
        mFloatingView = null;
        analyzerRootLayout = null;
    }
}
