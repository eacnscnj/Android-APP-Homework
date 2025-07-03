package com.example.hello_world.Database;

public class UserInfo {
    private int _id;
    private String username;
    private String password;
    private int register_type;   //用户类型  0代表普通给用户 1代表超级用户

    public static  UserInfo sUserInfo;

    public UserInfo() {
        ;
    }

    public static UserInfo getUserInfo() {
        return sUserInfo;
    }

    public static void setUserInfo(UserInfo userInfo) {
        sUserInfo = userInfo;
    }

    public UserInfo(int _id, String username, String password, int register_type) {
        this._id = _id;
        this.username = username;
        this.password = password;
        this.register_type = register_type;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRegister_type() {
        return register_type;
    }

    public void setRegister_type(int register_type) {
        this.register_type = register_type;
    }
}
