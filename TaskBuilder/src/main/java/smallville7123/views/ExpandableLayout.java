package smallville7123.views;

// based on:
// https://github.com/mlkt12/android-expandable-layout/blob/master/app/src/main/java/com/mlkt/development/expandablelayout/ExpandableLayout.java


import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import smallville7123.layoututils.LayoutUtils;

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
    int textSize;
    int textColor;
    boolean expanded;
    Drawable background;
    int chevronColor;

    void getAttributeParameters(Context context, AttributeSet attrs, Resources.Theme theme) {
        if (attrs != null) {
            TypedArray attributes = theme.obtainStyledAttributes(attrs, R.styleable.ExpandableLayout, 0, 0);
            text = attributes.getString(R.styleable.ExpandableLayout_android_text);
            textSize = LayoutUtils.getTextSizeAttributesSuitableForTextView(attributes, R.styleable.ExpandableLayout_android_textSize);
            textColor = attributes.getColor(R.styleable.ExpandableLayout_android_textColor, Color.WHITE);
            expanded = attributes.getBoolean(R.styleable.ExpandableLayout_android_state_expanded, false);
            background = attributes.getDrawable(R.styleable.ExpandableLayout_android_background);
            chevronColor = attributes.getColor(R.styleable.ExpandableLayout_chevron_color, -1);
            attributes.recycle();
        }
    }

    @Override
    public void setBackground(final Drawable background) {
        // no background
    }

    @Override
    public void setBackgroundResource(final int resid) {
        // no background
    }

    @Override
    public void setBackgroundColor(final int color) {
        // no background
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
        LayoutUtils.setTextSizeAttributesSuitableForTextView(title, textSize);
        title.setTextColor(textColor);
        chevron = root.findViewById(R.id.chevron);
        chevron.setTag(Internal);
        chevron.setImageResource(R.drawable.ic_chevron);
        if (chevronColor != -1) chevron.setColorFilter(chevronColor);

        final LinearLayout header = root.findViewById(R.id.header);
        header.setBackgroundResource(R.drawable.expandable_ripple);
        header.setTag(Internal);
        final LinearLayout headerBackground = root.findViewById(R.id.header_background);
        if (background == null) {
            headerBackground.setBackgroundResource(R.drawable.expandable_ripple_background);
        } else {
            headerBackground.setBackground(background);
        }
        headerBackground.setTag(Internal);
        headerBackground.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int vv = contentLayout.getVisibility();
                contentLayout.setVisibility(vv == VISIBLE ? GONE : VISIBLE);
                chevron.setRotation(vv == VISIBLE ? CHEVRON_POSITION_DOWN : CHEVRON_POSITION_UP);
            }
        });
        contentLayout = root.findViewById(R.id.content);
        contentLayout.setTag(Internal);
        if (!expanded) contentLayout.setVisibility(GONE);
        chevron.setRotation(expanded ? CHEVRON_POSITION_UP : CHEVRON_POSITION_DOWN);
        addView(root, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void addContent(final View v, int index, ViewGroup.LayoutParams params) {
        if (!expanded) contentLayout.setVisibility(GONE);
        chevron.setRotation(expanded ? CHEVRON_POSITION_UP : CHEVRON_POSITION_DOWN);

        // uncomment this if standAlone should be used
//        contentLayout.addView(v, index, params);

        // comment this if standAlone should be used
        TaskBuilder.addViewInternal(getContext(), contentLayout, v, params);

    }

    String TAG = "ExpandableLayout";

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        Object tag = child.getTag();
        if (tag instanceof Internal) {
            Log.d(TAG, "addView() called with INTERNAL: child = [" + child + "], index = [" + index + "], params = [" + params + "]");
            super.addView(child, index, params);
        } else {
            Log.d(TAG, "addView() called with EXTERNAL: child = [" + child + "], index = [" + index + "], params = [" + params + "]");
            // add items to linear layout
            addContent(child, index, params);
        }
    }
}