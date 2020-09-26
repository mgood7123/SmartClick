package screen.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Vector;

/**
 * a special variant of ImageView designed specifically for bitmaps
 *
 * prioritizes low memory consumption
 *
 */
public class BitmapView extends ImageView {

    private final String TAG = "BitmapView (" + getClass().getName() + "@" + Integer.toHexString(hashCode()) + ")";

    static Vector<BitmapView> bitmapViews = new Vector();

    private boolean isAllowedToScale = false;

    public BitmapView(Context context, @androidx.annotation.Nullable AttributeSet attrs) {
        super(context, attrs);
        bitmapViews.add(this);
    }

    private void ensureReferencesIfCurrentIsSet() {
        // TODO: remove
        if (cacheDecompressed != null && safe)
            if (copies.isEmpty())
                throw new IllegalStateException(
                    "bitmap is set but there are no references"
                );
    }

    static BitmapVector copies = new BitmapVector();

    private Bitmap store(Bitmap bm) {
        // TODO: remove
        return store(bm, false);
    }


    private Bitmap store(Bitmap bm, boolean recycleAfterUse) {
        // TODO: remove
        return store(bm, recycleAfterUse, true);
    }

    private Bitmap store(Bitmap bm, boolean recycleAfterUse, boolean makeCopy) {
        // TODO: remove
        if (bm == null) return null;
        ensureReferencesIfCurrentIsSet();
        Bitmap ref = bm;
        if (makeCopy) {
            // if makeCopy is true
            // then make a copy of our bitmap
            ref = bm.copy(bm.getConfig(), bm.isMutable());
            if (ref != null) {
                // recycle our bitmap as we no longer need it since we have a copy of it
                if (recycleAfterUse) bm.recycle();
                // add and return the copy
                copies.add(ref);
                return ref;
            }
        } else {
            // if makeCopy is false, assume our bitmap is internally allocated
            // do not copy it and do not recycle it, just add and return the copy
            //
            // only add if makeCopy is false, this ensures that we do not add null
            // if makeCopy is true and we failed to make a copy
            copies.add(ref);
        }
        return ref;
    }

    /**
     * recycles the currently set bitmap, does nothing if already recycled
     */
    public void recycle() {
        // TODO: remove?
        recycleInternal(false, true);
    }

    /**
     * recycles the currently set bitmap, does nothing if already recycled
     */
    public void recycle(boolean recycleCache) {
        // TODO: remove?
        recycleInternal(false, recycleCache);
    }

    /**
     * recycles the currently set bitmap,
     * does nothing if already recycled
     */
    void recycleInternal() {
        // TODO: remove?
        recycleInternal(false, false);
    }

    /**
     * recycles the currently set bitmap,
     * and if allowed, also recycles the current compressed bitmap cache.
     * does nothing if already recycled
     */
    void recycleInternal(boolean recycleCache) {
        // TODO: remove?
        recycleInternal(false, recycleCache);
    }

    /**
     * recycles the currently set bitmap,
     * and if allowed, also recycles the current compressed bitmap cache.
     * does nothing if already recycled
     */
    void recycleInternal(boolean internal, boolean recycleCache) {
        // TODO: remove?
        ensureReferencesIfCurrentIsSet();
        if (recycleCache) cache = null;
        if (cacheDecompressed != null) {
            if (!cacheDecompressed.isRecycled()) {
                copies.remove(cacheDecompressed);
                cacheDecompressed.recycle();
            } else {
                if (internal) throw new RuntimeException("trying to recycle an already recycled bitmap: " + cacheDecompressed);
            }
            cacheDecompressed = null;
        }
    }

    private void set(Bitmap bm) {
        // TODO: remove
        set(bm, false);
    }

    private void set(Bitmap bm, boolean recycleCache) {
        // TODO: remove
        if (bm == null) {
            recycleInternal(recycleCache);
        } else {
            ensureReferencesIfCurrentIsSet();
            if (cacheDecompressed != null) {
                if (BitmapVector.sameAs(cacheDecompressed, bm)) {
                    // do nothing if bm matches the currently set image
                    Log.i(TAG, "set: bitmap matches cached bitmap");
                    return;
                }
            }
            Log.i(TAG, "set: bitmap does not match cached bitmap");
            recycleInternal(recycleCache);
            // by the time we reach here, our current image is guaranteed to be not set
            cacheDecompressed = bm;
            super.setImageBitmap(bm);
        }
    }

