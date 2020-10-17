package smallville7123.layoututils;

import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityNodeInfo;

import static smallville7123.layoututils.ViewUtils.TOUCH_GESTURE_CLASSIFIED__CLASSIFICATION__DEEP_PRESS;
import static smallville7123.layoututils.ViewUtils.TOUCH_GESTURE_CLASSIFIED__CLASSIFICATION__LONG_PRESS;

class ViewOnMotionEvent {

    View view;

    public ViewOnMotionEvent(View view) {
        this.view = view;
    }

    /**
     * Implement this method to handle touch screen motion events.
     * <p>
     * If this method is used to detect click actions, it is recommended that
     * the actions be performed by implementing and calling
     * {@link View#performClick()}. This will ensure consistent system behavior,
     * including:
     * <ul>
     * <li>obeying click sound preferences
     * <li>dispatching OnClickListener calls
     * <li>handling {@link AccessibilityNodeInfo#ACTION_CLICK ACTION_CLICK} when
     * accessibility features are enabled
     * </ul>
     *
     * @param event The motion event.
     * @return True if the event was handled, false otherwise.
     */
    public boolean onTouchEvent(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();
        final int viewFlags = ViewUtils.get_mViewFlags(view);
        // NOTE: we do not not know how @hide works in this particular scenario
        //
        //     /**
        //     * {@hide}
        //     */
        //    @ViewDebug.ExportedProperty(flagMapping = {
        //        @ViewDebug.FlagToString(mask = PFLAG_FORCE_LAYOUT, equals = PFLAG_FORCE_LAYOUT,
        //                name = "FORCE_LAYOUT"),
        //        @ViewDebug.FlagToString(mask = PFLAG_LAYOUT_REQUIRED, equals = PFLAG_LAYOUT_REQUIRED,
        //                name = "LAYOUT_REQUIRED"),
        //        @ViewDebug.FlagToString(mask = PFLAG_DRAWING_CACHE_VALID, equals = PFLAG_DRAWING_CACHE_VALID,
        //            name = "DRAWING_CACHE_INVALID", outputIf = false),
        //        @ViewDebug.FlagToString(mask = PFLAG_DRAWN, equals = PFLAG_DRAWN, name = "DRAWN", outputIf = true),
        //        @ViewDebug.FlagToString(mask = PFLAG_DRAWN, equals = PFLAG_DRAWN, name = "NOT_DRAWN", outputIf = false),
        //        @ViewDebug.FlagToString(mask = PFLAG_DIRTY_MASK, equals = PFLAG_DIRTY, name = "DIRTY")
        //    }, formatToHexString = true)
        //
        //    /* @hide */
        //    @UnsupportedAppUsage(maxTargetSdk = Build.VERSION_CODES.P, trackingBug = 123769414)
        //    public int mPrivateFlags;
        //    @UnsupportedAppUsage(maxTargetSdk = Build.VERSION_CODES.P, trackingBug = 123768943)
        //    int mPrivateFlags2;
        //    @UnsupportedAppUsage(maxTargetSdk = Build.VERSION_CODES.P, trackingBug = 129147060)
        //    int mPrivateFlags3;
        //
        int mPrivateFlags = ViewUtils.get_mPrivateFlags(view);
        int mPrivateFlags2 = ViewUtils.get_mPrivateFlags2(view);
        int mPrivateFlags3 = ViewUtils.get_mPrivateFlags3(view);
        final int action = event.getAction();

        int CLICKABLE = ViewUtils.get_CLICKABLE(view);
        int LONG_CLICKABLE = ViewUtils.get_LONG_CLICKABLE(view);
        int CONTEXT_CLICKABLE = ViewUtils.get_CONTEXT_CLICKABLE(view);
        int ENABLED_MASK = ViewUtils.get_ENABLED_MASK(view);
        int DISABLED = ViewUtils.get_DISABLED(view);
        int PFLAG_PRESSED = ViewUtils.get_PFLAG_PRESSED(view);
        int PFLAG_PREPRESSED = ViewUtils.get_PFLAG_PREPRESSED(view);
        int PFLAG3_FINGER_DOWN = ViewUtils.get_PFLAG3_FINGER_DOWN(view);
        int TOOLTIP = ViewUtils.get_TOOLTIP(view);
        boolean mInContextButtonPress = ViewUtils.get_mInContextButtonPress(view);
        boolean mHasPerformedLongPress = ViewUtils.get_mHasPerformedLongPress(view);
        boolean mIgnoreNextUpEvent = ViewUtils.get_mIgnoreNextUpEvent(view);
        int mTouchSlop = ViewUtils.get_mTouchSlop(view);
        Runnable mPerformClick = ViewUtils.get_mPerformClick(view);
        Runnable mUnsetPressedState = ViewUtils.get_mUnsetPressedState(view);
        Runnable mPendingCheckForTap = ViewUtils.get_mPendingCheckForTap(view);

        /**
         * mViewFlags is annotated with @hide
         * however
         * according to View#setTooltipText
         *     public void setTooltipText(@Nullable CharSequence tooltipText) {
         *         if (TextUtils.isEmpty(tooltipText)) {
         *             setFlags(0, TOOLTIP);
         *             hideTooltip();
         *             mTooltipInfo = null;
         *         } else {
         *             setFlags(TOOLTIP, TOOLTIP);
         *             if (mTooltipInfo == null) {
         *                 mTooltipInfo = new TooltipInfo();
         *                 mTooltipInfo.mShowTooltipRunnable = this::showHoverTooltip;
         *                 mTooltipInfo.mHideTooltipRunnable = this::hideTooltip;
         *                 mTooltipInfo.mHoverSlop = ViewConfiguration.get(mContext).getScaledHoverSlop();
         *                 mTooltipInfo.clearAnchorPos();
         *             }
         *             mTooltipInfo.mTooltipText = tooltipText;
         *         }
         *     }
         *
         *     assume that TOOLTIP flag is cleared if tooltip text is null,
         *     and set if tooltip text is not null
         */
//        boolean hasToolTip = view.getTooltipText() != null;

//        final boolean clickable = (view.isClickable()
//                || view.isLongClickable())
//                || view.isContextClickable();
        final boolean clickable = ((viewFlags & CLICKABLE) == CLICKABLE
                || (viewFlags & LONG_CLICKABLE) == LONG_CLICKABLE)
                || (viewFlags & CONTEXT_CLICKABLE) == CONTEXT_CLICKABLE;

//        if (!view.isEnabled()) {
        if ((viewFlags & ENABLED_MASK) == DISABLED) {
//            if (action == MotionEvent.ACTION_UP && !view.isPressed()) {
            if (action == MotionEvent.ACTION_UP && (mPrivateFlags & PFLAG_PRESSED) != 0) {
                view.setPressed(false);
            }
            ViewUtils.set_mPrivateFlags3(view, ViewUtils.get_mPrivateFlags3(view) & ~PFLAG3_FINGER_DOWN);

            // A disabled view that is clickable still consumes the touch
            // events, it just doesn't respond to them.
            return clickable;
        }

        TouchDelegate mTouchDelegate = ViewUtils.get_mTouchDelegate(view);

        if (mTouchDelegate != null) {
            if (mTouchDelegate.onTouchEvent(event)) {
                return true;
            }
        }

//        if (clickable || hasToolTip) {
        if (clickable || (viewFlags & TOOLTIP) == TOOLTIP) {
            switch (action) {
                case MotionEvent.ACTION_UP:
                    ViewUtils.set_mPrivateFlags3(view, ViewUtils.get_mPrivateFlags3(view) & ~PFLAG3_FINGER_DOWN);
//                    if (hasToolTip) {
                    if ((viewFlags & TOOLTIP) == TOOLTIP) {
                        ViewUtils.handleTooltipUp(view);
                    }
                    if (!clickable) {
                        ViewUtils.removeTapCallback(view);
                        ViewUtils.removeLongPressCallback(view);
                        mInContextButtonPress = false;
                        mHasPerformedLongPress = false;
                        mIgnoreNextUpEvent = false;
                        ViewUtils.set_mInContextButtonPress(view, mInContextButtonPress);
                        ViewUtils.set_mHasPerformedLongPress(view, mHasPerformedLongPress);
                        ViewUtils.set_mIgnoreNextUpEvent(view, mIgnoreNextUpEvent);
                        break;
                    }
                    boolean prepressed = (mPrivateFlags & PFLAG_PREPRESSED) != 0;
                    if ((mPrivateFlags & PFLAG_PRESSED) != 0 || prepressed) {
                        // take focus if we don't have it already and we should in
                        // touch mode.
                        boolean focusTaken = false;
                        if (view.isFocusable() && view.isFocusableInTouchMode() && !view.isFocused()) {
                            focusTaken = view.requestFocus();
                        }

                        if (prepressed) {
                            // The button is being released before we actually
                            // showed it as pressed.  Make it show the pressed
                            // state now (before scheduling the click) to ensure
                            // the user sees it.
                            ViewUtils.setPressed(view, true, x, y);
                        }

                        if (!mHasPerformedLongPress && !mIgnoreNextUpEvent) {
                            // This is a tap, so remove the longpress check
                            ViewUtils.removeLongPressCallback(view);

                            // Only perform take click actions if we were in the pressed state
                            if (!focusTaken) {
                                // Use a Runnable and post this rather than calling
                                // performClick directly. This lets other visual state
                                // of the view update before click actions start.
                                if (mPerformClick == null) {
                                    mPerformClick = ViewUtils.new_PerformClick(view);
                                    ViewUtils.set_mPerformClick(view, mPerformClick);
                                }
                                if (!view.post(mPerformClick)) {
                                    ViewUtils.performClickInternal(view);
                                }
                            }
                        }

                        if (mUnsetPressedState == null) {
                            mUnsetPressedState = ViewUtils.new_UnsetPressedState(view);
                            ViewUtils.set_mUnsetPressedState(view, mUnsetPressedState);
                        }

                        if (prepressed) {
                            view.postDelayed(mUnsetPressedState,
                                    ViewConfiguration.getPressedStateDuration());
                        } else if (!view.post(mUnsetPressedState)) {
                            // If the post failed, unpress right now
                            mUnsetPressedState.run();
                        }

                        ViewUtils.removeTapCallback(view);
                    }
                    mIgnoreNextUpEvent = false;
                    ViewUtils.set_mIgnoreNextUpEvent(view, mIgnoreNextUpEvent);
                    break;

                case MotionEvent.ACTION_DOWN:
                    if (event.getSource() == InputDevice.SOURCE_TOUCHSCREEN) {
                        ViewUtils.set_mPrivateFlags3(view, ViewUtils.get_mPrivateFlags3(view) | PFLAG3_FINGER_DOWN);
                    }
                    mHasPerformedLongPress = false;
                    ViewUtils.set_mHasPerformedLongPress(view, mHasPerformedLongPress);

                    if (!clickable) {
                        ViewUtils.checkForLongClick(
                                view,
                                ViewConfiguration.getLongPressTimeout(),
                                x,
                                y,
                                TOUCH_GESTURE_CLASSIFIED__CLASSIFICATION__LONG_PRESS);
                        break;
                    }

                    if (ViewUtils.performButtonActionOnTouchDown(view, event)) {
                        break;
                    }

                    // Walk up the hierarchy to determine if we're inside a scrolling container.
                    boolean isInScrollingContainer = ViewUtils.isInScrollingContainer(view);

                    // For views inside a scrolling container, delay the pressed feedback for
                    // a short period in case this is a scroll.
                    if (isInScrollingContainer) {
                        ViewUtils.set_mPrivateFlags(view, ViewUtils.get_mPrivateFlags(view) | PFLAG_PREPRESSED);
                        if (mPendingCheckForTap == null) {
                            mPendingCheckForTap = ViewUtils.new_CheckForTap(view);
                            ViewUtils.set_mPendingCheckForTap(view, mPendingCheckForTap);
                        }
                        ViewUtils.set_CheckForTap_x(mPendingCheckForTap, event.getX());
                        ViewUtils.set_CheckForTap_y(mPendingCheckForTap, event.getY());
                        view.postDelayed(mPendingCheckForTap, ViewConfiguration.getTapTimeout());
                    } else {
                        // Not inside a scrolling container, so show the feedback right away
                        ViewUtils.setPressed(view, true, x, y);
                        ViewUtils.checkForLongClick(
                                view,
                                ViewConfiguration.getLongPressTimeout(),
                                x,
                                y,
                                TOUCH_GESTURE_CLASSIFIED__CLASSIFICATION__LONG_PRESS);
                    }
                    break;

                case MotionEvent.ACTION_CANCEL:
                    if (clickable) {
                        view.setPressed(false);
                    }
                    ViewUtils.removeTapCallback(view);
                    ViewUtils.removeLongPressCallback(view);
                    mInContextButtonPress = false;
                    mHasPerformedLongPress = false;
                    mIgnoreNextUpEvent = false;
                    ViewUtils.set_mInContextButtonPress(view, mInContextButtonPress);
                    ViewUtils.set_mHasPerformedLongPress(view, mHasPerformedLongPress);
                    ViewUtils.set_mIgnoreNextUpEvent(view, mIgnoreNextUpEvent);
                    ViewUtils.set_mPrivateFlags3(view, ViewUtils.get_mPrivateFlags3(view) & ~PFLAG3_FINGER_DOWN);
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (clickable) {
                        view.drawableHotspotChanged(x, y);
                    }

                    final int motionClassification;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        motionClassification = event.getClassification();
                    } else {
                        throw new RuntimeException("Q required for event.getClassification()");
                    }
                    final boolean ambiguousGesture =
                            motionClassification == MotionEvent.CLASSIFICATION_AMBIGUOUS_GESTURE;
                    int touchSlop = mTouchSlop;
                    if (ambiguousGesture && ViewUtils.hasPendingLongPressCallback(view)) {
                        final float ambiguousMultiplier =
                                ViewConfiguration.getAmbiguousGestureMultiplier();
                        if (!ViewUtils.pointInView(view, x, y, touchSlop)) {
                            // The default action here is to cancel long press. But instead, we
                            // just extend the timeout here, in case the classification
                            // stays ambiguous.
                            ViewUtils.removeLongPressCallback(view);
                            long delay = (long) (ViewConfiguration.getLongPressTimeout()
                                    * ambiguousMultiplier);
                            // Subtract the time already spent
                            delay -= event.getEventTime() - event.getDownTime();
                            ViewUtils.checkForLongClick(
                                    view,
                                    delay,
                                    x,
                                    y,
                                    TOUCH_GESTURE_CLASSIFIED__CLASSIFICATION__LONG_PRESS);
                        }
                        touchSlop *= ambiguousMultiplier;
                    }

                    // Be lenient about moving outside of buttons
                    if (!ViewUtils.pointInView(view, x, y, touchSlop)) {
                        // Outside button
                        // Remove any future long press/tap checks
                        ViewUtils.removeTapCallback(view);
                        ViewUtils.removeLongPressCallback(view);
                        if ((mPrivateFlags & PFLAG_PRESSED) != 0) {
                            view.setPressed(false);
                        }
                        ViewUtils.set_mPrivateFlags3(view, ViewUtils.get_mPrivateFlags3(view) & ~PFLAG3_FINGER_DOWN);
                    }

                    final boolean deepPress =
                            motionClassification == MotionEvent.CLASSIFICATION_DEEP_PRESS;
                    if (deepPress && ViewUtils.hasPendingLongPressCallback(view)) {
                        // process the long click action immediately
                        ViewUtils.removeLongPressCallback(view);
                        ViewUtils.checkForLongClick(
                                view,
                                0 /* send immediately */,
                                x,
                                y,
                                TOUCH_GESTURE_CLASSIFIED__CLASSIFICATION__DEEP_PRESS);
                    }

                    break;
            }

            return true;
        }

        return false;
    }
}
