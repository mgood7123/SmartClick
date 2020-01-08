package screen.utils;

import android.view.OrientationEventListener;

public class OrientationChangeCallback extends OrientationEventListener {

    private final Variables variables;
    private final MediaProjectionHelper mediaProjectionHelper;
    private int mRotation;

    public OrientationChangeCallback(Variables variables, MediaProjectionHelper mediaProjectionHelper) {
        super(variables.activity);
        this.variables = variables;
        this.mediaProjectionHelper = mediaProjectionHelper;
    }

    @Override
    public void onOrientationChanged(int orientation) {
        final int rotation = variables.mDisplay.getRotation();
        if (rotation != mRotation) {
            mRotation = rotation;
            try {
                // clean up
                if (variables.mVirtualDisplay != null) variables.mVirtualDisplay.release();
                if (variables.mImageReader != null) variables.mImageReader.setOnImageAvailableListener(null, null);

                // re-create virtual display depending on device width / height
                mediaProjectionHelper.createVirtualDisplay();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
