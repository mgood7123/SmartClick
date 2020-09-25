package screen.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import smallville7123.smartclick.R;

public class ImageAnalysisRecyclerViewAdapter extends
        RecyclerView.Adapter<ImageAnalysisRecyclerViewAdapter.MyViewHolder> {
    private LogUtils log = new LogUtils(this);

    BitmapView.RecordedFrames data = null;

    private ItemClickListener mClickListener;

    public void clearData() {
        if (data != null) data.clear();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout mainView;
        public TextView frame;
        public BitmapView bitmapView;
        public String text;
        public byte[] bitmapData;

        public MyViewHolder(ConstraintLayout v) {
            super(v);
            mainView = v;
            frame = v.findViewById(R.id.textView);
            bitmapView = v.findViewById(R.id.bitmapView);
        }
    }

    public void setData(final BitmapView.RecordedFrames data) {
        if (data != null) this.data = data.clone();
    }

    public void setData(final ImageAnalysisRecyclerViewAdapter adapter) {
        data = adapter.data;
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
    public void onBindViewHolder(final ImageAnalysisRecyclerViewAdapter.MyViewHolder holder, final int position) {

        log.logMethodNameWithClassName(this);
        // decompress memory to bitmap
        log.logWithClassName(this, "decompressing image");

        holder.text = (position+1) + "/" + getItemCount();
        holder.frame.setText(holder.text);

        holder.bitmapData = data.getByte(position);

        holder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null)
                    mClickListener.onItemClick(holder.bitmapData, holder.text);
            }
        });

        holder.bitmapView.setImageBitmap(holder.bitmapData);
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
        if (data == null) {
            log.logWithClassName(this, "data.size(): " + 0);
            return 0;
        }
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
        void onItemClick(byte[] memory, String text);
    }
}
