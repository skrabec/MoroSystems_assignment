package model.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Task {

    private String id;
    private String text;
    private boolean completed;
    private long createdDate;
    private Long completedDate;

    public Task() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public long getCreatedDate() { return createdDate; }
    public void setCreatedDate(long createdDate) { this.createdDate = createdDate; }

    public Long getCompletedDate() { return completedDate; }
    public void setCompletedDate(Long completedDate) { this.completedDate = completedDate; }

    @Override
    public String toString() {
        return "Task{id='" + id + "', text='" + text + "', completed=" + completed + "}";
    }
}
