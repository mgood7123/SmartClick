package smallville7123.views;

import android.widget.TextView;

import smallville7123.textbook.TextBookView;

public class TaskBuilder_CodeView_Renderer {
    private TextView textBookView;
    private TaskBuilder_CodeView_Editor editor;
    private String data = "";

    public void setTextBookView(TextView renderer) {
        textBookView = renderer;
    }

    public void setEditor(TaskBuilder_CodeView_Editor editor) {
        this.editor = editor;
    }

    public void renderText(String text) {
//        textBookView.setText(text);
    }

    public void render() {
        renderText(data);
    }

    public void setData(String data) {
        this.data = data;
    }

    public TextView getTextBookView() {
        return textBookView;
    }
}
