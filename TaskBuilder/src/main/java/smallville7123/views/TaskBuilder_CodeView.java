package smallville7123.views;

import android.content.Context;
import android.graphics.text.LineBreaker;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

import smallville7123.taggable.Taggable;
import smallville7123.textbook.TextBook;
import smallville7123.textbook.TextBookView;

class TaskBuilder_CodeView {
    public String TAG = Taggable.getTag(this);

    TaskBuilder taskBuilder;

    TaskBuilder_CodeView_Editor editor = new TaskBuilder_CodeView_Editor();
    TaskBuilder_CodeView_Renderer renderer = new TaskBuilder_CodeView_Renderer();

    void construct(TaskBuilder taskBuilder, @NonNull Context context, @Nullable AttributeSet attrs, @Nullable Integer defStyleAttr, @Nullable Integer defStyleRes) {
        this.taskBuilder = taskBuilder;
        renderer.setEditor(editor);
        editor.setRenderer(renderer);
        renderer.setTextBookView(taskBuilder.CodeView.findViewById(R.id.codeEditor));
        TextView textBookView = renderer.getTextBookView();
        textBookView.setTextSize(30.0f);
        textBookView.setBreakStrategy(LineBreaker.BREAK_STRATEGY_SIMPLE);
        try {
            textBookView.setText(TextBook.readTextFile(textBookView.getResources().getAssets().open(TextBookView.SAMPLE_TEXT_LONG)));
        } catch (IOException e) {
            e.printStackTrace();
        }
//        editor.append("hello");
//        editor.append(", goodbye. ");
//        editor.append(editor.getText(), 80);
        editor.render();
    }
}