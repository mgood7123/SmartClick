package screen.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.nio.ByteBuffer;
import java.util.Vector;

import smallville7123.libparcelablebundle.ParcelableBundle;

/**
 * a special variant of ImageView designed specifically for bitmaps
 *
 * prioritizes low memory consumption
 *
 */
@SuppressLint("AppCompatCustomView")
public class BitmapView extends ImageView {

    private final String TAG = "BitmapView (" + getClass().getName() + "@" + Integer.toHexString(hashCode()) + ")";

    static Vector<BitmapView> bitmapViews = new Vector();

    public BitmapView(Context context, @androidx.annotation.Nullable AttributeSet attrs) {
        super(context, attrs);
        bitmapViews.add(this);
    }

    static class ScaleMode {
        private static final int MODE_SHIFT = 30;
        private static final int MODE_MASK  = 0x3 << MODE_SHIFT;

        /**
         Scaling specification mode:
         <br>
         <br>
         keep original height and width
         <br>
         <br>
         */
        public static final int KEEP_ORIGINAL_WIDTH_AND_HEIGHT = 0;

        /**
         Scaling specification mode:
         <br>
         <br>
         only scale the width if larger than view width
         <br>
         <br>
         a view's width in portrait mode is the same as that view's height when rotated from portrait mode to landscape mode, and vice versa.
         <br>
         <br>
         refer to {@link BitmapView.ScaleMode#SCALE_HEIGHT_ONLY_IF_LARGER}
         <br>
         <br>
         */
        public static final int SCALE_WIDTH_ONLY_IF_LARGER = 1 << MODE_SHIFT;

        /**
         Scaling specification mode:
         <br>
         <br>
         only scale the width if smaller than view width
         <br>
         <br>
         a view's width in portrait mode is the same as that view's height when rotated from portrait mode to landscape mode, and vice versa.
         <br>
         <br>
         refer to {@link BitmapView.ScaleMode#SCALE_HEIGHT_ONLY_IF_SMALLER}
         <br>
         <br>
         */
        public static final int SCALE_WIDTH_ONLY_IF_SMALLER = 2 << MODE_SHIFT;

        /**
         Scaling specification mode:
         <br>
         <br>
         only scale the height if larger than view height
         <br>
         <br>
         before scaling
         <pre>
Row        Layout
 1         |                     |
 2         |--- BITMAP TOP    ---|
 3         |                     |
 4         |---  VIEW TOP     ---|
 5         |                     |
 6         |                     |
 7         |                     |
 8         |                     |
 9         |                     |
10         |                     |
11         |                     |
12         |                     |
13         |---  VIEW BOTTOM  ---|
14         |                     |
15         |--- BITMAP BOTTOM ---|
16         |                     |
         </pre>
         <br>
         <br>
         after scaling
         <pre>
Row        Layout
 1         |                     |
 2         |                     |
 3         |                     |
 4         |---  VIEW TOP     ---|
 5         |--- BITMAP TOP    ---|
 6         |                     |
 7         |                     |
 8         |                     |
 9         |                     |
10         |                     |
11         |                     |
12         |--- BITMAP BOTTOM ---|
13         |---  VIEW BOTTOM  ---|
14         |                     |
15         |                     |
16         |                     |
         </pre>
         */
        public static final int SCALE_HEIGHT_ONLY_IF_LARGER = 3 << MODE_SHIFT;

        /**
         Scaling specification mode:
         <br>
         <br>
         only scale the height if smaller than view height
         <br>
         <br>
         before scaling
         <pre>
Row        Layout
 1         |                     |
 2         |                     |
 3         |                     |
 4         |---  VIEW TOP     ---|
 5         |                     |
 6         |                     |
 7         |--- BITMAP TOP    ---|
 8         |                     |
 9         |                     |
10         |--- BITMAP BOTTOM ---|
11         |                     |
12         |                     |
13         |---  VIEW BOTTOM  ---|
14         |                     |
15         |                     |
16         |                     |
         </pre>
         <br>
         <br>
         after scaling
         <pre>
Row        Layout
 1         |                     |
 2         |                     |
 3         |                     |
 4         |---  VIEW TOP     ---|
 5         |--- BITMAP TOP    ---|
 6         |                     |
 7         |                     |
 8         |                     |
 9         |                     |
10         |                     |
11         |                     |
12         |--- BITMAP BOTTOM ---|
13         |---  VIEW BOTTOM  ---|
14         |                     |
15         |                     |
16         |                     |
         </pre>
         */
        public static final int SCALE_HEIGHT_ONLY_IF_SMALLER = 4 << MODE_SHIFT;

