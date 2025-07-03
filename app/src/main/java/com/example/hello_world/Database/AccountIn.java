package com.example.hello_world.Database;


//记录一条数据的相关内容类
public class AccountIn {
    int id;
    String typename;
    int focusImageID;
    String note;
    float studyTime;
    String time;
    int year;
    int mounth;
    int day;
    int kind;//pubilc 0 , major1;
    int userId; // **新增 userId 字段**

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    public int getFocusImageID() {
        return focusImageID;
    }

    public void setFocusImageID(int focusImageID) {
        this.focusImageID = focusImageID;
    }

    public float getStudyTime() {
        return studyTime;
    }

    public void setStudyTime(float studyTime) {
        this.studyTime = studyTime;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMounth() {
        return mounth;
    }

    public void setMounth(int mounth) {
        this.mounth = mounth;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    // **新增 userId 的 Getter 和 Setter**
    public int getUserId() { return userId; }

    public void setUserId(int userId) { this.userId = userId; }

    public AccountIn() {
    }

    public AccountIn(int id, String typename, int focusImageID, String note, float studyTime, int year, int mounth, int day, int kind) {
        this.id = id;
        this.typename = typename;
        this.focusImageID = focusImageID;
        this.note = note;
        this.studyTime = studyTime;
        this.year = year;
        this.mounth = mounth;
        this.day = day;
        this.kind = kind;
    }

    public AccountIn(int id, String typename, int focusImageID, String note, float studyTime, int year, int mounth, int day, int kind, int userId) {
        this.id = id;
        this.typename = typename;
        this.focusImageID = focusImageID;
        this.note = note;
        this.studyTime = studyTime;
        this.year = year;
        this.mounth = mounth;
        this.day = day;
        this.kind = kind;
        this.userId = userId;
    }
}
