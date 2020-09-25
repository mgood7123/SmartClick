package screen.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.widget.ImageView;

import java.nio.ByteBuffer;
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

    private boolean scalingDisallowed = true;

    public BitmapView(Context context, @androidx.annotation.Nullable AttributeSet attrs) {
        super(context, attrs);
        bitmapViews.add(this);
    }

    private void ensureReferencesIfCurrentIsSet() {
        if (cacheDecompressed != null && safe)
            if (copies.isEmpty())
                throw new IllegalStateException(
                    "bitmap is set but there are no references"
                );
    }

    static BitmapVector copies = new BitmapVector();

    private Bitmap store(Bitmap bm) {
        return store(bm, false);
    }


    private Bitmap store(Bitmap bm, boolean recycleAfterUse) {
        return store(bm, recycleAfterUse, true);
    }

    private Bitmap store(Bitmap bm, boolean recycleAfterUse, boolean makeCopy) {
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
        recycleInternal(false, true);
    }

    /**
     * recycles the currently set bitmap, does nothing if already recycled
     */
    public void recycle(boolean recycleCache) {
        recycleInternal(false, recycleCache);
    }

    /**
     * recycles the currently set bitmap,
     * does nothing if already recycled
     */
    void recycleInternal() {
        recycleInternal(false, false);
    }

    /**
     * recycles the currently set bitmap,
     * and if allowed, also recycles the current compressed bitmap cache.
     * does nothing if already recycled
     */
    void recycleInternal(boolean recycleCache) {
        recycleInternal(false, recycleCache);
    }

    /**
     * recycles the currently set bitmap,
     * and if allowed, also recycles the current compressed bitmap cache.
     * does nothing if already recycled
     */
    void recycleInternal(boolean internal, boolean recycleCache) {
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
        set(bm, false);
    }

    private void set(Bitmap bm, boolean recycleCache) {
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
        storeAndSet(bm, false);
    }

    private void storeAndSet(Bitmap bm, boolean recycleAfterUse) {
        storeAndSet(bm, recycleAfterUse, true);
    }

    private void storeAndSet(Bitmap bm, boolean recycleAfterUse, boolean makeCopy) {
        set(store(bm, recycleAfterUse, makeCopy));
    }

    private void setIfFoundOtherwiseStoreAndSet(Bitmap bm, int scaleMode) {
        setIfFoundOtherwiseStoreAndSet(bm, false, false, scaleMode);
    }

    private void setIfFoundOtherwiseStoreAndSet(Bitmap bm) {
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
        setIfFoundOtherwiseStoreAndSet(bm, recycleAfterUse, setImmediately, ScaleMode.KEEP_ORIGINAL_WIDTH_AND_HEIGHT);
    }

    private void internalSet(Bitmap bm, boolean internallyAllocated, boolean recycleAfterUse) {
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
        if (bm == null) {
            set(null);
        } else {
            final ScaleMode.FlagData flagData = ScaleMode.analyseFlags(scaleMode);
            Runnable r = new Runnable() {
                @Override
                public void run() {
                }
            };
            if (setImmediately || scalingDisallowed) {
                // set even if bitmap would be scaled
                r.run();
            } else {
                if (flagData.hasFlags) post(r);
                else r.run();
            }
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

    private Pair<Bitmap, Boolean> scale(Bitmap bm, Canvas canvas, boolean recycleAfterUse, boolean shouldScale, ScaleMode.FlagData flagData) {
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
        Log.i(TAG, "setImageBitmap: begin");
        if (bm == null) {
            Log.i(TAG, "setImageBitmap: cannot draw null bitmap");
        } else {
            this.bm = bm;
            this.recycleAfterUse = recycleAfterUse;
            this.setImmediately = setImmediately;
            this.scaleMode = scaleMode;
            invalidate();
        }
        Log.i(TAG, "setImageBitmap: end");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.i(TAG, "onDraw: begin");
        if (bm == null) {
            Log.i(TAG, "onDraw: cannot draw null bitmap");
        } else {
            final ScaleMode.FlagData flagData = ScaleMode.analyseFlags(scaleMode);
            Pair<Bitmap, Boolean> scaled = scale(bm, canvas, recycleAfterUse, true, flagData);
            cacheDecompressed = scaled.first;
            src.right = cacheDecompressed.getWidth();
            src.bottom = cacheDecompressed.getHeight();
            dst.right = canvas.getWidth();
            dst.bottom = canvas.getHeight();
            canvas.drawBitmap(cacheDecompressed, src, dst, paint);
            if (scaled.second || recycleAfterUse) scaled.first.recycle();
        }
        Log.i(TAG, "onDraw: end");
    }
}
