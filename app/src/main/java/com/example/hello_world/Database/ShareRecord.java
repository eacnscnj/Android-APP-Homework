package com.example.hello_world.Database;

public class ShareRecord {
    private int id;
    private int userId;
    private int recordId;
    private String shareNote;
    private String shareTime;

    public ShareRecord(int id, int userId, int recordId, String shareNote, String shareTime) {
        this.id = id;
        this.userId = userId;
        this.recordId = recordId;
        this.shareNote = shareNote;
        this.shareTime = shareTime;
    }

    // Getter 和 Setter
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getRecordId() { return recordId; }
    public String getShareNote() { return shareNote; }
    public String getShareTime() { return shareTime; }

    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setRecordId(int recordId) { this.recordId = recordId; }
    public void setShareNote(String shareNote) { this.shareNote = shareNote; }
    public void setShareTime(String shareTime) { this.shareTime = shareTime; }
}
