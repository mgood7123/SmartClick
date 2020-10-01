package screen.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.InspectableProperty;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.nio.ByteBuffer;
import java.util.Vector;

import smallville7123.libparcelablebundle.ParcelableBundle;
import smallville7123.libparcelablebundle.annotations.UnsupportedAppUsage;

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

    int targetSdkVersion;

    public BitmapView(Context context) {
        super(context);
        Log.i(TAG, "BitmapView: constructor");
        targetSdkVersion = context.getApplicationInfo().targetSdkVersion;
        bitmapViews.add(this);
    }

    public BitmapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.i(TAG, "BitmapView: constructor");
        targetSdkVersion = context.getApplicationInfo().targetSdkVersion;
        bitmapViews.add(this);
    }

    public BitmapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Log.i(TAG, "BitmapView: constructor");
        targetSdkVersion = context.getApplicationInfo().targetSdkVersion;
        bitmapViews.add(this);
    }

    public BitmapView(Context context, @androidx.annotation.Nullable AttributeSet attrs) {
        super(context, attrs);
        Log.i(TAG, "BitmapView: constructor");
        targetSdkVersion = context.getApplicationInfo().targetSdkVersion;
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


    private int computeScaledWidth(Bitmap bm, ScaleMode.FlagData flagData) {
        int computedWidth = bm.getWidth();
        if (flagData.hasWidthFlag) {
            int viewWidth = getMeasuredWidth();
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

    private int computeScaledHeight(Bitmap bm, ScaleMode.FlagData flagData) {
        int computedHeight = bm.getHeight();
        if (flagData.hasHeightFlag) {
            int viewHeight = getMeasuredHeight();
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

    private Pair scale(Bitmap bm, boolean recycleAfterUse, boolean shouldScale, ScaleMode.FlagData flagData) {
        if (bm == null) return null;
        if (shouldScale && flagData.hasFlags) {
            int width = computeScaledWidth(bm, flagData);
            int height = computeScaledHeight(bm, flagData);
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
    protected void onAttachedToWindow() {
        Log.i(TAG, "onAttachedToWindow: called");
        super.onAttachedToWindow();
    }

    @Override
    public void onStartTemporaryDetach() {
        Log.i(TAG, "onStartTemporaryDetach: called");
        super.onStartTemporaryDetach();
    }

    @Override
    public void onFinishTemporaryDetach() {
        Log.i(TAG, "onFinishTemporaryDetach: called");
        super.onFinishTemporaryDetach();
    }

    /**
     * visibility changes include deteching a view from a window, and window visibility changes.
     * <br>
     * <br>
     * normally, an assigned bitmap is recycled when:
     * <br>
     * 1. onDetachedFromWindow is called
     * <br>
     * 2. onWindowVisibilityChanged is called with a visibility of {@link View#GONE}
     * <br>
     * <br>
     * if this is set to false, the bitmap will not be recycled under the above conditions
     * <br>
     * and can be manually recycled by calling the {@link BitmapView#recycle()} method
     */
    public boolean automaticRecycleOnVisibilityChange = true;

    /**
     * does nothing if already recycled
     */
    public void recycle() {
        if (state.cache != null) state.cache = null;
        if (state.cacheDecompressed != null) {
            state.cacheDecompressed.recycle();
            state.cacheDecompressed = null;
            state.bm.recycle();
            state.bm = null;
            Log.i(TAG, "recycled");
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        Log.i(TAG, "onDetachedFromWindow: called");
        if (automaticRecycleOnVisibilityChange) recycle();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onWindowVisibilityChanged(final int visibility) {
        // this is called when the view is completely removed from the heirarchy
        // this IS NOT called when the view's visibility changed to, or from, GONE
        Log.i(TAG, "onWindowVisibilityChanged: changed to " + visibility);
        if (visibility == GONE && automaticRecycleOnVisibilityChange) recycle();
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
            recycle();
        } else {
            Log.i(TAG, "setImageBitmap: setting");
            Log.i(TAG, "setImageBitmap: state is " + state);
            state.bm = bm;
            Log.i(TAG, "setImageBitmap: state.bm is " + state.bm);
            state.recycleAfterUse = recycleAfterUse;
            state.setImmediately = setImmediately;
            state.scaleMode = scaleMode;
            if (getWindowVisibility() != GONE) {
                Log.i(TAG, "setImageBitmap: drawing because view is not gone");
                Log.i(TAG, "setImageBitmap: measuring");
                measure(MeasureSpec.AT_MOST, MeasureSpec.AT_MOST);
                Log.i(TAG, "setImageBitmap: measured");
                invalidate();
            } else {
                Log.i(TAG, "setImageBitmap: not drawing because view is gone");
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
        Log.i(TAG, "onDraw: called");
        Log.i(TAG, "onDraw: state is " + state);
        Log.i(TAG, "onDraw: state.bm is " + state.bm);
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
                scaled = scale(state.bm, state.recycleAfterUse, true, flagData);
                if (scaled.second) state.bm = scaled.first;
                state.cacheDecompressed = scaled.first;
            } else {
                state.cacheDecompressed = state.bm;
            }
            state.src.right = state.cacheDecompressed.getWidth();
            state.src.bottom = state.cacheDecompressed.getHeight();
            state.dst.right = getMeasuredWidth();
            state.dst.bottom = getMeasuredHeight();
            canvas.drawBitmap(state.cacheDecompressed, state.src, state.dst, null);
        }
    }

    int mDrawableWidth;
    int mDrawableHeight;

    @UnsupportedAppUsage
    private boolean mAdjustViewBounds = true;

    /**
     * True when ImageView is adjusting its bounds
     * to preserve the aspect ratio of its drawable
     *
     * @return whether to adjust the bounds of this view
     * to preserve the original aspect ratio of the drawable
     *
     * @see #setAdjustViewBounds(boolean)
     *
     * @attr ref android.R.styleable#ImageView_adjustViewBounds
     */
    @InspectableProperty
    public boolean getAdjustViewBounds() {
        return mAdjustViewBounds;
    }

    /**
     * Set this to true if you want the ImageView to adjust its bounds
     * to preserve the aspect ratio of its drawable.
     *
     * <p><strong>Note:</strong> If the application targets API level 17 or lower,
     * adjustViewBounds will allow the drawable to shrink the view bounds, but not grow
     * to fill available measured space in all cases. This is for compatibility with
     * legacy {@link android.view.View.MeasureSpec MeasureSpec} and
     * {@link android.widget.RelativeLayout RelativeLayout} behavior.</p>
     *
     * @param adjustViewBounds Whether to adjust the bounds of this view
     * to preserve the original aspect ratio of the drawable.
     *
     * @see #getAdjustViewBounds()
     *
     * @attr ref android.R.styleable#ImageView_adjustViewBounds
     */
    public void setAdjustViewBounds(boolean adjustViewBounds) {
        mAdjustViewBounds = adjustViewBounds;
        if (adjustViewBounds) {
            setScaleType(ScaleType.FIT_CENTER);
        }
    }

    int mPaddingLeft;
    int mPaddingRight;
    int mPaddingTop;
    int mPaddingBottom;

    @UnsupportedAppUsage(maxTargetSdk = Build.VERSION_CODES.P)
    private int mMaxWidth = Integer.MAX_VALUE;
    @UnsupportedAppUsage(maxTargetSdk = Build.VERSION_CODES.P)
    private int mMaxHeight = Integer.MAX_VALUE;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i(TAG, "onMeasure: called");
        int widthSize = 0;
        int heightSize = 0;
        mPaddingLeft = getPaddingLeft();
        mPaddingRight = getPaddingRight();
        mPaddingTop = getPaddingTop();
        mPaddingBottom = getPaddingBottom();

        if (state.bm == null) {
            Log.i(TAG, "onMeasure: bitmap is null");
            widthSize = resolveSizeAndState(0, widthMeasureSpec, 0);
            heightSize = resolveSizeAndState(0, heightMeasureSpec, 0);
            mMaxWidth = widthSize;
            mMaxHeight = heightSize;
            Log.i(TAG, "onMeasure: setting to " + widthSize + "x" + heightSize);
            setMeasuredDimension(widthSize, heightSize);
        } else {
            Log.i(TAG, "onMeasure: bitmap is not null");
            int w = state.bm.getWidth();
            int h = state.bm.getHeight();
            if (w <= 0) w = 1;
            if (h <= 0) h = 1;
            Log.i(TAG, "onMeasure: bitmap  size: " + w + "x" + h);
            Log.i(TAG, "onMeasure: maximum size: " + mMaxWidth + "x" + mMaxHeight);

            final int pleft = mPaddingLeft;
            final int pright = mPaddingRight;
            final int ptop = mPaddingTop;
            final int pbottom = mPaddingBottom;

            // Get the max possible width given our constraints
            widthSize = resolveAdjustedSize(w + pleft + pright, mMaxWidth, widthMeasureSpec);
            Log.i(TAG, "onMeasure: widthSize: " + widthSize);

            // Get the max possible height given our constraints
            heightSize = resolveAdjustedSize(h + ptop + pbottom, mMaxHeight, heightMeasureSpec);
            Log.i(TAG, "onMeasure: heightSize: " + heightSize);

            // Desired aspect ratio of the view's contents (not including padding)
            float desiredAspect = 0.0f;

            // We are allowed to change the view's width
            boolean resizeWidth = true;

            // We are allowed to change the view's height
            boolean resizeHeight = true;;

            // We are supposed to adjust view bounds to match the aspect
            // ratio of our drawable. See if that is possible.
            if (mAdjustViewBounds) desiredAspect = (float) w / (float) h;

            if (desiredAspect != 0.0f) {
                // See what our actual aspect ratio is
                final float actualAspect = (float) (widthSize - pleft - pright) /
                        (heightSize - ptop - pbottom);
                Log.i(TAG, "onMeasure: actualAspect: " + actualAspect);

                if (Math.abs(actualAspect - desiredAspect) > 0.0000001) {

                    boolean done = false;

                    // Try adjusting width to be proportional to height
                    if (resizeWidth) {
                        int newWidth = (int) (desiredAspect * (heightSize - ptop - pbottom)) +
                                pleft + pright;
                        Log.i(TAG, "onMeasure: newWidth: " + newWidth);

                        // Allow the width to outgrow its original estimate if height is fixed.
                        if (!resizeHeight && !(targetSdkVersion <= Build.VERSION_CODES.JELLY_BEAN_MR1)) {
                            widthSize = resolveAdjustedSize(newWidth, mMaxWidth, widthMeasureSpec);
                            Log.i(TAG, "onMeasure: widthSize: " + widthSize);
                        }

                        if (newWidth <= widthSize) {
                            widthSize = newWidth;
                            Log.i(TAG, "onMeasure: widthSize: " + widthSize);
                            done = true;
                        } else {
                            Log.i(
                                    TAG,
                                    "onMeasure: newWidth ("
                                            + newWidth
                                            + ") cannot be greater then widthSize ("
                                            + widthSize
                                            + ")"
                            );
                        }
                    }

                    // Try adjusting height to be proportional to width
                    if (!done && resizeHeight) {
                        int newHeight = (int) ((widthSize - pleft - pright) / desiredAspect) +
                                ptop + pbottom;
                        Log.i(TAG, "onMeasure: newHeight: " + newHeight);

                        // Allow the height to outgrow its original estimate if width is fixed.
                        if (!resizeWidth && !(targetSdkVersion <= Build.VERSION_CODES.JELLY_BEAN_MR1)) {
                            heightSize = resolveAdjustedSize(newHeight, mMaxHeight,
                                    heightMeasureSpec);
                            Log.i(TAG, "onMeasure: heightSize: " + heightSize);
                        }

                        if (newHeight <= heightSize) {
                            heightSize = newHeight;
                            Log.i(TAG, "onMeasure: heightSize: " + heightSize);
                        } else {
                            Log.i(
                                    TAG,
                                    "onMeasure: newHeight ("
                                            + newHeight
                                            + ") cannot be greater then heightSize ("
                                            + heightSize
                                            + ")"
                            );
                        }
                    }
                }
            }

            Log.i(TAG, "onMeasure: setting to " + widthSize + "x" + heightSize);
            setMeasuredDimension(widthSize, heightSize);
        }
    }

    private int resolveAdjustedSize(int desiredSize, int maxSize,
                                    int measureSpec) {
        int result = desiredSize;
        final int specMode = MeasureSpec.getMode(measureSpec);
        final int specSize =  MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                /* Parent says we can be as big as we want. Just don't be larger
                   than max size imposed on ourselves.
                */
                result = Math.min(desiredSize, maxSize);
                break;
            case MeasureSpec.AT_MOST:
                // Parent says we can be as big as we want, up to specSize.
                // Don't be larger than specSize, and don't be larger than
                // the max size imposed on ourselves.
                if (specSize == 0) result = Math.min(desiredSize, maxSize);
                else result = Math.min(Math.min(desiredSize, specSize), maxSize);
                break;
            case MeasureSpec.EXACTLY:
                // No choice. Do what we are told.
                result = specSize;
                break;
        }
        return result;
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