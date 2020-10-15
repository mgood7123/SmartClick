package screen.utils;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import smallville7123.taggable.Taggable;

class BitmapViewState implements Parcelable {

    private final String TAG = "BitmapViewState (" + Taggable.getTag(this) + ")";
    public boolean preScaled;

    BitmapViewState() {};

    boolean isAllowedToScale = true;
    @Nullable byte[] cache;
    @Nullable Bitmap cacheDecompressed;
    @Nullable Bitmap bm;
    @Nullable Bitmap scaledbm;
    int bmw = Integer.MAX_VALUE;
    int bmh = Integer.MAX_VALUE;
    Rect src = new Rect(0,0,0,0);
    Rect dst = new Rect(0,0,0, 0);
    boolean recycleAfterUse;
    boolean setImmediately;
    int scaleMode;
    @Nullable RecordedFrames recordedFrames;
    @NonNull final Object recordingStateLock = new Object();
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
        scaledbm = in.readParcelable(Bitmap.class.getClassLoader());
        src = in.readParcelable(Rect.class.getClassLoader());
        dst = in.readParcelable(Rect.class.getClassLoader());
        preScaled = in.readByte() != 0;
        recycleAfterUse = in.readByte() != 0;
        setImmediately = in.readByte() != 0;
        scaleMode = in.readInt();
        bmw = in.readInt();
        bmh = in.readInt();
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
        dest.writeParcelable(scaledbm, flags);
        dest.writeParcelable(src, flags);
        dest.writeParcelable(dst, flags);
        dest.writeByte((byte) (preScaled ? 1 : 0));
        dest.writeByte((byte) (recycleAfterUse ? 1 : 0));
        dest.writeByte((byte) (setImmediately ? 1 : 0));
        dest.writeInt(scaleMode);
        dest.writeInt(bmw);
        dest.writeInt(bmh);
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
        private final String TAG = "Creator<BitmapViewState> (" + Taggable.getTag(this) + ")";
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
