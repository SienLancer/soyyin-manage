package com.example.soyyinmanage.models;

public class Note {
    private String id;
    private String content;
    private boolean done;

    public Note() {} // bắt buộc cho Firebase

    public Note(String id, String content, boolean done) {
        this.id = id;
        this.content = content;
        this.done = done;
    }

    public String getId() { return id; }
    public String getContent() { return content; }
    public boolean isDone() { return done; }

    public void setId(String id) { this.id = id; }
    public void setContent(String content) { this.content = content; }
    public void setDone(boolean done) { this.done = done; }
}
