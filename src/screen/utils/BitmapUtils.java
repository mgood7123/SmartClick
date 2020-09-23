package screen.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class BitmapUtils {

    // TODO: correct documentation

    /**
     * creates a bitmap that is scaled to imageView width and height, from bitmap
     * optionally recycling the used bitmap
     *
     * @param imageView the ImageView to scale the bitmap to
     * @param bitmap the Bitmap to scale
     * @param recycleAfterUse if true, recycle() is invoked on the bitmap param if the scaling was
     *                       successful
     * @return the scaled bitmap, or null if the bitmap could not be scaled
     */
    @Nullable
    public static Bitmap scaleBitmap(@Nullable final ImageView imageView, @Nullable final Bitmap bitmap, final boolean recycleAfterUse) {
        if (imageView == null || bitmap == null) return null;
        final Bitmap tmp = Bitmap.createScaledBitmap(bitmap, imageView.getWidth(), imageView.getHeight(), false);
        if (recycleAfterUse) bitmap.recycle();
        return tmp;
    }

    /**
     * creates a bitmap that is scaled to imageView width and height, from bitmap
     * and then sets imageView to the created bitmap
     *
     * does nothing if either imageView or bitmap are null
     *
     * @param imageView the ImageView to contain the contents of bitmap
     * @param bitmap the Bitmap to be the contents of imageView
     * @param recycleAfterUse if true, recycle() is invoked on the bitmap param if the scaling was
     *                       successful
     */
    public static void setBitmap(@Nullable final ImageView imageView, @Nullable Bitmap bitmap, final boolean recycleAfterUse) {
        if (imageView != null && bitmap != null) {
            final Bitmap tmp = scaleBitmap(imageView, bitmap, recycleAfterUse);
            if (tmp != null) {
                imageView.setImageBitmap(tmp);
            }
        }
    }

    /**
     * decompresses imageData into a bitmap
     * then creates a bitmap that is scaled to imageView width and height, from bitmap
     * and then sets imageView to the created bitmap, and then recycles the decompressed bitmap
     *
     * does nothing if either imageView or imageData are null
     *
     * @param imageView the ImageView to contain the contents of imageData
     * @param imageData the Bitmap to be the contents of imageView
     */
    public static void setBitmap(@Nullable final ImageView imageView, @Nullable final byte[] imageData) {
        setBitmap(imageView, decompress(imageData), true);
    }

    /**
     * decompresses imageData into a bitmap
     * then creates a bitmap that is scaled to imageView width and height, from bitmap
     * and then sets imageView to the created bitmap, and then recycles the created bitmaps
     *
     * does nothing if either imageView or config is null
     *
     * @param imageView the ImageView to contain the contents of imageData
     */
    public static void setBlankBitmap(@Nullable final ImageView imageView, final int width, final int height, @Nullable final Bitmap.Config config) {
        if (imageView != null && config != null) {
            Bitmap bitmap = Bitmap.createBitmap(width, height, config);
            imageView.setImageBitmap(bitmap);
            // we dont need bitmap anymore, garbage collect it
            bitmap.recycle();
        }
    }

    public static byte[] createCompressedBitmap(final int width, final int height, final Bitmap.Config config, final ByteBuffer byteBuffer, final Bitmap.CompressFormat compressFormat, final int quality) {
        if (byteBuffer == null) return null;
        final Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        bitmap.copyPixelsFromBuffer(byteBuffer);
        return compress(bitmap, compressFormat, quality, false);
    }

    public static byte[] createCompressedBitmap(final Bitmap bitmap, final int width, final int height, final Bitmap.Config config, final ByteBuffer byteBuffer, final Bitmap.CompressFormat compressFormat, final int quality) {
        if (byteBuffer == null) return null;
        bitmap.copyPixelsFromBuffer(byteBuffer);
        return compress(bitmap, compressFormat, quality, false);
    }

    public static byte[] compress(final Bitmap bitmap, final Bitmap.CompressFormat compressFormat, final int quality) {
        return compress(bitmap, compressFormat, quality, false);
    }

    public static byte[] compress(final Bitmap bitmap, final Bitmap.CompressFormat compressFormat, final int quality, final boolean recycleAfterCompression) {
        if (bitmap == null) return null;
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(compressFormat, quality, stream);
        if (recycleAfterCompression) bitmap.recycle();
        return stream.toByteArray();
    }

    /**
     * decompresses imageData into a bitmap
     * @param imageData image data, obtained by calling compress on a bitmap
     * @return The decoded bitmap, or null if the image data could not be decoded.
     */
    public static Bitmap decompress(final byte[] imageData) {
        if (imageData == null) return null;
        return BitmapFactory.decodeStream(new ByteArrayInputStream(imageData));
    }

    public static boolean arraysMatch(byte[] a, byte[] a2) {
        if (a.length != a2.length) {
            return false;
        }
        for (int i = 0; i < a.length; i++)
            if (a[i] != a2[i]) {
                return false;
            }
        return true;
    }

}