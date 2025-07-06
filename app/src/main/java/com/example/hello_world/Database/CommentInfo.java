package com.example.hello_world.Database;

public class CommentInfo {
    private int commentId;
    private int shareId;
    private int userId;
    private String content;
    private String time;

    public CommentInfo(int commentId, int shareId, int userId, String content, String time) {
        this.commentId = commentId;
        this.shareId = shareId;
        this.userId = userId;
        this.content = content;
        this.time = time;
    }

    public int getCommentId() { return commentId; }

    public int getShareId() { return shareId; }

    public int getUserId() { return userId; }

    public String getContent() { return content; }

    public String getTime() { return time; }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public void setShareId(int shareId) {
        this.shareId = shareId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
