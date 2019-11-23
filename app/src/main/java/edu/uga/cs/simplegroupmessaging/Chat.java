package edu.uga.cs.simplegroupmessaging;

public class Chat {

    private String title;

    public Chat() {
        this.title = null;
    }

    public Chat(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
