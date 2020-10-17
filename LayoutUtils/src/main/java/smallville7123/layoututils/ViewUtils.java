package smallville7123.layoututils;

import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import smallville7123.reflectionutils.ReflectionUtils;
import smallville7123.todo.TODO;

class ViewUtils {

    // the following is generated values obtained from
    // .../gen/com/android/internal/util/FrameworkStatsLog.java
    //
    // start of generated values
    //

    // Values for TouchGestureClassified.classification
    public static final int TOUCH_GESTURE_CLASSIFIED__CLASSIFICATION__UNKNOWN_CLASSIFICATION = 0;
    public static final int TOUCH_GESTURE_CLASSIFIED__CLASSIFICATION__SINGLE_TAP = 1;
    public static final int TOUCH_GESTURE_CLASSIFIED__CLASSIFICATION__DOUBLE_TAP = 2;
    public static final int TOUCH_GESTURE_CLASSIFIED__CLASSIFICATION__LONG_PRESS = 3;
    public static final int TOUCH_GESTURE_CLASSIFIED__CLASSIFICATION__DEEP_PRESS = 4;
    public static final int TOUCH_GESTURE_CLASSIFIED__CLASSIFICATION__SCROLL = 5;

    //
    // end of generated values
    //

    static ReflectionUtils reflectionUtils = new ReflectionUtils();

    public static void handleTooltipUp(View view) {
        reflectionUtils.invokeMethod(view, "handleTooltipUp");
    }

    public static void setPressed(View view, boolean b, float x, float y) {
        reflectionUtils.invokeMethod(view, "setPressed", new Boolean(b), new Float(x), new Float(y));
    }

    public static void removeTapCallback(View view) {
        reflectionUtils.invokeMethod(view, "removeTapCallback");
    }

    public static void removeLongPressCallback(View view) {
        reflectionUtils.invokeMethod(view, "removeTapCallback");

    }

    public static TouchDelegate get_mTouchDelegate(View view) {
        return (TouchDelegate) reflectionUtils.getField(view, "mTouchDelegate");
    }

    /**
     * mViewFlags is annotated with @hide
     */
    public static int get_mViewFlags(View view) {
        return (int) reflectionUtils.getField(view, "mViewFlags");
    }

    /**
     * mViewFlags is annotated with @hide
     */
    public static void set_mViewFlags(View view, int value) {
        reflectionUtils.setField(view, "mViewFlags", value);
    }

    /**
     * mPrivateFlags is annotated with @hide
     */
    public static int get_mPrivateFlags(View view) {
        return (int) reflectionUtils.getField(view, "mPrivateFlags");
    }

    /**
     * mPrivateFlags is annotated with @hide
     */
    public static void set_mPrivateFlags(View view, int value) {
        reflectionUtils.setField(view, "mPrivateFlags", value);
    }

    /**
     * mPrivateFlags2 is annotated with @hide
     */
    public static int get_mPrivateFlags2(View view) {
        return (int) reflectionUtils.getField(view, "mPrivateFlags2");
    }

    /**
     * mPrivateFlags2 is annotated with @hide
     */
    public static void set_mPrivateFlags2(View view, int value) {
        reflectionUtils.setField(view, "mPrivateFlags2", value);
    }

    /**
     * mPrivateFlags3 is annotated with @hide
     */
    public static int get_mPrivateFlags3(View view) {
        return (int) reflectionUtils.getField(view, "mPrivateFlags3");
    }

    /**
     * mPrivateFlags3 is annotated with @hide
     */
    public static void set_mPrivateFlags3(View view, int value) {
        reflectionUtils.setField(view, "mPrivateFlags3", value);
    }

    public static int get_PFLAG3_FINGER_DOWN(View view) {
        return (int) reflectionUtils.getField(view, "PFLAG3_FINGER_DOWN");
    }

    public static int get_CLICKABLE(View view) {
        return (int) reflectionUtils.getField(view, "CLICKABLE");
    }

    public static int get_LONG_CLICKABLE(View view) {
        return (int) reflectionUtils.getField(view, "LONG_CLICKABLE");
    }

    public static int get_CONTEXT_CLICKABLE(View view) {
        return (int) reflectionUtils.getField(view, "CONTEXT_CLICKABLE");
    }

    public static int get_ENABLED_MASK(View view) {
        return (int) reflectionUtils.getField(view, "ENABLED_MASK");
    }

    public static int get_ENABLED(View view) {
        return (int) reflectionUtils.getField(view, "ENABLED");
    }

    public static int get_DISABLED(View view) {
        return (int) reflectionUtils.getField(view, "DISABLED");
    }

    public static int get_PFLAG_PRESSED(View view) {
        return (int) reflectionUtils.getField(view, "PFLAG_PRESSED");
    }

    public static int get_PFLAG_PREPRESSED(View view) {
        return (int) reflectionUtils.getField(view, "PFLAG_PREPRESSED");
    }

    public static int get_TOOLTIP(View view) {
        return (int) reflectionUtils.getField(view, "TOOLTIP");
    }

    public static boolean get_mInContextButtonPress(View view) {
        return (boolean) reflectionUtils.getField(view, "mInContextButtonPress");
    }

    public static void set_mInContextButtonPress(View view, boolean value) {
        reflectionUtils.setField(view, "mInContextButtonPress", value);
    }

    public static boolean get_mHasPerformedLongPress(View view) {
        return (boolean) reflectionUtils.getField(view, "mHasPerformedLongPress");
    }

    public static void set_mHasPerformedLongPress(View view, boolean value) {
        reflectionUtils.setField(view, "mHasPerformedLongPress", value);
    }

