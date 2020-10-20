package smallville7123.views;

import android.animation.Animator;
import android.content.Context;
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

import smallville7123.taggable.Taggable;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static smallville7123.layoututils.LayoutUtils.setTextSizeAttributesSuitableForTextView;
import static smallville7123.views.TaskBuilder.matchParent;

class TaskBuilder_BlockView {
    public String TAG = Taggable.getTag(this);

    TaskBuilder taskBuilder;

    ConstraintLayout CurrentTasksContainer;
    LinearLayout CurrentTasks;
    ConstraintLayout TaskMenuContainer;
    ScrollView TaskMenuScrollView;
    ConstraintLayout TaskMenuScrollViewContainer;
    LinearLayout TaskMenu;
    ImageButton ToggleTaskMenuVisibility;

    void construct(TaskBuilder taskBuilder, @NonNull Context context, @Nullable AttributeSet attrs, @Nullable Integer defStyleAttr, @Nullable Integer defStyleRes) {
        this.taskBuilder = taskBuilder;
        CurrentTasksContainer = taskBuilder.BlockView.findViewById(R.id.CurrentTasksContainer);
        CurrentTasks = taskBuilder.BlockView.findViewById(R.id.CurrentTasks);
        TaskMenuContainer = taskBuilder.BlockView.findViewById(R.id.TaskMenuContainer);
        TaskMenuScrollView = taskBuilder.BlockView.findViewById(R.id.TaskMenuScrollView);
        TaskMenuScrollViewContainer = taskBuilder.BlockView.findViewById(R.id.TaskMenuScrollViewContainer);
        TaskMenu = taskBuilder.BlockView.findViewById(R.id.TaskMenu);
        ToggleTaskMenuVisibility = taskBuilder.BlockView.findViewById(R.id.ToggleTaskMenuVisibility);



        CurrentTasksContainer.setBackground(taskBuilder.CurrentTasksBackground);
        TaskMenuScrollViewContainer.setBackground(taskBuilder.TaskMenuBackground);
        if (!taskBuilder.showTaskMenu) TaskMenuContainer.setVisibility(GONE);
        ViewGroup.LayoutParams layoutParams = TaskMenuScrollViewContainer.getLayoutParams();
        layoutParams.width = taskBuilder.taskMenu_Layout_Width;
        layoutParams.height = taskBuilder.taskMenu_Layout_Height;
        TaskMenuScrollViewContainer.setLayoutParams(layoutParams);

        CurrentTasks.addView(newPlaceholder(context, attrs, defStyleAttr, defStyleRes));

        addAnimations(taskBuilder);
    }

    private void addAnimations(final TaskBuilder taskBuilder) {
        final ViewPropertyAnimator viewPropertyAnimator = TaskMenuScrollViewContainer.animate();

        ToggleTaskMenuVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPropertyAnimator.cancel();
                viewPropertyAnimator.setListener(null);
                TaskMenuContainer.setVisibility(VISIBLE);
                TaskMenuScrollViewContainer.setAlpha(0.0f);
                viewPropertyAnimator.alpha(1.0f).setDuration(500).start();
            }
        });

        TaskMenuContainer.setOnClickListener(new View.OnClickListener() {
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

    public void addView(View child, ViewGroup.LayoutParams params) {
        addViewInternal(taskBuilder.getContext(), TaskMenu, child, params);
    }

    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        addViewInternal(taskBuilder.getContext(), TaskMenu, child, index, params);
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
            Task tmp = new Task(taskBuilder.getContext());
            tmp.setText(taskData.text);
            tmp.setTextColor(taskBuilder.textColor);
            // accepts a TextViewSize object
            tmp.setTextSize(taskBuilder.textSize);
            tmp.setImage(taskData.icon);
            if (!taskBuilder.placeholdersRemoved) {
                removePlaceHolders();
                taskBuilder.placeholdersRemoved = true;
            }
            CurrentTasks.addView(tmp);
        } else throw new RuntimeException("failed to process view: " + view);
    }

    abstract static class ViewHolder {
        abstract boolean process(View view, TaskData taskData);
    }

    public static void make_InterceptTouchFrameLayout_Compatible_with_TaskBuilder(InterceptTouchFrameLayout interceptTouchFrameLayout) {
        // handle View onClick before processing View itself
        interceptTouchFrameLayout.callOnClickBefore = true;
        interceptTouchFrameLayout.setInterceptOnClickListener(new InterceptTouchFrameLayout.OnInterceptClickListener() {
            @Override
            public void onInterceptClick(View view) {
                Log.d(TaskBuilder.TAG, "onInterceptClick() called with: view = [" + view + "]");
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
        PLACEHOLDER.setText(taskBuilder.text == null ? "PLACEHOLDER" : taskBuilder.text);
        PLACEHOLDER.setTextColor(taskBuilder.textColor);
        setTextSizeAttributesSuitableForTextView(PLACEHOLDER, taskBuilder.textSize);
        return PLACEHOLDER;
    }
}