package screen.utils;

import android.graphics.Bitmap;
import android.media.Image;
import android.media.ImageReader;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Vector;

public class ImageAvailableListener implements ImageReader.OnImageAvailableListener {
    private final Variables variables;
    String TAG;
    long IMAGES_PRODUCED;
    int mWidth;
    int mHeight;

    boolean single;

    Vector<File> randomAccessFileBuffer = new Vector<File>();

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

                        if (variables.screenRecord) {
                            if (randomAccessFileBuffer.size() == 120)
                                randomAccessFileBuffer.remove(0);

                            // compress bitmap to memory
                            // create a memory file
                            File outFile = new File(variables.cacheDir + "/bitmap" + randomAccessFileBuffer.size());
                            // create an output stream to the memory file
                            FileOutputStream out = new FileOutputStream(outFile);
                            // copy bitmap into memory and compress
                            //
                            // lower quality increases recording latency
                            // and thus decreases frame accuracy
                            // however allows for longer durations of recording
                            //
                            // keep at 100 for now for max recording speed
                            //
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            // close the output stream
                            out.close();
                            // add the file into the buffer
                            randomAccessFileBuffer.add(outFile);

                            // performance can be slightly improved by not reading this back in
                            // and instead copying directly as above

                            // kept for reference

//                            // decompress memory to bitmap
//                            // create an input stream from the memory file
//                            FileInputStream in = new FileInputStream(randomAccessFileBuffer.lastElement());
//                            // decompress bitmap into memory
//                            last = BitmapFactory.decodeStream(in);
//                            // close the input stream
//                            in.close();
                        }

                        final Bitmap finalLast = last;
                        variables.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                variables.imageView.setImageBitmap(finalLast);
                            }
                        });

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

    private void bitmapInfo(final Vector<RandomAccessFile> randomAccessFileBuffer, RandomAccessFile last, long bytes, int fps) {
//        if (last == null) last = randomAccessFileBuffer.lastElement();
//        long fileSizeInBytes = 0;
//        try {
//            fileSizeInBytes = last.length();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        long RAM_Bytes = bytes;
//        long RAM_KB = BytesToKB(RAM_Bytes);
//        long RAM_MB = BytesToMB(RAM_Bytes);
//        long RAM_GB = BytesToGB(RAM_Bytes);
//
//        long fileSize_Bytes = fileSizeInBytes;
//        long fileSize_KB = BytesToKB(fileSize_Bytes);
//        long fileSize_MB = BytesToMB(fileSize_Bytes);
//        long fileSize_GB = BytesToGB(fileSize_Bytes);
//
//        long BytesPerFPS = fps*fileSizeInBytes;
//        long KBPerFPS = BytesToKB(BytesPerFPS);
//        long MBPerFPS = BytesToMB(BytesPerFPS);
//        long GBPerFPS = BytesToGB(BytesPerFPS);
//
//        long bufferSize = randomAccessFileBuffer.size();
//
//        variables.log.errorNoStackTrace(
//                "bitmap array length: " + bufferSize + "," +
//                        " size: " + fileSize_MB*bufferSize + " MB" +
//                        " (" + fileSize_KB*bufferSize + " KB)" +
//                        " (" + fileSize_Bytes*bufferSize + " Bytes)"
//
//        );
//        variables.log.errorNoStackTrace(
//                "single bitmap:" +
//                        " resolution (width x height): " + last.getWidth() + "x" + last.getHeight()
//                        + ", size: " + fileSize_MB + " MB (" + fileSize_KB + " KB)" +
//                        " (" + fileSize_Bytes + " Bytes)"
//        );
//        variables.log.errorNoStackTrace(
//                "max amount of seconds recordable at " + fps + " FPS," +
//                        " for " + RAM_GB + " GB of memory: " + RAM_Bytes/BytesPerFPS +
//                        " (" + RAM_Bytes + "/" + BytesPerFPS + ")"
//        );
    }
}
