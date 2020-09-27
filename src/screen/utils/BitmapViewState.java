package screen.utils;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.Nullable;

class BitmapViewState implements Parcelable {

    private final String TAG = "BitmapViewState (" + getClass().getName() + "@" + Integer.toHexString(hashCode()) + ")";

    BitmapViewState() {};

    boolean isAllowedToScale = true;
    byte[] cache;
    Bitmap cacheDecompressed;
    @Nullable Bitmap bm;
    Rect src = new Rect(0,0,0,0);
    Rect dst = new Rect (0,0,0, 0);
    boolean recycleAfterUse;
    boolean setImmediately;
    int scaleMode;
    RecordedFrames recordedFrames = null;
    Object recordingStateLock = new Object();
    int recordingState = BitmapView.RecordingState.stopped;
    Bitmap.CompressFormat compressionFormat = Bitmap.CompressFormat.JPEG;

    /**
     * the higher the quality, the slower the compression
     * <br>
     * must be a range from 0 to 100
     */
    int compressionQuality = 40;


    int maxRecordingFrames = 200;

    protected BitmapViewState(Parcel in) {
        isAllowedToScale = in.readByte() != 0;
        cache = in.createByteArray();
        cacheDecompressed = in.readParcelable(Bitmap.class.getClassLoader());
        bm = in.readParcelable(Bitmap.class.getClassLoader());
        src = in.readParcelable(Rect.class.getClassLoader());
        dst = in.readParcelable(Rect.class.getClassLoader());
        recycleAfterUse = in.readByte() != 0;
        setImmediately = in.readByte() != 0;
        scaleMode = in.readInt();
        recordedFrames = in.readParcelable(RecordedFrames.class.getClassLoader());
        recordingState = in.readInt();
        compressionQuality = in.readInt();
        maxRecordingFrames = in.readInt();
        Log.i(TAG, "BitmapViewState: read from parcel");
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isAllowedToScale ? 1 : 0));
        dest.writeByteArray(cache);
        dest.writeParcelable(cacheDecompressed, flags);
        dest.writeParcelable(bm, flags);
        dest.writeParcelable(src, flags);
        dest.writeParcelable(dst, flags);
        dest.writeByte((byte) (recycleAfterUse ? 1 : 0));
        dest.writeByte((byte) (setImmediately ? 1 : 0));
        dest.writeInt(scaleMode);
        dest.writeParcelable(recordedFrames, flags);
        dest.writeInt(recordingState);
        dest.writeInt(compressionQuality);
        dest.writeInt(maxRecordingFrames);
        Log.i(TAG, "writeToParcel: wrote to parcel");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BitmapViewState> CREATOR = new Creator<BitmapViewState>() {
        private final String TAG = "Creator<BitmapViewState> (" + getClass().getName() + "@" + Integer.toHexString(hashCode()) + ")";
        @Override
        public BitmapViewState createFromParcel(Parcel in) {
            Log.i(TAG, "createFromParcel: creating from parcel");
            return new BitmapViewState(in);
        }

        @Override
        public BitmapViewState[] newArray(int size) {
            Log.i(TAG, "createFromParcel: creating array");
            return new BitmapViewState[size];
        }
    };
}
