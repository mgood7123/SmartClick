package com.mtsahakis.mediaprojectiondemo.screen.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.ImageReader;
import android.util.Log;
import android.widget.ImageView;

import java.nio.ByteBuffer;

public class ImageAvailableListener implements ImageReader.OnImageAvailableListener {
    private final Variables variables;
    String TAG;
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
                    final Bitmap finalBitmap = bitmap.copy(bitmap.getConfig(), bitmap.isMutable());
                    variables.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            variables.imageView.setImageBitmap(finalBitmap);
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
