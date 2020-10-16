package smallville7123.layoututils;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.util.ArrayList;

import smallville7123.taggable.Taggable;

import static smallville7123.taggable.Taggable.getTag;
import static smallville7123.taggable.Taggable.getLastClassName;

public class MotionEventDispatcher {
    public String TAG = getTag(this);

    View.OnClickListener onClickListenerDoNothing = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick: do nothing");
        }
    };

    View.OnTouchListener onTouchListenerDoNothing = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.d(TAG, "onTouch: do nothing");
            return false;
        }
    };

    public boolean dispatchMotionEvent(final View root, MotionEvent motionEvent) {
        ArrayList<View> viewArrayList = findTouchedView(root, motionEvent);
        if (viewArrayList == null) throw new RuntimeException("failed to find Touched View");
        if (viewArrayList.size() == 0) return false;
        ArrayList<String> mChildrenNames = getClassNames(viewArrayList);
        // log available listeners
        for (int i = 0; i < viewArrayList.size(); i++) {
            Log.d(TAG, LayoutUtils.indentString(i, "    ") + mChildrenNames.get(i) + " hasOnClickListener = [" + (ViewHierarchy.getOnClickListener(viewArrayList.get(i)) != null) + "]");
            Log.d(TAG, LayoutUtils.indentString(i, "    ") + mChildrenNames.get(i) + " hasOnTouchListener = [" + (ViewHierarchy.getOnTouchListener(viewArrayList.get(i)) != null) + "]");
        }
        // click last available listener
        final View child = getLastListener(viewArrayList);
        View a = viewArrayList.get(0);
        View b = viewArrayList.get(1);
        final View v = a == root ? b : a;
        final View caller = child != null ? child : v;
        boolean callerHasOnClickListener = ViewHierarchy.getOnClickListener(caller) != null;
        boolean callerHasOnTouchListener = ViewHierarchy.getOnTouchListener(caller) != null;
        ViewHierarchy viewHierarchy = new ViewHierarchy();
        viewHierarchy.analyze(v, false);
        viewHierarchy.setOnViewSaveData(new ViewHierarchy.ViewHolder() {
            @Override
            void process(ViewHierarchy viewHierarchy, Bundle data) {
                if (viewHierarchy.view != caller) {
                    viewHierarchy.saveOnClickListener();
                    viewHierarchy.saveOnTouchListener();
                    data.putBoolean("viewHierarchy.view.isClickable()", viewHierarchy.view.isClickable());
                    data.putBoolean("viewHierarchy.view.isScrollContainer()", viewHierarchy.view.isScrollContainer());
                    data.putBoolean("viewHierarchy.view.isEnabled()", viewHierarchy.view.isEnabled());
                    data.putBoolean("viewHierarchy.view.isActivated()", viewHierarchy.view.isActivated());
                }
            }
        });
        viewHierarchy.setOnProcessView(new ViewHierarchy.ViewHolder() {
            @Override
            void process(ViewHierarchy viewHierarchy, Bundle data) {
                if (viewHierarchy.view != caller) {
                    viewHierarchy.view.setOnClickListener(onClickListenerDoNothing);
                    viewHierarchy.view.setOnTouchListener(onTouchListenerDoNothing);
                    viewHierarchy.view.setClickable(false);
                    viewHierarchy.view.setScrollContainer(false);
                    viewHierarchy.view.setActivated(false);
                }
            }
        });
        viewHierarchy.setOnViewRestoreData(new ViewHierarchy.ViewHolder() {
            @Override
            void process(ViewHierarchy viewHierarchy, Bundle data) {
                if (viewHierarchy.view != caller) {
                    viewHierarchy.restoreOnClickListener();
                    viewHierarchy.restoreOnTouchListener();
                    viewHierarchy.view.setClickable(data.getBoolean("view.isClickable()"));
                    viewHierarchy.view.setScrollContainer(data.getBoolean("view.isScrollContainer()"));
                    viewHierarchy.view.setEnabled(data.getBoolean("view.isEnabled()"));
                    viewHierarchy.view.setActivated(data.getBoolean("viewHierarchy.view.isActivated()"));
                }
            }
        });

        viewHierarchy.save();
        viewHierarchy.process();
        boolean result = true;
        if (callerHasOnClickListener) {
            Log.d(TAG, "dispatchMotionEvent: calling callOnClick for view = [" + getLastClassName(caller) + "]");
            result = caller.callOnClick();
        }
        if (callerHasOnTouchListener) {
            Log.d(TAG, "dispatchMotionEvent: calling dispatchTouchEvent for view = [" + getLastClassName(caller) + "]");
            result = caller.dispatchTouchEvent(motionEvent);
        }
        viewHierarchy.restore();
        return result;
    }

    public View getLastListener(ArrayList<View> viewArrayList) {
        for (int i = viewArrayList.size() - 1; i >= 0; i--) {
            View child = viewArrayList.get(i);
            if (ViewHierarchy.getOnClickListener(child) != null) return child;
            if (ViewHierarchy.getOnTouchListener(child) != null) return child;
        }
        return null;
    }

    public ArrayList<View> findTouchedView(View root, MotionEvent ev) {
        if (root instanceof ViewGroup) return findTouchedView((ViewGroup) root, ev);
        else {
            ViewParent parent = root.getParent();
            if (parent instanceof View) return findTouchedView((View) parent, ev);
            else throw new RuntimeException("unknown parent type");
        }
    }

    public ArrayList<View> findTouchedView(ViewGroup root, MotionEvent ev) {
        // traverse the view hierarchy
        final ArrayList<View> mChildren = new ArrayList();
        StringBuilder out = new StringBuilder("\n");
        StringBuilder matches = new StringBuilder("\n");
        StringBuilder doesNotMatch = new StringBuilder("\n");
        getTouchedViews(out, matches, doesNotMatch, mChildren, root, ev);
        Log.d(TAG, "matches = [" + matches + "]");
        return mChildren;
    }

    public ArrayList<String> getClassNames(ArrayList<? extends View> mChildren) {
        ArrayList<String> arrayList = new ArrayList(mChildren.size());
        for (View child : mChildren) {
            arrayList.add(getLastClassName(Taggable.getName(child)));
        }
        return arrayList;
    }

    int indent;

    public void getTouchedViews(StringBuilder out, StringBuilder matches, StringBuilder doesNotMatch, ArrayList<View> mChildren, View root, MotionEvent ev) {
        // scan front to back
        if (motionEventIsInsideView(out, matches, doesNotMatch, root, ev)) mChildren.add(root);
        if (root instanceof ViewGroup) {
            indent++;
            ViewGroup rootGroup = (ViewGroup) root;
            View[] children = LayoutUtils.getChildren(rootGroup);
            for (int i = 0; i < children.length; i++)
                getTouchedViews(out, matches, doesNotMatch, mChildren, rootGroup.getChildAt(i), ev);
            indent--;
        }
    }

    public boolean motionEventIsInsideView(StringBuilder out, StringBuilder matches, StringBuilder doesNotMatch, View view, MotionEvent ev) {
        return isPointInsideView(out, matches, doesNotMatch, ev.getRawX(), ev.getRawY(), view);
    }

    /**
     * Determines if given points are inside view
     *
     * @param x    - x coordinate of point
     * @param y    - y coordinate of point
     * @param view - view object to compare
     * @return true if the points are within view bounds, false otherwise
     */
    public boolean isPointInsideView(StringBuilder out, StringBuilder matches, StringBuilder doesNotMatch, float x, float y, View view) {
        int location[] = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];

        //point is inside view bounds
        if ((x > viewX && x < (viewX + view.getWidth())) &&
                (y > viewY && y < (viewY + view.getHeight()))) {
            String str = LayoutUtils.indentString(indent, "    ") + "Within: " + true + ", view = [" + getLastClassName(view) + "], xy = [" + x + "," + y + "], location = [" + LayoutUtils.locationToString(location) + "]\n";
            out.append(str);
            matches.append(str);
            return true;
        } else {
            String str = LayoutUtils.indentString(indent, "    ") + "Within: " + false + ", view = [" + getLastClassName(view) + "], xy = [" + x + "," + y + "], location = [" + LayoutUtils.locationToString(location) + "]\n";
            out.append(str);
            doesNotMatch.append(str);
            return false;
        }
    }
}
