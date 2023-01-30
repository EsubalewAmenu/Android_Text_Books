package com.herma.apps.textbooks.comment;

import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class Comment {
    private String comment;
    private String author;
    private String timestamp;
    private Button btnLike;
    private Button btnReply;
    private List<Comment> replies;

    public Comment(String comment, String author, String timestamp) {
        this.comment = comment;
        this.author = author;
        this.timestamp = timestamp;

        this.replies = new ArrayList<>();
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<Comment> getReplies() {
        return replies;
    }

    public void addReply(Comment reply) {
        this.replies.add(reply);
    }
}
