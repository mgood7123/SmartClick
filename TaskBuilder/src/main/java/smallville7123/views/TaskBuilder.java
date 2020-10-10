package smallville7123.views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.icu.util.MeasureUnit;
import android.os.Parcel;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewPropertyAnimator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.motion.widget.MotionScene;
import androidx.constraintlayout.motion.widget.TransitionBuilder;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Placeholder;
import androidx.core.util.Pair;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;

public class TaskBuilder extends ConstraintLayout {

    public static final String TAG = "TaskBuilder";

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

    static LayoutParams matchParent = new LayoutParams(MATCH_PARENT, MATCH_PARENT);
    static LayoutParams wrapContent = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
    static LayoutParams matchConstraint = new LayoutParams(MATCH_CONSTRAINT, MATCH_CONSTRAINT);

    Context getContext(Context context) {
        return context == null ? getContext() : context;
    }

    /**
     * Finds the first descendant view with the given ID, the view itself if
     * the ID matches {@link #getId()}, or {@code null} if the ID is invalid
     * (< 0) or there is no matching view in the hierarchy.
     * <p>
     * <strong>Note:</strong> In most cases -- depending on compiler support --
     * the resulting view is automatically cast to the target class type. If
     * the target class type is unconstrained, an explicit cast may be
     * necessary.
     *
     * @return a view with given ID if found, or {@code null} otherwise
     * @see View#requireViewById(int)
     */
    @androidx.annotation.Nullable
    public static final <T extends View> T constructView(Class viewClass, Context context, AttributeSet attrs, Integer defStyleAttr, Integer defStyleRes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (!View.class.isAssignableFrom(viewClass)) throw new RuntimeException("viewClass must extend View");

        boolean attr = attrs != null;
        boolean style = defStyleAttr != null;
        boolean res = defStyleRes != null;
        View instance = null;

        // any of these can be null
        if (attr) {
            if (style) {
                if (res) {
                    instance = (View) viewClass.getConstructor(Context.class, AttributeSet.class, int.class, int.class).newInstance(context, attrs, defStyleAttr, defStyleRes);
                } else {
                    instance = (View) viewClass.getConstructor(Context.class, AttributeSet.class, int.class).newInstance(context, attrs, defStyleAttr);
                }
            } else {
                instance = (View) viewClass.getConstructor(Context.class, AttributeSet.class).newInstance(context, attrs);
            }
        } else {
            instance = (View) viewClass.getConstructor(Context.class).newInstance(context);
        }
        if (instance == null) throw new RuntimeException("failed to construct view");
        instance.setId(generateViewId());
        return (T) instance;
    }

