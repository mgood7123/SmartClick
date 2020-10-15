package smallville7123.views;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.lang.reflect.InvocationTargetException;

import smallville7123.layoututils.LayoutUtils;
import smallville7123.taggable.Taggable;

import static smallville7123.layoututils.LayoutUtils.constructView;

class Task extends LinearLayout {

    private final String TAG = Taggable.getTag(this);

    public Task(Context context) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        super(context);
        construct(context, null, null, null);
    }

    public Task(Context context, AttributeSet attrs) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        super(context, attrs);
        construct(context, attrs, null, null);
    }

    public Task(Context context, AttributeSet attrs, int defStyleAttr) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        super(context, attrs, defStyleAttr);
        construct(context, attrs, defStyleAttr, null);
    }

    public Task(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        super(context, attrs, defStyleAttr, defStyleRes);
        construct(context, attrs, defStyleAttr, defStyleRes);
    }

    int imageSrc;

    String text;
    int textSize;
    int textColor;
    void getAttributeParameters(Context context, AttributeSet attrs, Resources.Theme theme) {
        if (attrs != null) {
            TypedArray attributes = theme.obtainStyledAttributes(attrs, R.styleable.Task, 0, 0);
            imageSrc = attributes.getResourceId(R.styleable.Task_android_src, -1);
            text = attributes.getString(R.styleable.Task_android_text);
            textSize = LayoutUtils.getTextSizeAttributesSuitableForTextView(attributes, R.styleable.Task_android_textSize);
            textColor = attributes.getColor(R.styleable.Task_android_textColor, Color.BLACK);
            attributes.recycle();
        }
    }

    void construct(Context context, AttributeSet attrs, Integer defStyleAttr, Integer defStyleRes) throws NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Resources.Theme theme = TaskBuilder.getContext(this, context).getTheme();
        getAttributeParameters(context, attrs, theme);

        build_layer_1(context, attrs, defStyleAttr, defStyleRes);
    }

    ImageView imageView;

    TextView textView;
    private void build_layer_1(Context context, AttributeSet attrs, Integer defStyleAttr, Integer defStyleRes) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // create all our instances
//        imageView = constructView(ImageView.class, context, attrs, defStyleAttr, defStyleRes);
        textView = constructView(TextView.class, context, attrs, defStyleAttr, defStyleRes);

        // set our parameters
        setImageResource(imageSrc);
        setText(text);
        setTextSize(textSize);
        setTextColor(textColor);

        // add our views
        Log.i(TAG, "build_layer_1: " + getWidth());
