package com.example.hello_world.Database;

public class LikeInfo {
    private int userId;
    private int shareId;

    public LikeInfo(int userId, int shareId) {
        this.userId = userId;
        this.shareId = shareId;
    }

    public int getUserId() { return userId; }

    public int getShareId() { return shareId; }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setShareId(int shareId) {
        this.shareId = shareId;
    }
}
