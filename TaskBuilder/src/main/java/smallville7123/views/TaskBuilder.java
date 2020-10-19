package smallville7123.views;

import android.animation.Animator;
import android.content.Context;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewPropertyAnimator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.Collections;

import smallville7123.layoututils.LayoutUtils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
import static smallville7123.layoututils.LayoutUtils.setTextSizeAttributesSuitableForTextView;
import static smallville7123.views.R.styleable.TaskBuilder_Layout;
import static smallville7123.views.R.styleable.TaskBuilder_Parameters;

public class TaskBuilder extends ConstraintLayout {
    public static final String TAG = "TaskBuilder";
    public static LayoutParams matchParent = new LayoutParams(MATCH_PARENT, MATCH_PARENT);
    public static LayoutParams wrapContent = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
    public static LayoutParams matchConstraint = new LayoutParams(MATCH_CONSTRAINT, MATCH_CONSTRAINT);

    // Views
    private ConstraintLayout CurrentTasksContainer;
    private LinearLayout CurrentTasks;
    private ConstraintLayout TaskMenuContainer;
    private ScrollView TaskMenuScrollView;
    private ConstraintLayout TaskMenuScrollViewContainer;
    private LinearLayout TaskMenu;
    private ImageButton ToggleTaskMenuVisibility;

    // Parameters
    private static class Internal {}
    private Internal Internal = new Internal();
    private Drawable TaskMenuBackground;
    private Drawable CurrentTasksBackground;
    private boolean showTaskMenu;
    private String nonTaskViews;
    private TypedArray attributes;
    private int taskMenu_Layout_Width;
    private int taskMenu_Layout_Height;
    private String text;
    private LayoutUtils.TextViewSize textSize;
    private int textColor;

    //internal
    private boolean placeholdersRemoved = false;

    void getAttributeParameters(Context context, AttributeSet attrs, Theme theme) {
        if (attrs != null) {
            attributes = theme.obtainStyledAttributes(attrs, TaskBuilder_Parameters, 0, 0);
            text = attributes.getString(R.styleable.TaskBuilder_Parameters_android_text);
            textSize = LayoutUtils.getTextSizeAttributesSuitableForTextView(attributes, R.styleable.TaskBuilder_Parameters_android_textSize, 30f);
            textColor = attributes.getColor(R.styleable.TaskBuilder_Parameters_android_textColor, Color.WHITE);
            CurrentTasksBackground = attributes.getDrawable(R.styleable.TaskBuilder_Parameters_android_background);
            TaskMenuBackground = attributes.getDrawable(R.styleable.TaskBuilder_Parameters_taskMenu_background);
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

    void construct(@NonNull Context context, @Nullable AttributeSet attrs, @Nullable Integer defStyleAttr, @Nullable Integer defStyleRes) {
        Theme theme = context.getTheme();
        getAttributeLayout(context, attrs, theme);
        getAttributeParameters(context, attrs, theme);

        View root = inflate(context, R.layout.taskbuilder, null);
        root.setTag(Internal);
        addView(root, matchParent);

        CurrentTasksContainer = findViewById(R.id.CurrentTasksContainer);
        CurrentTasks = findViewById(R.id.CurrentTasks);
        TaskMenuContainer = findViewById(R.id.TaskMenuContainer);
        TaskMenuScrollView = findViewById(R.id.TaskMenuScrollView);
        TaskMenuScrollViewContainer = findViewById(R.id.TaskMenuScrollViewContainer);
        TaskMenu = findViewById(R.id.TaskMenu);
        ToggleTaskMenuVisibility = findViewById(R.id.ToggleTaskMenuVisibility);

        CurrentTasksContainer.setBackground(CurrentTasksBackground);
        TaskMenuScrollViewContainer.setBackground(TaskMenuBackground);
        if (!showTaskMenu) TaskMenuContainer.setVisibility(GONE);
        ViewGroup.LayoutParams layoutParams = TaskMenuScrollViewContainer.getLayoutParams();
        layoutParams.width = taskMenu_Layout_Width;
        layoutParams.height = taskMenu_Layout_Height;
        TaskMenuScrollViewContainer.setLayoutParams(layoutParams);

        CurrentTasks.addView(newPlaceholder(context, attrs, defStyleAttr, defStyleRes));

        addAnimations();
    }

    private void addAnimations() {
        final ViewPropertyAnimator viewPropertyAnimator = TaskMenuScrollViewContainer.animate();

        ToggleTaskMenuVisibility.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPropertyAnimator.cancel();
                viewPropertyAnimator.setListener(null);
                TaskMenuContainer.setVisibility(VISIBLE);
                TaskMenuScrollViewContainer.setAlpha(0.0f);
                viewPropertyAnimator.alpha(1.0f).setDuration(500).start();
            }
        });

        TaskMenuContainer.setOnClickListener(new OnClickListener() {
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
                        TaskMenuContainer.setVisibility(GONE);
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

    public static void make_InterceptTouchFrameLayout_Compatible_with_TaskBuilder(InterceptTouchFrameLayout interceptTouchFrameLayout) {
        // handle View onClick before processing View itself
        interceptTouchFrameLayout.callOnClickBefore = true;
        interceptTouchFrameLayout.setInterceptOnClickListener(new InterceptTouchFrameLayout.OnInterceptClickListener() {
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
    }

    /**
     * @param v the view to add
     * @return a new InterceptTouchFrameLayout that is set up to work with TaskBuilder
     */
    public static InterceptTouchFrameLayout new_TaskBuilder_Compatible_InterceptTouchFrameLayout(Context context, View v) {
        final InterceptTouchFrameLayout x;
        try {
            x = InterceptTouchFrameLayout.class.getConstructor(Context.class).newInstance(context);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        make_InterceptTouchFrameLayout_Compatible_with_TaskBuilder(x);
        x.addView(v, matchParent);
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
            addViewInternal(getContext(), TaskMenu, child, params);
        }
    }

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
                // no image for now
                taskData.text = textView.getText();
                return true;
            }
            return false;
        }
    }));

    private boolean removePlaceholdersInternal() {
        int children = CurrentTasks.getChildCount();
        if (children != 0) {
            for (int i = 0; i < children; i++) {
                if (CurrentTasks.getChildAt(i) instanceof PLACEHOLDER) {
                    CurrentTasks.removeViewAt(i);
                    return true;
                }
            }
        }
        return false;
    }

    private void removePlaceHolders() {
        while (true) {
            if (!removePlaceholdersInternal()) {
                break;
            }
        }
    }

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
            tmp.setText(taskData.text);
            tmp.setTextColor(textColor);
            // accepts a TextViewSize object
            tmp.setTextSize(textSize);
            tmp.setImage(taskData.icon);
            if (!placeholdersRemoved) {
                removePlaceHolders();
                placeholdersRemoved = true;
            }
            CurrentTasks.addView(tmp);
        } else throw new RuntimeException("failed to process view: " + view);
    }

    abstract static class ViewHolder {
        abstract boolean process(View view, TaskData taskData);
    }


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
}