//        addView(imageView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(textView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private void setImageResource(int res) {
        imageSrc = res;
        if (imageSrc != -1)
            if (imageView != null)
                imageView.setImageResource(imageSrc);
    }

    private void setText(String text) {
        if (text != null) textView.setText(text);
    }

    private void setTextSize(int textSize) {
        LayoutUtils.setTextSizeAttributesSuitableForTextView(textView, textSize);
    }

    private void setTextColor(int textColor) {
        textView.setTextColor(textColor);
    }

    /**
     * Creates and returns a copy of this object.  The precise meaning
     * of "copy" may depend on the class of the object. The general
     * intent is that, for any object {@code x}, the expression:
     * <blockquote>
     * <pre>
     * x.clone() != x</pre></blockquote>
     * will be true, and that the expression:
     * <blockquote>
     * <pre>
     * x.clone().getClass() == x.getClass()</pre></blockquote>
     * will be {@code true}, but these are not absolute requirements.
     * While it is typically the case that:
     * <blockquote>
     * <pre>
     * x.clone().equals(x)</pre></blockquote>
     * will be {@code true}, this is not an absolute requirement.
     * <p>
     * By convention, the returned object should be obtained by calling
     * {@code super.clone}.  If a class and all of its superclasses (except
     * {@code Object}) obey this convention, it will be the case that
     * {@code x.clone().getClass() == x.getClass()}.
     * <p>
     * By convention, the object returned by this method should be independent
     * of this object (which is being cloned).  To achieve this independence,
     * it may be necessary to modify one or more fields of the object returned
     * by {@code super.clone} before returning it.  Typically, this means
     * copying any mutable objects that comprise the internal "deep structure"
     * of the object being cloned and replacing the references to these
     * objects with references to the copies.  If a class contains only
     * primitive fields or references to immutable objects, then it is usually
     * the case that no fields in the object returned by {@code super.clone}
     * need to be modified.
     * <p>
     * The method {@code clone} for class {@code Object} performs a
     * specific cloning operation. First, if the class of this object does
     * not implement the interface {@code Cloneable}, then a
     * {@code CloneNotSupportedException} is thrown. Note that all arrays
     * are considered to implement the interface {@code Cloneable} and that
     * the return type of the {@code clone} method of an array type {@code T[]}
     * is {@code T[]} where T is any reference or primitive type.
     * Otherwise, this method creates a new instance of the class of this
     * object and initializes all its fields with exactly the contents of
     * the corresponding fields of this object, as if by assignment; the
     * contents of the fields are not themselves cloned. Thus, this method
     * performs a "shallow copy" of this object, not a "deep copy" operation.
     * <p>
     * The class {@code Object} does not itself implement the interface
     * {@code Cloneable}, so calling the {@code clone} method on an object
     * whose class is {@code Object} will result in throwing an
     * exception at run time.
     *
     * @return a clone of this instance.
     * @throws CloneNotSupportedException if the object's class does not
     *                                    support the {@code Cloneable} interface. Subclasses
     *                                    that override the {@code clone} method can also
     *                                    throw this exception to indicate that an instance cannot
     *                                    be cloned.
     * @see Cloneable
     */
    @NonNull
    final Task clone(Context context) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Task task = new Task(context);
        task.setImageResource(imageSrc);
        task.setText(text);
        task.setTextSize(textSize);
        task.setTextColor(textColor);
        return task;
    }

    /**
     * Creates and returns a copy of this object.  The precise meaning
     * of "copy" may depend on the class of the object. The general
     * intent is that, for any object {@code x}, the expression:
     * <blockquote>
     * <pre>
     * x.clone() != x</pre></blockquote>
     * will be true, and that the expression:
     * <blockquote>
     * <pre>
     * x.clone().getClass() == x.getClass()</pre></blockquote>
     * will be {@code true}, but these are not absolute requirements.
     * While it is typically the case that:
     * <blockquote>
     * <pre>
     * x.clone().equals(x)</pre></blockquote>
     * will be {@code true}, this is not an absolute requirement.
     * <p>
     * By convention, the returned object should be obtained by calling
     * {@code super.clone}.  If a class and all of its superclasses (except
     * {@code Object}) obey this convention, it will be the case that
     * {@code x.clone().getClass() == x.getClass()}.
     * <p>
     * By convention, the object returned by this method should be independent
     * of this object (which is being cloned).  To achieve this independence,
     * it may be necessary to modify one or more fields of the object returned
     * by {@code super.clone} before returning it.  Typically, this means
     * copying any mutable objects that comprise the internal "deep structure"
     * of the object being cloned and replacing the references to these
     * objects with references to the copies.  If a class contains only
     * primitive fields or references to immutable objects, then it is usually
     * the case that no fields in the object returned by {@code super.clone}
     * need to be modified.
     * <p>
     * The method {@code clone} for class {@code Object} performs a
     * specific cloning operation. First, if the class of this object does
     * not implement the interface {@code Cloneable}, then a
     * {@code CloneNotSupportedException} is thrown. Note that all arrays
     * are considered to implement the interface {@code Cloneable} and that
     * the return type of the {@code clone} method of an array type {@code T[]}
     * is {@code T[]} where T is any reference or primitive type.
     * Otherwise, this method creates a new instance of the class of this
     * object and initializes all its fields with exactly the contents of
     * the corresponding fields of this object, as if by assignment; the
     * contents of the fields are not themselves cloned. Thus, this method
     * performs a "shallow copy" of this object, not a "deep copy" operation.
     * <p>
     * The class {@code Object} does not itself implement the interface
     * {@code Cloneable}, so calling the {@code clone} method on an object
     * whose class is {@code Object} will result in throwing an
     * exception at run time.
     *
     * @return a clone of this instance.
     * @throws CloneNotSupportedException if the object's class does not
     *                                    support the {@code Cloneable} interface. Subclasses
     *                                    that override the {@code clone} method can also
     *                                    throw this exception to indicate that an instance cannot
     *                                    be cloned.
     * @see Cloneable
     */
    @NonNull
    final Task clone(Context context, AttributeSet attrs) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Task task = new Task(context, attrs);
        task.setImageResource(imageSrc);
        task.setText(text);
        task.setTextSize(textSize);
        task.setTextColor(textColor);
        return task;
    }

    /**
     * Creates and returns a copy of this object.  The precise meaning
     * of "copy" may depend on the class of the object. The general
     * intent is that, for any object {@code x}, the expression:
     * <blockquote>
     * <pre>
     * x.clone() != x</pre></blockquote>
     * will be true, and that the expression:
     * <blockquote>
     * <pre>
     * x.clone().getClass() == x.getClass()</pre></blockquote>
     * will be {@code true}, but these are not absolute requirements.
     * While it is typically the case that:
     * <blockquote>
     * <pre>
     * x.clone().equals(x)</pre></blockquote>
     * will be {@code true}, this is not an absolute requirement.
     * <p>
     * By convention, the returned object should be obtained by calling
     * {@code super.clone}.  If a class and all of its superclasses (except
     * {@code Object}) obey this convention, it will be the case that
     * {@code x.clone().getClass() == x.getClass()}.
     * <p>
     * By convention, the object returned by this method should be independent
     * of this object (which is being cloned).  To achieve this independence,
     * it may be necessary to modify one or more fields of the object returned
     * by {@code super.clone} before returning it.  Typically, this means
     * copying any mutable objects that comprise the internal "deep structure"
     * of the object being cloned and replacing the references to these
     * objects with references to the copies.  If a class contains only
     * primitive fields or references to immutable objects, then it is usually
     * the case that no fields in the object returned by {@code super.clone}
     * need to be modified.
     * <p>
     * The method {@code clone} for class {@code Object} performs a
     * specific cloning operation. First, if the class of this object does
     * not implement the interface {@code Cloneable}, then a
     * {@code CloneNotSupportedException} is thrown. Note that all arrays
     * are considered to implement the interface {@code Cloneable} and that
     * the return type of the {@code clone} method of an array type {@code T[]}
     * is {@code T[]} where T is any reference or primitive type.
     * Otherwise, this method creates a new instance of the class of this
     * object and initializes all its fields with exactly the contents of
     * the corresponding fields of this object, as if by assignment; the
     * contents of the fields are not themselves cloned. Thus, this method
     * performs a "shallow copy" of this object, not a "deep copy" operation.
     * <p>
     * The class {@code Object} does not itself implement the interface
     * {@code Cloneable}, so calling the {@code clone} method on an object
     * whose class is {@code Object} will result in throwing an
     * exception at run time.
     *
     * @return a clone of this instance.
     * @throws CloneNotSupportedException if the object's class does not
     *                                    support the {@code Cloneable} interface. Subclasses
     *                                    that override the {@code clone} method can also
     *                                    throw this exception to indicate that an instance cannot
     *                                    be cloned.
     * @see Cloneable
     */
    @NonNull
    final Task clone(Context context, AttributeSet attrs, int defStyleAttr) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Task task = new Task(context, attrs, defStyleAttr);
        task.setImageResource(imageSrc);
        task.setText(text);
        task.setTextSize(textSize);
        task.setTextColor(textColor);
        return task;
    }

    /**
     * Creates and returns a copy of this object.  The precise meaning
     * of "copy" may depend on the class of the object. The general
     * intent is that, for any object {@code x}, the expression:
     * <blockquote>
     * <pre>
     * x.clone() != x</pre></blockquote>
     * will be true, and that the expression:
     * <blockquote>
     * <pre>
     * x.clone().getClass() == x.getClass()</pre></blockquote>
     * will be {@code true}, but these are not absolute requirements.
     * While it is typically the case that:
     * <blockquote>
     * <pre>
     * x.clone().equals(x)</pre></blockquote>
     * will be {@code true}, this is not an absolute requirement.
     * <p>
     * By convention, the returned object should be obtained by calling
     * {@code super.clone}.  If a class and all of its superclasses (except
     * {@code Object}) obey this convention, it will be the case that
     * {@code x.clone().getClass() == x.getClass()}.
     * <p>
     * By convention, the object returned by this method should be independent
     * of this object (which is being cloned).  To achieve this independence,
     * it may be necessary to modify one or more fields of the object returned
     * by {@code super.clone} before returning it.  Typically, this means
     * copying any mutable objects that comprise the internal "deep structure"
     * of the object being cloned and replacing the references to these
     * objects with references to the copies.  If a class contains only
     * primitive fields or references to immutable objects, then it is usually
     * the case that no fields in the object returned by {@code super.clone}
     * need to be modified.
     * <p>
     * The method {@code clone} for class {@code Object} performs a
     * specific cloning operation. First, if the class of this object does
     * not implement the interface {@code Cloneable}, then a
     * {@code CloneNotSupportedException} is thrown. Note that all arrays
     * are considered to implement the interface {@code Cloneable} and that
     * the return type of the {@code clone} method of an array type {@code T[]}
     * is {@code T[]} where T is any reference or primitive type.
     * Otherwise, this method creates a new instance of the class of this
     * object and initializes all its fields with exactly the contents of
     * the corresponding fields of this object, as if by assignment; the
     * contents of the fields are not themselves cloned. Thus, this method
     * performs a "shallow copy" of this object, not a "deep copy" operation.
     * <p>
     * The class {@code Object} does not itself implement the interface
     * {@code Cloneable}, so calling the {@code clone} method on an object
     * whose class is {@code Object} will result in throwing an
     * exception at run time.
     *
     * @return a clone of this instance.
     * @throws CloneNotSupportedException if the object's class does not
     *                                    support the {@code Cloneable} interface. Subclasses
     *                                    that override the {@code clone} method can also
     *                                    throw this exception to indicate that an instance cannot
     *                                    be cloned.
     * @see Cloneable
     */
    @NonNull
    final Task clone(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Task task = new Task(context, attrs, defStyleAttr, defStyleRes);
        task.setImageResource(imageSrc);
        task.setText(text);
        task.setTextSize(textSize);
        task.setTextColor(textColor);
        return task;
    }
}