        /**
         Scaling specification mode:
         <br>
         <br>
         scales the width if larger or smaller than the view width
         <br>
         <br>
         a view's width in portrait mode is the same as that view's height when rotated from portrait mode to landscape mode, and vice versa.
         <br>
         <br>
         refer to {@link BitmapView.ScaleMode#SCALE_HEIGHT_ONLY_IF_LARGER SCALE_HEIGHT_ONLY_IF_LARGER}
         and {@link BitmapView.ScaleMode#SCALE_HEIGHT_ONLY_IF_SMALLER SCALE_HEIGHT_ONLY_IF_SMALLER}
         <br>
         <br>
         */
        public static final int SCALE_WIDTH = SCALE_WIDTH_ONLY_IF_LARGER | SCALE_WIDTH_ONLY_IF_SMALLER;

        /**
         Scaling specification mode:
         <br>
         <br>
         scales the height if larger or smaller than the view height
         <br>
         <br>
         refer to {@link BitmapView.ScaleMode#SCALE_HEIGHT_ONLY_IF_LARGER SCALE_HEIGHT_ONLY_IF_LARGER}
         and {@link BitmapView.ScaleMode#SCALE_HEIGHT_ONLY_IF_SMALLER SCALE_HEIGHT_ONLY_IF_SMALLER}
         <br>
         <br>
         */
        public static final int SCALE_HEIGHT = SCALE_HEIGHT_ONLY_IF_LARGER | SCALE_HEIGHT_ONLY_IF_SMALLER;

        /**
         Scaling specification mode:
         <br>
         <br>
         scales the width if larger or smaller than the view width
         <br>
         also scales the height if larger or smaller than the view height
         <br>
         <br>
         a view's width in portrait mode is the same as that view's height when rotated from portrait mode to landscape mode, and vice versa.
         <br>
         <br>
         refer to {@link BitmapView.ScaleMode#SCALE_HEIGHT_ONLY_IF_LARGER SCALE_HEIGHT_ONLY_IF_LARGER}
         and {@link BitmapView.ScaleMode#SCALE_HEIGHT_ONLY_IF_SMALLER SCALE_HEIGHT_ONLY_IF_SMALLER}
         <br>
         <br>
         */
        public static final int SCALE_WIDTH_HEIGHT = SCALE_WIDTH | SCALE_HEIGHT;

        static class FlagData {
            boolean hasFlags;
            boolean hasWidthFlag;
            boolean hasHeightFlag;
            boolean hasScaleHeightIfLargerThanViewHeightFlag;
            boolean hasScaleHeightIfSmallerThanViewHeightFlag;
            boolean hasScaleWidthIfLargerThanViewWidthFlag;
            boolean hasScaleWidthIfSmallerThanViewWidthFlag;
        }

        static final FlagData analyseFlags(int flags) {
            FlagData flagData = new FlagData();
            if (flags == 0) {
                flagData.hasFlags = false;
                return flagData;
            }
            flagData.hasFlags = true;
            if ((flags & SCALE_WIDTH_ONLY_IF_LARGER) == SCALE_WIDTH_ONLY_IF_LARGER) {
                flagData.hasWidthFlag = true;
                flagData.hasScaleWidthIfLargerThanViewWidthFlag = true;
            }
            if ((flags & SCALE_WIDTH_ONLY_IF_SMALLER) == SCALE_WIDTH_ONLY_IF_SMALLER) {
                flagData.hasWidthFlag = true;
                flagData.hasScaleWidthIfSmallerThanViewWidthFlag = true;
            }
            if ((flags & SCALE_HEIGHT_ONLY_IF_LARGER) == SCALE_HEIGHT_ONLY_IF_LARGER) {
                flagData.hasHeightFlag = true;
                flagData.hasScaleHeightIfLargerThanViewHeightFlag = true;
            }
            if ((flags & SCALE_HEIGHT_ONLY_IF_SMALLER) == SCALE_HEIGHT_ONLY_IF_SMALLER) {
                flagData.hasHeightFlag = true;
                flagData.hasScaleHeightIfSmallerThanViewHeightFlag = true;
            }
            return flagData;
        }
    }

