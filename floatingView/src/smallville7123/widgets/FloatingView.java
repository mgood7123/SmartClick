package smallville7123.widgets;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import smallville7123.smartclick.R;

import static android.content.Context.WINDOW_SERVICE;
import static org.junit.Assert.assertNotNull;


@RequiresApi(api = Build.VERSION_CODES.O)
public class FloatingView extends FrameLayout {
    private View collapsedView;

    public View getCollapsedView() {
        return collapsedView;
    }

    private View expandedView;

    public View getExpandedView() {
        return expandedView;
    }

    private boolean expanded;

    public boolean isExpanded() {
        return expanded;
    }

    public boolean fullscreenWhenExpanded;

    private WindowManager windowManager;

    public final WindowManager.LayoutParams maximizedLayout = new WindowManager.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
            PixelFormat.TRANSLUCENT
    );

    public final WindowManager.LayoutParams minimizedLayout = new WindowManager.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
            PixelFormat.TRANSLUCENT
    );

    private WindowManager.LayoutParams layout = minimizedLayout;

    private LogUtils log = new LogUtils(this);

    private int collapsedViewRes = -1;

    public int getCollapsedViewRes() {
        return collapsedViewRes;
    }

    private int expandedViewRes = -1;

    public int getExpandedViewRes() {
        return expandedViewRes;
    }

    private boolean attachedToWindowManager;

    public boolean isAttachedToWindowManager() {
        return attachedToWindowManager;
    }

    private String TAG = "FloatingView";

    private AttributeSet attributes;

    public AttributeSet getAttributes() {
        return attributes;
    }

    public FloatingView(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);

        windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);

        log.logMethodName();

        attributes = attrs;

        final TypedArray a = context.getTheme().obtainStyledAttributes(
                attributes,
                R.styleable.FloatingView,
                0, 0
        );

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
        a.recycle();

        reloadCollapsedView(context);
        reloadExpandedView(context);

        if (expanded) {
            expand();
        } else {
            collapse();
        }
    }

    public void reloadCollapsedView(@NonNull Context context) {
        if (collapsedView != null) {
            removeView(collapsedView);
            collapsedView = null;
        }
        collapsedView = log.errorAndThrowIfNull(View.inflate(context, collapsedViewRes, null),  "collapsed view could not be inflated");

        setCollapsedViewOnTouchListener();

        addView(collapsedView);
    }

    public void reloadExpandedView(@NonNull Context context) {
        if (expandedView != null) {
            removeView(expandedView);
            expandedView = null;
        }

        expandedView = log.errorAndThrowIfNull(View.inflate(context, expandedViewRes, null),  "expanded view could not be inflated");

        setExpandedViewOnClickListener();

        addView(expandedView);
    }

    public void collapse() {
        expandedView.setVisibility(View.GONE);
        updateWindowManagerLayout(minimizedLayout);
        collapsedView.setVisibility(View.VISIBLE);
        expanded = false;
    }

    public void expand() {
        collapsedView.setVisibility(View.GONE);
        if (fullscreenWhenExpanded) updateWindowManagerLayout(maximizedLayout);
        else updateWindowManagerLayout(minimizedLayout);
        expandedView.setVisibility(View.VISIBLE);
        expanded = true;
    }

    public WindowManager.LayoutParams getLayout() {
        return layout;
    }

    // default to the default layout if given layout is null
    public void attachToWindowManager(final WindowManager.LayoutParams layout) {
        if (!attachedToWindowManager) {
            if (layout == null) windowManager.addView(this, this.layout);
            else {
                windowManager.addView(this, layout);
                this.layout = layout;
            }
            attachedToWindowManager = true;
        }
    }

    public void detachFromWindowManager() {
        if (attachedToWindowManager) {
            windowManager.removeViewImmediate(this);
            attachedToWindowManager = false;
        }
    }

    public void attachToWindowManager() {
        attachToWindowManager(null);
    }

    public void reattachToWindowManager() {
        detachFromWindowManager();
        attachToWindowManager();
    }

    // default to the default layout if given layout is null
    public void reattachToWindowManager(final WindowManager.LayoutParams layout) {
        detachFromWindowManager();
        attachToWindowManager(layout);
    }

    // default to the default layout if given layout is null
    public void updateWindowManagerLayout(final WindowManager.LayoutParams layout) {
        if (attachedToWindowManager) {
            if (layout == null) windowManager.updateViewLayout(this, this.layout);
            else {
                windowManager.updateViewLayout(this, layout);
                this.layout = layout;
            }
        }
    }

    @Override
    protected void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        log.logMethodName();
        reloadResources();
    }

    public void reloadResources() {
        log.logMethodName();

        // cache internal variables

        boolean cacheexpanded = expanded;
        boolean cachefullscreenWhenExpanded = fullscreenWhenExpanded;
        WindowManager.LayoutParams cachelayout = layout;
        boolean cacheattachedToWindowManager = attachedToWindowManager;

        detachFromWindowManager();

        // cache external variables
        Bundle state = new Bundle();
        onSaveState.run(state);

        // get all required view's
        Context context = getContext();
        reloadCollapsedView(context);
        reloadExpandedView(context);

        // restore internal variables

        expanded = cacheexpanded;
        fullscreenWhenExpanded = cachefullscreenWhenExpanded;
        layout = cachelayout;

        onSetupExternalViews.run(this);

        // restore external variables
        onRestoreState.run(state);
        
        onSetupExternalListeners.run(this);


        if (expanded)
            expand();
        else
            collapse();

        if (cacheattachedToWindowManager) attachToWindowManager(layout);
    }

    private void setCollapsedViewOnTouchListener() {
        collapsedView.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(final View v, final MotionEvent event) {
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
    }

    private void setExpandedViewOnClickListener() {
        expandedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                collapse();
            }
        });
    }

    public interface Callback<T> {
        /**
         * When an object implementing interface <code>Runnable</code> is used
         * to create a thread, starting the thread causes the object's
         * <code>run</code> method to be called in that separately executing
         * thread.
         * <p>
         * The general contract of the method <code>run</code> is that it may
         * take any action whatsoever.
         *
         * @see     java.lang.Thread#run()
         */
        void run(T argument);
    }

    private Callback<Bundle> stub = new Callback<Bundle>() {
        @Override
        public void run(Bundle bundle) {
            // stub
        }
    };

    private Callback<FloatingView> stub2 = new Callback<FloatingView>() {
        @Override
        public void run(FloatingView floatingView) {
            // stub
        }
    };

    private Callback<Bundle> onSaveState = stub;

    public void setOnSaveState(Callback<Bundle> runnable) {
        onSaveState = runnable;
    }

    private Callback<FloatingView> onSetupExternalViews = stub2;

    public void setOnSetupExternalViews(Callback<FloatingView> runnable) {
        onSetupExternalViews = runnable;
    }

    private Callback<Bundle> onRestoreState = stub;

    public void setOnRestoreState(Callback<Bundle> runnable) {
        onRestoreState = runnable;
    }

    private Callback<FloatingView> onSetupExternalListeners = stub2;

    public void setOnSetupExternalListeners(Callback<FloatingView> runnable) {
        onSetupExternalListeners = runnable;
    }

    // embed a copy of LogUtils to make this view fully self contained

    private class LogUtils {
        private String TAG = "";
        private String ERRORMESSAGE = "An error has occured";

        public LogUtils(Object tag) {
            setTag(tag);
        }

        public LogUtils(String tag) {
            setTag(tag);
        }

        public LogUtils(Object tag, String errorMessage) {
            setTag(tag);
            setErrorMessage(errorMessage);
        }

        public LogUtils(String tag, String errorMessage) {
            setTag(tag);
            setErrorMessage(errorMessage);
        }

        public void setTag(Object object) {
            setTag(object.getClass().getName());
        }

        public void setTag(String tag) {
            TAG = tag;
        }

        public void setErrorMessage(String errorMessage) {
            ERRORMESSAGE = errorMessage;
        }

        public final void log(String message) {
            Log.i("LogUtils", TAG + ": " + message);
        }

        public void logWithClassName(Object object, String message) {
            Log.i("LogUtils", TAG + ": " + object.getClass().getName() + ": " + message);
        }

        public final Throwable error() {
            return error(ERRORMESSAGE);
        }

        public final AssertionError error(String message) {
            AssertionError t = new AssertionError(message);
            Log.e("LogUtils", TAG + ": " + Log.getStackTraceString(t));
            return t;
        }

        public void errorWithClassName(Object object, Exception exception) {
            AssertionError t = new AssertionError(Log.getStackTraceString(exception));
            Log.e("LogUtils", TAG + ": " + object.getClass().getName() + ": " + Log.getStackTraceString(t));
        }

        public void errorWithClassName(Object object, String message) {
            AssertionError t = new AssertionError(message);
            Log.e("LogUtils", TAG + ": " + object.getClass().getName() + ": " + Log.getStackTraceString(t));
        }

        public final void errorNoStackTrace() {
            errorNoStackTrace(ERRORMESSAGE);
        }

        public final void errorNoStackTraceWithClassName(Object object) {
            errorNoStackTraceWithClassName(object, ERRORMESSAGE);
        }

        public final void errorNoStackTrace(String message) {
            Log.e("LogUtils", TAG + ": " + message);
        }

        public final void errorNoStackTraceWithClassName(Object object, String message) {
            Log.e("LogUtils", TAG + ": " + object.getClass().getName() + ": " + message);
        }

        @Nullable
        @SuppressWarnings("ConstantOnRightSideOfComparison")
        public final <T> T errorIfNull(@Nullable T object) {
            return errorIfNull(object, ERRORMESSAGE);
        }

        @Nullable
        @SuppressWarnings("ConstantOnRightSideOfComparison")
        public final <T> T errorIfNull(@Nullable T object, String message) {
            if (object == null) error(message);
            return object;
        }

        @Nullable
        @SuppressWarnings("ConstantOnRightSideOfComparison")
        public final <T> T errorIfNullNoStackTrace(@Nullable T object) {
            return errorIfNullNoStackTrace(object, ERRORMESSAGE);
        }

        @Nullable
        @SuppressWarnings("ConstantOnRightSideOfComparison")
        public final <T> T errorIfNullNoStackTrace(@Nullable T object, String message) {
            if (object == null) errorNoStackTrace(message);
            return object;
        }

        @Nullable
        @SuppressWarnings("ConstantOnRightSideOfComparison")
        public final <T> T errorAndThrowIfNull(@Nullable T object) {
            return errorAndThrowIfNull(object, ERRORMESSAGE);
        }

        @Nullable
        @SuppressWarnings("ConstantOnRightSideOfComparison")
        public final <T> T errorAndThrowIfNull(@Nullable T object, String message) {
            assertNotNull(message, object);
            return object;
        }

        @Nullable
        @SuppressWarnings("ConstantOnRightSideOfComparison")
        public final <T> T errorAndThrowIfNullWithClass(Object object_, @Nullable T object, String message) {
            assertNotNull(object_.getClass().getName() + ": " + message, object);
            return object;
        }

        @Nullable
        @SuppressWarnings("ConstantOnRightSideOfComparison")
        public final void errorAndThrow(String message) {
            assertNotNull(message, null);
        }

        @Nullable
        @SuppressWarnings("ConstantOnRightSideOfComparison")
        public final void errorAndThrowWithClass(Object object, String message) {
            assertNotNull(object.getClass().getName() + ": " + message, null);
        }

        public void logMethodName() {
            Log.i("LogUtils", TAG + ": " + Thread.currentThread().getStackTrace()[3].getMethodName() + "() called");
        }

        public void logParentMethodName() {
            Log.i("LogUtils", TAG + ": " + Thread.currentThread().getStackTrace()[4].getMethodName() + "() called");
        }

        public void logMethodNameWithClassName(Object object) {
            Log.i("LogUtils",
                    TAG + ": " + object.getClass().getName() + ": " +
                            Thread.currentThread().getStackTrace()[3].getMethodName() + "() called");
        }

        public String getMethodName() {
            return getMethodName(1);
        }

        public String getMethodName(int methodDepthOffset) {
            return Thread.currentThread().getStackTrace()[3+methodDepthOffset].getMethodName();
        }

        public String getParentMethodName() {
            return getParentMethodName(1);
        }

        public String getParentMethodName(int methodDepthOffset) {
            return Thread.currentThread().getStackTrace()[4+methodDepthOffset].getMethodName();
        }
    }
}
