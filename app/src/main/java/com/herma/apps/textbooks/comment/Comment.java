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
    private int like;
    private int dislike;
    private boolean addReplyToParent = false;


    public Comment() {
        this.replies = new ArrayList<>();
    }

    public boolean isAddReplyToParent() {
        return addReplyToParent;
    }

    public void setAddReplyToParent(boolean addReplyToParent) {
        this.addReplyToParent = addReplyToParent;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getDislike() {
        return dislike;
    }

    public void setDislike(int dislike) {
        this.dislike = dislike;
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
