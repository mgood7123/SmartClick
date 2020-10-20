package smallville7123.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.DynamicLayout;
import android.text.SpannableStringBuilder;
import android.text.DynamicLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.LruCache;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.InspectableProperty;
import androidx.annotation.Nullable;

import smallville7123.taggable.Taggable;

/**
 * A user interface element for entering and modifying text.
 * <p>You also can receive callbacks as a user changes text by
 * adding a {@link android.text.TextWatcher} to the edit text.
 * This is useful when you want to add auto-save functionality as changes are made,
 * or validate the format of user input, for example.
 * You add a text watcher using the {@link android.widget.TextView#addTextChangedListener} method.
 * </p>
 * <p>
 * This widget does not support auto-sizing text.
 * <p>
 */

@SuppressLint("AppCompatCustomView")
public class TextView extends View implements ViewTreeObserver.OnPreDrawListener {
    public String TAG = Taggable.getTag(this);

    private static final int LINES = 1;
    private static final int PIXELS = 2;

    @Nullable
    private SpannableStringBuilder mText;
    private int mTextSize;
    private ColorStateList mTextColor;
    private boolean mPreDrawRegistered;
    private boolean mPreDrawListenerDetached;


    private final TextPaint mTextPaint;
    private final Paint mBackgroundPaint;
    DynamicLayout mDynamicLayout;

    int mCursorDrawableRes;
    private Drawable mCursorDrawable;

    private Context mContext;
    private TextBook textBook;

    /**
     * Kick-start the font cache for the zygote process (to pay the cost of
     * initializing freetype for our default font only once).
     */
    private static void preloadFontCache() {
        Paint p = new Paint();
        p.setAntiAlias(true);
        // Ensure that the Typeface is loaded here.
        // Typically, Typeface is preloaded by zygote but not on all devices, e.g. Android Auto.
        // So, sets Typeface.DEFAULT explicitly here for ensuring that the Typeface is loaded here
        // since Paint.measureText can not be called without Typeface static initializer.
        p.setTypeface(Typeface.DEFAULT);
        // We don't care about the result, just the side-effect of measuring.
        p.measureText("H");
    }

    final Resources res;

    public TextView(Context context) {
        this(context, null);
    }

    public TextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public TextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public TextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        preloadFontCache();
        // TextView is important by default, unless app developer overrode attribute.
        if (getImportantForAutofill() == IMPORTANT_FOR_AUTOFILL_AUTO) {
            setImportantForAutofill(IMPORTANT_FOR_AUTOFILL_YES);
        }

        mText = new SpannableStringBuilder();
        textBook = new TextBook(mText);

        res = getResources();

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.density = res.getDisplayMetrics().density;
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(Color.WHITE);
        mBackgroundPaint.setStyle(Paint.Style.FILL);

