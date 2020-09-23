package screen.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
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

    private Bitmap store(final Bitmap bm) {
        if (bm == null) return null;
        ensureReferencesIfCurrentIsSet();
        Bitmap ref = bm.copy(bm.getConfig(), bm.isMutable());
        copies.add(ref);
        return ref;
    }

    /**
     * recycles the currently set bitmap, does nothing if already recycled
     */
    public void recycle() {
        recycle(false);
    }

    /**
     * recycles the currently set bitmap, does nothing if already recycled
     */
    public void recycle(boolean internal) {
        ensureReferencesIfCurrentIsSet();
        cache = null;
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

    private void set(final Bitmap bm) {
        if (bm == null) {
            recycle(true);
        } else {
            ensureReferencesIfCurrentIsSet();
            if (cacheDecompressed != null) {
                if (BitmapVector.sameAs(cacheDecompressed, bm)) {
                    // do nothing if bm matches the currently set image
                    return;
                }
                // else recycle
                recycle(true);
            }
            // by the time we reach here, our current image is guaranteed to be not set
            cacheDecompressed = bm;
            super.setImageBitmap(bm);
        }
    }

    private void storeAndSet(final Bitmap bm) {
        set(store(bm));
    }

    private void setIfFoundOtherwiseStoreAndSet(Bitmap bm) {
        if (bm == null) {
            set(null);
        } else {
            // check if bm can be located
            int index = copies.indexOf(bm);
            if (index >= 0) {
                Bitmap bitmap = copies.elementAt(index);
                // bm was found, set the image to the copy
                set(bitmap);
            } else {
                // bm was not found, store a copy
                storeAndSet(bm);
            }
        }
    }

    boolean safe = true;

    /**
     * it is safe to recycle the bitmap after calling this method
     */
    @Override
    public void setImageBitmap(final Bitmap bm) {
        if (!safe) {
            if (bm == null) return;
            cacheDecompressed = bm;
            super.setImageBitmap(cacheDecompressed);
            return;
        }
        // invalidate our cache
        cache = null;
        setIfFoundOtherwiseStoreAndSet(bm);
    }

    byte[] cache;
    Bitmap cacheDecompressed;


    public void setImageBitmap(final byte[] compressedBitmap) {
        if (!safe) {
            if (compressedBitmap == null) return;
            cacheDecompressed = BitmapUtils.decompress(compressedBitmap);
            super.setImageBitmap(cacheDecompressed);
            return;
        }
        if (compressedBitmap == null) recycle(true);
        else {
            if (cache == null || !BitmapUtils.arraysMatch(cache, compressedBitmap) || cache.length != compressedBitmap.length) {
                recycle(true);
                cache = compressedBitmap.clone();
            }
            Bitmap tmp = BitmapUtils.decompress(cache);
            setImageBitmap(tmp);
            tmp.recycle();
        }
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        if (cacheDecompressed == null) return;
        super.onDraw(canvas);
    }

    public void setImageBitmap(int width, int height, Bitmap.Config config, ByteBuffer buffer) {
        if (config != null) {
            Bitmap bitmap = Bitmap.createBitmap(width, height, config);
            bitmap.copyPixelsFromBuffer(buffer);
            setImageBitmap(bitmap);
            bitmap.recycle();
        }
    }

    public void setImageBitmap(int width, int height, Bitmap.Config config) {
        if (config != null) {
            Bitmap bitmap = Bitmap.createBitmap(width, height, config);
            setImageBitmap(bitmap);
            bitmap.recycle();
        }

    }
}
