package smallville7123.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.lang.reflect.InvocationTargetException;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static androidx.constraintlayout.widget.ConstraintProperties.MATCH_CONSTRAINT;
import static smallville7123.views.TaskBuilder.ConstraintBuilder;
import static smallville7123.views.TaskBuilder.constructView;
import static smallville7123.views.TaskBuilder.matchParent;
import static smallville7123.views.TaskBuilder.toDP;

public class TaskBuilderCompatibleListView extends ConstraintLayout {

    public static final String TAG = "TaskBuilderCompatibleListView";

    public TaskBuilderCompatibleListView(Context context) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        super(context);
        construct(context, null, null, null);
    }

    public TaskBuilderCompatibleListView(Context context, AttributeSet attrs) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        super(context, attrs);
        construct(context, attrs, null, null);
    }

    public TaskBuilderCompatibleListView(Context context, AttributeSet attrs, int defStyleAttr) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        super(context, attrs, defStyleAttr);
        construct(context, attrs, defStyleAttr, null);
    }

    public TaskBuilderCompatibleListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        super(context, attrs, defStyleAttr, defStyleRes);
        construct(context, attrs, defStyleAttr, defStyleRes);
    }

    void construct(Context context, AttributeSet attrs, Integer defStyleAttr, Integer defStyleRes) throws NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Resources.Theme theme = TaskBuilder.getContext(this, context).getTheme();
        build_layer_1(context, attrs, defStyleAttr, defStyleRes);
        build_layer_2(context, attrs, defStyleAttr, defStyleRes);
    }

    private static class Internal {}
    Internal Internal = new Internal();

    LinearLayout list;
    ImageButton expandCollapse;
    ConstraintLayout views_TaskMenuContainer;

    private void build_layer_1(Context context, AttributeSet attrs, Integer defStyleAttr, Integer defStyleRes) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ConstraintBuilder.Builder builder = ConstraintBuilder.withTag(TAG).withTarget(this);

        // create all our instances
        views_TaskMenuContainer = constructView(ConstraintLayout.class, context, attrs, defStyleAttr, defStyleRes);

        // set our parameters
        views_TaskMenuContainer.setTag(Internal);
        views_TaskMenuContainer.setBackgroundColor(Color.LTGRAY);
        builder.setLayoutConstraintsTarget(views_TaskMenuContainer);
        builder.layout_constraintAll_ToAllOf(ConstraintBuilder.parent);

        // add our views
        builder.addView(views_TaskMenuContainer, matchParent);
        builder.build();
    }

    private void build_layer_2(Context context, AttributeSet attrs, Integer defStyleAttr, Integer defStyleRes) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ConstraintBuilder.Builder builder = ConstraintBuilder.withTag(TAG).withTarget(views_TaskMenuContainer);

        // create all our instances
        list = constructView(LinearLayout.class, context, attrs, defStyleAttr, defStyleRes);
        expandCollapse = constructView(ImageButton.class, context, attrs, defStyleAttr, defStyleRes);

        // set our parameters
        list.setOrientation(LinearLayout.VERTICAL);
        list.setTag(Internal);

        expandCollapse.setTag(Internal);
        expandCollapse.setBackgroundColor(Color.BLUE);

        builder.setLayoutConstraintsTarget(list);
        builder.layout_constraintTop_toTopOf(ConstraintBuilder.parent);
        builder.layout_constraintBottom_toBottomOf(ConstraintBuilder.parent);
        builder.layout_constraintLeft_toLeftOf(ConstraintBuilder.parent);
        builder.layout_constraintRight_toLeftOf(expandCollapse);
        builder.setLayoutConstraintsTarget(expandCollapse);
        builder.layout_constraintTop_toTopOf(ConstraintBuilder.parent);
        builder.layout_constraintBottom_toBottomOf(ConstraintBuilder.parent);
        builder.layout_constraintRight_toRightOf(ConstraintBuilder.parent);
        builder.layout_constraintLeft_toRightOf(list);

        // add our views
        builder.addView(list, MATCH_CONSTRAINT, MATCH_PARENT);
        builder.addView(expandCollapse, toDP(this, 50), MATCH_CONSTRAINT);
        builder.build();
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        Object tag = child.getTag();
        if (tag instanceof Internal) {
            Log.d(TAG, "addView() called with INTERNAL: child = [" + child + "], index = [" + index + "], params = [" + params + "]");
            super.addView(child, index, params);
        } else {
            child.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick() called with: v = [" + v + "]");
                    // this will reparent, we do not want that
                    // views_TaskList.addView(v, v.getLayoutParams());
                }
            });
            Log.d(TAG, "addView() called with EXTERNAL: child = [" + child + "], index = [" + index + "], params = [" + params + "]");
            // add items to linear layout
            list.addView(child, index, params);
        }
    }

}
