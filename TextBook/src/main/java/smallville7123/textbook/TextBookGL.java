package smallville7123.textbook;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class TextBookGL extends SurfaceView implements SurfaceHolder.Callback2 {

    private static final String TAG = "TextBookGL";

    static {
        System.loadLibrary("TextBookGL");
    }

    native void nativeSetSurface(Surface surface);

    public TextBookGL(Context context) {
        super(context);
        getHolder().addCallback(this);
    }

    public TextBookGL(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
    }

    public TextBookGL(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getHolder().addCallback(this);
    }

    public TextBookGL(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        getHolder().addCallback(this);
    }

    /**
     * This is called immediately after the surface is first created.
     * Implementations of this should start up whatever rendering code
     * they desire.  Note that only one thread can ever draw into
     * a {@link Surface}, so you should not draw into the Surface here
     * if your normal rendering will be in another thread.
     *
     * @param holder The SurfaceHolder whose surface is being created.
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        nativeSetSurface(holder.getSurface());
    }

    /**
     * This is called immediately after any structural changes (format or
     * size) have been made to the surface.  You should at this point update
     * the imagery in the surface.  This method is always called at least
     * once, after {@link #surfaceCreated}.
     *
     * @param holder The SurfaceHolder whose surface has changed.
     * @param format The new PixelFormat of the surface.
     * @param width  The new width of the surface.
     * @param height The new height of the surface.
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    /**
     * This is called immediately before a surface is being destroyed. After
     * returning from this call, you should no longer try to access this
     * surface.  If you have a rendering thread that directly accesses
     * the surface, you must ensure that thread is no longer touching the
     * Surface before returning from this function.
     *
     * @param holder The SurfaceHolder whose surface is being destroyed.
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        nativeSetSurface(null);
    }

    /**
     * Called when the application needs to redraw the content of its
     * surface, after it is resized or for some other reason.  By not
     * returning from here until the redraw is complete, you can ensure that
     * the user will not see your surface in a bad state (at its new
     * size before it has been correctly drawn that way).  This will
     * typically be preceeded by a call to {@link #surfaceChanged}.
     * <p>
     * As of O, {@link #surfaceRedrawNeededAsync} may be implemented
     * to provide a non-blocking implementation. If {@link #surfaceRedrawNeededAsync}
     * is not implemented, then this will be called instead.
     *
     * @param holder The SurfaceHolder whose surface has changed.
     */
    @Override
    public void surfaceRedrawNeeded(SurfaceHolder holder) {
        Log.d(TAG, "surfaceRedrawNeeded() called with: holder = [" + holder + "]");
    }

    /**
     * An alternative to surfaceRedrawNeeded where it is not required to block
     * until the redraw is complete. You should initiate the redraw, and return,
     * later invoking drawingFinished when your redraw is complete.
     * <p>
     * This can be useful to avoid blocking your main application thread on rendering.
     * <p>
     * As of O, if this is implemented {@link #surfaceRedrawNeeded} will not be called.
     * However it is still recommended to implement {@link #surfaceRedrawNeeded} for
     * compatibility with older versions of the platform.
     *
     * @param holder          The SurfaceHolder which needs redrawing.
     * @param drawingFinished A runnable to signal completion. This may be invoked
     */
    @Override
    public void surfaceRedrawNeededAsync(SurfaceHolder holder, Runnable drawingFinished) {
        Log.d(TAG, "surfaceRedrawNeededAsync() called with: holder = [" + holder + "], drawingFinished = [" + drawingFinished + "]");
    }
}
