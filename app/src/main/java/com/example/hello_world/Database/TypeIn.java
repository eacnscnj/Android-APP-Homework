package com.example.hello_world.Database;


//学习的具体科目
public class TypeIn {
    int id;
    String typename;
    int imageID;
    int focusImageID;
    int kind;//公共课0，专业课1

    public TypeIn() {
    }

    public TypeIn(int id, String typename, int imageID, int focusImageID, int kind) {
        this.id = id;
        this.typename = typename;
        this.imageID = imageID;
        this.focusImageID = focusImageID;
        this.kind = kind;
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

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }

    public int getFocusImageID() {
        return focusImageID;
    }

    public void setFocusImageID(int focusImageID) {
        this.focusImageID = focusImageID;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }
}