    public static boolean get_mIgnoreNextUpEvent(View view) {
        return (boolean) reflectionUtils.getField(view, "mIgnoreNextUpEvent");
    }

    public static void set_mIgnoreNextUpEvent(View view, boolean value) {
        reflectionUtils.setField(view, "mIgnoreNextUpEvent", value);
    }

    /**
     * the class PerformClick is private, however it implements Runnable
     * so return Runnable type instead
     */
    public static Runnable get_mPerformClick(View view) {
        return (Runnable) reflectionUtils.getField(view, "mPerformClick");
    }

    /**
     * the class PerformClick is private, however it implements Runnable
     * so accept Runnable type instead
     */
    public static void set_mPerformClick(View view, Runnable value) {
        reflectionUtils.setField(view, "mPerformClick", value);
    }

    public static Runnable new_PerformClick(View view) {
        return (Runnable) reflectionUtils.instantiate(view, "PerformClick");
    }

    public static void performClickInternal(View view) {
        reflectionUtils.invokeMethod(view, "performClickInternal");
    }

    /**
     * the class UnsetPressedState is private, however it implements Runnable
     * so return Runnable type instead
     */
    public static Runnable get_mUnsetPressedState(View view) {
        return (Runnable) reflectionUtils.getField(view, "mUnsetPressedState");
    }

    /**
     * the class UnsetPressedState is private, however it implements Runnable
     * so accept Runnable type instead
     */
    public static void set_mUnsetPressedState(View view, Runnable value) {
        reflectionUtils.setField(view, "mPerformClick", value);
    }

    public static Runnable new_UnsetPressedState(View view) {
        return (Runnable) reflectionUtils.instantiate(view, "UnsetPressedState");
    }

    /**
     * the class CheckForTap is private, however it implements Runnable
     * so return Runnable type instead
     */
    public static Runnable get_mPendingCheckForTap(View view) {
        return (Runnable) reflectionUtils.getField(view, "mPendingCheckForTap");
    }

    /**
     * the class CheckForTap is private, however it implements Runnable
     * so accept Runnable type instead
     */
    public static void set_mPendingCheckForTap(View view, Runnable value) {
        reflectionUtils.setField(view, "mPendingCheckForTap", value);
    }

    /**
     * the class CheckForTap is private, however it implements Runnable
     * so accept Runnable type instead
     */
    public static void set_CheckForTap_x(Runnable mCheckForTap, float value) {
        reflectionUtils.setField(mCheckForTap, "x", value);
    }

    /**
     * the class CheckForTap is private, however it implements Runnable
     * so accept Runnable type instead
     */
    public static void set_CheckForTap_y(Runnable mCheckForTap, float value) {
        reflectionUtils.setField(mCheckForTap, "y", value);
    }

    public static Runnable new_CheckForTap(View view) {
        return (Runnable) reflectionUtils.instantiate(view, "CheckForTap");
    }

    public static void checkForLongClick(View view, long delay, float x, float y, int classification) {
        reflectionUtils.invokeMethod(view, "checkForLongClick", delay, x, y, classification);
    }

    /**
     * performButtonActionOnTouchDown is annotated with @hide
     * <br>
     * copy its code
     * <br>
     * <br>
     * Performs button-related actions during a touch down event.
     *
     * @param event The event.
     * @return True if the down was consumed.
     */
    public static boolean performButtonActionOnTouchDown(View view, MotionEvent event) {
        if (event.isFromSource(InputDevice.SOURCE_MOUSE) &&
                (event.getButtonState() & MotionEvent.BUTTON_SECONDARY) != 0) {
            view.showContextMenu(event.getX(), event.getY());
            set_mPrivateFlags(view, get_mPrivateFlags(view) | get_PFLAG_CANCEL_NEXT_UP_EVENT(view));
            return true;
        }
        return false;
    }

    private static int get_PFLAG_CANCEL_NEXT_UP_EVENT(View view) {
        return (int) reflectionUtils.getField(view, "PFLAG_CANCEL_NEXT_UP_EVENT");
    }

    public static int get_mTouchSlop(View view) {
        return (int) reflectionUtils.getField(view, "mTouchSlop");
    }

    public static boolean hasPendingLongPressCallback(View view) {
        return (boolean) reflectionUtils.getField(view, "hasPendingLongPressCallback");
    }

    /**
     * pointInView is annotated with @hide
     * <br>
     * copy its code
     * <br>
     * <br>
     * Determines whether the given point, in local coordinates is inside the view.
     */
    public static boolean pointInView(View view, float localX, float localY) {
        return pointInView(view, localX, localY, 0);
    }

    /**
     * pointInView is annotated with @hide
     * <br>
     * copy its code
     * <br>
     * <br>
     * Utility method to determine whether the given point, in local coordinates,
     * is inside the view, where the area of the view is expanded by the slop factor.
     * This method is called while processing touch-move events to determine if the event
     * is still within the view.
     */
    public static boolean pointInView(View view, float localX, float localY, float slop) {
        return localX >= -slop && localY >= -slop && localX < ((view.getRight() - view.getLeft()) + slop) &&
                localY < ((view.getBottom() - view.getTop()) + slop);
    }

    /**
     * isInScrollingContainer is annotated with @hide
     * <br>
     * copy its code
     */
    public static boolean isInScrollingContainer(View view) {
        ViewParent p = view.getParent();
        while (p != null && p instanceof ViewGroup) {
            if (((ViewGroup) p).shouldDelayChildPressedState()) {
                return true;
            }
            p = p.getParent();
        }
        return false;
    }
}