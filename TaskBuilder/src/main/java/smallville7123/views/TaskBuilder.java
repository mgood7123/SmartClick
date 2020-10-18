package smallville7123.views;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewPropertyAnimator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;

import smallville7123.layoututils.LayoutUtils;
import smallville7123.tools.Builder;
import smallville7123.tools.ConstraintBuilder;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.widget.LinearLayout.SHOW_DIVIDER_MIDDLE;
import static androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
import static smallville7123.layoututils.LayoutUtils.constructView;
import static smallville7123.layoututils.LayoutUtils.setTextSizeAttributesSuitableForTextView;
import static smallville7123.layoututils.LayoutUtils.toDP;
import static smallville7123.views.R.styleable.TaskBuilder_Layout;
import static smallville7123.views.R.styleable.TaskBuilder_Parameters;

public class TaskBuilder extends ConstraintLayout {

    public static final String TAG = "TaskBuilder";
    static LayoutParams matchParent = new LayoutParams(MATCH_PARENT, MATCH_PARENT);
    static LayoutParams wrapContent = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
    static LayoutParams matchConstraint = new LayoutParams(MATCH_CONSTRAINT, MATCH_CONSTRAINT);
    private Drawable taskMenu_background;
    private Drawable taskBuilder_background;
    private Drawable divider;

    @Override
    public void setBackground(final Drawable background) {
        // no background
    }

    @Override
    public void setBackgroundResource(final int resid) {
        // no background
    }

    @Override
    public void setBackgroundColor(final int color) {
        // no background
    }


    static class TaskData {
        CharSequence text;
        Drawable icon;
    }

    ArrayList<ViewHolder> taskBuilders = new ArrayList(Collections.singleton(new ViewHolder() {

        @Override
        boolean process(View view, TaskData taskData) {
            if (view instanceof Task) {
                Task task = ((Task) view);
                taskData.text = task.text;
                if (task.imageView != null) taskData.icon = task.imageView.getDrawable();
                return true;
            } else if (view instanceof TextView) {
                TextView textView = ((TextView) view);
                // no image
                taskData.text = textView.getText();
                return true;
            }
            return false;
        }
    }));

    public void addTask(View view) {
        TaskData taskData = new TaskData();
        boolean processed = false;
        for (ViewHolder taskBuilder : taskBuilders) {
            if (taskBuilder.process(view, taskData)) {
                processed = true;
                break;
            }
        }
        if (processed) {
            Task tmp = new Task(getContext());
            tmp.setBackgroundResource(R.drawable.outline);
            tmp.setText(taskData.text);
            tmp.setTextColor(textColor);
            // accepts a TextViewSize object
            tmp.setTextSize(textSize);
            tmp.setImage(taskData.icon);
            if (views_TaskList.getChildAt(0) instanceof PLACEHOLDER) views_TaskList.removeViewAt(0);
            views_TaskList.addView(tmp);
        } else throw new RuntimeException("failed to process view: " + view);
    }

    abstract static class ViewHolder {
        abstract boolean process(View view, TaskData taskData);
    }

    private static class Internal {}
    Internal Internal = new Internal();
    private boolean showTaskMenu;
    private String nonTaskViews;
    TypedArray attributes;
    int taskMenu_Layout_Width;
    int taskMenu_Layout_Height;


    public TaskBuilder(Context context)  {
        super(context);
        construct(context, null, null, null);
    }

    public TaskBuilder(Context context, AttributeSet attrs)  {
        super(context, attrs);
        construct(context, attrs, null, null);
    }

    public TaskBuilder(Context context, AttributeSet attrs, int defStyleAttr)  {
        super(context, attrs, defStyleAttr);
        construct(context, attrs, defStyleAttr, null);
    }

    public TaskBuilder(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)  {
        super(context, attrs, defStyleAttr, defStyleRes);
        construct(context, attrs, defStyleAttr, defStyleRes);
    }


    static Context getContext(View view, Context context) {
        return context == null ? view.getContext() : context;
    }

    @SuppressLint("AppCompatCustomView")
    class PLACEHOLDER extends TextView {
        public PLACEHOLDER(Context context) {
            super(context);
        }