    private void storeAndSet(Bitmap bm) {
        // TODO: remove
        storeAndSet(bm, false);
    }

    private void storeAndSet(Bitmap bm, boolean recycleAfterUse) {
        // TODO: remove
        storeAndSet(bm, recycleAfterUse, true);
    }

    private void storeAndSet(Bitmap bm, boolean recycleAfterUse, boolean makeCopy) {
        // TODO: remove
        set(store(bm, recycleAfterUse, makeCopy));
    }

    private void setIfFoundOtherwiseStoreAndSet(Bitmap bm, int scaleMode) {
        // TODO: remove
        setIfFoundOtherwiseStoreAndSet(bm, false, false, scaleMode);
    }

    private void setIfFoundOtherwiseStoreAndSet(Bitmap bm) {
        // TODO: remove
        setIfFoundOtherwiseStoreAndSet(bm, false, false);
    }

    static boolean safe = true;

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

    private void setIfFoundOtherwiseStoreAndSet(final Bitmap bm, final boolean recycleAfterUse, final boolean setImmediately) {
        // TODO: remove
        setIfFoundOtherwiseStoreAndSet(bm, recycleAfterUse, setImmediately, ScaleMode.KEEP_ORIGINAL_WIDTH_AND_HEIGHT);
    }

    private void internalSet(Bitmap bm, boolean internallyAllocated, boolean recycleAfterUse) {
        // TODO: remove
        // check if bm can be located
        int index = copies.indexOf(bm);
        if (index >= 0) {
            // recycle scaled bitmap if it is internally allocated
            // otherwise recycle if allowed
            if (internallyAllocated || recycleAfterUse) bm.recycle();

            // bm was found, set the image to the copy
            set(copies.elementAt(index));
        } else {
            // bm was not found, store a copy

            // recycle scaled bitmap if it is internally allocated
            // otherwise recycle if allowed
            storeAndSet(bm, internallyAllocated || recycleAfterUse);
        }
    }

