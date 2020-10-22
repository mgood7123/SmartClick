package smallville7123.textbook;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.InspectableProperty;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;

import smallville7123.draggable.Draggable;

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
public class TextBookView extends View {
    static String TAG = "TextBookView";

    private final SpannableStringBuilder mText;
    private final TextBook mTextBook;
    private final Resources mRes;
    private final TextPaint mTextBookPaint;
    private final Paint mBackgroundPaint;
    private final ColorStateList mTextBookTextColor;
    private final Context mContext;
    public static final String SAMPLE_TEXT_SHORT = "sample text";
    public static final String SAMPLE_TEXT_LONG = "Wiki";

    public String getSampleText() {
        try {
            InputStream i = mRes.getAssets().open(SAMPLE_TEXT_LONG);
            return TextBook.readTextFile(i);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String setSampleText() {
        try {
            mTextBook.setText(mRes.getAssets().open(SAMPLE_TEXT_LONG));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public TextBookView(Context context) {
        this(context, null);
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     * that were specified in the XML file. This version uses a default style of
     * 0, so the only attribute values applied are those in the Context's Theme
     * and the given AttributeSet.
     *
     * <p>
     * The method onFinishInflate() will be called after all children have been
     * added.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     * @see #TextBookView(Context, AttributeSet, int)
     */
    public TextBookView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, android.R.attr.scrollViewStyle);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style from a
     * theme attribute. This constructor of View allows subclasses to use their
     * own base style when they are inflating. For example, a Button class's
     * constructor would call this version of the super class constructor and
     * supply <code>R.attr.buttonStyle</code> for <var>defStyleAttr</var>; this
     * allows the theme's button style to modify all of the base view attributes
     * (in particular its background) as well as the Button class's attributes.
     *
     * @param context      The Context the view is running in, through which it can
     *                     access the current theme, resources, etc.
     * @param attrs        The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a
     *                     reference to a style resource that supplies default values for
     *                     the view. Can be 0 to not look for defaults.
     * @see #TextBookView(Context, AttributeSet)
     */
    public TextBookView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style from a
     * theme attribute or style resource. This constructor of View allows
     * subclasses to use their own base style when they are inflating.
     * <p>
     * When determining the final value of a particular attribute, there are
     * four inputs that come into play:
     * <ol>
     * <li>Any attribute values in the given AttributeSet.
     * <li>The style resource specified in the AttributeSet (named "style").
     * <li>The default style specified by <var>defStyleAttr</var>.
     * <li>The default style specified by <var>defStyleRes</var>.
     * <li>The base values in this theme.
     * </ol>
     * <p>
     * Each of these inputs is considered in-order, with the first listed taking
     * precedence over the following ones. In other words, if in the
     * AttributeSet you have supplied <code>&lt;Button * textColor="#ff000000"&gt;</code>
     * , then the button's text will <em>always</em> be black, regardless of
     * what is specified in any of the styles.
     *
     * @param context      The Context the view is running in, through which it can
     *                     access the current theme, resources, etc.
     * @param attrs        The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a
     *                     reference to a style resource that supplies default values for
     *                     the view. Can be 0 to not look for defaults.
     * @param defStyleRes  A resource identifier of a style resource that
     *                     supplies default values for the view, used only if
     *                     defStyleAttr is 0 or can not be found in the theme. Can be 0
     *                     to not look for defaults.
     * @see #TextBookView(Context, AttributeSet, int)
     */
    public TextBookView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        // since TextBookView can be used as a drop-in TextView, it should also be
        // important for AutoFill
        // TextView is important by default, unless app developer overrode attribute.
        if (getImportantForAutofill() == IMPORTANT_FOR_AUTOFILL_AUTO) {
            setImportantForAutofill(IMPORTANT_FOR_AUTOFILL_YES);
        }

        mContext = context;
        mText = new SpannableStringBuilder();
        mTextBook = new TextBook(mText);
        mRes = getResources();
        mTextBookPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextBookPaint.density = mRes.getDisplayMetrics().density;
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(Color.WHITE);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        setTextSize(30.0f);
        mTextBookTextColor = ColorStateList.valueOf(0xFF000000);

        initScrollView();
    }

    private void setTextInternal(@Nullable CharSequence text) {
        mText.clear();
        mText.clearSpans();
        mText.append(text);
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
//        if (!isAutoSizeEnabled()) {
            setTextSizeInternal(unit, size, true /* shouldRequestLayout */);
//        }
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
        if (size != mTextBookPaint.getTextSize()) {
            mTextBookPaint.setTextSize(size);

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
            mTextBookPaint.setFakeBoldText((need & Typeface.BOLD) != 0);
            mTextBookPaint.setTextSkewX((need & Typeface.ITALIC) != 0 ? -0.25f : 0);
        } else {
            mTextBookPaint.setFakeBoldText(false);
            mTextBookPaint.setTextSkewX(0);
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
        if (mTextBookPaint.getTypeface() != tf) {
            mTextBookPaint.setTypeface(tf);
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
        return mTextBookPaint.getTypeface();
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        // Draw the background for this view
        super.onDraw(canvas);
        canvas.drawPaint(mBackgroundPaint);
        mTextBook.draw(canvas, mTextBookPaint);
    }





    // ScrollView

    private void initScrollView() {
        setOnTouchListener(new Draggable() {

            @Override
            public boolean isClickable() {
                return false;
            }

            boolean invertX = true;
            boolean invertY = true;

            @Override
            public void getViewLocationOnScreen(int[] outLocation) {
                outLocation[0] = mTextBook.getOffsetX();
                outLocation[1] = mTextBook.getOffsetY();
            }

            @Override
            public int getX() {
                int x = mTextBook.getOffsetX();
                return invertX ? -x : x;
            }

            @Override
            public void setX(int x) {
                mTextBook.setOffsetX(invertX ? -x : x);
                invalidate();
            }

            @Override
            public int getY() {
                int y = mTextBook.getOffsetY();
                return invertY ? -y : y;
            }

            @Override
            public void setY(int y) {
                mTextBook.setOffsetY(invertY ? -y : y);
                invalidate();
            }

            @Override
            public void onClick(View v) {

            }

            @Override
            public void onDrag(View v) {

            }

            @Override
            public void onMovement(View v) {

            }

            @Override
            public int getLayoutParamsWidth() {
                return 1; // do not drag on width axis
            }

            @Override
            public int getLayoutParamsHeight() {
                return 0; // drag on height axis
            }

            @Override
            public int getLayoutParamsWRAP_CONTENT() {
                // if this matches above param calls, then movement for corresponding
                // axis is enabled
                return 0;
            }
        });
    }

    private SavedState mSavedState;

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Log.d(TAG, "onRestoreInstanceState() called with: state = [" + state + "]");
        if (mContext.getApplicationInfo().targetSdkVersion <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            // Some old apps reused IDs in ways they shouldn't have.
            // Don't break them, but they don't get scroll state restoration.
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mSavedState = ss;
        mTextBook.offset_x = mSavedState.scrollPositionX;
        mTextBook.offset_y = mSavedState.scrollPositionY;
        requestLayout();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Log.d(TAG, "onSaveInstanceState() called");
        if (mContext.getApplicationInfo().targetSdkVersion <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            // Some old apps reused IDs in ways they shouldn't have.
            // Don't break them, but they don't get scroll state restoration.
            return super.onSaveInstanceState();
        }
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.scrollPositionX = mTextBook.offset_x;
        ss.scrollPositionY = mTextBook.offset_y;
        return ss;
    }

    static class SavedState extends BaseSavedState {
        public int scrollPositionX;
        public int scrollPositionY;

        SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source) {
            super(source);
            scrollPositionX = source.readInt();
            scrollPositionY = source.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(scrollPositionX);
            dest.writeInt(scrollPositionY);
        }

        @Override
        public String toString() {
            return "TextBookView.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " scrollPositionX=" + scrollPositionX + ", scrollPositionY=" + scrollPositionY + "}";
        }

        public static final @NonNull Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
