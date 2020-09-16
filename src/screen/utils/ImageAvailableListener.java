package screen.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.ImageReader;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
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
    String dir = "";

    boolean single;

    Vector<File> randomAccessFileBuffer = new Vector<File>();

    public ImageAvailableListener(Variables variables, int mWidth, int mHeight) {
        this.variables = variables;
        this.mWidth = mWidth;
        this.mHeight = mHeight;
        if (null != variables.activity) {
            dir = variables.activity.getCacheDir().getAbsolutePath();
        } else if (null != variables.service) {
            dir = variables.service.getCacheDir().getAbsolutePath();
        }
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

                    // remove bitmap
                    if (randomAccessFileBuffer.size() == 10) randomAccessFileBuffer.remove(0);

                    // holy crap we get 1 frame per second if we use disk io

                    // compress bitmap to memory
                        // create a memory file
                        File outFile = new File(dir + "/bitmap" + randomAccessFileBuffer.size());
                        // create an output stream to the memory file
                        FileOutputStream out = new FileOutputStream(outFile);
                        // copy bitmap into memory and compress
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        // close the output stream
                        out.close();
                        // add the file into the buffer
                        randomAccessFileBuffer.add(outFile);

//                    bitmapInfo(randomAccessFileBuffer, null, 1024*1024*1024, 30);
                    
                    // decompress memory to bitmap
                        // create an input stream from the memory file
                        FileInputStream in = new FileInputStream(randomAccessFileBuffer.lastElement());
                        // decompress bitmap into memory
                        final Bitmap last = BitmapFactory.decodeStream(in);
                        // close the input stream
                        in.close();

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
