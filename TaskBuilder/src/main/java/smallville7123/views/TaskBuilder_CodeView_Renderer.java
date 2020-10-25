package smallville7123.views;

import smallville7123.textbook.TextBookView;

public class TaskBuilder_CodeView_Renderer {
    private TextBookView textBookView;
    private TaskBuilder_CodeView_Editor editor;
    private String data = "";

    public void setTextBookView(TextBookView renderer) {
        textBookView = renderer;
    }

    public void setEditor(TaskBuilder_CodeView_Editor editor) {
        this.editor = editor;
    }

    public void renderText(String text) {
        textBookView.setText(text);
    }

    public void render() {
        renderText(data);
    }

    public void setData(String data) {
        this.data = data;
    }

    public TextBookView getTextBookView() {
        return textBookView;
    }
}
