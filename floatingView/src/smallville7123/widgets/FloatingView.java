package smallville7123.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import screen.utils.LogUtils;
import smallville7123.smartclick.R;


public class FloatingView extends FrameLayout {
    public View collapsedView;
    public View expandedView;
    public boolean expanded;

    LogUtils log = new LogUtils(this);
    
    int collapsedViewRes = -1;
    int expandedViewRes = -1;

    String TAG = "FloatingView";

    public FloatingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        log.logMethodName();

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.FloatingView,
                0, 0);

        collapsedViewRes = a.getResourceId(R.styleable.FloatingView_collapsedLayout, 0);
        if (collapsedViewRes == 0) {
            a.recycle();
            log.errorAndThrow("collapsed view could not be found");
        }

        expandedViewRes = a.getResourceId(R.styleable.FloatingView_expandedLayout, 0);
        if (expandedViewRes == 0) {
            a.recycle();
            log.errorAndThrow("expanded view could not be found");
        }

        expanded = a.getBoolean(R.styleable.FloatingView_expanded, false);

        a.recycle();

        collapsedView = inflate(context, collapsedViewRes, null);
        log.errorAndThrowIfNull(collapsedView,  "collapsed view could not be inflated");
        log.log("collapsed view inflated successfully");

        addView(collapsedView);

        expandedView = inflate(context, expandedViewRes, null);
        log.errorAndThrowIfNull(expandedView,  "expanded view could not be inflated");
        log.log("expanded view inflated successfully");

        addView(expandedView);

        if (expanded) {
            expand();
        } else {
            collapse();
        }
    }

    public void collapse() {
        expandedView.setVisibility(View.GONE);
        collapsedView.setVisibility(View.VISIBLE);
        expanded = false;
    }

    public void expand() {
        collapsedView.setVisibility(View.GONE);
        expandedView.setVisibility(View.VISIBLE);
        expanded = true;
    }
}
