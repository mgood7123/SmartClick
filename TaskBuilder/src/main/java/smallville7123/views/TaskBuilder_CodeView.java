package smallville7123.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import smallville7123.taggable.Taggable;

class TaskBuilder_CodeView {
    public String TAG = Taggable.getTag(this);

    TaskBuilder taskBuilder;

    TaskBuilder_CodeView_Editor editor = new TaskBuilder_CodeView_Editor();
    TaskBuilder_CodeView_Renderer renderer = new TaskBuilder_CodeView_Renderer();

    void construct(TaskBuilder taskBuilder, @NonNull Context context, @Nullable AttributeSet attrs, @Nullable Integer defStyleAttr, @Nullable Integer defStyleRes) {
        this.taskBuilder = taskBuilder;
//        renderer.setEditor(editor);
//        editor.setRenderer(renderer);
//        renderer.setTextBookView(taskBuilder.CodeView.findViewById(R.id.codeEditor));
//        renderer.getTextBookView().setSampleText();
//        editor.append("hello");
//        editor.append(", goodbye. ");
//        editor.append(editor.getText(), 80);
//        editor.render();
    }
}