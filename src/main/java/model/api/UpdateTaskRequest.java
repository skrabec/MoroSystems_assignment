package model.api;

public class UpdateTaskRequest {

    private String text;

    public UpdateTaskRequest() {}

    public UpdateTaskRequest(String text) {
        this.text = text;
    }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}
