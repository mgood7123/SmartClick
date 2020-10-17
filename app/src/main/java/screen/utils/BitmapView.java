package screen.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
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
import smallville7123.taggable.Taggable;

/**
 * a special variant of ImageView designed specifically for bitmaps
 *
 * prioritizes low memory consumption
 *
 */
@SuppressLint("AppCompatCustomView")
public class BitmapView extends ImageView {

    static final Bitmap nullBitmap = null;
    static final byte[] nullByteArray = null;
    private final String TAG = "BitmapView (" + Taggable.getTag(this) + ")";
    static Vector<BitmapView> bitmapViews = new Vector();
    int targetSdkVersion;
    private boolean drawNothing = false;

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
        return computeScaledWidth(bm.getWidth(), flagData);
    }

    private int computeScaledWidth(int width, ScaleMode.FlagData flagData) {
        int computedWidth = width;
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
        return computeScaledHeight(bm.getHeight(), flagData);
    }

    private int computeScaledHeight(int height, ScaleMode.FlagData flagData) {
        int computedHeight = height;
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
        Log.i(TAG, "willNotDraw: called");
        boolean hasBitmap;
        if (state.cacheDecompressed != null) {
            hasBitmap = true;
        } else if (state.bm != null) {
            hasBitmap = true;
        } else if (state.scaledbm != null) {
            hasBitmap = true;
        } else {
            hasBitmap = false;
        }
        return state.preScaled || hasBitmap;
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
     * visibility changes include detaching a view from a window, and window visibility changes.
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
        if (state.cache != null) {
            // only clear cache if it is not the current image source
            if (!state.preScaled) {
                state.cache = null;
                Log.i(TAG, "cleared compressed cache");
            }
        }
        if (state.cacheDecompressed != null) {
            state.cacheDecompressed.recycle();
            state.cacheDecompressed = null;
            Log.i(TAG, "recycled cacheDecompressed");
        }
        if (state.scaledbm != null) {
            state.scaledbm.recycle();
            state.scaledbm = null;
            Log.i(TAG, "recycled scaledbm");
        }
        if (state.bm != null) {
            state.bm.recycle();
            state.bm = null;
            Log.i(TAG, "recycled bm");
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
        setImageBitmap(bm, recycleAfterUse, setImmediately, true, scaleMode);
    }

    @Nullable private Bitmap scale(Bitmap bm, boolean recycleAfterUse, boolean shouldScale, ScaleMode.FlagData flagData) {
        if (bm == null) return null;
        if (shouldScale && flagData.hasFlags) {
            int width = computeScaledWidth(bm, flagData);
            int height = computeScaledHeight(bm, flagData);
            Log.i(TAG, "scale: scaling bitmap from " + bm.getWidth() + "x" + bm.getHeight() + " to " + width + "x" + height);
            Bitmap scaled = Bitmap.createScaledBitmap(bm, width, height, false);
            if (scaled != null) {
                // recycle if allowed
                if (recycleAfterUse) bm.recycle();
                return scaled;
            } else return null;
        }
        return bm;
    }

    @Override
    public void invalidate() {
        Log.i(TAG, "setImageBitmap: invalidated");
        super.invalidate();
    }

    void internalRecord() {
        if (state.recordedFrames.isCompressed()) {
            if (state.recordedFrames.frames.size() == state.maxRecordingFrames) {
                state.recordedFrames.remove(0);
            }
            if (state.preScaled) {
                state.recordedFrames.add(state.cache);
            } else {
                state.recordedFrames.add(BitmapUtils.compress(state.bm, state.compressionFormat, state.compressionQuality));
            }
        }
    }

    @Nullable private Bitmap scale(byte[] bm, boolean shouldScale, BitmapView.ScaleMode.FlagData flagData) {
        if (bm == null) return null;
        if (shouldScale && flagData.hasFlags) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(bm, 0, bm.length, options);
            int width = computeScaledWidth(options.outWidth, flagData);
            int height = computeScaledHeight(options.outHeight, flagData);
            Log.i(TAG, "scale: scaling bitmap from " + options.outWidth + "x" + options.outHeight + " to " + width + "x" + height);
            return BitmapUtils.decompressAndScale(bm, width, height);
        } else {
            return BitmapUtils.decompress(bm);
        }
    }

    public void setImageBitmap(byte[] compressedBitmap, int scaleMode) {
        state.preScaled = false;
        if (compressedBitmap == null) {
            Log.w(TAG, "setImageBitmap: compressedBitmap is null, did you mean to invoke clearImage()?");
            recycle();
            drawNothing = true;
            invalidate();
        } else {
            drawNothing = false;
            // TODO: full recycle here?
            state.cache = compressedBitmap.clone();
            if (state.cacheDecompressed != null) {
                state.cacheDecompressed.recycle();
                state.cacheDecompressed = null;
            }

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(compressedBitmap, 0, compressedBitmap.length, options);

            state.bmw = options.outWidth;
            state.bmh = options.outHeight;
            state.scaleMode = scaleMode;
            state.preScaled = true;
            if (getWindowVisibility() != GONE) {
                Log.i(TAG, "setImageBitmap: drawing because view is not gone");
                Log.i(TAG, "setImageBitmap: measuring");
                measure(MeasureSpec.AT_MOST, MeasureSpec.AT_MOST);
                Log.i(TAG, "setImageBitmap: measured");
                postInvalidate();
            } else {
                Log.i(TAG, "setImageBitmap: not drawing because view is gone");
            }
        }
    }

    public void setImageBitmap(Bitmap bm, boolean recycleAfterUse, boolean setImmediately, boolean clearCache, int scaleMode) {
        state.preScaled = false;
        if (clearCache) {
            state.cache = null;
            Log.i(TAG, "setImageBitmap: cleared compressed cache");
        }
        if (bm == null) {
            Log.w(TAG, "setImageBitmap: bm is null, did you mean to invoke clearImage()?");
            recycle();
            drawNothing = true;
            invalidate();
        } else {
            drawNothing = false;
            Log.i(TAG, "setImageBitmap: setting");
            Log.i(TAG, "setImageBitmap: state is " + state);
            if (state.bm != null) state.bm.recycle();
            // Bitmap#copy introduces a lot of lag
            state.bm = bm;
            state.bmw = bm.getWidth();
            state.bmh = bm.getHeight();
            Log.i(TAG, "setImageBitmap: state.bm is " + state.bm);
            state.recycleAfterUse = recycleAfterUse;
            state.setImmediately = setImmediately;
            state.scaleMode = scaleMode;
            state.preScaled = false;
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

    public void clearImage() {
        clearImage(false);
    }

    public void clearImage(boolean clearCache) {
        if (clearCache) {
            state.cache = null;
            Log.i(TAG, "clearImage: cleared compressed cache");
        }
        recycle();
        drawNothing = true;
        invalidate();
    }

    static final IllegalStateException drawRecycled = new IllegalStateException("onDraw: cannot draw a recycled bitmap");

    @Override
    protected void onDraw(Canvas canvas) {
        Log.i(TAG, "onDraw: called");
        if (drawNothing) {
            Log.i(TAG, "onDraw: drawing nothing");
            state.dst.right = getMeasuredWidth();
            state.dst.bottom = getMeasuredHeight();
            canvas.drawRect(state.dst, new Paint());
            drawNothing = false;
            return;
        }
        Log.i(TAG, "onDraw: state is " + state);
        Log.i(TAG, "onDraw: state.cache is " + state.cache);
        Log.i(TAG, "onDraw: state.bm is " + state.bm);
        Log.i(TAG, "onDraw: state.cacheDecompressed is " + state.cacheDecompressed);
        Log.i(TAG, "onDraw: state.scaledbm is " + state.scaledbm);
        if (state.preScaled) {
            if (state.cache == null) {
                Log.i(TAG, "onDraw: cannot draw a pre-scaled bitmap without a cache");
            } else {
                state.recycleAfterUse = true;
                // the original bitmap w/h is stored in state.bmw and state.bmh
                Bitmap scaled = null;
                if (state.isAllowedToScale) {
                    final ScaleMode.FlagData flagData = ScaleMode.analyseFlags(state.scaleMode);
                    // the original bitmap must be kept in order to correctly handle orientation changes
                    // as the bitmap will need to be re-scaled
                    // this decodes the cache and scales it using getMeasured*()
                    scaled = scale(state.cache, true, flagData);
                    if (scaled != null) {
                        if (state.scaledbm != null) state.scaledbm.recycle();
                        state.scaledbm = scaled;
                    } else throw new RuntimeException("failed to scale bitmap");
                }

                if (BitmapVector.sameAs(state.bm, state.scaledbm)) {
                    state.bm.recycle();
                    Log.i(TAG, "onDraw: recycled bm");
                }
                Log.i(TAG, "onDraw: setting");
                Log.i(TAG, "onDraw: state is " + state);
                if (state.bm != null) state.bm.recycle();
                state.bm = state.scaledbm;
                Log.i(TAG, "onDraw: state.bm is " + state.bm);
                state.recycleAfterUse = true;
                state.setImmediately = false;
                state.cacheDecompressed = state.bm;
                int w = state.cacheDecompressed.getWidth();
                int h = state.cacheDecompressed.getHeight();
                state.src.right = w;
                state.src.bottom = h;
                Log.i(TAG, "onDraw: drawing a bitmap with size " + w + "x" + h);
                state.dst.right = getMeasuredWidth();
                state.dst.bottom = getMeasuredHeight();
                canvas.drawBitmap(state.cacheDecompressed, state.src, ratio.toRect(), null);
                if (state.recycleAfterUse) {
                    // only recycle if we can restore from compressed cache
                    if (state.cache != null) {
                        if (BitmapVector.sameAs(state.cacheDecompressed, state.bm)) {
                            state.bm.recycle();
                            state.bm = null;
                            Log.i(TAG, "onDraw: recycled bm");
                        } else if (BitmapVector.sameAs(state.cacheDecompressed, state.scaledbm)) {
                            state.scaledbm.recycle();
                            state.scaledbm = null;
                            Log.i(TAG, "onDraw: recycled scaledbm");
                        } else {
                            state.cacheDecompressed.recycle();
                            Log.i(TAG, "onDraw: recycled cacheDecompressed");
                        }
                        state.cacheDecompressed = null;
                    } else {
                        Log.i(TAG, "onDraw: not recycling due to cache unavailable");
                    }
                }
            }
        } else if (state.bm == null) {
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
            if (state.scaledbm != null) {
                state.scaledbm.recycle();
                Log.i(TAG, "onDraw: recycled scaledbm");
                state.scaledbm = null;
            }
            Bitmap scaled = null;
            if (state.isAllowedToScale) {
                final ScaleMode.FlagData flagData = ScaleMode.analyseFlags(state.scaleMode);
                // the original bitmap must be kept in order to correctly handle orientation changes
                // as the bitmap will need to be re-scaled
                scaled = scale(state.bm, false, true, flagData);
                if (scaled != null) {
                    if (state.scaledbm != null) state.scaledbm.recycle();
                    state.scaledbm = scaled;
                }
            }
            if (scaled == null) {
                if (!BitmapVector.sameAs(state.cacheDecompressed, state.bm)) {
                    state.cacheDecompressed.recycle();
                    Log.i(TAG, "onDraw: recycled cacheDecompressed");
                }
                state.cacheDecompressed = state.bm;
            } else {
                if (BitmapVector.sameAs(state.cacheDecompressed, state.scaledbm)) {
                    state.cacheDecompressed.recycle();
                    Log.i(TAG, "onDraw: recycled cacheDecompressed");
                }
                state.cacheDecompressed = state.scaledbm;
            }
            int w = state.cacheDecompressed.getWidth();
            int h = state.cacheDecompressed.getHeight();
            state.src.right = w;
            state.src.bottom = h;
            Log.i(TAG, "onDraw: drawing a bitmap with size " + w + "x" + h);
            state.dst.right = getMeasuredWidth();
            state.dst.bottom = getMeasuredHeight();
            canvas.drawBitmap(state.cacheDecompressed, state.src, ratio.toRect(), null);
            if (state.recycleAfterUse) {
                // only recycle if we can restore from compressed cache
                if (state.cache != null) {
                    if (BitmapVector.sameAs(state.cacheDecompressed, state.bm)) {
                        state.bm.recycle();
                        state.bm = null;
                        Log.i(TAG, "onDraw: recycled bm");
                    } else if (BitmapVector.sameAs(state.cacheDecompressed, state.scaledbm)) {
                        state.scaledbm.recycle();
                        state.scaledbm = null;
                        Log.i(TAG, "onDraw: recycled scaledbm");
                    } else {
                        state.cacheDecompressed.recycle();
                        Log.i(TAG, "onDraw: recycled cacheDecompressed");
                    }
                    state.cacheDecompressed = null;
                } else {
                    Log.i(TAG, "onDraw: not recycling due to cache unavailable");
                }
            }
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

    static public class AspectRatio {
        private final String TAG = Taggable.getTag(this);
        int x;
        int y;
        int w;
        int h;

        AspectRatio() {
            x = 0;
            y = 0;
            w = 0;
            h = 0;
        }

        AspectRatio(int w, int h) {
            x = 0;
            y = 0;
            this.w = w;
            this.h = h;
        }

        static public float ratio(int w, int h) {
            return (float) w / (float) h;
        }

        public float ratio() {
            return (float) w / (float) h;
        }

        public Rect toRect() {
            return new Rect(x, y, w, h);
        }

        /* brute force the width, preserving aspect ratio */
        public void bruteForceKnownWidth(AspectRatio from, AspectRatio to) {
            Log.i(TAG, "brute-forcing with known width");
            // width is known
            w = to.w;
            // height is unknown
            h = from.h;
            float X = from.ratio();
            float value;
            while (true) {
                h--;
                value = ratio();
                if (h == 0 || value > X) break;
            }
            int yB = y;
            int hB = h;
            float valueB = ratio();
            h++;
            int yA = y;
            int hA = h;
            float valueA = ratio();
            float rA = Math.abs(valueA-X);
            float rB = Math.abs(X-valueB);
            if (rA < rB) h = hA;
            else h = hB;
        }

        /* brute force the width, preserving aspect ratio */
        void bruteForceKnownHeight(AspectRatio from, AspectRatio to) {
            Log.i(TAG, "brute-forcing with known height");
            // width is unknown
            w = from.w;
            // height is known
            h = to.h;
            float X = from.ratio();
            float value;
            while (true) {
                w--;
                value = ratio();
                if (w == 0 || value < X) break;
            }
            int xB = x;
            int wB = w;
            float valueB = ratio();
            w++;
            int xA = x;
            int wA = w;
            float valueA = ratio();
            float rA = Math.abs(valueA-X);
            float rB = Math.abs(X-valueB);
            if (rA < rB) w = wA;
            else w = wB;
        }

        @Override
        public String toString() {
            return "[(" + x + "," + y + ") to (" + w + "," + h + ")]";
        }
    }

    // should this become part of the state?
    AspectRatio ratio = new AspectRatio();

    static int orientation = Configuration.ORIENTATION_UNDEFINED;

    @Override
    protected void onConfigurationChanged(final Configuration newConfig) {
        Log.i(TAG, "onConfigurationChanged: called");
        orientation = newConfig.orientation;
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i(TAG, "onMeasure: called");
        int widthSize = 0;
        int heightSize = 0;
        mPaddingLeft = getPaddingLeft();
        mPaddingRight = getPaddingRight();
        mPaddingTop = getPaddingTop();
        mPaddingBottom = getPaddingBottom();
        if (mMaxWidth == Integer.MAX_VALUE) {
            int tmp = resolveSizeAndState(0, widthMeasureSpec, 0);
            if (tmp != 0) mMaxWidth = tmp;
        }
        if (mMaxHeight == Integer.MAX_VALUE) {
            int tmp = resolveSizeAndState(0, heightMeasureSpec, 0);
            if (tmp != 0) mMaxHeight = tmp;
        }
        Log.i(TAG, "onMeasure: max: " + mMaxWidth + "x" + mMaxHeight);

        if (state.bmw == Integer.MAX_VALUE && state.bmh == Integer.MAX_VALUE) {
            Log.i(TAG, "onMeasure: bitmap is null");
            Log.i(TAG, "onMeasure: setting to " + mMaxWidth + "x" + mMaxHeight);
            setMeasuredDimension(mMaxWidth, mMaxHeight);
        } else {
            Log.i(TAG, "onMeasure: bitmap is not null");
            int w = state.bmw;
            int h = state.bmh;
            if (w <= 0) w = 1;
            if (h <= 0) h = 1;
            AspectRatio bitmapDimensions = new AspectRatio(w, h);
            AspectRatio boundaryDimensions = new AspectRatio(mMaxWidth, mMaxHeight);
            Log.i(TAG, "onMeasure: bitmap dimensions: " + bitmapDimensions);
            Log.i(TAG, "onMeasure: target dimensions: " + boundaryDimensions);

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

            ratio = new AspectRatio(widthSize, heightSize);

            // Desired aspect ratio of the view's contents (not including padding)
            float desiredAspect = 0.0f;

            // We are allowed to change the view's width
            boolean resizeWidth = true;

            // We are allowed to change the view's height
            boolean resizeHeight = true;

            // We are supposed to adjust view bounds to match the aspect
            // ratio of our drawable. See if that is possible.
            if (mAdjustViewBounds) desiredAspect = bitmapDimensions.ratio();

            if (desiredAspect != 0.0f) {
                // See what our actual aspect ratio is
                int aw = widthSize - pleft - pright;
                int ah = heightSize - ptop - pbottom;
                final float actualAspect = AspectRatio.ratio(aw, ah);
                Log.i(TAG, "onMeasure: desiredAspect: " + desiredAspect);
                Log.i(TAG, "onMeasure: actualAspect: " + actualAspect);

                if (Math.abs(actualAspect - desiredAspect) > 0.0000001) {
                    Log.i(
                            TAG,
                            "onMeasure: scaling from " + bitmapDimensions
                                    + " to " + boundaryDimensions
                    );

                    Log.i(TAG, "onMeasure: old dimensions: " + bitmapDimensions);
                    Log.i(TAG, "onMeasure: old ratio: " + bitmapDimensions.ratio());
                    Log.i(TAG, "onMeasure: target dimensions: " + boundaryDimensions);
                    Log.i(TAG, "onMeasure: target ratio: " + boundaryDimensions.ratio());

                    if (heightSize != Integer.MAX_VALUE)  {
                        ratio.bruteForceKnownHeight(
                                bitmapDimensions, new AspectRatio(0, heightSize)
                        );
                        if (ratio.w > widthSize) {
                            Log.i(TAG, "onMeasure: computed width (" + ratio.w + ") exceeds widthSize (" + widthSize + ")");
                            ratio.w = widthSize;
                        }
                        if (boundaryDimensions.w != Integer.MAX_VALUE) {
                            Log.i(TAG, "onMeasure: ratio.w: " + ratio.w);
                            int canvasWidth = widthSize; // 817
                            Log.i(TAG, "onMeasure: canvasWidth: " + canvasWidth);
                            int canvasCenter = widthSize / 2;
                            Log.i(TAG, "onMeasure: canvasCenter: " + canvasCenter);
                            int imageWidth = ratio.w; // 506
                            Log.i(TAG, "onMeasure: imageWidth: " + imageWidth);
                            int imageCenter = ratio.w / 2;
                            Log.i(TAG, "onMeasure: imageCenter: " + imageCenter);
                            if (canvasCenter > imageCenter) {
                                Log.i(TAG, "onMeasure: canvasCenter > imageCenter");
                                ratio.x = canvasCenter - imageCenter;
                            } else {
                                Log.i(TAG, "onMeasure: canvasCenter < imageCenter");
                                ratio.x = imageCenter - canvasCenter;
                            }
                            Log.i(TAG, "onMeasure: ratio.w: " + ratio.w);
                            Log.i(TAG, "onMeasure: ratio.x: " + ratio.x);
                            ratio.w = ratio.x + ratio.w;
                            Log.i(TAG, "onMeasure: ratio.w: " + ratio.w);
                        }
                    }
                    if (widthSize != Integer.MAX_VALUE) {
                        ratio.bruteForceKnownWidth(
                                bitmapDimensions, new AspectRatio(ratio.w, 0)
                        );
                        if (ratio.h > heightSize) {
                            Log.i(TAG, "onMeasure: computed height (" + ratio.h + ") exceeds heightSize (" + heightSize + ")");
                            ratio.h = heightSize;
                        }
                        Log.i(TAG, "onMeasure: ratio.h: " + ratio.h);
                        if (boundaryDimensions.h != Integer.MAX_VALUE) {
                            Log.i(TAG, "onMeasure: ratio.h: " + ratio.h);
                            int canvasHeight = heightSize; // 817
                            Log.i(TAG, "onMeasure: canvasHeight: " + canvasHeight);
                            int canvasCenter = heightSize / 2;
                            Log.i(TAG, "onMeasure: canvasCenter: " + canvasCenter);
                            int imageHeight = ratio.h; // 506
                            Log.i(TAG, "onMeasure: imageHeight: " + imageHeight);
                            int imageCenter = ratio.h / 2;
                            Log.i(TAG, "onMeasure: imageCenter: " + imageCenter);
                            if (canvasCenter > imageCenter) {
                                Log.i(TAG, "onMeasure: canvasCenter > imageCenter");
                                ratio.y = canvasCenter - imageCenter;
                            } else {
                                Log.i(TAG, "onMeasure: canvasCenter < imageCenter");
                                ratio.y = imageCenter - canvasCenter;
                            }
                            Log.i(TAG, "onMeasure: ratio.h: " + ratio.h);
                            Log.i(TAG, "onMeasure: ratio.y: " + ratio.y);
                            ratio.h = ratio.y + ratio.h;
                            Log.i(TAG, "onMeasure: ratio.h: " + ratio.h);
                        }
                    }
                    Log.i(TAG, "onMeasure: new dimensions: " + ratio);
                    Log.i(TAG, "onMeasure: new ratio: " + ratio.ratio());
                }
            }

            if (ratio.w == 0 && ratio.h == 0) {
                Log.i(TAG, "onMeasure: invalid WxH, resetting to obtained WxH");
                ratio.w = widthSize;
                ratio.h = heightSize;
            }

            Log.i(TAG, "onMeasure: setting to " + ratio.w + "x" + ratio.h);
            setMeasuredDimension(ratio.w, ratio.h);
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