    private void setIfFoundOtherwiseStoreAndSet(final Bitmap bm, final boolean recycleAfterUse, final boolean setImmediately, final int scaleMode) {
        // TODO: remove
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

    byte[] cache;
    Bitmap cacheDecompressed;


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
            boolean viewWidthScaled = false;
            if (flagData.hasScaleWidthIfLargerThanViewWidthFlag) {
                if (computedWidth > viewWidth) {
                    computedWidth = viewWidth;
                    viewWidthScaled = true;
                }
            }
            if (!viewWidthScaled && flagData.hasScaleWidthIfSmallerThanViewWidthFlag) {
                if (computedWidth < viewWidth) {
                    computedWidth = viewWidth;
                }
            }
        }
        return computedWidth;
    }

    private int computeScaledHeight(Bitmap bm, Canvas canvas, ScaleMode.FlagData flagData) {
        int computedHeight = bm.getHeight();
        if (flagData.hasHeightFlag) {
            int viewHeight = canvas.getHeight();
            boolean viewHeightScaled = false;
            if (flagData.hasScaleHeightIfLargerThanViewHeightFlag) {
                if (computedHeight > viewHeight) {
                    computedHeight = viewHeight;
                    viewHeightScaled = true;
                }
            }
            if (!viewHeightScaled && flagData.hasScaleHeightIfSmallerThanViewHeightFlag) {
                if (computedHeight < viewHeight) {
                    computedHeight = viewHeight;
                }
            }
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
            Bitmap scaled = null;
            scaled = Bitmap.createScaledBitmap(bm, width, height, false);
            if (scaled != null) {
                // recycle if allowed
                if (recycleAfterUse) bm.recycle();
                return new Pair(scaled, true);
            }
        }
        return new Pair(bm, false);
    }

    Rect src = new Rect(0,0,0,0);
    Rect dst = new Rect (0,0,0, 0);
    Paint paint = null;

    boolean shouldScale;
    boolean recycleAfterDraw;
    Bitmap bm;
    boolean recycleAfterUse;
    boolean setImmediately;
    int scaleMode;

    public void setImageBitmap(byte[] compressedBitmap, int scaleMode) {
        if (compressedBitmap != null) {
            cache = compressedBitmap.clone();
            cacheDecompressed = BitmapUtils.decompress(cache);
            setImageBitmap(cacheDecompressed, false, false, scaleMode);
        } else {
            cache = null;
        }
    }

    public void setImageBitmap(Bitmap bm, boolean recycleAfterUse, boolean setImmediately, int scaleMode) {
        cache = null;
        if (bm == null) {
            if (cacheDecompressed != null) {
                cacheDecompressed.recycle();
                cacheDecompressed = null;
                Log.i(TAG, "setImageBitmap: recycled");
            }
        } else {
            if (getWindowVisibility() != GONE) {
                this.bm = bm;
                this.recycleAfterUse = recycleAfterUse;
                this.setImmediately = setImmediately;
                this.scaleMode = scaleMode;
                invalidate();
            }
        }
    }

    int maxRecordingFrames = 200;

    class RecordedFrames<Type> {
        private final String TAG = "RecordedFrames (" + getClass().getName() + "@" + Integer.toHexString(hashCode()) + ")";
        ArrayList<Type> frames = new ArrayList();
        Integer width = 0;
        Integer height = 0;

        static final String typeMismatchStr = "given parameter 'o' must be of type byte[] or of type Bitmap";
        static final String typeMismatchBitmapStr = "type 'frame' must be of type Bitmap or of type byte[]";
        static final String typeMismatchByteStr = "type 'frame' must be of type byte[] or of type Bitmap";
        static final String nullFrameStr = "frame cannot be null";
        final ClassCastException illegalCast = new ClassCastException(typeMismatchStr);
        final ClassCastException illegalBitmapCast = new ClassCastException(typeMismatchBitmapStr);
        final ClassCastException illegalByteCast = new ClassCastException(typeMismatchByteStr);
        final NullPointerException nullFrame = new NullPointerException(nullFrameStr);

        private Boolean compressRecordedFrames = Boolean.TRUE;

        void add(Type frame) throws ClassCastException, NullPointerException {
            if (!(frame instanceof byte[]) && !(frame instanceof Bitmap)) throw illegalCast;
            if (frame == null) throw nullFrame;
            frames.add(frame);
        }

        public Bitmap getBitmap(int index) {
            Type frame;
            frame = frames.get(index);
            if (!(frame instanceof Bitmap)) throw illegalBitmapCast;
            if (frame == null) throw nullFrame;
            return (Bitmap) frame;
        }

        public byte[] getByte(int index) {
            Type frame;
            frame = frames.get(index);
            if (!(frame instanceof byte[])) throw illegalByteCast;
            if (frame == null) throw nullFrame;
            return (byte[]) frame;
        }

        void remove(int index) {
            frames.remove(0);
        }

        public boolean isCompressed() {
            return compressRecordedFrames;
        }

        public void setCompressRecordedFrames(Boolean shouldCompress) {
            compressRecordedFrames = shouldCompress;
        }

        ArrayList<Type> getFrames() {
            return frames;
        }

        public int size() {
            return frames.size();
        }

        /**
         * if the frames are not compressed return a reference to this to save memory
         * otherwise return a copy of this
         * @return
         */

        protected RecordedFrames<Type> clone() {
            if (!compressRecordedFrames) {
                return this;
            }

            int size = frames.size();
            if (size == 0) {
                RecordedFrames<Type> copy = new RecordedFrames();
                copy.compressRecordedFrames = isCompressed();
                return copy;
            }

            Object sample = frames.get(0);
            if (sample == null) throw nullFrame;
            if (sample instanceof byte[]) {
                RecordedFrames<byte[]> copy = new RecordedFrames();
                copy.compressRecordedFrames = isCompressed();
                copy.width = getWidth();
                copy.height = getHeight();
                copy.frames.ensureCapacity(size);
                for (int i = 0, framesSize = frames.size(); i < framesSize; i++) {
                    byte[] frame = (byte[]) frames.get(i);
                    if (frame == null) throw nullFrame;
                    copy.frames.add(frame.clone());
                }
                return (RecordedFrames<Type>) copy;
            } else if (sample instanceof Bitmap) {
                // TODO: allow this under certain conditions
                //  this is currently disabled via if (!compressRecordedFrames) return this;
                RecordedFrames<Bitmap> copy = new RecordedFrames();
                copy.compressRecordedFrames = isCompressed();
                copy.frames.ensureCapacity(size);
                for (int i = 0, framesSize = frames.size(); i < framesSize; i++) {
                    Bitmap frame = (Bitmap) frames.get(i);
                    if (frame == null) throw nullFrame;
                    copy.frames.add(frame.copy(frame.getConfig(), frame.isMutable()));
                }
                return (RecordedFrames<Type>) copy;
            } else {
                throw illegalCast;
            }
        }

        public void clear() {
            int size = frames.size();
            if (size == 0) return;

            Object sample = frames.get(0);
            if (sample == null) throw nullFrame;
            if (sample instanceof byte[]) {
                frames.clear();
            } else if (sample instanceof Bitmap) {
                for (int i = 0, framesSize = frames.size(); i < framesSize; i++) {
                    Bitmap frame = (Bitmap) frames.get(i);
                    if (frame == null) throw nullFrame;
                    frame.recycle();
                    Log.i(TAG, "clear: recycled");
                }
                frames.clear();
            } else {
                throw illegalCast;
            }
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getWidth() {
            return this.width;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getHeight() {
            return this.height;
        }
    }

    RecordedFrames recordedFrames = null;

    static class RecordingState {
        static final Integer started = 1;
        static final Integer recording = 2;
        static final Integer paused = 3;
        static final Integer stopped = 4;
    }

    Integer recordingState = RecordingState.stopped;

    Bitmap.CompressFormat compressionFormat = Bitmap.CompressFormat.JPEG;
    int compressionQuality = 40;

    void beginRecording(boolean compressFrames) {
        if (recordingState == RecordingState.stopped) {
            if (compressFrames) {
                // TODO: a LruCache could be used for higher performance, see
                //  https://developer.android.com/topic/performance/graphics/manage-memory
                if (recordedFrames == null) recordedFrames = new RecordedFrames<byte[]>();
                else {
                    recordedFrames.clear();
                }
            } else {
                if (recordedFrames == null) {
                    recordedFrames = new RecordedFrames<Bitmap>();
                    recordedFrames.setCompressRecordedFrames(Boolean.FALSE);
                } else {
                    recordedFrames.clear();
                }
            }
            recordingState = RecordingState.started;
        }
    }

    void pauseRecording() {
        recordingState = RecordingState.paused;
    }

    void resumeRecording() {
        recordingState = RecordingState.recording;
    }

    void endRecording() {
        recordingState = RecordingState.stopped;
    }

    RecordedFrames getRecordedData() {
        return recordedFrames;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bm == null) {
            Log.i(TAG, "onDraw: cannot draw null bitmap");
        } else if (bm.isRecycled()) {
            Log.i(TAG, "onDraw: cannot draw a recycled bitmap");
        } else {
            if (recordingState == RecordingState.started) {
                recordedFrames.setWidth(bm.getWidth());
                recordedFrames.setHeight(bm.getHeight());
                recordingState = RecordingState.recording;
            }
            if (recordingState == RecordingState.recording) {
                if (recordedFrames.isCompressed()) {
                    if (recordedFrames.frames.size() == maxRecordingFrames) {
                        recordedFrames.remove(0);
                    }
                    recordedFrames.add(BitmapUtils.compress(bm, compressionFormat, compressionQuality));
                }
            }
            Pair scaled = null;
            if (isAllowedToScale) {
                final ScaleMode.FlagData flagData = ScaleMode.analyseFlags(scaleMode);
                scaled = scale(bm, canvas, recycleAfterUse, true, flagData);
                if (scaled.second) bm = scaled.first;
                cacheDecompressed = scaled.first;
            } else {
                cacheDecompressed = bm;
            }
            src.right = cacheDecompressed.getWidth();
            src.bottom = cacheDecompressed.getHeight();
            dst.right = canvas.getWidth();
            dst.bottom = canvas.getHeight();
            canvas.drawBitmap(cacheDecompressed, src, dst, paint);
        }
    }

    @Override
    public boolean willNotDraw() {
        return cacheDecompressed != null || bm == null;
    }

    @Override
    protected void onDetachedFromWindow() {
        if (cache != null) cache = null;
        if (cacheDecompressed != null) {
            cacheDecompressed.recycle();
            cacheDecompressed = null;
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
            if (cache != null) cache = null;
            if (cacheDecompressed != null) {
                cacheDecompressed.recycle();
                cacheDecompressed = null;
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
}
