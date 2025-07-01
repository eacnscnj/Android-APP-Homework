package com.example.hello_world.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class LoginDBManage  {

//    private static SQLiteDatabase db;
//
//    public static void initDB(Context context) {
//        DBOpenHelper helper = new DBOpenHelper(context);
//        db = helper.getWritableDatabase();
//    }
//
//    /*
//    注册
//     */
//    public int Insert(String username, String password, int register_type) {
//
//
//        ContentValues values = new ContentValues();
//        //填充占位符
//        values.put("username", username);
//        values.put("password", password);
//        values.put("register_type", register_type);
//        String nullColumnHack = "values(null,?,?,?)";
//        //执行
//        int insert = (int) db.insert("user_table", nullColumnHack, values);
//        db.close();
//        return insert;
//    }



}
