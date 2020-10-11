package smallville7123.views;

// based on:
// https://github.com/mlkt12/android-expandable-layout/blob/master/app/src/main/java/com/mlkt/development/expandablelayout/ExpandableLayout.java


import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class ExpandableLayout extends LinearLayout {

    public ExpandableLayout(Context context) {
        super(context);
        init(context, null);
    }

    public ExpandableLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context, attributeSet);
    }

    public ExpandableLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public ExpandableLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private ImageView chevron;
    private TextView title;
    private LinearLayout contentLayout;

    private static final int CHEVRON_POSITION_UP = 270;
    private static final int CHEVRON_POSITION_DOWN = 90;

    private static class Internal {}
    Internal Internal = new Internal();

    String text;
    Float textSize;
    int textColor;

    void getAttributeParameters(Context context, AttributeSet attrs, Resources.Theme theme) {
        if (attrs != null) {
            TypedArray attributes = theme.obtainStyledAttributes(attrs, R.styleable.ExpandableLayout, 0, 0);
            text = attributes.getString(R.styleable.ExpandableLayout_android_text);
            textSize = attributes.getDimension(R.styleable.ExpandableLayout_android_textSize, 20.0f);
            textColor = attributes.getColor(R.styleable.ExpandableLayout_android_textColor, Color.BLACK);
            attributes.recycle();
        }
    }

    private void init(Context context, AttributeSet attrs) {
        getAttributeParameters(context, attrs, context.getTheme());
        LinearLayout root = (LinearLayout) inflate(context, R.layout.item_expandable, null);
        root.findViewById(R.id.layout).setTag(Internal);
        root.setTag(Internal);
        root.setOrientation(VERTICAL);
        title = root.findViewById(R.id.title);
        title.setTag(Internal);
        if (text != null) title.setText(text);
        title.setTextSize(textSize);
        title.setTextColor(textColor);
        chevron = root.findViewById(R.id.chevron);
        chevron.setTag(Internal);
        chevron.setImageResource(R.drawable.ic_chevron);

        LinearLayout header = root.findViewById(R.id.header);
        header.setBackgroundResource(R.drawable.expandable_ripple_bg);
        header.setTag(Internal);
        header.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int vv = contentLayout.getVisibility();
                contentLayout.setVisibility(vv == VISIBLE ? GONE : VISIBLE);
                chevron.setRotation(vv == VISIBLE ? CHEVRON_POSITION_DOWN : CHEVRON_POSITION_UP);
            }
        });
        contentLayout = root.findViewById(R.id.content);
        contentLayout.setTag(Internal);
        contentLayout.setVisibility(GONE);
        chevron.setRotation(CHEVRON_POSITION_DOWN);
        addView(root, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void addContent(View v, int index, ViewGroup.LayoutParams params) {
        contentLayout.setVisibility(GONE);
        chevron.setRotation(CHEVRON_POSITION_DOWN);
        contentLayout.addView(v, index, params);
    }

    String TAG = "ExpandableLayout";

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        Object tag = child.getTag();
        if (tag instanceof Internal) {
            Log.d(TAG, "addView() called with INTERNAL: child = [" + child + "], index = [" + index + "], params = [" + params + "]");
            super.addView(child, index, params);
        } else {
            child.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick() called with: v = [" + v + "]");
                    // this will reparent, we do not want that
                    // views_TaskList.addView(v, v.getLayoutParams());
                }
            });
            Log.d(TAG, "addView() called with EXTERNAL: child = [" + child + "], index = [" + index + "], params = [" + params + "]");
            // add items to linear layout
            addContent(child, index, params);
        }
    }
}