    TextView newPlaceholder(Context context, AttributeSet attrs, Integer defStyleAttr, Integer defStyleRes) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        TextView PLACEHOLDER = constructView(TextView.class, context, null, null, null);
        PLACEHOLDER.setTextColor(Color.BLACK);
        PLACEHOLDER.setText("PLACEHOLDER");
        PLACEHOLDER.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30f);
        PLACEHOLDER.setBackgroundColor(Color.GREEN);
        return PLACEHOLDER;
    }

    // todo: item_list.xml

    void construct(final Context context, final AttributeSet attrs, Integer defStyleAttr, Integer defStyleRes) throws NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        setBackgroundColor(Color.BLACK);

        // be organized:
        //
        // create all our instances
        // set our parameters
        // add our views
        //

        // build layer 1

        ConstraintBuilder.Builder builder = ConstraintBuilder.withTarget(this);

        // create all our instances

        ScrollView scrollView = constructView(ScrollView.class, context, attrs, defStyleAttr, defStyleRes);
        LinearLayout linearLayout = constructView(LinearLayout.class, context, attrs, defStyleAttr, defStyleRes);
        View fab = constructView(ImageButton.class, context, attrs, defStyleAttr, defStyleRes);

        // set our parameters
        builder.setLayoutConstraintsTarget(scrollView);
        builder.layout_constraintAll_ToAllOf(ConstraintBuilder.parent);
        builder.setLayoutConstraintsTarget(fab);
        builder.layout_constraintBottom_toBottomOf(ConstraintBuilder.parent);
        builder.layout_constraintRight_toRightOf(ConstraintBuilder.parent);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        for (int i = 0; i < 30; i++) linearLayout.addView(newPlaceholder(context, attrs, defStyleAttr, defStyleRes), wrapContent);
        fab.setBackgroundColor(Color.MAGENTA);
        fab.setBackgroundResource(R.drawable.plus);

        // add our views
        scrollView.addView(linearLayout, matchParent);
        builder.addView(scrollView, matchParent);
        builder.addView(fab, toDP(100), toDP(100), 0, 0, toDP(16), toDP(16));
        builder.build();

        // Layer 1 is built

        // build layer 2

        builder = ConstraintBuilder.withTarget(this);

        // create all our instances
        final ConstraintLayout area = constructView(ConstraintLayout.class, context, attrs, defStyleAttr, defStyleRes);

        // set our parameters
        area.setVisibility(GONE);
        builder.setLayoutConstraintsTarget(area);
        builder.layout_constraintAll_ToAllOf(ConstraintBuilder.parent);

        // add our views
        builder.addView(area, matchParent);
        builder.build();

        // layer 3

        builder = ConstraintBuilder.withTarget(area);

        // create all our instances
        final ScrollView scrollView1 = constructView(ScrollView.class, context, attrs, defStyleAttr, defStyleRes);
        LinearLayout linearLayout1 = constructView(LinearLayout.class, context, attrs, defStyleAttr, defStyleRes);

        // set our parameters
        builder.setLayoutConstraintsTarget(scrollView1);
        builder.layout_constraintAll_ToAllOf(ConstraintBuilder.parent);
        scrollView1.setBackgroundColor(Color.DKGRAY);
        scrollView1.setAlpha(0.0f);
        linearLayout1.setOrientation(LinearLayout.VERTICAL);

        // add our views
        for (int i = 0; i < 30; i++)
            linearLayout1.addView(newPlaceholder(context, attrs, defStyleAttr, defStyleRes), wrapContent);
        scrollView1.addView(linearLayout1, matchParent);
        builder.addView(scrollView1, toDP(400), toDP(400));
        builder.build();

        final ViewPropertyAnimator a = scrollView1.animate();

        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                a.cancel();
                a.setListener(null);
                area.setVisibility(VISIBLE);
                scrollView1.setAlpha(0.0f);
                a.alpha(1.0f).setDuration(500).start();
            }
        });

        area.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                a.cancel();
                a.alpha(0f).setDuration(500).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        area.setVisibility(GONE);
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

    static class ConstraintBuilder {
        public static final int parent = -500;

        public static Builder withTarget(ConstraintLayout constraintLayout) {
            Builder tmp = new Builder();
            tmp.withTarget(constraintLayout);
            return tmp;
        }

        static class Builder {
            private View target;
            private ConstraintSet constraintSet;
            private ConstraintLayout applyTo;

            public void withTarget(ConstraintLayout constraintLayout) {
                commandList.add(Commands.withTarget, constraintLayout);
            }

            static abstract class CommandBuilder {
                static class Command {
                    int command;
                    Object[] arguments;

                    Command(Command cmd) {
                        command = cmd.command;
                        arguments = cmd.arguments.clone();
                    }

                    Command(int cmd) {
                        command = cmd;
                        arguments = null;
                    }

                    Command(int cmd, Object args) {
                        command = cmd;
                        arguments = new Object[]{args};
                    }

                    Command(int cmd, Object ... args) {
                        command = cmd;
                        arguments = args;
                    }
                };


                ArrayList<Command> commands = new ArrayList();

                protected void add(Command command) {
                    commands.add(new Command(command));
                }

                public void add(int command_id) {
                    commands.add(new Command(command_id));
                }

                public void add(int command_id, Object Argument) {
                    commands.add(new Command(command_id, Argument));
                }

                public void add(int command_id, Object ... Arguments) {
                    commands.add(new Command(command_id, Arguments));
                }

                public abstract void execute();
            }


            private CommandBuilder commandList = new CommandBuilder() {
                @Override
                public void execute() {

                    int size = commands.size();

                    // NOTE: adding views without first setting a target is an error
                    for (int i1 = 0; i1 < commands.size(); i1++) {
                        if (commands.get(i1).command == Commands.withTarget) {
                            // work backwards
                            for (int i2 = i1-1; i2 >= 0; i2--) {
                                int cmd = commands.get(i2).command;
                                // stop processing if we reach a previous target
                                if (cmd == Commands.withTarget) break;
                                if (cmd == Commands.addView) {
                                    throw new RuntimeException("error: addView called before withTarget");
                                }
                            }
                        }
                    }

                    // we should have determined sufficiently that there are no errors

                    // build as normal

                    // build order: process in groups of withTarget's
                    // a group should always start with a withTarget

                    ArrayList<CommandBuilder> groups = new ArrayList();

                    // we need to process all addView commands first

                    // first pass

                    for (int i1 = 0; i1 < commands.size(); i1++) {
                        // search until we get to a withTarget
                        if (commands.get(i1).command == Commands.withTarget) {
                            CommandBuilder tmp = new CommandBuilder() {
                                @Override
                                public void execute() {
                                    int size = commands.size();
                                    for (int i = 0; i < size; i++) {
                                        Command command = commands.get(i);
                                        switch (command.command) {
                                            case Commands.withTarget:
                                                Log.i(TAG, "execute: withTarget");
                                                applyTo = (ConstraintLayout) command.arguments[0];
                                                break;
                                            case Commands.addView:
                                                Log.i(TAG, "execute: addView");
                                                View view = (View) command.arguments[0];
                                                Object o = command.arguments[1];

                                                // ConstraintLayout.LayoutParams extends LayoutParams
                                                // so check for ConstraintLayout.LayoutParams first
                                                if (o instanceof ConstraintLayout.LayoutParams) {
                                                    applyTo.addView(view, (ConstraintLayout.LayoutParams) o);
                                                } else if (o instanceof ViewGroup.LayoutParams) {
                                                    // upgrade to ConstraintLayout.LayoutParams
                                                    // if o is directly cast to ConstraintLayout.LayoutParams it's margins will not be preserved
                                                    ViewGroup.MarginLayoutParams x = (ViewGroup.MarginLayoutParams) o;
                                                    ConstraintLayout.LayoutParams marginLayoutParams = new ConstraintLayout.LayoutParams(x);
                                                    marginLayoutParams.layoutAnimationParameters = x.layoutAnimationParameters;
                                                    marginLayoutParams.setMargins(x.leftMargin, x.topMargin, x.rightMargin, x.bottomMargin);
                                                    applyTo.addView(view, marginLayoutParams);
                                                } else if (o instanceof ViewGroup.LayoutParams) {
                                                    applyTo.addView(view, (ViewGroup.LayoutParams) o);
                                                } else {
                                                    throw new RuntimeException("unknown LayoutParams type");
                                                }

                                                break;
                                            default:
                                                throw new IllegalStateException("Unexpected value: " + command.command);
                                        }
                                    }
                                }
                            };

                            // add until reach next withTarget or end
                            // add current withTarget
                            tmp.add(commands.get(i1));
                            // save i1 so we can restore it later
                            int saved = i1;
                            // skip current withTarget so we dont immediately break
                            i1++;
                            for (; i1 < commands.size(); i1++) {
                                Command cmd = commands.get(i1);
                                if (cmd.command == Commands.withTarget) {
                                    break;
                                }
                                // only add specific commands
                                if (cmd.command == Commands.addView) tmp.add(cmd);
                            }
                            // restore i1
                            i1 = saved;
                            groups.add(tmp);
                        }
                    }

                    // at this point, our add views and withTarget's have been added to groups
                    // process as normal now

                    // second pass

                    for (int i1 = 0; i1 < commands.size(); i1++) {
                        if (commands.get(i1).command == Commands.withTarget) {
                            CommandBuilder tmp = new CommandBuilder() {
                                @Override
                                public void execute() {
                                    int size = commands.size();
                                    boolean withTarget_called = false;
                                    for (int i = 0; i < size; i++) {
                                        Command command = commands.get(i);
                                        switch (command.command) {
                                            case Commands.withTarget:
                                                Log.i(TAG, "execute: withTarget");
                                                if (withTarget_called) throw new RuntimeException("setting new withTarget without applying to old target");
                                                withTarget_called = true;
                                                applyTo = (ConstraintLayout) command.arguments[0];
                                                constraintSet = new ConstraintSet();
                                                constraintSet.clone(applyTo);
                                                break;
                                            case Commands.setTargetView:
                                                Log.i(TAG, "execute: setTargetView");
                                                target = (View) command.arguments[0];
                                                break;
                                            // addView has already been processed, skip it
                                            case Commands.connect:
                                                Log.i(TAG, "execute: connect");
                                                int id = (Integer) command.arguments[1];
                                                constraintSet.connect(target.getId(), (Integer) command.arguments[0], id == parent ? applyTo.getId() : id, (Integer) command.arguments[2]);
                                                break;
                                            case Commands.apply:
                                                Log.i(TAG, "execute: apply");
                                                constraintSet.applyTo(applyTo);
                                                withTarget_called = false;
                                                break;
                                            default:
                                                throw new IllegalStateException("Unexpected value: " + command.command);
                                        }
                                    }
                                }
                            };

                            // add until reach next withTarget or end
                            // add current withTarget
                            tmp.add(commands.get(i1));
                            // save i1 so we can restore it later
                            int saved = i1;
                            // skip current withTarget so we dont immediately break
                            i1++;
                            boolean need_to_apply = true;
                            for (; i1 < commands.size(); i1++) {
                                Command cmd = commands.get(i1);
                                if (cmd.command == Commands.withTarget) {
                                    // need_to_apply is true if we have commands that need applying
                                    if (need_to_apply) {
                                        tmp.add(Commands.apply);
                                        need_to_apply = false;
                                    }
                                    break;
                                }
                                // process all commands except for addView
                                if (cmd.command == Commands.connect) {
                                    need_to_apply = true;
                                    tmp.add(cmd);
                                } else if (cmd.command != Commands.addView) {
                                    tmp.add(cmd);
                                }
                            }
                            // need_to_apply is true if we have commands that need applying
                            if (need_to_apply) tmp.add(Commands.apply);
                            // restore i1
                            i1 = saved;
                            groups.add(tmp);
                        }
                    }

                    // at this point, our group should be fully built, begin execution
                    // execute each group
                    for (CommandBuilder commandBuilder : groups) commandBuilder.execute();
                }
            };

            private static class Commands {
                static final int setTargetView = 1;
                static final int connect = 2;
                static final int addView = 3;
                static final int withTarget = 4;
                static final int apply = 5;
            }

            // cannot be instantiated by user
            private Builder() {}

            public Builder setLayoutConstraintsTarget(View view) {
                commandList.add(Commands.setTargetView, view);
                return this;
            }

            public Builder layout_constraintLeft_toLeftOf(View view) {
                commandList.add(Commands.connect, ConstraintSet.LEFT, view.getId(), ConstraintSet.LEFT);
                return this;
            }

            public Builder layout_constraintLeft_toLeftOf(int id) {
                commandList.add(Commands.connect, ConstraintSet.LEFT, id, ConstraintSet.LEFT);
                return this;
            }

            public Builder layout_constraintRight_toRightOf(View view) {
                commandList.add(Commands.connect, ConstraintSet.RIGHT, view.getId(), ConstraintSet.RIGHT);
                return this;
            }

            public Builder layout_constraintRight_toRightOf(int id) {
                commandList.add(Commands.connect, constraintSet.RIGHT, id, constraintSet.RIGHT);
                return this;
            }

            public Builder layout_constraintTop_toTopOf(View view) {
                commandList.add(Commands.connect, constraintSet.TOP, view.getId(), constraintSet.TOP);
                return this;
            }

            public Builder layout_constraintTop_toTopOf(int id) {
                commandList.add(Commands.connect, constraintSet.TOP, id, constraintSet.TOP);
                return this;
            }

            public Builder layout_constraintBottom_toBottomOf(View view) {
                commandList.add(Commands.connect, constraintSet.BOTTOM, view.getId(), constraintSet.BOTTOM);
                return this;
            }

            public Builder layout_constraintBottom_toBottomOf(int id) {
                commandList.add(Commands.connect, constraintSet.BOTTOM, id, constraintSet.BOTTOM);
                return this;
            }

            public Builder layout_constraintAll_ToAllOf(int id) {
                return layout_constraintLeft_toLeftOf(id)
                        .layout_constraintTop_toTopOf(id)
                        .layout_constraintRight_toRightOf(id)
                        .layout_constraintBottom_toBottomOf(id);
            }

            public Builder layout_constraintAll_ToAllOf(View view) {
                return layout_constraintLeft_toLeftOf(view)
                        .layout_constraintTop_toTopOf(view)
                        .layout_constraintRight_toRightOf(view)
                        .layout_constraintBottom_toBottomOf(view);
            }

            public void addView(View view, ConstraintLayout.LayoutParams layoutParams, int left, int top, int right, int bottom) {
                commandList.add(Commands.addView, view, layoutParams);
            }

            public void addView(View view, ViewGroup.LayoutParams layoutParams) {
                ConstraintLayout.LayoutParams layoutParams1 = new ConstraintLayout.LayoutParams(layoutParams.width, layoutParams.height);
                layoutParams1.layoutAnimationParameters = layoutParams.layoutAnimationParameters;
                commandList.add(Commands.addView, view, layoutParams1);
            }

            public void addView(View view, ViewGroup.MarginLayoutParams layoutParams) {
                ConstraintLayout.LayoutParams layoutParams1 = new ConstraintLayout.LayoutParams(layoutParams.width, layoutParams.height);
                layoutParams1.layoutAnimationParameters = layoutParams.layoutAnimationParameters;
                layoutParams1.setMargins(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.rightMargin, layoutParams.bottomMargin);
                commandList.add(Commands.addView, view, layoutParams1);
            }

            public void addView(View view, int height, int width) {
                ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(width, height);
                commandList.add(Commands.addView, view, layoutParams);
            }

            public void addView(View view, ViewGroup.LayoutParams layoutParams, int left, int top, int right, int bottom) {
                ConstraintLayout.LayoutParams layoutParams1 = new ConstraintLayout.LayoutParams(layoutParams.width, layoutParams.height);
                layoutParams1.layoutAnimationParameters = layoutParams.layoutAnimationParameters;
                layoutParams1.setMargins(left, top, right, bottom);
                commandList.add(Commands.addView, view, layoutParams1);
            }

            public void addView(View view, int height, int width, int left, int top, int right, int bottom) {
                ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(width, height);
                layoutParams.setMargins(left, top, right, bottom);
                commandList.add(Commands.addView, view, layoutParams);
            }

            public void build() {
                commandList.execute();
            }
        }
    }
    int toDP(float val) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, val, getResources().getDisplayMetrics());
    }
}
