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
    private BitmapView bitmapViewMainPicker;
    private BitmapView bitmapViewMainEditor;
    
    private String cachedText;
    private byte[] cachedCompressedBitmap;

    private RecyclerView recyclerView;
    private ImageAnalysisRecyclerViewAdapter currentAdapter;
    private ImageAnalysisRecyclerViewAdapter previousAdapter;
    private RecyclerView.LayoutManager layoutManager;

    View analyzerRootLayout;

    Variables variables;
    private View sourceButton;
    private View analyzerPicker;
    private View analyzerEditor;

    void initVariables() {
        mFloatingView = null;
        textViewMain = null;
        bitmapViewMainPicker = null;
        bitmapViewMainEditor = null;
        cachedText = null;
        cachedCompressedBitmap = null;
        recyclerView = null;
        currentAdapter = null;
        previousAdapter = null;
        layoutManager = null;
        analyzerRootLayout = null;
        sourceButton = null;
        analyzerPicker = null;
        analyzerEditor = null;
    }

    public ImageAnalysisFloatingView(Variables variables) {
        initVariables();
        this.variables = variables;
    }

    public void runOnUiThread(final Runnable action) {
        variables.runOnUiThread(action);
    }

    final String analyzerRootLayoutKey = "1";
    final String analyzerPickerKey = "2";
    final String textViewMainKey = "3";
    final String bitmapViewMainPickerKey = "4";
    final String recyclerViewKey = "5";
    final String analyzerEditorKey = "6";
    final String bitmapViewMainEditorKey = "7";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onCreate(Context context) {
        initVariables();
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
                        bitmapViewMainPicker.setImageBitmap(memory, BitmapView.ScaleMode.SCALE_WIDTH_HEIGHT);
                    }
                });

                analyzerPicker.findViewById(R.id.analyzerEraseVideoBufferButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentAdapter.clearData();
                        currentAdapter.notifyDataSetChanged();
                    }
                });

                analyzerPicker.findViewById(R.id.analyzerProcessImageButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (cachedCompressedBitmap != null) {
                            analyzerEditor.setVisibility(View.VISIBLE);
                            analyzerPicker.setVisibility(View.GONE);
                            bitmapViewMainEditor.setImageBitmap(cachedCompressedBitmap, BitmapView.ScaleMode.SCALE_WIDTH_HEIGHT);
                        }
                    }
                });

                //adding click listener to close button
                analyzerEditor.findViewById(R.id.analyzerEditorFinishButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        analyzerPicker.setVisibility(View.VISIBLE);
                        analyzerEditor.setVisibility(View.GONE);
                    }
                });

                //adding click listener to close button
                analyzerPicker.findViewById(R.id.analyzerFinishButton).setOnClickListener(new View.OnClickListener() {
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
                if (analyzerPicker != null)
                    state.putInt(analyzerPickerKey, analyzerPicker.getVisibility());
                if (textViewMain != null)
                    state.putInt(textViewMainKey, textViewMain.getVisibility());
                if (bitmapViewMainPicker != null)
                    state.putInt(bitmapViewMainPickerKey, bitmapViewMainPicker.getVisibility());
                if (recyclerView != null)
                    state.putInt(recyclerViewKey, recyclerView.getVisibility());
                if (analyzerEditor != null)
                    state.putInt(analyzerEditorKey, analyzerEditor.getVisibility());
                if (bitmapViewMainEditor != null)
                    state.putInt(bitmapViewMainEditorKey, bitmapViewMainEditor.getVisibility());
            }
        });

        mFloatingView.setOnSetupExternalViews(new FloatingView.Callback<FloatingView>() {
            @Override
            public void run(FloatingView floatingView) {
                // get all required view's
                analyzerRootLayout = floatingView.findViewById(R.id.analyzerRootLayout);

                analyzerPicker = floatingView.findViewById(R.id.analyzerPicker);
                textViewMain = analyzerPicker.findViewById(R.id.analyzerTextView);
                bitmapViewMainPicker = analyzerPicker.findViewById(R.id.analyzerSelectedImage);
                recyclerView = analyzerPicker.findViewById(R.id.analyzerRecyclerView);

                analyzerEditor = floatingView.findViewById(R.id.analyzerEditor);
                bitmapViewMainEditor = analyzerEditor.findViewById(R.id.analyzerEditorSelectedImage);

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
                visibility = state.getInt(analyzerPickerKey, View.VISIBLE);
                analyzerPicker.setVisibility(visibility);
                visibility = state.getInt(textViewMainKey, View.VISIBLE);
                textViewMain.setVisibility(visibility);
                visibility = state.getInt(bitmapViewMainPickerKey, View.VISIBLE);
                bitmapViewMainPicker.setVisibility(visibility);
                visibility = state.getInt(recyclerViewKey, View.VISIBLE);
                recyclerView.setVisibility(visibility);
                visibility = state.getInt(analyzerEditorKey, View.GONE);
                analyzerEditor.setVisibility(visibility);
                visibility = state.getInt(bitmapViewMainEditorKey, View.VISIBLE);
                bitmapViewMainEditor.setVisibility(visibility);

                if (cachedText != null) textViewMain.setText(cachedText);
                if (cachedCompressedBitmap != null) {
                    bitmapViewMainPicker.setImageBitmap(cachedCompressedBitmap);
                    bitmapViewMainEditor.setImageBitmap(cachedCompressedBitmap);
                }
                
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
        analyzerPicker.setVisibility(View.VISIBLE);
        analyzerEditor.setVisibility(View.GONE);
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
        if (currentAdapter != null) {
            currentAdapter.clearData();
            currentAdapter.notifyDataSetChanged();
        }

        if (mFloatingView != null) {
            mFloatingView.detachFromWindowManager();
        }
        initVariables();
    }
}
