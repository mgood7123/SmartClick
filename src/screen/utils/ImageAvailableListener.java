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

                        // copying directly appears to be faster than reading and decoding
                        Bitmap last = bitmap.copy(bitmap.getConfig(), bitmap.isMutable());

//                        bitmapInfo(last, 1L*1024*1024*1024, 60);
//                        bitmapInfo(last, 4L*1024*1024*1024, 60);

                        if (variables.screenRecord) {
                            //
                            // lower quality increases recording latency
                            // and thus decreases frame accuracy
                            // however allows for drastically longer durations of recording
                            //

                            // compress bitmap to memory
                            int size = variables.bitmapBuffer.size();
                            if (size == variables.max_bitmaps) {
                                variables.bitmapBuffer.remove(0);
                            }
                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//                            bitmapInfo(out, last.getWidth(), last.getHeight(), 1L*1024*1024*1024, 60);
//                            bitmapInfo(out, last.getWidth(), last.getHeight(), 4L*1024*1024*1024, 60);
                            variables.bitmapBuffer.add(out);
                        }

                        final Bitmap finalLast = last;
                        variables.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                variables.imageView.setImageBitmap(finalLast);
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

                } catch (Exception e) {
                    variables.log.errorWithClassName(this, e);
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
