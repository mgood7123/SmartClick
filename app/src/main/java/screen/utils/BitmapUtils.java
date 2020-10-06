package screen.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.Nullable;
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
        int width = imageView.getWidth();
        int height = imageView.getHeight();
        final Bitmap tmp = Bitmap.createScaledBitmap(bitmap, width, height, false);
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
     * then creates a bitmap that is scaled to imageView width and height
     * and then sets imageView to the created bitmap, and then recycles the created bitmaps
     * <br>
     * <br>
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
        ByteArrayInputStream stream = new ByteArrayInputStream(imageData);
        Bitmap bitmap = BitmapFactory.decodeStream(stream);
        return bitmap;
    }

    /**
     * returns the opposite of {@link BitmapUtils#arraysDoNotMatch(byte[], byte[]) arraysDoNotMatch}
     */
    public static boolean arraysDoMatch(byte[] a, byte[] a2) {
        return !arraysDoMatch(a, a2);
    }

    /**
     *
     * compares the contents of array 1 with array 2
     * <br>
     * does the following, in the order specified:
     * <br>
     * 1. if array 1 is null, or if array 2 is null, return true
     * <br>
     * 2. the two arrays are not null, if the two arrays do not match in length, return true
     * <br>
     * 3. the two arrays are of equal length, if the two arrays do not match in contents, return true
     * <br>
     * 4. none of the above was true, return false
     */
    public static boolean arraysDoNotMatch(byte[] a, byte[] a2) {
        // 1. if array 1 is null, or if array 2 is null, return true
        if (a == null || a2 == null) return true;
        // 2. the two arrays are not null, if the two arrays do not match in length, return true
        if (a.length != a2.length) return true;
        // 3. the two arrays are of equal length, if the two arrays do not match in contents, return true
        for (int i = 0; i < a.length; i++) {
            if (a[i] != a2[i]) {
                return true;
            }
        }
        // 4. none of the above was true, return false
        return false;
    }
}