    public void setImageBitmap(Bitmap bm, int scaleMode) {
        setImageBitmap(bm, false, scaleMode);
    }

    /**
     * it is safe to recycle the bitmap after calling this method
     */
    @Override
    public void setImageBitmap(Bitmap bm) {
        setImageBitmap(bm, false);
    }

    public void setImageBitmap(Bitmap bm, boolean recycleAfterUse, int scaleMode) {
        setImageBitmap(bm, recycleAfterUse, false, scaleMode);
    }

    public void setImageBitmap(Bitmap bm, boolean recycleAfterUse) {
        setImageBitmap(bm, recycleAfterUse, false);
    }

    public void setImageBitmap(Bitmap bm, boolean recycleAfterUse, boolean setImmediately) {
        setImageBitmap(bm, recycleAfterUse, setImmediately, ScaleMode.KEEP_ORIGINAL_WIDTH_AND_HEIGHT);
    }

    public void setImageBitmap(byte[] bitmapData) {
        setImageBitmap(bitmapData, ScaleMode.KEEP_ORIGINAL_WIDTH_AND_HEIGHT);
    }

    public void setImageBitmap(int width, int height, Bitmap.Config config, ByteBuffer buffer, boolean recycleAfterUse) {
        setImageBitmap(width, height, config, buffer, recycleAfterUse, ScaleMode.KEEP_ORIGINAL_WIDTH_AND_HEIGHT);
    }

    public void setImageBitmap(int width, int height, Bitmap.Config config, ByteBuffer buffer, boolean recycleAfterUse, int scaledMode) {
        if (config != null) {
            Bitmap bitmap = Bitmap.createBitmap(width, height, config);
            bitmap.copyPixelsFromBuffer(buffer);
            setImageBitmap(bitmap, recycleAfterUse, scaledMode);
        }
    }

    // creates an empty bitmap
    public void setImageBitmap(int width, int height, Bitmap.Config config) {
        setImageBitmap(width, height, config, ScaleMode.KEEP_ORIGINAL_WIDTH_AND_HEIGHT);
    }

    // creates an empty bitmap
    public void setImageBitmap(int width, int height, Bitmap.Config config, int scaleMode) {
        if (config != null) {
            setImageBitmap(Bitmap.createBitmap(width, height, config), true, true, scaleMode);
        }
    }


    private int computeScaledWidth(Bitmap bm, Canvas canvas, ScaleMode.FlagData flagData) {
        int computedWidth = bm.getWidth();
        if (flagData.hasWidthFlag) {
            int viewWidth = canvas.getWidth();
            return
                    flagData.hasScaleWidthIfLargerThanViewWidthFlag ?
                            (
                                    (computedWidth > viewWidth) ?
                                        (
                                            viewWidth
                                        ) :
                                    (
                                        flagData.hasScaleWidthIfSmallerThanViewWidthFlag ?
                                            (
                                                (computedWidth < viewWidth) ? viewWidth : computedWidth
                                            ) :
                                        computedWidth
                                    )
                                ) :
                            computedWidth;
        }
        return computedWidth;
    }

    private int computeScaledHeight(Bitmap bm, Canvas canvas, ScaleMode.FlagData flagData) {
        int computedHeight = bm.getHeight();
        if (flagData.hasHeightFlag) {
            int viewHeight = canvas.getHeight();
            return
                    flagData.hasScaleHeightIfLargerThanViewHeightFlag ?
                            (
                                    (computedHeight > viewHeight) ?
                                        (
                                            viewHeight
                                        ) :
                                    (
                                        flagData.hasScaleHeightIfSmallerThanViewHeightFlag ?
                                            (
                                                (computedHeight < viewHeight) ? viewHeight : computedHeight
                                            ) :
                                        computedHeight
                                    )
                                ) :
                            computedHeight;
        }
        return computedHeight;
    }

