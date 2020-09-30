package screen.utils;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

class RecordedFrames<Type> implements Parcelable {
    private final String TAG = "RecordedFrames (" + getClass().getName() + "@" + Integer.toHexString(hashCode()) + ")";
    ArrayList<Type> frames = new ArrayList();
    int width = 0;
    int height = 0;

    static final String typeMismatchStr = "given parameter 'o' must be of type byte[] or of type Bitmap";
    static final String typeMismatchBitmapStr = "type 'frame' must be of type Bitmap or of type byte[]";
    static final String typeMismatchByteStr = "type 'frame' must be of type byte[] or of type Bitmap";
    static final String nullFrameStr = "frame cannot be null";
    final ClassCastException illegalCast = new ClassCastException(typeMismatchStr);
    final ClassCastException illegalBitmapCast = new ClassCastException(typeMismatchBitmapStr);
    final ClassCastException illegalByteCast = new ClassCastException(typeMismatchByteStr);
    final NullPointerException nullFrame = new NullPointerException(nullFrameStr);

    private boolean compressRecordedFrames = true;

    protected RecordedFrames(Parcel in) {
        width = in.readInt();
        height = in.readInt();
        compressRecordedFrames = in.readByte() != 0;
    }

    public RecordedFrames() {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeByte((byte) (compressRecordedFrames ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RecordedFrames> CREATOR = new Creator<RecordedFrames>() {
        @Override
        public RecordedFrames createFromParcel(Parcel in) {
            return new RecordedFrames(in);
        }

        @Override
        public RecordedFrames[] newArray(int size) {
            return new RecordedFrames[size];
        }
    };

    void add(Type frame) throws ClassCastException, NullPointerException {
        if (!(frame instanceof byte[]) && !(frame instanceof Bitmap)) throw illegalCast;
        if (frame == null) throw nullFrame;
        frames.add(frame);
    }

    public Bitmap getBitmap(int index) throws ClassCastException {
        Type frame;
        frame = frames.get(index);
        if (!(frame instanceof Bitmap)) throw illegalBitmapCast;
        if (frame == null) throw nullFrame;
        return (Bitmap) frame;
    }

    public byte[] getByte(int index) throws ClassCastException {
        Type frame;
        frame = frames.get(index);
        if (!(frame instanceof byte[])) throw illegalByteCast;
        if (frame == null) throw nullFrame;
        return (byte[]) frame;
    }

    void remove(int index) {
        frames.remove(0);
    }

    public boolean isCompressed() {
        return compressRecordedFrames;
    }

    public void setCompressRecordedFrames(boolean shouldCompress) {
        compressRecordedFrames = shouldCompress;
    }

    ArrayList<Type> getFrames() {
        return frames;
    }

    public int size() {
        return frames.size();
    }

    /**
     * if the frames are not compressed return a reference to this to save memory
     * otherwise return a copy of this
     */

    protected RecordedFrames<Type> clone() {
        if (!compressRecordedFrames) {
            return this;
        }

        int size = frames.size();
        if (size == 0) {
            RecordedFrames<Type> copy = new RecordedFrames();
            copy.compressRecordedFrames = compressRecordedFrames;
            return copy;
        }

        Object sample = frames.get(0);
        if (sample == null) throw nullFrame;
        if (sample instanceof byte[]) {
            RecordedFrames<byte[]> copy = new RecordedFrames();
            copy.compressRecordedFrames = compressRecordedFrames;
            copy.width = getWidth();
            copy.height = getHeight();
            copy.frames.ensureCapacity(size);
            copy.frames.add(((byte[]) sample).clone());
            for (int i = 1, framesSize = frames.size(); i < framesSize; i++) {
                byte[] frame = (byte[]) frames.get(i);
                if (frame == null) throw nullFrame;
                copy.frames.add(frame.clone());
            }
            return (RecordedFrames<Type>) copy;
        } else if (sample instanceof Bitmap) {
            // TODO: allow this under certain conditions
            //  this is currently disabled via if (!compressRecordedFrames) return this;
            Bitmap sampleFrame = (Bitmap) sample;
            RecordedFrames<Bitmap> copy = new RecordedFrames();
            copy.compressRecordedFrames = compressRecordedFrames;
            copy.frames.ensureCapacity(size);
            copy.frames.add(sampleFrame.copy(sampleFrame.getConfig(), sampleFrame.isMutable()));
            for (int i = 1, framesSize = frames.size(); i < framesSize; i++) {
                Bitmap frame = (Bitmap) frames.get(i);
                if (frame == null) throw nullFrame;
                copy.frames.add(frame.copy(frame.getConfig(), frame.isMutable()));
            }
            return (RecordedFrames<Type>) copy;
        } else {
            throw illegalCast;
        }
    }

    public void clear() throws NullPointerException, ClassCastException {
        int size = frames.size();
        if (size == 0) return;

        Object sample = frames.get(0);
        if (sample == null) throw nullFrame;
        if (sample instanceof byte[]) {
            frames.clear();
        } else if (sample instanceof Bitmap) {
            ((Bitmap) sample).recycle();
            for (int i = 1, framesSize = frames.size(); i < framesSize; i++) {
                Bitmap frame = (Bitmap) frames.get(i);
                if (frame == null) throw nullFrame;
                frame.recycle();
                Log.i(TAG, "clear: recycled");
            }
            frames.clear();
        } else {
            throw illegalCast;
        }
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getWidth() {
        return width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

}
