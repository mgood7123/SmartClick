package smallville7123.draggable;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Size;

import smallville7123.clickable.Clickable;
import smallville7123.taggable.Taggable;


public abstract class Draggable extends Clickable {
    // Often, there will be a slight, unintentional, drag when the user taps on the screen,
    // so we need to account for this.
    // TODO: would this value be affected by screen density?
    private static final float CLICK_DRAG_TOLERANCE = 30.0F;
    private final String TAG = Taggable.getTag(this);
    public float downRawX, downRawY, downDX, downDY, newX, newY;
    int originalX;
    int originalY;
    public abstract void getViewLocationOnScreen(@Size(2) int[] outLocation);
    int[] location = new int[2];
    int[] locationPrevious = new int[2];

    public abstract int getX();
    public abstract void setX(int x);
    public abstract int getY();
    public abstract void setY(int y);
    public abstract void onClick(View v);
    public abstract void onDrag(View v);
    public abstract void onMovement(View v);
    public abstract int getLayoutParamsWidth();
    public abstract int getLayoutParamsHeight();
    public abstract int getLayoutParamsWRAP_CONTENT();
    public boolean isClickable() {
        return true;
    };

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        motionEvent = event;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                originalX = getX();
                originalY = getY();
                downRawX = event.getRawX();
                downRawY = event.getRawY();
                downDX = originalX - downRawX;
                downDY = originalY - downRawY;
                return true;

            case MotionEvent.ACTION_UP:
                upRawX = event.getRawX();
                upRawY = event.getRawY();
                upDX = upRawX - downRawX;
                upDY = upRawY - downRawY;
                // TODO: would this value be affected by screen density?
                if (isClickable()) {
                    if ((Math.abs(upDX) < CLICK_DRAG_TOLERANCE) && (Math.abs(upDY) < CLICK_DRAG_TOLERANCE)) {
                        // assume that the drag was unintentional, restore the original x and y
                        setX(originalX);
                        setY(originalY);
                        onMovement(v);
                        onClick(v);
                    } else {
                        onDrag(v);
                    }
                } else {
                    onDrag(v);
                }
                return true;

            case MotionEvent.ACTION_MOVE:
                float savedNewY = 0;
                int savedY = 0;
                float savedNewX = 0;
                int savedX = 0;
                boolean needsLayout = false;
                boolean widthWrap = getLayoutParamsWidth() == getLayoutParamsWRAP_CONTENT();
                boolean heightWrap = getLayoutParamsHeight() == getLayoutParamsWRAP_CONTENT();
                if (widthWrap) {
                    locationPrevious[0] = location[0];
                    savedNewX = newX;
                    savedX = getX();
                    newX = event.getRawX() + downDX;
                    setX((int) newX);
                    needsLayout = true;
                }
                if (heightWrap) {
                    locationPrevious[1] = location[1];
                    savedNewY = newY;
                    savedY = getY();
                    newY = event.getRawY() + downDY;
                    setY((int) newY);
                    needsLayout = true;
                }
                if (needsLayout) {
                    onMovement(v);
                    getViewLocationOnScreen(location);
                    if (widthWrap) {
                        if (locationPrevious[0] == location[0]) {
                            newX = savedNewX;
                            setX(savedX);
                        }
                    }
                    if (heightWrap) {
                        if (locationPrevious[1] == location[1]) {
                            newY = savedNewY;
                            setY(savedY);
                        }
                    }
                }
                return true;
        }
        return false;
    }
}