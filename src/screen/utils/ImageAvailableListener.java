package screen.utils;

import android.graphics.Bitmap;
import android.media.Image;
import android.media.ImageReader;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class ImageAvailableListener implements ImageReader.OnImageAvailableListener {
    private final Variables variables;
    long IMAGES_PRODUCED;
    int mWidth;
    int mHeight;

    boolean single;

    public ImageAvailableListener(Variables variables, int mWidth, int mHeight) {
        this.variables = variables;
        this.mWidth = mWidth;
        this.mHeight = mHeight;
    }

    @Override
    public void onImageAvailable(ImageReader reader) {
        if (!variables.stop) {
            if (!single) {
                Image image = reader.acquireLatestImage();
                if (image != null) {
                    Image.Plane[] planes = image.getPlanes();
                    final ByteBuffer buffer = planes[0].getBuffer();
                    int pixelStride = planes[0].getPixelStride();
                    int rowStride = planes[0].getRowStride();
                    int rowPadding = rowStride - pixelStride * mWidth;

                    variables.videoMemoryWidth = mWidth + rowPadding / pixelStride;
                    variables.videoMemoryHeight = mHeight;

                    final Bitmap bitmap = Bitmap.createBitmap(
                            variables.videoMemoryWidth,
                            variables.videoMemoryHeight,
                            Bitmap.Config.ARGB_8888
                    );

                    bitmap.copyPixelsFromBuffer(buffer);

                    if (variables.screenRecord) {

                        // compress bitmap to memory
                        if (variables.videoMemory.size() == variables.max_bitmaps) {
                            variables.videoMemory.remove(0);
                        }
                        byte[] array = BitmapUtils.compress(bitmap, Bitmap.CompressFormat.JPEG, 40);
                        variables.log.log("converted stream to an array of length " + array.length);
                        variables.videoMemory.add(array);
                    }

                    variables.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            variables.bitmapView.setImageBitmap(bitmap);
                            bitmap.recycle();
                        }
                    });

                    IMAGES_PRODUCED++;
                    variables.log.logWithClassName(this, "number of images produced: " + IMAGES_PRODUCED);

                    if (variables.screenshot) {
                        new Thread() {
                            @Override
                            public void run() {
                                variables.log.logWithClassName(ImageAvailableListener.this, "took screenshot");
                                variables.mediaProjectionHelper.stopScreenMirror();
                                variables.screenshot = false;
                                single = false;
                            }
                        }.start();
                        single = true;
                    }
                }
                if (image != null) {
                    image.close();
                }
            }
        }
    }

    long divideBy1024(long what) {
        if (what >= 1024) return what / 1024;
        else return -1L;
    }

    long BytesToKB(long bytes) {
        return divideBy1024(bytes);
    }

    long BytesToMB(long bytes) {
        long kb = BytesToKB(bytes);
        return -1L == kb ? kb : divideBy1024(kb);
    }

    long BytesToGB(long bytes) {
        long mb = BytesToMB(bytes);
        return -1L == mb ? mb : divideBy1024(mb);
    }

    private void bitmapInfo(long fileSizeInBytes, long width, long height, long bytes, int fps) {
        long RAM_Bytes = bytes;
        long RAM_KB = BytesToKB(RAM_Bytes);
        long RAM_MB = BytesToMB(RAM_Bytes);
        long RAM_GB = BytesToGB(RAM_Bytes);

        long fileSize_Bytes = fileSizeInBytes;
        long fileSize_KB = BytesToKB(fileSize_Bytes);
        long fileSize_MB = BytesToMB(fileSize_Bytes);
        long fileSize_GB = BytesToGB(fileSize_Bytes);

        long BytesPerFPS = fps*fileSizeInBytes;
        long KBPerFPS = BytesToKB(BytesPerFPS);
        long MBPerFPS = BytesToMB(BytesPerFPS);
        long GBPerFPS = BytesToGB(BytesPerFPS);

        variables.log.errorNoStackTraceWithClassName(this,
                "single bitmap:" +
                        " resolution (width x height): " + width + "x" + height
                        + ", size: " + fileSize_MB + " MB (" + fileSize_KB + " KB)" +
                        " (" + fileSize_Bytes + " Bytes)"
        );
        variables.log.errorNoStackTraceWithClassName(this,
                "max amount of seconds recordable at " + fps + " FPS," +
                        " for " + RAM_GB + " GB of memory: " + RAM_Bytes/BytesPerFPS +
                        " (" + RAM_Bytes + "/" + BytesPerFPS + ")"
        );
    }

    private void bitmapInfo(Bitmap last, long bytes, int fps){
        bitmapInfo(last.getAllocationByteCount(), last.getWidth(), last.getHeight(), bytes, fps);
    }

    private void bitmapInfo(ByteArrayOutputStream last, long width, long height, long bytes, int fps){
        bitmapInfo(last.size(), width, height, bytes, fps);
    }
}
