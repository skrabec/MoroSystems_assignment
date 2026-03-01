package model.api;

public class CreateTaskRequest {

    private String text;

    public CreateTaskRequest() {}

    public CreateTaskRequest(String text) {
        this.text = text;
    }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}