    public class Pair {
        public final Bitmap first;
        public final boolean second;
        /**
         * Constructor for a Pair.
         *
         * @param first the first object in the Pair
         * @param second the second object in the pair
         */
        public Pair(Bitmap first, boolean second) {
            this.first = first;
            this.second = second;
        }
    }

    private Pair scale(Bitmap bm, Canvas canvas, boolean recycleAfterUse, boolean shouldScale, ScaleMode.FlagData flagData) {
        if (bm == null) return null;
        if (shouldScale && flagData.hasFlags) {
            int width = computeScaledWidth(bm, canvas, flagData);
            int height = computeScaledHeight(bm, canvas, flagData);
            Bitmap scaled = Bitmap.createScaledBitmap(bm, width, height, false);
            if (scaled != null) {
                // recycle if allowed
                if (recycleAfterUse) bm.recycle();
                return new Pair(scaled, true);
            }
        }
        return new Pair(bm, false);
    }

    public void setImageBitmap(byte[] compressedBitmap, int scaleMode) {
        if (compressedBitmap == null) {
            state.cache = null;
        } else {
            state.cache = compressedBitmap.clone();
            state.cacheDecompressed = BitmapUtils.decompress(state.cache);
            setImageBitmap(state.cacheDecompressed, false, false, scaleMode);
        }
    }

    static class RecordingState {
        static final int started = 1;
        static final int recording = 2;
        static final int paused = 3;
        static final int stopped = 4;
    }

    void beginRecording(boolean compressFrames) {
        synchronized (state.recordingStateLock) {
            if (state.recordingState == RecordingState.stopped) {
                if (compressFrames) {
                    // TODO: a LruCache could be used for higher performance, see
                    //  https://developer.android.com/topic/performance/graphics/manage-memory
                    if (state.recordedFrames == null) state.recordedFrames = new RecordedFrames<byte[]>();
                    else {
                        state.recordedFrames.clear();
                    }
                } else {
                    if (state.recordedFrames == null) {
                        state.recordedFrames = new RecordedFrames<Bitmap>();
                        state.recordedFrames.setCompressRecordedFrames(Boolean.FALSE);
                    } else {
                        state.recordedFrames.clear();
                    }
                }
                state.recordingState = RecordingState.started;
            }
        }
    }

    void pauseRecording() {
        synchronized (state.recordingStateLock) {
            state.recordingState = RecordingState.paused;
        }
    }

    void resumeRecording() {
        synchronized (state.recordingStateLock) {
            state.recordingState = RecordingState.recording;
        }
    }

    void endRecording() {
        synchronized (state.recordingStateLock) {
            state.recordingState = RecordingState.stopped;
        }
    }

    RecordedFrames getRecordedData() {
        return state.recordedFrames;
    }

    @Override
    public boolean willNotDraw() {
        return state.cacheDecompressed != null || state.bm == null;
    }

