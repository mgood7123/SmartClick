package screen.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Vector;

public class ImageAnalysisRecyclerViewAdapter extends
        RecyclerView.Adapter<ImageAnalysisRecyclerViewAdapter.MyViewHolder> {
    private LogUtils log = new LogUtils(this);
    private Vector<byte[]> data = new Vector<>();

    public void clearData() {
        data.clear();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView imageView;
        public MyViewHolder(ImageView v) {
            super(v);
            imageView = v;
        }
    }

    public void setData(final Vector<ByteArrayOutputStream> data) {
        // duplicate the video memory
        int bufferSize = data.size();
        log.logWithClassName(this, "data.size(): " + bufferSize);
        this.data.setSize(bufferSize);
        for (int i = 0; i < data.size(); i++) {
            ByteArrayOutputStream buf = log.errorAndThrowIfNullWithClass(this, data.get(i), "data at index " + i + " is null");
            byte[] byteArray = buf.toByteArray();
            this.data.set(i, byteArray);
        }
        int dataBufferSize = this.data.size();
        log.logWithClassName(this, "this.data.size(): " + dataBufferSize);
    }

    @Override
    public ImageAnalysisRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        log.logMethodNameWithClassName(this);
        return new ImageAnalysisRecyclerViewAdapter.MyViewHolder(new ImageView(parent.getContext()));
    }

    @Override
    public boolean onFailedToRecycleView(@NonNull final ImageAnalysisRecyclerViewAdapter.MyViewHolder holder) {
        log.logMethodNameWithClassName(this);
        return super.onFailedToRecycleView(holder);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull final RecyclerView recyclerView) {
        log.logMethodNameWithClassName(this);
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(ImageAnalysisRecyclerViewAdapter.MyViewHolder holder, int position) {
        log.logMethodNameWithClassName(this);
        // decompress memory to bitmap
        log.logWithClassName(this, "decompressing image");
        byte[] buf = data.get(position);
        Bitmap image = BitmapFactory.decodeStream(new ByteArrayInputStream(buf));
        holder.imageView.setImageBitmap(image);
        log.logWithClassName(this, "decompressed image");
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull final RecyclerView recyclerView) {
        log.logMethodNameWithClassName(this);
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull final ImageAnalysisRecyclerViewAdapter.MyViewHolder holder) {
        log.logMethodNameWithClassName(this);
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull final ImageAnalysisRecyclerViewAdapter.MyViewHolder holder) {
        log.logMethodNameWithClassName(this);
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewRecycled(@NonNull final ImageAnalysisRecyclerViewAdapter.MyViewHolder holder) {
        log.logMethodNameWithClassName(this);
        super.onViewRecycled(holder);
    }

    @Override
    public long getItemId(final int position) {
        log.logMethodNameWithClassName(this);
        return super.getItemId(position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        int bufferSize = data.size();
        log.logWithClassName(this, "data.size(): " + bufferSize);
        return bufferSize;
    }
}
