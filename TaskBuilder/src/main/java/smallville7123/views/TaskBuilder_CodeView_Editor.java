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

    public void append(String hello) {
        data = data.concat(hello);
    }
}
