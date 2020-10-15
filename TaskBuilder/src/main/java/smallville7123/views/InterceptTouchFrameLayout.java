package smallville7123.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import smallville7123.taggable.Taggable;

/**
 * A FrameLayout that allow setting a delegate for intercept touch event
 */
public class InterceptTouchFrameLayout extends FrameLayout {
    public String TAG = Taggable.getTag(this);

    private boolean mDisallowIntercept;

    public interface OnInterceptTouchEventListener {
        /**
         * If disallowIntercept is true the touch event can't be stealed and the return value is ignored.
         * @see android.view.ViewGroup#onInterceptTouchEvent(android.view.MotionEvent)
         */
        boolean onInterceptTouchEvent(InterceptTouchFrameLayout view, MotionEvent motionEvent, boolean disallowIntercept);

        /**
         * @see android.view.View#onTouchEvent(android.view.MotionEvent)
         */
        boolean onTouchEvent(InterceptTouchFrameLayout view, MotionEvent motionEvent);
    }

    private static final class DummyInterceptTouchEventListener implements OnInterceptTouchEventListener {
        public String TAG = Taggable.getTag(this);
        @Override
        public boolean onInterceptTouchEvent(InterceptTouchFrameLayout view, MotionEvent motionEvent, boolean disallowIntercept) {
            Log.d(TAG, "MotionEvent.actionToString(ev.getAction()) = [" + MotionEvent.actionToString(motionEvent.getAction()) + "]");
            Log.d(TAG, "motionEvent.getX() = [" + motionEvent.getX() + "]");
            Log.d(TAG, "motionEvent.getRawX() = [" + motionEvent.getRawX() + "]");
            Log.d(TAG, "motionEvent.getY() = [" + motionEvent.getY() + "]");
            Log.d(TAG, "motionEvent.getRawY() = [" + motionEvent.getRawY() + "]");
            return false;
        }
        @Override
        public boolean onTouchEvent(InterceptTouchFrameLayout view, MotionEvent motionEvent) {
            Log.d(TAG, "MotionEvent.actionToString(ev.getAction()) = [" + MotionEvent.actionToString(motionEvent.getAction()) + "]");
            return false;
        }
    }

    private static final OnInterceptTouchEventListener DUMMY_LISTENER = new DummyInterceptTouchEventListener();

    private OnInterceptTouchEventListener mInterceptTouchEventListener = DUMMY_LISTENER;

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

    public void setOnInterceptTouchEventListener(OnInterceptTouchEventListener interceptTouchEventListener) {
        mInterceptTouchEventListener = interceptTouchEventListener != null ? interceptTouchEventListener : DUMMY_LISTENER;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean stealTouchEvent = mInterceptTouchEventListener.onInterceptTouchEvent(this, ev, mDisallowIntercept);
        return stealTouchEvent && !mDisallowIntercept || super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = mInterceptTouchEventListener.onTouchEvent(this, event);
        return handled || super.onTouchEvent(event);
    }
}