package smallville7123.views;

public class TaskBuilder_CodeView_Renderer {
    private TextView textView;
    private TaskBuilder_CodeView_Editor editor;
    private String data = "";

    public void setTextView(TextView renderer) {
        textView = renderer;
    }

    public void setEditor(TaskBuilder_CodeView_Editor editor) {
        this.editor = editor;
    }

    public void renderText(String text) {
        textView.setText(text);
    }

    public void render() {
        renderText(data);
    }

    public void setData(String data) {
        this.data = data;
    }
}
