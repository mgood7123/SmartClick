package smallville7123.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import smallville7123.layoututils.ViewHierarchy;
import smallville7123.layoututils.ViewUtils;
import smallville7123.taggable.Taggable;

/**
 * A FrameLayout that allow setting a delegate for intercept touch event
 */
public class InterceptTouchFrameLayout extends FrameLayout {
    public String TAG = Taggable.getTag(this);

    /**
     * if true, calls onClick after handling its own onClick listener
     * <br>
     * if {@link #callOnClickBefore} is also true, then onClick will be called twice
     */
    public boolean callOnClickAfter;

    /**
     * if true, calls onClick before handling its own onClick listener
     * <br>
     * if {@link #callOnClickAfter} is also true, then onClick will be called twice
     */
    public boolean callOnClickBefore;

    private boolean onClick = false;
    private boolean mDisallowIntercept;

    public interface OnInterceptTouchEventListener {
        /**
         * If disallowIntercept is true the touch event can't be stealed and the return value is ignored.
         * @see android.view.ViewGroup#onInterceptTouchEvent(android.view.MotionEvent)
         */
        boolean onInterceptTouchEvent(View view, MotionEvent motionEvent, boolean disallowIntercept);

        /**
         * @see android.view.View#onTouchEvent(android.view.MotionEvent)
         */
        boolean onTouchEvent(View view, MotionEvent motionEvent);
    }

    private static final class DummyInterceptTouchEventListener implements OnInterceptTouchEventListener {
        public String TAG = Taggable.getTag(this);
        @Override
        public boolean onInterceptTouchEvent(View view, MotionEvent motionEvent, boolean disallowIntercept) {
            return false;
        }
        @Override
        public boolean onTouchEvent(View view, MotionEvent motionEvent) {
            return false;
        }
    }

    private static final OnInterceptTouchEventListener DUMMY_LISTENER = new DummyInterceptTouchEventListener();

    private OnInterceptTouchEventListener mInterceptTouchEventListener = DUMMY_LISTENER;

    public interface OnInterceptClickListener {
        /**
         * @see android.view.View#setOnClickListener(OnClickListener)
         */
        void onInterceptClick(View view);
    }

    private static final class DummyOnInterceptClickListener implements OnInterceptClickListener {
        public String TAG = Taggable.getTag(this);

        @Override
        public void onInterceptClick(final View view) {}
    }

    private static final OnInterceptClickListener DUMMY_CLICK_LISTENER = new DummyOnInterceptClickListener();

    private OnInterceptClickListener mOnInterceptClickListener = DUMMY_CLICK_LISTENER;

    public InterceptTouchFrameLayout(Context context) {
        super(context);
    }

    public InterceptTouchFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InterceptTouchFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public InterceptTouchFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyle) {
        super(context, attrs, defStyleAttr, defStyle);
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        getParent().requestDisallowInterceptTouchEvent(disallowIntercept);
        mDisallowIntercept = disallowIntercept;
    }

    public void setInterceptOnClickListener(OnInterceptClickListener interceptClickListener) {
        mOnInterceptClickListener = interceptClickListener != null ? interceptClickListener : DUMMY_CLICK_LISTENER;
        setClickable(true);
    }

    public void setOnInterceptTouchEventListener(OnInterceptTouchEventListener interceptTouchEventListener) {
        mInterceptTouchEventListener = interceptTouchEventListener != null ? interceptTouchEventListener : DUMMY_LISTENER;
    }

    private void onClick() {
        // restore clickable to ensure correct behaviour,
        // also so that update can set it to un-clickable again
        // just to be safe, as setOnClickListener sets the view to Clickable,
        currentChild.setClickable(true);
        // however a quick browse of the source code reveals
        // that both performClick and callOnClick
        // do not care if the view is visible or not,
        // and they do not care if the view is clickable or not
        // they simply call the listener's onClick if it is present
        if (callOnClickBefore) {
            /**
             * Call this view's OnClickListener, if it is defined.  Performs all normal
             * actions associated with clicking: reporting accessibility event, playing
             * a sound, etc.
             */
            currentChild.performClick();
        }
        mOnInterceptClickListener.onInterceptClick(currentChild);
        if (callOnClickAfter) {
            /**
             * Call this view's OnClickListener, if it is defined.  Performs all normal
             * actions associated with clicking: reporting accessibility event, playing
             * a sound, etc.
             */
            currentChild.performClick();
        }
    }

    @Override
    public boolean callOnClick() {
        Log.d(TAG, "callOnClick() called");
        onClick();
        return true;
    }

    @Override
    public boolean performClick() {
        Log.d(TAG, "performClick() called");
        onClick();
        return true;
    }

    View currentChild = null;

    private void update() {
        Log.d(TAG, "update() called");
        // we do not need to obtain the child's onClick listener
        // all we need to do is set it to non-clickable to ensure correct behaviour
        currentChild.setClickable(false);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d(TAG, "onInterceptTouchEvent: MotionEvent.actionToString(ev.getAction()) = [" + MotionEvent.actionToString(ev.getAction()) + "]");
        // onInterceptTouchEvent is called every time,
        // take advantage of this to set up our onClick interceptor
        update();
        boolean stealTouchEvent = mInterceptTouchEventListener.onInterceptTouchEvent(currentChild, ev, mDisallowIntercept);
        return stealTouchEvent && !mDisallowIntercept || super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent: MotionEvent.actionToString(ev.getAction()) = [" + MotionEvent.actionToString(event.getAction()) + "]");
        boolean handled = mInterceptTouchEventListener.onTouchEvent(currentChild, event);
        return handled || super.onTouchEvent(event);
    }

    @Override
    public void addView(final View child, final int index, final ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        // FrameLayout throws if given more than one children, so set current child after
        // even tho this will not actually make any difference as it will still throw
        currentChild = child;
    }
}