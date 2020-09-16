package screen.utils;

import android.graphics.Bitmap;
import android.media.Image;
import android.media.ImageReader;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.Vector;

public class ImageAvailableListener implements ImageReader.OnImageAvailableListener {
    private final Variables variables;
    String TAG;
    long IMAGES_PRODUCED;
    int mWidth;
    int mHeight;

    boolean single;

    Vector<Bitmap> bitmapBuffer = new Vector<>();

    public ImageAvailableListener(Variables variables, int mWidth, int mHeight) {
        this.variables = variables;
        this.mWidth = mWidth;
        this.mHeight = mHeight;
    }

    @Override
    public void onImageAvailable(ImageReader reader) {
        if (!single) {
            Image image = null;
            Bitmap bitmap = null;

            try {
                image = reader.acquireLatestImage();
                if (image != null) {
                    Image.Plane[] planes = image.getPlanes();
                    ByteBuffer buffer = planes[0].getBuffer();
                    int pixelStride = planes[0].getPixelStride();
                    int rowStride = planes[0].getRowStride();
                    int rowPadding = rowStride - pixelStride * mWidth;

                    // create bitmap
                    bitmap = Bitmap.createBitmap(mWidth + rowPadding / pixelStride, mHeight, Bitmap.Config.ARGB_8888);
                    bitmap.copyPixelsFromBuffer(buffer);

                    // render bitmap
                    if (bitmapBuffer.size() == 10) bitmapBuffer.remove(0);
                    bitmapBuffer.add(bitmap.copy(bitmap.getConfig(), bitmap.isMutable()));
                    final Bitmap last = bitmapBuffer.lastElement();
                    long fileSizeInBytes = last.getAllocationByteCount();
                    // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
                    long fileSizeInKB = fileSizeInBytes / 1024;
                    // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
                    long fileSizeInMB = fileSizeInKB / 1024;
                    variables.log.errorNoStackTrace(
                            "bitmap resolution (width x height): " + last.getWidth() + "x" + last.getHeight() + ", size: " + fileSizeInMB + " MB (" + fileSizeInKB + " KB)");
                    variables.log.errorNoStackTrace(
                            "bitmap array length: " + bitmapBuffer.size() + ", size: " + fileSizeInMB*bitmapBuffer.size() + " MB (" + fileSizeInKB*bitmapBuffer.size() + " KB)");
                    if (variables.screenRecord) {

                    }
                    if (variables.activity != null) {
                        variables.activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                variables.imageView.setImageBitmap(last);
                            }
                        });
                    } else if (variables.service != null) {
                        ((FloatingViewService) variables.service).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                variables.imageView.setImageBitmap(last);
                            }
                        });
                    }

                    IMAGES_PRODUCED++;
                    Log.e(TAG, "captured image: " + IMAGES_PRODUCED);
                    if (variables.screenshot) {
                        new Thread() {
                            @Override
                            public void run() {
                                Log.e("TAG", "took screenshot");
                                variables.mediaProjectionHelper.stopScreenMirror();
                                variables.screenshot = false;
                                single = false;
                            }
                        }.start();
                        single = true;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bitmap != null) {
                    bitmap.recycle();
                }

                if (image != null) {
                    image.close();
                }
            }
        }
    }
}