        public PLACEHOLDER(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        public PLACEHOLDER(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        public PLACEHOLDER(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }
    }

    TextView newPlaceholder(Context context, AttributeSet attrs, Integer defStyleAttr, Integer defStyleRes)  {
        if (context == null) throw new RuntimeException("context must not be null");

        boolean attr = attrs != null;
        boolean style = defStyleAttr != null;
        boolean res = defStyleRes != null;
        PLACEHOLDER PLACEHOLDER;

        // any of these can be null
        if (attr) {
            if (style) {
                if (res) {
                    PLACEHOLDER = new PLACEHOLDER(context, attrs, defStyleAttr, defStyleRes);
                } else {
                    PLACEHOLDER = new PLACEHOLDER(context, attrs, defStyleAttr);
                }
            } else {
                PLACEHOLDER = new PLACEHOLDER(context, attrs);
            }
        } else {
            PLACEHOLDER = new PLACEHOLDER(context);
        }
        PLACEHOLDER.setId(View.generateViewId());
        PLACEHOLDER.setText(text == null ? "PLACEHOLDER" : text);
        PLACEHOLDER.setTextColor(textColor);
        setTextSizeAttributesSuitableForTextView(PLACEHOLDER, textSize);
        return PLACEHOLDER;
    }

    @Override
    public void setOnClickListener(@Nullable final OnClickListener l) {
        throw new UnsupportedOperationException("setOnClickListener(onClickListener) is not supported in TaskBuilder");
    }

    /**
     * @param v the view to add
     * @return a new InterceptTouchFrameLayout that is set up to work with TaskBuilder
     */
    static public InterceptTouchFrameLayout new_TaskBuilder_Compatible_InterceptTouchFrameLayout(Context context, View v) {
        final InterceptTouchFrameLayout x;
        try {
            x = InterceptTouchFrameLayout.class.getConstructor(Context.class).newInstance(context);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // handle View onClick before processing View itself
        x.callOnClickBefore = true;
        x.setInterceptOnClickListener(new InterceptTouchFrameLayout.OnInterceptClickListener() {
            @Override
            public void onInterceptClick(View view) {
                Log.d(TAG, "onInterceptClick() called with: view = [" + view + "]");
                ViewParent p = view.getParent();
                while (p != null && p instanceof ViewGroup) {
                    if (p instanceof TaskBuilder) {
                        TaskBuilder tb = ((TaskBuilder) p);
                        tb.addTask(view);
                        break;
                    }
                    p = p.getParent();
                }
            }
        });
        x.addView(v, TaskBuilder.matchParent);
        return x;
    }

    public static void addViewInternal(Context context, ViewGroup viewToAddTo, View view, ViewGroup.LayoutParams params) {
        addViewInternal(context, viewToAddTo, view, -1, params);
    }

    public static void addViewInternal(Context context, ViewGroup viewToAddTo, View view, int index, ViewGroup.LayoutParams params) {
        View target = view;
        // comment this if standAlone should be used
        if (!(view instanceof ExpandableLayout))
            target = new_TaskBuilder_Compatible_InterceptTouchFrameLayout(context, view);
        viewToAddTo.addView(target, index, params);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        Object tag = child.getTag();
        if (tag instanceof Internal) {
            Log.d(TAG, "addView() called with INTERNAL: child = [" + child + "], index = [" + index + "], params = [" + params + "]");
            super.addView(child, index, params);
        } else {
            Log.d(TAG, "addView() called with EXTERNAL: child = [" + child + "], index = [" + index + "], params = [" + params + "]");
            addViewInternal(getContext(), views_TaskMenuContainer_Internal_TaskMenu, child, params);
        }
    }

    String text;
    LayoutUtils.TextViewSize textSize;
    int textColor;

    void getAttributeParameters(Context context, AttributeSet attrs, Theme theme) {
        if (attrs != null) {
            attributes = theme.obtainStyledAttributes(attrs, TaskBuilder_Parameters, 0, 0);
            text = attributes.getString(R.styleable.TaskBuilder_Parameters_android_text);
            textSize = LayoutUtils.getTextSizeAttributesSuitableForTextView(attributes, R.styleable.TaskBuilder_Parameters_android_textSize, 30f);
            textColor = attributes.getColor(R.styleable.TaskBuilder_Parameters_android_textColor, Color.WHITE);
            taskBuilder_background = attributes.getDrawable(R.styleable.TaskBuilder_Parameters_android_background);
            taskMenu_background = attributes.getDrawable(R.styleable.TaskBuilder_Parameters_taskMenu_background);
            showTaskMenu = attributes.getBoolean(R.styleable.TaskBuilder_Parameters_showTaskMenu, false);
            nonTaskViews = attributes.getString(R.styleable.TaskBuilder_Parameters_nonTaskViews);
            attributes.recycle();
        } else {
            textSize = LayoutUtils.new_TextViewSize(30f);
            textColor = Color.BLACK;
        }
    }

    void getAttributeLayout(Context context, AttributeSet attrs, Theme theme) {
        if (attrs != null) {
            attributes = theme.obtainStyledAttributes(attrs, TaskBuilder_Layout, 0, 0);
            taskMenu_Layout_Width = attributes.getDimensionPixelSize(R.styleable.TaskBuilder_Layout_taskMenu_layout_width, MATCH_PARENT);
            taskMenu_Layout_Height = attributes.getDimensionPixelSize(R.styleable.TaskBuilder_Layout_taskMenu_layout_height, MATCH_PARENT);
            attributes.recycle();
        }
    }

    void construct(Context context, AttributeSet attrs, Integer defStyleAttr, Integer defStyleRes) {
        Theme theme = getContext(this, context).getTheme();
        divider = getResources().getDrawable(R.drawable.divider, theme);
        getAttributeLayout(context, attrs, theme);
        getAttributeParameters(context, attrs, theme);

        build_layer_1(context, attrs, defStyleAttr, defStyleRes);
        build_layer_2(context, attrs, defStyleAttr, defStyleRes);
        build_layer_3(context, attrs, defStyleAttr, defStyleRes);
        build_layer_4(context, attrs, defStyleAttr, defStyleRes);
        build_layer_5(context, attrs, defStyleAttr, defStyleRes);

        addAnimations();

        views_TaskList.addView(newPlaceholder(context, attrs, defStyleAttr, defStyleRes), wrapContent);
    }

    // be organized:
    //
    // create all our instances
    // set our parameters
    // add our views
    //

    ConstraintLayout views_TaskListContainer;
    ScrollView views_TaskListScrollView;
    LinearLayout views_TaskList;
    ImageButton views_ShowTaskMenu;

    private void build_layer_1(Context context, AttributeSet attrs, Integer defStyleAttr, Integer defStyleRes)  {
        Builder builder = new ConstraintBuilder().withTag(TAG).withTarget(this);

        // create all our instances
        views_TaskListContainer = constructView(ConstraintLayout.class, context, attrs, defStyleAttr, defStyleRes);

        // set our parameters
        views_TaskListContainer.setTag(Internal);
        views_TaskListContainer.setBackground(taskBuilder_background);
        builder.setLayoutConstraintsTarget(views_TaskListContainer);
        builder.layout_constraintAll_ToAllOf(ConstraintBuilder.parent);

        // add our views
        builder.addView(views_TaskListContainer, matchParent);
        builder.build();
    }
    
    private void build_layer_2(Context context, AttributeSet attrs, Integer defStyleAttr, Integer defStyleRes)  {
        Builder builder = new ConstraintBuilder().withTag(TAG).withTarget(views_TaskListContainer);

        // create all our instances
        views_TaskListScrollView = constructView(ScrollView.class, context, attrs, defStyleAttr, defStyleRes);
        views_TaskList = constructView(LinearLayout.class, context, attrs, defStyleAttr, defStyleRes);
        views_ShowTaskMenu = constructView(ImageButton.class, context, attrs, defStyleAttr, defStyleRes);

        // set our parameters
        views_TaskListScrollView.setTag(Internal);
        views_TaskListScrollView.setBackground(null);
        builder.setLayoutConstraintsTarget(views_TaskListScrollView);
        builder.layout_constraintAll_ToAllOf(ConstraintBuilder.parent);

        views_TaskList.setTag(Internal);
        views_TaskList.setOrientation(LinearLayout.VERTICAL);
        views_TaskList.setBackground(null);
        views_TaskList.setShowDividers(SHOW_DIVIDER_MIDDLE);
        views_TaskList.setDividerDrawable(divider);
        views_TaskList.setDividerPadding(toDP(getResources(), 22f));

        views_ShowTaskMenu.setTag(Internal);
        builder.setLayoutConstraintsTarget(views_ShowTaskMenu);
        builder.layout_constraintBottom_toBottomOf(ConstraintBuilder.parent);
        builder.layout_constraintRight_toRightOf(ConstraintBuilder.parent);
        views_ShowTaskMenu.setBackgroundResource(R.drawable.plus);

        // add our views
        views_TaskListScrollView.addView(views_TaskList, matchParent);
        builder.addView(views_TaskListScrollView, matchParent);
        builder.addView(views_ShowTaskMenu, toDP(getResources(), 100), toDP(getResources(), 100), 0, 0, toDP(getResources(), 16), toDP(getResources(), 16));
        builder.build();
    }

    ConstraintLayout views_TaskMenuContainer;

    private void build_layer_3(Context context, AttributeSet attrs, Integer defStyleAttr, Integer defStyleRes)  {
        Builder builder = new ConstraintBuilder().withTag(TAG).withTarget(views_TaskListContainer);

        // create all our instances
        views_TaskMenuContainer = constructView(ConstraintLayout.class, context, attrs, defStyleAttr, defStyleRes);

        // set our parameters
        views_TaskMenuContainer.setTag(Internal);
        views_TaskMenuContainer.setBackground(null);
        if (!showTaskMenu) views_TaskMenuContainer.setVisibility(GONE);
        builder.setLayoutConstraintsTarget(views_TaskMenuContainer);
        builder.layout_constraintAll_ToAllOf(ConstraintBuilder.parent);

        // add our views
        builder.addView(views_TaskMenuContainer, matchParent);
        builder.build();
    }

    ConstraintLayout views_TaskMenuScrollViewContainer;

    private void build_layer_4(Context context, AttributeSet attrs, Integer defStyleAttr, Integer defStyleRes)  {
        Builder builder = new ConstraintBuilder().withTag(TAG).withTarget(views_TaskMenuContainer);

        // create all our instances
        views_TaskMenuScrollViewContainer = constructView(ConstraintLayout.class, context, attrs, defStyleAttr, defStyleRes);

        // set our parameters
        views_TaskMenuScrollViewContainer.setTag(Internal);
        views_TaskMenuScrollViewContainer.setBackgroundResource(R.drawable.outline);
        builder.setLayoutConstraintsTarget(views_TaskMenuScrollViewContainer);
        builder.layout_constraintAll_ToAllOf(ConstraintBuilder.parent);
        if (!showTaskMenu) views_TaskMenuScrollViewContainer.setAlpha(0.0f);

        // add our views
        builder.addView(views_TaskMenuScrollViewContainer, taskMenu_Layout_Width, taskMenu_Layout_Height);
        builder.build();
    }

    ScrollView views_TaskMenuContainer_Internal_ScrollView;
    LinearLayout views_TaskMenuContainer_Internal_TaskMenu;

    private void build_layer_5(Context context, AttributeSet attrs, Integer defStyleAttr, Integer defStyleRes)  {
        Builder builder = new ConstraintBuilder().withTag(TAG).withTarget(views_TaskMenuScrollViewContainer);

        // create all our instances
        views_TaskMenuContainer_Internal_ScrollView = constructView(ScrollView.class, context, attrs, defStyleAttr, defStyleRes);
        views_TaskMenuContainer_Internal_TaskMenu = constructView(LinearLayout.class, context, attrs, defStyleAttr, defStyleRes);

        // set our parameters
        views_TaskMenuContainer_Internal_ScrollView.setTag(Internal);
        builder.setLayoutConstraintsTarget(views_TaskMenuContainer_Internal_ScrollView);
        builder.layout_constraintAll_ToAllOf(ConstraintBuilder.parent);

        views_TaskMenuContainer_Internal_TaskMenu.setTag(Internal);
        builder.setLayoutConstraintsTarget(views_TaskMenuContainer_Internal_TaskMenu);
        builder.layout_constraintAll_ToAllOf(ConstraintBuilder.parent);
        views_TaskMenuContainer_Internal_TaskMenu.setOrientation(LinearLayout.VERTICAL);
        views_TaskMenuContainer_Internal_TaskMenu.setBackground(taskMenu_background);
        views_TaskMenuContainer_Internal_TaskMenu.setShowDividers(SHOW_DIVIDER_MIDDLE);
        views_TaskMenuContainer_Internal_TaskMenu.setDividerDrawable(divider);
        views_TaskMenuContainer_Internal_TaskMenu.setDividerPadding(toDP(getResources(), 22f));

        // add our views
        views_TaskMenuContainer_Internal_ScrollView.addView(views_TaskMenuContainer_Internal_TaskMenu, matchParent);
        builder.addView(views_TaskMenuContainer_Internal_ScrollView,
                taskMenu_Layout_Width-toDP(getResources(), 16),
                taskMenu_Layout_Height-toDP(getResources(), 16),
                toDP(getResources(), 3),
                toDP(getResources(), 3),
                toDP(getResources(), 3),
                toDP(getResources(), 3)
        );
        builder.build();
    }

    private void addAnimations() {
        final ViewPropertyAnimator viewPropertyAnimator = views_TaskMenuScrollViewContainer.animate();

        views_ShowTaskMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPropertyAnimator.cancel();
                viewPropertyAnimator.setListener(null);
                views_TaskMenuContainer.setVisibility(VISIBLE);
                views_TaskMenuScrollViewContainer.setAlpha(0.0f);
                viewPropertyAnimator.alpha(1.0f).setDuration(500).start();
            }
        });

        views_TaskMenuContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPropertyAnimator.cancel();
                viewPropertyAnimator.alpha(0.0f).setDuration(500);
                viewPropertyAnimator.setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        views_TaskMenuContainer.setVisibility(GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).start();
            }
        });
    }
}
