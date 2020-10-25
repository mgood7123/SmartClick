package smallville7123.textbook;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.Log;

import java.time.Duration;
import java.time.Instant;

public class Skia {
    private static final String TAG = "Skia";

    static {
        System.loadLibrary("jniSkia");
    }

    private long native_skia_ptr = 0;

    private Bitmap bitmap;
    private int width;
    private int height;

    public Skia() {
        native_skia_ptr = createNativeInstance();
    }

    private native void createCanvas(long native_skia_ptr, int width, int height);
    private native long createNativeInstance();
    private native int[] getPixels(long native_skia_ptr);
    private native long constructPaint(int alpha, boolean antiAlias, int textColor, int style);
    private native long constructFont(float textSize, float textScaleX, float textSkewX);
    private native void drawText(long native_skia_ptr, String text, int index, int count, float x, float y, long paint, long font);
    private native int getWidth(long native_skia_ptr);
    private native int getHeight(long native_skia_ptr);
    private native int getStride(long native_skia_ptr);
    private native void clear(long native_skia_ptr, int color);

    public void createCanvas(int w, int h) {
        width = w;
        height = h;
        createCanvas(native_skia_ptr, w, h);
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    }

    public long constructSkiaPaint(Paint paint) {
        return constructPaint(
                paint.getAlpha(),
                paint.isAntiAlias(),
                paint.getColor(),
                paint.getStyle().ordinal()
        );
    }

    public long constructSkiaFont(Paint paint) {
        return constructFont(
                paint.getTextSize(),
                paint.getTextScaleX(),
                paint.getTextSkewX()
        );
    }


    /**
     * Draw the internal bitmap, with its top/left corner at (0,0), using the specified paint,
     * transformed by the current matrix.
     * <p>
     * If the bitmap and canvas have different densities, this function will take care of
     * automatically scaling the bitmap to draw at the same density as the canvas.
     *
     * @param canvas The canvas to draw the bitmap to
     */
    public void draw(Canvas canvas) {
        draw(canvas, null);
    }

    /**
     * Draw the internal bitmap, with its top/left corner at (0,0), using the specified paint,
     * transformed by the current matrix.
     * <p>
     * Note: if the paint contains a maskfilter that generates a mask which extends beyond the
     * bitmap's original width/height (e.g. BlurMaskFilter), then the bitmap will be drawn as if it
     * were in a Shader with CLAMP mode. Thus the color outside of the original width/height will be
     * the edge color replicated.
     * <p>
     * If the bitmap and canvas have different densities, this function will take care of
     * automatically scaling the bitmap to draw at the same density as the canvas.
     *
     * @param canvas The canvas to draw the bitmap to
     * @param paint The paint used to draw the bitmap (may be null)
     */
    public void draw(Canvas canvas, Paint paint) {
        draw(canvas, 0, 0, paint);
    }

    /**
     * Draw the internal bitmap, with its top/left corner at (x,y), using the specified paint,
     * transformed by the current matrix.
     * <p>
     * Note: if the paint contains a maskfilter that generates a mask which extends beyond the
     * bitmap's original width/height (e.g. BlurMaskFilter), then the bitmap will be drawn as if it
     * were in a Shader with CLAMP mode. Thus the color outside of the original width/height will be
     * the edge color replicated.
     * <p>
     * If the bitmap and canvas have different densities, this function will take care of
     * automatically scaling the bitmap to draw at the same density as the canvas.
     *
     * @param canvas The canvas to draw the bitmap to
     * @param left The position of the left side of the bitmap being drawn
     * @param top The position of the top side of the bitmap being drawn
     * @param paint The paint used to draw the bitmap (may be null)
     */
    public void draw(Canvas canvas, int left, int top, Paint paint) {
        Instant before = Instant.now();
        /**
         * stride – The number of colors in pixels[] to skip between rows.
         *          Normally this value will be the same as the width of the bitmap,
         *          but it can be larger (or negative).
         */
        bitmap.setPixels(
                getPixels(native_skia_ptr),
                0,
                getStride(native_skia_ptr),
                0,
                0,
                width,
                height
        );
        canvas.drawBitmap(bitmap, left, top, paint);
        Instant after = Instant.now();
        Log.d(TAG, "draw: drawn in " + Duration.between(before, after).toMillis() + " milliseconds");
    }

    public void drawText(String line, int index, int count, float x, float y, TextPaint textPaint) {
        long paint = constructSkiaPaint(textPaint);
        long font = constructSkiaFont(textPaint);
        drawText(native_skia_ptr, line, index, count, x, y, paint, font);
    }

    public int getWidth() {
        return getWidth(native_skia_ptr);
    }

    public int getHeight() {
        return getHeight(native_skia_ptr);
    }

    public void clear(int color) {
        clear(native_skia_ptr, color);
    }

    public void clear(Paint background) {
        clear(background.getColor());
    }
}
