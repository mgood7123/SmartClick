package smallville7123.views;

import android.animation.Animator;
import android.content.Context;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.lang.reflect.InvocationTargetException;

import smallville7123.tools.Builder;
import smallville7123.tools.ConstraintBuilder;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
import static smallville7123.layoututils.LayoutUtils.constructView;
import static smallville7123.views.R.styleable.TaskBuilder_Layout;
import static smallville7123.views.R.styleable.TaskBuilder_Parameters;

public class TaskBuilder extends ConstraintLayout {

    public static final String TAG = "TaskBuilder";
    static LayoutParams matchParent = new LayoutParams(MATCH_PARENT, MATCH_PARENT);
    static LayoutParams wrapContent = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
    static LayoutParams matchConstraint = new LayoutParams(MATCH_CONSTRAINT, MATCH_CONSTRAINT);
    private static class Internal {}
    Internal Internal = new Internal();
    private boolean showTaskMenu;
    Theme theme;
    TypedArray attributes;
    int taskMenu_Layout_Width;
    int taskMenu_Layout_Height;


    public TaskBuilder(Context context) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        super(context);
        construct(context, null, null, null);
    }

    public TaskBuilder(Context context, AttributeSet attrs) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        super(context, attrs);
        construct(context, attrs, null, null);
    }

    public TaskBuilder(Context context, AttributeSet attrs, int defStyleAttr) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        super(context, attrs, defStyleAttr);
        construct(context, attrs, defStyleAttr, null);
    }

    public TaskBuilder(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        super(context, attrs, defStyleAttr, defStyleRes);
        construct(context, attrs, defStyleAttr, defStyleRes);
    }


    static Context getContext(View view, Context context) {
        return context == null ? view.getContext() : context;
    }

    TextView newPlaceholder(Context context, AttributeSet attrs, Integer defStyleAttr, Integer defStyleRes) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        TextView PLACEHOLDER = constructView(TextView.class, getContext(this, context), null, null, null);
        PLACEHOLDER.setTextColor(Color.BLACK);
        PLACEHOLDER.setText("PLACEHOLDER");
        PLACEHOLDER.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30f);
        PLACEHOLDER.setBackgroundColor(Color.GREEN);
        new ExpandableListView(context).addView(this);
        return PLACEHOLDER;
    }

    @Override
    public void setOnClickListener(@Nullable final OnClickListener l) {
        throw new UnsupportedOperationException("setOnClickListener(onClickListener) is not supported in TaskBuilder");
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        Object tag = child.getTag();
        if (tag instanceof Internal) {
            Log.d(TAG, "addView() called with INTERNAL: child = [" + child + "], index = [" + index + "], params = [" + params + "]");
            super.addView(child, index, params);
        } else {
            Log.d(TAG, "addView() called with EXTERNAL: child = [" + child + "], index = [" + index + "], params = [" + params + "]");
            views_TaskMenuContainer_Internal_TaskMenu.addView(child, index, params);
        }
    }

    void getAttributeParameters(Context context, AttributeSet attrs, Theme theme) {
        if (attrs != null) {
            attributes = theme.obtainStyledAttributes(attrs, TaskBuilder_Parameters, 0, 0);
            showTaskMenu = attributes.getBoolean(R.styleable.TaskBuilder_Parameters_showTaskMenu, false);
            attributes.recycle();
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

    void construct(Context context, AttributeSet attrs, Integer defStyleAttr, Integer defStyleRes) throws NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Theme theme = getContext(this, context).getTheme();
        getAttributeLayout(context, attrs, theme);
        getAttributeParameters(context, attrs, theme);

        build_layer_1(context, attrs, defStyleAttr, defStyleRes);
        build_layer_2(context, attrs, defStyleAttr, defStyleRes);
        build_layer_3(context, attrs, defStyleAttr, defStyleRes);

        addAnimations();
    }

    // be organized:
    //
    // create all our instances
    // set our parameters
    // add our views
    //

    ScrollView views_TaskListScrollView;
    LinearLayout views_TaskList;
    ImageButton views_ShowTaskMenu;

    private void build_layer_1(Context context, AttributeSet attrs, Integer defStyleAttr, Integer defStyleRes) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Builder builder = new ConstraintBuilder().withTag(TAG).withTarget(this);

        // create all our instances
        views_TaskListScrollView = constructView(ScrollView.class, context, attrs, defStyleAttr, defStyleRes);
        views_TaskList = constructView(LinearLayout.class, context, attrs, defStyleAttr, defStyleRes);
        views_ShowTaskMenu = constructView(ImageButton.class, context, attrs, defStyleAttr, defStyleRes);

        // set our parameters
        views_TaskListScrollView.setTag(Internal);
        builder.setLayoutConstraintsTarget(views_TaskListScrollView);
        builder.layout_constraintAll_ToAllOf(ConstraintBuilder.parent);

        views_TaskList.setOrientation(LinearLayout.VERTICAL);
        views_TaskList.setTag(Internal);

        views_ShowTaskMenu.setTag(Internal);
        builder.setLayoutConstraintsTarget(views_ShowTaskMenu);
        builder.layout_constraintBottom_toBottomOf(ConstraintBuilder.parent);
        builder.layout_constraintRight_toRightOf(ConstraintBuilder.parent);
        views_ShowTaskMenu.setBackgroundResource(R.drawable.plus);

        // add our views
        views_TaskListScrollView.addView(views_TaskList, matchParent);
        builder.addView(views_TaskListScrollView, matchParent);
        builder.addView(views_ShowTaskMenu, toDP(this, 100), toDP(this, 100), 0, 0, toDP(this, 16), toDP(this, 16));
        builder.build();
    }

    ConstraintLayout views_TaskMenuContainer;

    private void build_layer_2(Context context, AttributeSet attrs, Integer defStyleAttr, Integer defStyleRes) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Builder builder = new ConstraintBuilder().withTag(TAG).withTarget(this);

        // create all our instances
        views_TaskMenuContainer = constructView(ConstraintLayout.class, context, attrs, defStyleAttr, defStyleRes);

        // set our parameters
        views_TaskMenuContainer.setTag(Internal);
        if (!showTaskMenu) views_TaskMenuContainer.setVisibility(GONE);
        builder.setLayoutConstraintsTarget(views_TaskMenuContainer);
        builder.layout_constraintAll_ToAllOf(ConstraintBuilder.parent);

        // add our views
        builder.addView(views_TaskMenuContainer, matchParent);
        builder.build();
    }

    ScrollView views_TaskMenuContainer_Internal_ScrollView;
    LinearLayout views_TaskMenuContainer_Internal_TaskMenu;

    private void build_layer_3(Context context, AttributeSet attrs, Integer defStyleAttr, Integer defStyleRes) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Builder builder = new ConstraintBuilder().withTag(TAG).withTarget(views_TaskMenuContainer);

        // create all our instances
        views_TaskMenuContainer_Internal_ScrollView = constructView(ScrollView.class, context, attrs, defStyleAttr, defStyleRes);
        views_TaskMenuContainer_Internal_TaskMenu = constructView(LinearLayout.class, context, attrs, defStyleAttr, defStyleRes);

        // set our parameters
        views_TaskMenuContainer_Internal_ScrollView.setTag(Internal);
        builder.setLayoutConstraintsTarget(views_TaskMenuContainer_Internal_ScrollView);
        builder.layout_constraintAll_ToAllOf(ConstraintBuilder.parent);
        if (!showTaskMenu) views_TaskMenuContainer_Internal_ScrollView.setAlpha(0.0f);
        views_TaskMenuContainer_Internal_ScrollView.setBackgroundColor(Color.DKGRAY);

        views_TaskMenuContainer_Internal_TaskMenu.setTag(Internal);
        builder.setLayoutConstraintsTarget(views_TaskMenuContainer_Internal_TaskMenu);
        builder.layout_constraintAll_ToAllOf(ConstraintBuilder.parent);
        views_TaskMenuContainer_Internal_TaskMenu.setOrientation(LinearLayout.VERTICAL);
        views_TaskMenuContainer_Internal_TaskMenu.setBackgroundColor(Color.LTGRAY);

        // add our views
        views_TaskMenuContainer_Internal_ScrollView.addView(views_TaskMenuContainer_Internal_TaskMenu, matchParent);
        builder.addView(views_TaskMenuContainer_Internal_ScrollView, taskMenu_Layout_Width, taskMenu_Layout_Height);
        builder.build();
    }

    private void addAnimations() {
        final ViewPropertyAnimator viewPropertyAnimator = views_TaskMenuContainer_Internal_ScrollView.animate();

        views_ShowTaskMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPropertyAnimator.cancel();
                viewPropertyAnimator.setListener(null);
                views_TaskMenuContainer.setVisibility(VISIBLE);
                views_TaskMenuContainer_Internal_ScrollView.setAlpha(0.0f);
                viewPropertyAnimator.alpha(1.0f).setDuration(500).start();
            }
        });

        views_TaskMenuContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPropertyAnimator.cancel();
                viewPropertyAnimator.alpha(0f).setDuration(500).setListener(new Animator.AnimatorListener() {
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

    static int toDP(View view, float val) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, val, view.getResources().getDisplayMetrics());
    }
}
