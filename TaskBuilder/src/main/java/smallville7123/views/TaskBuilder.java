package smallville7123.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import smallville7123.layoututils.LayoutUtils;
import smallville7123.layoututils.ViewHierarchy;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
import static smallville7123.views.R.styleable.TaskBuilder_Layout;
import static smallville7123.views.R.styleable.TaskBuilder_Parameters;

public class TaskBuilder extends ConstraintLayout {
    public static final String TAG = "TaskBuilder";
    public static LayoutParams matchParent = new LayoutParams(MATCH_PARENT, MATCH_PARENT);
    public static LayoutParams wrapContent = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
    public static LayoutParams matchConstraint = new LayoutParams(MATCH_CONSTRAINT, MATCH_CONSTRAINT);

    // Views
    Button Blocks;
    View BlockView;
    Button Code;
    View CodeView;

    public static void make_InterceptTouchFrameLayout_Compatible_with_TaskBuilder(InterceptTouchFrameLayout interceptor) {
        TaskBuilder_BlockView.make_InterceptTouchFrameLayout_Compatible_with_TaskBuilder(interceptor);
    }

    // Parameters
    static class Internal {}
    Internal Internal = new Internal();
    Drawable TaskMenuBackground;
    Drawable CurrentTasksBackground;
    boolean showTaskMenu;
    String nonTaskViews;
    TypedArray attributes;
    int taskMenu_Layout_Width;
    int taskMenu_Layout_Height;
    String text;
    LayoutUtils.TextViewSize textSize;
    int textColor;

    //internal
    private boolean isCodeView = false;
    boolean placeholdersRemoved = false;
    private TaskBuilder_BlockView taskBuilder_blockView = new TaskBuilder_BlockView();
    private TaskBuilder_CodeView taskBuilder_codeView = new TaskBuilder_CodeView();

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

        Blocks = findViewById(R.id.button);
        BlockView = findViewById(R.id.BlockView);
        Code = findViewById(R.id.button2);
        CodeView = findViewById(R.id.CodeView);
        BlockView.setVisibility(GONE);
        Blocks.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
        Code.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
        CodeView.setVisibility(VISIBLE);

        Blocks.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isCodeView = false;
                BlockView.setVisibility(VISIBLE);
                Blocks.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                CodeView.setVisibility(GONE);
                Code.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            }
        });

        Code.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isCodeView = true;
                BlockView.setVisibility(GONE);
                Blocks.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                CodeView.setVisibility(VISIBLE);
                Code.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
            }
        });

        taskBuilder_blockView.construct(this, context, attrs, defStyleAttr, defStyleRes);
        taskBuilder_codeView.construct(this, context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * traverses up the View Hierarchy to find a TaskBuilder view.
     * <br>
     * for performance this does not do a in-depth search
     * such as recursing into other children
     */
    public static TaskBuilder findInstanceQuickSearch(View view) {
        ViewParent p = view.getParent();
        while (p != null && p instanceof ViewGroup) {
            if (p instanceof TaskBuilder) return (TaskBuilder) p;
            p = p.getParent();
        }
        return null;
    }

    /**
     * traverses up the View Hierarchy to find a TaskBuilder view.
     * <br>
     * does a full in-depth search. this may impact performance
     */
    public static TaskBuilder findInstanceFullSearch(View view) {
        ViewHierarchy viewHierarchy = new ViewHierarchy();
        viewHierarchy.analyzeFull(view);
        // find root view
        ViewParent p = view.getParent();
        while (p != null && p instanceof ViewGroup) {
            if (p instanceof TaskBuilder) return (TaskBuilder) p;
            p = p.getParent();
        }
        return null;
    }

    public void addTask(View view) {
        taskBuilder_blockView.addTask(view);
    }

    public void addViewInternal(Context context, ViewGroup viewToAddTo, View view, ViewGroup.LayoutParams params) {
        taskBuilder_blockView.addViewInternal(context, viewToAddTo, view, params);
    }

    public void addViewInternal(Context context, ViewGroup viewToAddTo, View view, int index, ViewGroup.LayoutParams params) {
        taskBuilder_blockView.addViewInternal(context, viewToAddTo, view, index, params);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        Object tag = child.getTag();
        if (tag instanceof Internal) {
            Log.d(TAG, "addView() called with INTERNAL: child = [" + child + "], index = [" + index + "], params = [" + params + "]");
            super.addView(child, index, params);
        } else {
            Log.d(TAG, "addView() called with EXTERNAL: child = [" + child + "], index = [" + index + "], params = [" + params + "]");
            taskBuilder_blockView.addView(child, index, params);
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
}