        setTextSize(30.0f);
        mTextColor = ColorStateList.valueOf(0xFF000000);
    }

    private void setTextInternal(@Nullable CharSequence text) {
        mText.clear();
        mText.clearSpans();
        mText.append(text);
        mText.append(String.valueOf(repeat(mText, 10)));
    }

    public void setText(String text) {
        setTextInternal(text);
    }

    /**
     * Set the default text size to the given value, interpreted as "scaled
     * pixel" units.  This size is adjusted based on the current density and
     * user font size preference.
     *
     * <p>Note: if this TextView has the auto-size feature enabled than this function is no-op.
     *
     * @param size The scaled pixel size.
     *
     * @attr ref android.R.styleable#TextView_textSize
     */
    public void setTextSize(float size) {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    /**
     * Set the default text size to a given unit and value. See {@link
     * TypedValue} for the possible dimension units.
     *
     * <p>Note: if this TextView has the auto-size feature enabled than this function is no-op.
     *
     * @param unit The desired dimension unit.
     * @param size The desired size in the given units.
     *
     * @attr ref android.R.styleable#TextView_textSize
     */
    public void setTextSize(int unit, float size) {
        if (!isAutoSizeEnabled()) {
            setTextSizeInternal(unit, size, true /* shouldRequestLayout */);
        }
    }

    private boolean isAutoSizeEnabled() {
        return false;
    }

    private void setTextSizeInternal(int unit, float size, boolean shouldRequestLayout) {
        Context c = getContext();
        Resources r;

        if (c == null) {
            r = Resources.getSystem();
        } else {
            r = c.getResources();
        }

        setRawTextSize(TypedValue.applyDimension(unit, size, r.getDisplayMetrics()),
                shouldRequestLayout);
    }

    private void setRawTextSize(float size, boolean shouldRequestLayout) {
        if (size != mTextPaint.getTextSize()) {
            mTextPaint.setTextSize(size);

            if (shouldRequestLayout) {
                requestLayout();
                invalidate();
            }
        }
    }

    /**
     * Sets the typeface and style in which the text should be displayed,
     * and turns on the fake bold and italic bits in the Paint if the
     * Typeface that you provided does not have all the bits in the
     * style that you specified.
     *
     * @attr ref android.R.styleable#TextView_typeface
     * @attr ref android.R.styleable#TextView_textStyle
     */
    public void setTypeface(@Nullable Typeface tf, int style) {
        if (style > 0) {
            if (tf == null) {
                tf = Typeface.defaultFromStyle(style);
            } else {
                tf = Typeface.create(tf, style);
            }

            setTypeface(tf);
            // now compute what (if any) algorithmic styling is needed
            int typefaceStyle = tf != null ? tf.getStyle() : 0;
            int need = style & ~typefaceStyle;
            mTextPaint.setFakeBoldText((need & Typeface.BOLD) != 0);
            mTextPaint.setTextSkewX((need & Typeface.ITALIC) != 0 ? -0.25f : 0);
        } else {
            mTextPaint.setFakeBoldText(false);
            mTextPaint.setTextSkewX(0);
            setTypeface(tf);
        }
    }

    /**
     * Sets the typeface and style in which the text should be displayed.
     * Note that not all Typeface families actually have bold and italic
     * variants, so you may need to use
     * {@link #setTypeface(Typeface, int)} to get the appearance
     * that you actually want.
     *
     * @see #getTypeface()
     *
     * @attr ref android.R.styleable#TextView_fontFamily
     * @attr ref android.R.styleable#TextView_typeface
     * @attr ref android.R.styleable#TextView_textStyle
     */
    public void setTypeface(@Nullable Typeface tf) {
        if (mTextPaint.getTypeface() != tf) {
            mTextPaint.setTypeface(tf);
            requestLayout();
            invalidate();
        }
    }

    /**
     * Gets the current {@link Typeface} that is used to style the text.
     * @return The current Typeface.
     *
     * @see #setTypeface(Typeface)
     *
     * @attr ref android.R.styleable#TextView_fontFamily
     * @attr ref android.R.styleable#TextView_typeface
     * @attr ref android.R.styleable#TextView_textStyle
     */
    @InspectableProperty
    public Typeface getTypeface() {
        return mTextPaint.getTypeface();
    }

    /**
     * Callback method to be invoked when the view tree is about to be drawn. At this point, all
     * views in the tree have been measured and given a frame. Clients can use this to adjust
     * their scroll bounds or even to request a new layout before drawing occurs.
     *
     * @return Return true to proceed with the current drawing pass, or false to cancel.
     * @see View#onMeasure
     * @see View#onLayout
     * @see View#onDraw
     */
    @Override
    public boolean onPreDraw() {
        return true;
    }

    class DynamicLayoutCache {
        private final int MAX_SIZE = 50; // Max number of cached items
        private final LruCache cache = new LruCache<String, DynamicLayout>(MAX_SIZE);

        void set(String key, DynamicLayout dynamicLayout) {
            cache.put(key, dynamicLayout);
        }

        @Nullable DynamicLayout get(String key) {
            return (DynamicLayout) cache.get(key);
        }
    }

    DynamicLayoutCache cache = new DynamicLayoutCache();

    @Override
    protected void onDraw(final Canvas canvas) {
        // Draw the background for this view
        super.onDraw(canvas);
        canvas.drawPaint(mBackgroundPaint);
        textBook.draw(canvas, mTextPaint);
//        String cacheKey = String.valueOf(width);
//        DynamicLayout tmp = cache.get(cacheKey);
//        if (tmp == null) {
//            tmp = DynamicLayout.Builder.obtain(mText, mTextPaint, width).build();
//            cache.set(cacheKey, tmp);
//        }
//        mDynamicLayout = tmp;
//        mText.append("\nheight = " + mDynamicLayout.getHeight());
//        mText.append("\nheight = " + mDynamicLayout.getHeight());
//        mText.append("\nheight = " + mDynamicLayout.getHeight());
//        mText.append("\nheight = " + mDynamicLayout.getHeight());
//        mText.append("\nheight = " + mDynamicLayout.getHeight());
//        mDynamicLayout.draw(canvas);
    }

    /**
     * returns the result of appending text to itself n times
     */
    @Nullable
    public static char[] repeat(CharSequence text, int n) {
        return repeat(toArray(text, text.length()), n);
    }

    /**
     * returns the result of appending text to itself n times
     */
    @Nullable
    public static char[] repeat(char[] text, int n) {
        if (text == null) return null;
        int totalLength = text.length*n;
        char[] x = new char[totalLength];
        for (int i = 0; i < totalLength; i += text.length) {
            System.arraycopy(text, 0, x, i, text.length);
        }
        return x;
    }

    @Nullable
    public static char[] toArray(CharSequence text, int length) {
        if (text == null) return null;
        char[] array = new char[length];
        for (int i = 0; i < length; i++) array[i] = text.charAt(i);
        return array;
    }
}
