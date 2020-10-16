package smallville7123.layoututils;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static smallville7123.taggable.Taggable.getLastClassName;
import static smallville7123.taggable.Taggable.getShortTag;

public class ViewHierarchy {

    public String TAG = getLastClassName(this);

    ArrayList<ViewHierarchy> children;
    View view;
    View.OnTouchListener savedTouchListener;
    View.OnClickListener savedClickListener;
    Bundle data = new Bundle();
    ViewHolder saveData;
    ViewHolder processView;
    ViewHolder restoreData;

    // build up a child hierarchy

    public void analyze(View root) {
        analyze(root, true);
    }

    public void analyze(ViewGroup root) {
        analyze(root, true);
    }

    public void analyze(View root, boolean analyzeChildren) {
        if (root instanceof ViewGroup) analyze((ViewGroup) root, analyzeChildren);
        else view = root;
    }

    public void analyze(ViewGroup root, boolean analyzeChildren) {
        view = root;
        if (analyzeChildren) {
            int childCount = root.getChildCount();
            if (childCount != 0) children = new ArrayList<>();
            for (int i = 0; i < childCount; i++) {
                ViewHierarchy viewHierarchy = new ViewHierarchy();
                viewHierarchy.analyze(root.getChildAt(i));
                children.add(viewHierarchy);
            }
        }
    }
    //Used for new ListenerInfo class structure used beginning with API 14 (ICS)

    public static View.OnClickListener getOnClickListener(View view) {
        if (view == null) return null;
        View.OnClickListener retrievedListener = null;
        String viewStr = "android.view.View";
        String lInfoStr = "android.view.View$ListenerInfo";
        Class x = null;
        try {
            x = Class.forName(viewStr);
        } catch (ClassNotFoundException ex) {
            Log.e("Reflection", "Class Not Found.", ex);
        }
        Field listenerField = null;
        try {
            listenerField = x.getDeclaredField("mListenerInfo");
        } catch (NoSuchFieldException ex) {
            Log.e("Reflection", "No Such Field.", ex);
        }
        Object listenerInfo = null;

        if (listenerField != null) {
            listenerField.setAccessible(true);
            try {
                listenerInfo = listenerField.get(view);
            } catch (IllegalAccessException ex) {
                Log.e("Reflection", "Illegal Access.", ex);
            }
        }

        Class z = null;
        try {
            z = Class.forName(lInfoStr);
        } catch (ClassNotFoundException ex) {
            Log.e("Reflection", "Class Not Found.", ex);
        }
        Field clickListenerField = null;
        try {
            clickListenerField = z.getDeclaredField("mOnClickListener");
        } catch (NoSuchFieldException ex) {
            Log.e("Reflection", "No Such Field.", ex);
        }

        if (clickListenerField != null && listenerInfo != null) {
            clickListenerField.setAccessible(true);
            try {
                retrievedListener = (View.OnClickListener) clickListenerField.get(listenerInfo);
            } catch (IllegalAccessException ex) {
                Log.e("Reflection", "Illegal Access.", ex);
            }
        }

        return retrievedListener;
    }
    //Used for new ListenerInfo class structure used beginning with API 14 (ICS)

    public static View.OnTouchListener getOnTouchListener(View view) {
        if (view == null) return null;
        View.OnTouchListener retrievedListener = null;
        String viewStr = "android.view.View";
        String lInfoStr = "android.view.View$ListenerInfo";
        Class x = null;
        try {
            x = Class.forName(viewStr);
        } catch (ClassNotFoundException ex) {
            Log.e("Reflection", "Class Not Found.", ex);
        }
        Field listenerField = null;
        try {
            listenerField = x.getDeclaredField("mListenerInfo");
        } catch (NoSuchFieldException ex) {
            Log.e("Reflection", "No Such Field.", ex);
        }
        Object listenerInfo = null;

        if (listenerField != null) {
            listenerField.setAccessible(true);
            try {
                listenerInfo = listenerField.get(view);
            } catch (IllegalAccessException ex) {
                Log.e("Reflection", "Illegal Access.", ex);
            }
        }

        Class z = null;
        try {
            z = Class.forName(lInfoStr);
        } catch (ClassNotFoundException ex) {
            Log.e("Reflection", "Class Not Found.", ex);
        }
        Field touchListenerField = null;
        try {
            touchListenerField = z.getDeclaredField("mOnTouchListener");
        } catch (NoSuchFieldException ex) {
            Log.e("Reflection", "No Such Field.", ex);
        }

        if (touchListenerField != null && listenerInfo != null) {
            touchListenerField.setAccessible(true);
            try {
                retrievedListener = (View.OnTouchListener) touchListenerField.get(listenerInfo);
            } catch (IllegalAccessException ex) {
                Log.e("Reflection", "Illegal Access.", ex);
            }
        }

        return retrievedListener;
    }

    public void save() {
        if (saveData != null) {
            Log.d(TAG, "save: invoking saveData.process for view " + getLastClassName(view));
            saveData.process(this, data);
        }
        if (children != null) {
            for (ViewHierarchy child : children) {
                child.saveData = saveData;
                child.save();
                child.saveData = null;
            }
        }
    }

    public void process() {
        if (processView != null) {
            Log.d(TAG, "process: invoking processView.process for view " + getLastClassName(view));
            processView.process(this, data);
        }
        if (children != null) {
            for (ViewHierarchy child : children) {
                child.processView = processView;
                child.process();
                child.processView = null;
            }
        }
    }

    public void restore() {
        if (restoreData != null) {
            Log.d(TAG, "restore: invoking restoreData.process for view " + getLastClassName(view));
            restoreData.process(this, data);
        }
        if (children != null) {
            for (ViewHierarchy child : children) {
                child.restoreData = restoreData;
                child.restore();
                child.restoreData = null;
            }
        }
    }

    public void saveOnClickListener() {
        savedClickListener = getOnClickListener(view);
    }

    public void saveOnTouchListener() {
        savedTouchListener = getOnTouchListener(view);
    }

    public void restoreOnClickListener() {
        view.setOnClickListener(savedClickListener);
        savedClickListener = null;
    }

    public void restoreOnTouchListener() {
        view.setOnTouchListener(savedTouchListener);
        savedTouchListener = null;
    }

    abstract static class ViewHolder {
        abstract void process(ViewHierarchy viewHierarchy, Bundle data);
    }

    public void setOnViewSaveData(ViewHolder viewHolder) {
        saveData = viewHolder;
    }

    public void setOnProcessView(ViewHolder viewHolder) {
        processView = viewHolder;
    }

    public void setOnViewRestoreData(ViewHolder viewHolder) {
        restoreData = viewHolder;
    }
}
