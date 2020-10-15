package smallville7123.tools;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.util.ArrayList;

public class Builder {
    private View target;
    private ConstraintSet constraintSet;
    private ConstraintLayout applyTo;

    public void withTarget(ConstraintLayout constraintLayout) {
        commandList.add(Commands.withTarget, constraintLayout);
    }


    CommandBuilder commandList = new CommandBuilder() {
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
                    tmp.TAG = TAG;

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
                                        constraintSet.connect(target.getId(), (Integer) command.arguments[0], id == ConstraintBuilder.parent ? applyTo.getId() : id, (Integer) command.arguments[2]);
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
                    tmp.TAG = TAG;

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
    Builder() {}

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

    public Builder layout_constraintLeft_toRightOf(View view) {
        commandList.add(Commands.connect, ConstraintSet.LEFT, view.getId(), ConstraintSet.RIGHT);
        return this;
    }

    public Builder layout_constraintLeft_toRightOf(int id) {
        commandList.add(Commands.connect, ConstraintSet.LEFT, id, ConstraintSet.RIGHT);
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

    public Builder layout_constraintRight_toLeftOf(View view) {
        commandList.add(Commands.connect, ConstraintSet.RIGHT, view.getId(), ConstraintSet.LEFT);
        return this;
    }

    public Builder layout_constraintRight_toLeftOf(int id) {
        commandList.add(Commands.connect, constraintSet.RIGHT, id, constraintSet.LEFT);
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

    public void addView(View view, int width, int height) {
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(width, height);
        commandList.add(Commands.addView, view, layoutParams);
    }

    public void addView(View view, ViewGroup.LayoutParams layoutParams, int left, int top, int right, int bottom) {
        ConstraintLayout.LayoutParams layoutParams1 = new ConstraintLayout.LayoutParams(layoutParams.width, layoutParams.height);
        layoutParams1.layoutAnimationParameters = layoutParams.layoutAnimationParameters;
        layoutParams1.setMargins(left, top, right, bottom);
        commandList.add(Commands.addView, view, layoutParams1);
    }

    public void addView(View view, int width, int height, int left, int top, int right, int bottom) {
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(width, height);
        layoutParams.setMargins(left, top, right, bottom);
        commandList.add(Commands.addView, view, layoutParams);
    }

    public void build() {
        commandList.execute();
    }
}
