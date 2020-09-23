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
    Vector<byte[]> data = new Vector<>();
    int dataWidth;
    int dataHeight;
    private ItemClickListener mClickListener;

    public void clearData() {
        data.clear();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout mainView;
        public TextView frame;
        public ImageView imageView;
        public String text;
        public byte[] bitmapData;

        public MyViewHolder(ConstraintLayout v) {
            super(v);
            mainView = v;
            frame = v.findViewById(R.id.textView);
            imageView = v.findViewById(R.id.imageView);
        }
    }

    public void setData(final Vector<ByteArrayOutputStream> data, int width, int height) {
        dataWidth = width;
        dataHeight = height;
        // duplicate the video memory
        int bufferSize = data.size();
        log.logWithClassName(this, "data.size(): " + bufferSize);
        this.data.setSize(bufferSize);
        for (int i = 0; i < data.size(); i++) {
            ByteArrayOutputStream buf = log.errorAndThrowIfNullWithClass(this, data.get(i), "data at index " + i + " is null");
            byte[] byteArray = buf.toByteArray();
            this.data.set(i, byteArray);
        }
        int dataSize = this.data.size();
        log.logWithClassName(this, "this.data.size(): " + dataSize);
    }

    public void setData(final ImageAnalysisRecyclerViewAdapter adapter) {
        data = adapter.data;
        dataWidth = adapter.dataWidth;
        dataHeight = adapter.dataHeight;
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

        holder.bitmapData = data.get(position);

        holder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null)
                    mClickListener.onItemClick(holder.bitmapData, holder.text);
            }
        });

        holder.imageView.post(new Runnable() {
            @Override
            public void run() {
                // decompress memory to bitmap
                log.logWithClassName(ImageAnalysisRecyclerViewAdapter.this, "decompressing image");
                log.errorAndThrowIfNull(holder.bitmapData);
                Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(holder.bitmapData));
                log.errorAndThrowIfNull(bitmap);
                holder.imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, holder.imageView.getWidth(), holder.imageView.getHeight(), false));
                log.logWithClassName(ImageAnalysisRecyclerViewAdapter.this, "decompressed image");
            }
        });

        // set image view to a empty bitmap
        holder.imageView.setImageBitmap(Bitmap.createBitmap(dataWidth, dataHeight, Bitmap.Config.ARGB_8888));
        log.log("created empty bitmap");

//        Bitmap image = BitmapFactory.decodeStream(new ByteArrayInputStream(buf));
//
//        // TODO: resize bitmap
//
//        holder.imageView.setImageBitmap(image);
//
//        log.logWithClassName(this, "decompressed image");
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
        void onItemClick(byte[] memory, String text);
    }
}
