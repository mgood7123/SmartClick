package screen.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Vector;

import smallville7123.smartclick.R;

public class ImageAnalysisRecyclerViewAdapter extends
        RecyclerView.Adapter<ImageAnalysisRecyclerViewAdapter.MyViewHolder> {

    private LogUtils log = new LogUtils(this);
    private Vector<byte[]> data = new Vector<>();
    private ItemClickListener mClickListener;

    public void clearData() {
        data.clear();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView frame;
        public ImageView imageView;

        public MyViewHolder(ConstraintLayout v) {
            super(v);
            frame = v.findViewById(R.id.frameNumber);
            imageView = v.findViewById(R.id.ImageViewItem);
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
        return new ImageAnalysisRecyclerViewAdapter.MyViewHolder(
                (ConstraintLayout) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recyclerview_item, parent, false)
        );
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

        holder.frame.setText((position+1) + "/" + getItemCount());

        final byte[] buf = data.get(position);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) mClickListener.onItemClick(buf);
            }
        });

        Bitmap image = BitmapFactory.decodeStream(new ByteArrayInputStream(buf));

        // TODO: resize bitmap

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

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(byte[] memory);
    }}