    @Override
    protected void onDetachedFromWindow() {
        if (state.cache != null) state.cache = null;
        if (state.cacheDecompressed != null) {
            state.cacheDecompressed.recycle();
            state.cacheDecompressed = null;
            state.bm = null;
            Log.i(TAG, "onDetachedFromWindow: recycled");
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected void onWindowVisibilityChanged(final int visibility) {
        // this is called when the view is completely removed from the heirarchy
        // this IS NOT called when the view's visibility changed to, or from, GONE
        Log.i(TAG, "onWindowVisibilityChanged: changed to " + visibility);
        if (visibility == GONE) {
            if (state.cache != null) state.cache = null;
            if (state.cacheDecompressed != null) {
                state.cacheDecompressed.recycle();
                state.cacheDecompressed = null;
                state.bm = null;
                Log.i(TAG, "onWindowVisibilityChanged: recycled");
            }
        }
        super.onWindowVisibilityChanged(visibility);
    }

    @Override
    protected void onVisibilityChanged(@NonNull final View changedView, final int visibility) {
        Log.i(TAG, "onVisibilityChanged: changed to " + visibility);
        super.onVisibilityChanged(changedView, visibility);
    }

    public void setImageBitmap(Bitmap bm, boolean recycleAfterUse, boolean setImmediately, int scaleMode) {
        state.cache = null;
        if (bm == null) {
            if (state.cacheDecompressed != null) {
                state.cacheDecompressed.recycle();
                state.cacheDecompressed = null;
                state.bm = null;
                Log.i(TAG, "setImageBitmap: recycled");
            }
        } else {
            if (getWindowVisibility() != GONE) {
                state.bm = bm;
                state.recycleAfterUse = recycleAfterUse;
                state.setImmediately = setImmediately;
                state.scaleMode = scaleMode;
                invalidate();
            }
        }
    }

    void internalRecord() {
        if (state.recordedFrames.isCompressed()) {
            if (state.recordedFrames.frames.size() == state.maxRecordingFrames) {
                state.recordedFrames.remove(0);
            }
            state.recordedFrames.add(BitmapUtils.compress(state.bm, state.compressionFormat, state.compressionQuality));
        }
    }

    static final IllegalStateException drawRecycled = new IllegalStateException("onDraw: cannot draw a recycled bitmap");

    @Override
    protected void onDraw(Canvas canvas) {
        if (state.bm == null) {
            // TODO: cache bitmap so recorder still has something to record
            //  otherwise frame skips will occur
            Log.i(TAG, "onDraw: cannot draw null bitmap");
        } else if (state.bm.isRecycled()) {
            // TODO: cache bitmap so recorder still has something to record
            //  otherwise frame skips will occur
            throw drawRecycled;
        } else {
            boolean shouldRecord = false;
            synchronized (state.recordingStateLock) {
                switch (state.recordingState) {
                    case RecordingState.started:
                        state.recordedFrames.setWidth(state.bm.getWidth());
                        state.recordedFrames.setHeight(state.bm.getHeight());
                        state.recordingState = RecordingState.recording;
                        shouldRecord = true;
                        break;
                    case RecordingState.recording:
                        shouldRecord = true;
                        // other states are ignored
                        break;
                }
            }
            if (shouldRecord) internalRecord();
            Pair scaled = null;
            if (state.isAllowedToScale) {
                final ScaleMode.FlagData flagData = ScaleMode.analyseFlags(state.scaleMode);
                scaled = scale(state.bm, canvas, state.recycleAfterUse, true, flagData);
                if (scaled.second) state.bm = scaled.first;
                state.cacheDecompressed = scaled.first;
            } else {
                state.cacheDecompressed = state.bm;
            }
            state.src.right = state.cacheDecompressed.getWidth();
            state.src.bottom = state.cacheDecompressed.getHeight();
            state.dst.right = canvas.getWidth();
            state.dst.bottom = canvas.getHeight();
            canvas.drawBitmap(state.cacheDecompressed, state.src, state.dst, null);
        }
    }

    BitmapViewState state = new BitmapViewState();

    BitmapViewState getState() {
        return state;
    }

    public void saveState(ParcelableBundle bundle, String key) {
        bundle.putParcelable(key, state);
        Log.i(TAG, "saveState: stored state");
    }

    public void restoreState(ParcelableBundle bundle, String key) {
        BitmapViewState tmp = bundle.getParcelable(key);
        if (tmp != null) {
            state = tmp;
            Log.i(TAG, "restoreState: restored state");
        } else Log.i(TAG, "restoreState: state is null");
    }

    public static void saveState(BitmapView bitmapView, ParcelableBundle bundle, String key) {
        if (bitmapView != null) bitmapView.saveState(bundle, key);
    }

    public static void restoreState(BitmapView bitmapView, ParcelableBundle bundle, String key) {
        if (bitmapView != null) bitmapView.restoreState(bundle, key);
    }
}