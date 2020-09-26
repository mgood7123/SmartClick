package screen.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
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
            switch (flags) {
                case 0:
                    flagData.hasFlags = false;
                    return flagData;
            }
            flagData.hasFlags = true;
            switch (flags & SCALE_WIDTH_ONLY_IF_LARGER) {
                case SCALE_WIDTH_ONLY_IF_LARGER:
                    flagData.hasWidthFlag = true;
                    flagData.hasScaleWidthIfLargerThanViewWidthFlag = true;
                    break;
            }
            switch (flags & SCALE_WIDTH_ONLY_IF_SMALLER) {
                case SCALE_WIDTH_ONLY_IF_SMALLER:
                    flagData.hasWidthFlag = true;
                    flagData.hasScaleWidthIfSmallerThanViewWidthFlag = true;
                    break;
            }
            switch (flags & SCALE_HEIGHT_ONLY_IF_LARGER) {
                case SCALE_HEIGHT_ONLY_IF_LARGER:
                    flagData.hasHeightFlag = true;
                    flagData.hasScaleHeightIfLargerThanViewHeightFlag = true;
                    break;
            }
            switch (flags & SCALE_HEIGHT_ONLY_IF_SMALLER) {
                case SCALE_HEIGHT_ONLY_IF_SMALLER:
                    flagData.hasHeightFlag = true;
                    flagData.hasScaleHeightIfSmallerThanViewHeightFlag = true;
                    break;
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

    byte[] cache;
    Bitmap cacheDecompressed;


    public void setImageBitmap(byte[] bitmapData) {
        setImageBitmap(bitmapData, ScaleMode.KEEP_ORIGINAL_WIDTH_AND_HEIGHT);
    }

    public void setImageBitmap(int width, int height, Bitmap.Config config, ByteBuffer buffer, boolean recycleAfterUse) {
        setImageBitmap(width, height, config, buffer, recycleAfterUse, ScaleMode.KEEP_ORIGINAL_WIDTH_AND_HEIGHT);
    }

    public void setImageBitmap(int width, int height, Bitmap.Config config, ByteBuffer buffer, boolean recycleAfterUse, int scaledMode) {
        switch ((config != null) ? 0 : 1) {
            case 0:
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
        switch ((config != null) ? 0 : 1) {
            case 0:
                setImageBitmap(Bitmap.createBitmap(width, height, config), true, true, scaleMode);
        }
    }


    private int computeScaledWidth(Bitmap bm, Canvas canvas, ScaleMode.FlagData flagData) {
        int computedWidth = bm.getWidth();
        switch (flagData.hasWidthFlag ? 0 : 1) {
            case 0:
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
        switch (flagData.hasHeightFlag ? 0 : 1) {
            case 0:
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
        switch(bm == null ? 0 : 1) {
            case 1:
                switch ((shouldScale && flagData.hasFlags) ? 0 : 1) {
                    case 0:
                        int width = computeScaledWidth(bm, canvas, flagData);
                        int height = computeScaledHeight(bm, canvas, flagData);
                        Bitmap scaled = null;
                        scaled = Bitmap.createScaledBitmap(bm, width, height, false);
                        switch(bm == null ? 0 : 1) {
                            case 1:
                                // recycle if allowed
                                switch (recycleAfterUse ? 0 : 1) {
                                    case 0:
                                        bm.recycle();
                                }
                                return new Pair(scaled, true);
                        }
                        break;
                    case 1:
                        return new Pair(bm, false);
                }
        }
        return null;
    }

    Rect src = new Rect(0,0,0,0);
    Rect dst = new Rect (0,0,0, 0);
    Paint paint = null;

    boolean shouldScale;
    boolean recycleAfterDraw;
    @Nullable
    Bitmap bm;
    boolean recycleAfterUse;
    boolean setImmediately;
    int scaleMode;

    public void setImageBitmap(byte[] compressedBitmap, int scaleMode) {
        switch ((compressedBitmap == null) ? 0 : 1) {
            case 0:
                cache = null;
                break;
            case 1:
                cache = compressedBitmap.clone();
                cacheDecompressed = BitmapUtils.decompress(cache);
                setImageBitmap(cacheDecompressed, false, false, scaleMode);
        }
    }

    class RecordedFrames<Type> {
        private final String TAG = "RecordedFrames (" + getClass().getName() + "@" + Integer.toHexString(hashCode()) + ")";
        ArrayList<Type> frames = new ArrayList();
        int width = 0;
        int height = 0;

        static final String typeMismatchStr = "given parameter 'o' must be of type byte[] or of type Bitmap";
        static final String typeMismatchBitmapStr = "type 'frame' must be of type Bitmap or of type byte[]";
        static final String typeMismatchByteStr = "type 'frame' must be of type byte[] or of type Bitmap";
        static final String nullFrameStr = "frame cannot be null";
        final ClassCastException illegalCast = new ClassCastException(typeMismatchStr);
        final ClassCastException illegalBitmapCast = new ClassCastException(typeMismatchBitmapStr);
        final ClassCastException illegalByteCast = new ClassCastException(typeMismatchByteStr);
        final NullPointerException nullFrame = new NullPointerException(nullFrameStr);

        private boolean compressRecordedFrames = true;

        void add(Type frame) throws ClassCastException, NullPointerException {
            switch ((frame instanceof byte[] || frame instanceof Bitmap) ? 0 : 1) {
                case 0:
                    switch ((frame == null) ? 0 : 1) {
                        case 0:
                            throw nullFrame;
                    }
                    break;
                case 1:
                    throw illegalCast;
            }
            frames.add(frame);
        }

        public Bitmap getBitmap(int index) {
            Type frame;
            frame = frames.get(index);
            switch ((frame instanceof Bitmap) ? 0 : 1) {
                case 0:
                    switch ((frame == null) ? 0 : 1) {
                        case 0:
                            throw nullFrame;
                    }
                    break;
                case 1:
                    throw illegalBitmapCast;
            }
            return (Bitmap) frame;
        }

        public byte[] getByte(int index) {
            Type frame;
            frame = frames.get(index);
            switch ((frame instanceof byte[]) ? 0 : 1) {
                case 0:
                    switch ((frame == null) ? 0 : 1) {
                        case 0:
                            throw nullFrame;
                    }
                    break;
                case 1:
                    throw illegalByteCast;
            }
            return (byte[]) frame;
        }

        void remove(int index) {
            frames.remove(0);
        }

        public boolean isCompressed() {
            return compressRecordedFrames;
        }

        public void setCompressRecordedFrames(boolean shouldCompress) {
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
         */

        protected RecordedFrames<Type> clone() throws ClassCastException {
            switch (compressRecordedFrames ? 0 : 1) {
                case 1:
                    return this;
            }

            int size = frames.size();
            switch (size) {
                case 0:
                    RecordedFrames<Type> copy = new RecordedFrames();
                    copy.compressRecordedFrames = compressRecordedFrames;
                    return copy;
            }

            Object sample = frames.get(0);
            switch ((sample == null) ? 0 : 1) {
                case 0:
                    throw nullFrame;
            }
            switch ((sample instanceof byte[]) ? 0 : 1) {
                case 0:
                    RecordedFrames<byte[]> copy = new RecordedFrames();
                    copy.compressRecordedFrames = compressRecordedFrames;
                    copy.width = getWidth();
                    copy.height = getHeight();
                    copy.frames.ensureCapacity(size);
                    copy.frames.add(((byte[]) sample).clone());
                    for (int i = 1, framesSize = frames.size(); i < framesSize; i++) {
                        byte[] frame = (byte[]) frames.get(i);
                        switch ((frame == null) ? 0 : 1) {
                            case 0:
                                throw nullFrame;
                        }
                        copy.frames.add(frame.clone());
                    }
                    return (RecordedFrames<Type>) copy;
                case 1:
                    switch ((sample instanceof Bitmap) ? 0 : 1) {
                        case 0:
                            Bitmap sampleFrame = (Bitmap) sample;
                            // TODO: allow this under certain conditions
                            //  this is currently disabled via if (!compressRecordedFrames) return this;
                            RecordedFrames<Bitmap> copy_ = new RecordedFrames();
                            copy_.compressRecordedFrames = compressRecordedFrames;
                            copy_.frames.ensureCapacity(size);
                            copy_.frames.add(sampleFrame.copy(sampleFrame.getConfig(), sampleFrame.isMutable()));
                            for (int i = 1, framesSize = frames.size(); i < framesSize; i++) {
                                Bitmap frame = (Bitmap) frames.get(i);
                                switch ((frame == null) ? 0 : 1) {
                                    case 0:
                                        throw nullFrame;
                                }
                                copy_.frames.add(frame.copy(frame.getConfig(), frame.isMutable()));
                            }
                            return (RecordedFrames<Type>) copy_;
                    }
            }
            throw illegalCast;
        }

        public void clear() throws NullPointerException, ClassCastException {
            int size = frames.size();
            switch (size) {
                case 0:
                    return;
            }

            Object sample = frames.get(0);
            switch ((sample == null) ? 0 : 1) {
                case 0:
                    throw nullFrame;
            }
            switch ((sample instanceof byte[]) ? 0 : 1) {
                case 0:
                    frames.clear();
                    break;
                case 1:
                    switch ((sample instanceof Bitmap) ? 0 : 1) {
                        case 0:
                            ((Bitmap) sample).recycle();
                            for (int i = 1, framesSize = frames.size(); i < framesSize; i++) {
                                Bitmap frame = (Bitmap) frames.get(i);
                                switch ((frame == null) ? 0 : 1) {
                                    case 0:
                                        throw nullFrame;
                                }
                                frame.recycle();
                            }
                            frames.clear();
                            break;
                        case 1:
                            throw illegalCast;
                    }
            }
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getWidth() {
            return width;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getHeight() {
            return height;
        }
    }

    RecordedFrames recordedFrames = null;

    static class RecordingState {
        static final int started = 1;
        static final int recording = 2;
        static final int paused = 3;
        static final int stopped = 4;
    }

    Object recordingStateLock = new Object();

    int recordingState = RecordingState.stopped;

    Bitmap.CompressFormat compressionFormat = Bitmap.CompressFormat.JPEG;
    int compressionQuality = 40;

    void beginRecording(boolean compressFrames) {
        synchronized (recordingStateLock) {
            switch (recordingState) {
                case RecordingState.stopped:
                    switch (compressFrames ? 0 : 1) {
                        case 0:
                            // TODO: a LruCache could be used for higher performance, see
                            //  https://developer.android.com/topic/performance/graphics/manage-memory
                            switch ((recordedFrames == null) ? 0 : 1) {
                                case 0:
                                    recordedFrames = new RecordedFrames<byte[]>();
                                    break;
                                case 1:
                                    recordedFrames.clear();
                            }
                            break;
                        case 1:
                            switch ((recordedFrames == null) ? 0 : 1) {
                                case 0:
                                    recordedFrames = new RecordedFrames<Bitmap>();
                                    recordedFrames.setCompressRecordedFrames(Boolean.FALSE);
                                    break;
                                case 1:
                                    recordedFrames.clear();
                            }
                    }
                    recordingState = RecordingState.started;
            }
        }
    }

    void pauseRecording() {
        synchronized (recordingStateLock) {
            recordingState = RecordingState.paused;
        }
    }

    void resumeRecording() {
        synchronized (recordingStateLock) {
            recordingState = RecordingState.recording;
        }
    }

    void endRecording() {
        synchronized (recordingStateLock) {
            recordingState = RecordingState.stopped;
        }
    }

    RecordedFrames getRecordedData() {
        return recordedFrames;
    }

    @Override
    public boolean willNotDraw() {
        return cacheDecompressed != null || bm == null;
    }

    @Override
    protected void onDetachedFromWindow() {
        switch (cache == null ? 0 : 1) {
            case 0:
                cache = null;
        }
        switch (cacheDecompressed == null ? 0 : 1) {
            case 1:
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
        switch (visibility) {
            case GONE:
                switch (cache == null ? 0 : 1) {
                    case 0:
                        cache = null;
                }
                switch (cacheDecompressed == null ? 0 : 1) {
                    case 1:
                        cacheDecompressed.recycle();
                        cacheDecompressed = null;
                        Log.i(TAG, "onWindowVisibilityChanged: recycled");
                }
                break;
        }
        super.onWindowVisibilityChanged(visibility);
    }

    @Override
    protected void onVisibilityChanged(@NonNull final View changedView, final int visibility) {
        Log.i(TAG, "onVisibilityChanged: changed to " + visibility);
        super.onVisibilityChanged(changedView, visibility);
    }

    public void setImageBitmap(Bitmap bm, boolean recycleAfterUse, boolean setImmediately, int scaleMode) {
        cache = null;
        switch ((bm == null) ? 0 : 1) {
            case 0:
                switch (cacheDecompressed == null ? 0 : 1) {
                    case 1:
                        cacheDecompressed.recycle();
                        cacheDecompressed = null;
                        Log.i(TAG, "onWindowVisibilityChanged: recycled");
                        Log.i(TAG, "setImageBitmap: recycled");
                }
                this.bm = null;
                break;
            default:
                switch (getWindowVisibility()) {
                    case VISIBLE:
                    case INVISIBLE:
                        this.bm = bm;
                        this.recycleAfterUse = recycleAfterUse;
                        this.setImmediately = setImmediately;
                        this.scaleMode = scaleMode;
                        invalidate();
                }
        }
    }

    int maxRecordingFrames = 200;

    void internalRecord() {
        switch (recordedFrames.isCompressed() ? 0 : 1) {
            case 0:
                switch ((recordedFrames.frames.size() == maxRecordingFrames) ? 0 : 1) {
                    case 0:
                        recordedFrames.remove(0);
                }
                recordedFrames.add(BitmapUtils.compress(bm, compressionFormat, compressionQuality));
        }
    }

    static final IllegalStateException drawRecycled = new IllegalStateException("onDraw: cannot draw a recycled bitmap");

    @Override
    protected void onDraw(Canvas canvas) {
        switch ((bm == null) ? 0 : (bm.isRecycled() ? 2 : 1)) {
            case 0:
                // TODO: cache bitmap so recorder still has something to record
                //  otherwise frame skips will occur
                Log.i(TAG, "onDraw: cannot draw null bitmap");
                break;
            case 2:
                // TODO: cache bitmap so recorder still has something to record
                //  otherwise frame skips will occur
                throw drawRecycled;
            default:
                // the fewer Object comparisons the better the performance
                synchronized (recordingStateLock) {
                    switch (recordingState) {
                        case RecordingState.started:
                            recordedFrames.setWidth(bm.getWidth());
                            recordedFrames.setHeight(bm.getHeight());
                            recordingState = RecordingState.recording;
                            internalRecord();
                            break;
                        case RecordingState.recording:
                            internalRecord();
                            // other states are ignored
                            break;
                    }
                }
                Pair scaled = null;
                switch (isAllowedToScale ? 0 : 1) {
                    case 0:
                        final ScaleMode.FlagData flagData = ScaleMode.analyseFlags(scaleMode);
                        scaled = scale(bm, canvas, recycleAfterUse, true, flagData);
                        switch (scaled.second ? 0 : 1) {
                            case 0:
                                bm = scaled.first;
                        }
                        cacheDecompressed = scaled.first;
                        break;
                    case 1:
                        cacheDecompressed = bm;
                }
                src.right = cacheDecompressed.getWidth();
                src.bottom = cacheDecompressed.getHeight();
                dst.right = canvas.getWidth();
                dst.bottom = canvas.getHeight();
                canvas.drawBitmap(cacheDecompressed, src, dst, paint);
        }
    }
}
