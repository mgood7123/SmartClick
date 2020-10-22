package smallville7123.views;

public class TaskBuilder_CodeView_Editor {

    private TaskBuilder_CodeView_Renderer renderer;
    private String data = "";

    public void setRenderer(TaskBuilder_CodeView_Renderer renderer) {
        this.renderer = renderer;
    }

    public void render() {
        renderer.setData(data);
        renderer.render();
    }

    public void append(String text) {
        append(text, 1);
    }

    public void append(String text, int repeatTimes) {
        if (text == null) return;
        for (int i = 0; i < repeatTimes; i++) {
            data = data.concat(text);
        }
    }

    public String getText() {
        return data;
    }
}
