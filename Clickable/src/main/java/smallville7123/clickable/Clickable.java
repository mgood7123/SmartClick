package smallville7123.clickable;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import smallville7123.taggable.Taggable;

public abstract class Clickable implements View.OnTouchListener {
    public MotionEvent motionEvent;
    // Often, there will be a slight, unintentional, drag when the user taps on the screen,
    // so we need to account for this.
    // TODO: would this value be affected by screen density?
    private static final float CLICK_DRAG_TOLERANCE = 30.0F;
    private final String TAG = Taggable.getTag(this);
    public float downX, downRawX, downY, downRawY, upX, upRawX, upY, upRawY, upDX, upDY;

    public abstract void onClick(View v);

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        motionEvent = event;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downRawX = event.getRawX();
                downY = event.getY();
                downRawY = event.getRawY();
                return true;

            case MotionEvent.ACTION_UP:
                upX = event.getY();
                upRawX = event.getRawX();
                upY = event.getY();
                upRawY = event.getRawY();
                upDX = upRawX - downRawX;
                upDY = upRawY - downRawY;
                // TODO: would this value be affected by screen density?
                if ((Math.abs(upDX) < CLICK_DRAG_TOLERANCE) && (Math.abs(upDY) < CLICK_DRAG_TOLERANCE)) {
                    // assume that the drag was unintentional, restore the original x and y
                    Log.d(TAG, "upX = [" + upX + "]");
                    Log.d(TAG, "upRawX = [" + upRawX + "]");
                    Log.d(TAG, "upY = [" + upY + "]");
                    Log.d(TAG, "upRawY = [" + upRawY + "]");
                    Log.d(TAG, "upDX = [" + upDX + "]");
                    Log.d(TAG, "upDY = [" + upDY + "]");
                    onClick(v);
                }
                return true;

            case MotionEvent.ACTION_MOVE:
                return true;
        }
        return false;
    }
}