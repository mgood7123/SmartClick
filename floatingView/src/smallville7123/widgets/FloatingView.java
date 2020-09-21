package smallville7123.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import screen.utils.LogUtils;
import smallville7123.smartclick.R;

import static android.content.Context.WINDOW_SERVICE;


public class FloatingView extends FrameLayout {
    public View collapsedView;
    public View expandedView;
    public boolean expanded;
    public boolean fullscreenWhenExpanded = false;

    WindowManager windowManager;

    public final WindowManager.LayoutParams maximizedLayout = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
    );

    public final WindowManager.LayoutParams minimizedLayout = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
    );

    WindowManager.LayoutParams layout = minimizedLayout;

    LogUtils log = new LogUtils(this);
    
    int collapsedViewRes = -1;
    int expandedViewRes = -1;

    boolean attachedToWindowManager = false;

    String TAG = "FloatingView";

    public FloatingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);

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
        fullscreenWhenExpanded = a.getBoolean(R.styleable.FloatingView_fullscreenWhenExpanded, false);
        log.log("fullscreenWhenExpanded is " + fullscreenWhenExpanded);

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

        collapsedView.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = minimizedLayout.x;
                        initialY = minimizedLayout.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_UP:
                        //when the drag is ended switching the state of the widget
                        //
                        // TODO: do not change visibility when location has changed significantly
                        //  such as a drag from one area of the screen to another area of the screen
                        //

                        expand();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        //this code is helping the widget to move around the screen with fingers
                        minimizedLayout.x = initialX + (int) (event.getRawX() - initialTouchX);
                        minimizedLayout.y = initialY + (int) (event.getRawY() - initialTouchY);
                        updateWindowManagerLayout(minimizedLayout);
                        return true;
                }
                return false;
            }
        });

        expandedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapse();
            }
        });
    }

    public void collapse() {
        log.logMethodName();
        expandedView.setVisibility(View.GONE);
        updateWindowManagerLayout(minimizedLayout);
        collapsedView.setVisibility(View.VISIBLE);
        expanded = false;
    }

    public void expand() {
        log.logMethodName();
        collapsedView.setVisibility(View.GONE);
        log.log("fullscreenWhenExpanded is " + fullscreenWhenExpanded);
        if (attachedToWindowManager) {
            if (fullscreenWhenExpanded) updateWindowManagerLayout(maximizedLayout);
            else updateWindowManagerLayout(minimizedLayout);
        }
        expandedView.setVisibility(View.VISIBLE);
        expanded = true;
    }

    public WindowManager.LayoutParams getLayout() {
        return layout;
    }

    // default to the default layout if given layout is null
    public void attachToWindowManager(WindowManager.LayoutParams layout) {
        log.log("attaching to window manager with a layout of " + layout);
        if (layout == null) windowManager.addView(this, this.layout);
        else {
            windowManager.addView(this, layout);
            this.layout = layout;
        }
        attachedToWindowManager = true;
    }

    public void detachFromWindowManager() {
        log.log("detaching from window manager");
        windowManager.removeViewImmediate(this);
        attachedToWindowManager = false;
    }

    public void attachToWindowManager() {
        attachToWindowManager(null);
    }

    public void reattachToWindowManager() {
        detachFromWindowManager();
        attachToWindowManager();
    }

    // default to the default layout if given layout is null
    public void reattachToWindowManager(WindowManager.LayoutParams layout) {
        detachFromWindowManager();
        attachToWindowManager(layout);
    }

    // default to the default layout if given layout is null
    public void updateWindowManagerLayout(WindowManager.LayoutParams layout) {
        if (attachedToWindowManager) {
            log.log("updating window manager with a layout of " + layout);
            if (layout == null) windowManager.updateViewLayout(this, this.layout);
            else {
                windowManager.updateViewLayout(this, layout);
                this.layout = layout;
            }
        }
    }
}
