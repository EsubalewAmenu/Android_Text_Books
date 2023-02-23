package com.herma.apps.textbooks.comment;

import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class Comment {
    private int commentId;
    private String comment;
    private String author;
    private String timestamp;
    private String author_avatar_url;
    private Button btnLike;
    private Button btnReply;
    private List<Comment> replies;
    private int childCommentCount;
    private boolean childSeen;
    private int like;
    private int dislike;
    private boolean addReplyToParent = false;
    private int is_user_liked;
    private int is_user_disliked;

    public Comment() {
        this.replies = new ArrayList<>();
    }


    public int getIs_user_liked() {
        return is_user_liked;
    }

    public void setIs_user_liked(int is_user_liked) {
        this.is_user_liked = is_user_liked;
    }

    public int getIs_user_disliked() {
        return is_user_disliked;
    }

    public void setIs_user_disliked(int is_user_disliked) {
        this.is_user_disliked = is_user_disliked;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public String getAuthor_avatar_url() {
        return author_avatar_url;
    }

    public void setAuthor_avatar_url(String author_avatar_url) {
        this.author_avatar_url = author_avatar_url;
    }

    public boolean isAddReplyToParent() {
        return addReplyToParent;
    }

    public void setAddReplyToParent(boolean addReplyToParent) {
        this.addReplyToParent = addReplyToParent;
    }

    public int getChildCommentCount() {
        return childCommentCount;
    }

    public void setChildCommentCount(int childCommentCount) {
        this.childCommentCount = childCommentCount;
    }

    public boolean isChildSeen() {
        return childSeen;
    }

    public void setChildSeen(boolean childSeen) {
        this.childSeen = childSeen;
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
