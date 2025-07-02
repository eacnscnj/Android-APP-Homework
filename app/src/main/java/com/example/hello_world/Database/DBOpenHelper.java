package com.example.hello_world.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.hello_world.R;

public class DBOpenHelper extends SQLiteOpenHelper {

    public DBOpenHelper(@Nullable Context context) {
        super(context, "studyTime.dp", null, 1);
    }

    /*int id;
    String typename;
    int focusImageID;
    String note;
    float studyTime;
    String time;
    int year;
    int mounth;
    int day;
    int kind;*/
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.i("tag","manbabbbbbbbbbbbbbbbbbbbbbbbbbbbb");

        String sqlCreat="create table typetb(" +
                "id integer primary key autoincrement," +
                "typename varchar(12)," +
                "imageID integer," +
                "focusImageID integer," +
                "kind integer)";
        sqLiteDatabase.execSQL(sqlCreat);
        insertType(sqLiteDatabase);


        /*
        id integer primary key autoincrement
        username
        password
        register_type integer
         */
        //创建login使用的 user_table表
        String sql_Create_user_table="create table user_table(id integer primary key autoincrement,username varchar(12),password varchar(20),register_type integer)";
        sqLiteDatabase.execSQL(sql_Create_user_table);
        insertUserTable(sqLiteDatabase);




        //记录表
        String sql = "create table studyTimeTable(id integer primary key autoincrement,typename varchar(12),focuseImageID integer,note varchar(128),studyTime float,time varchar(64)," +
                "year integer,mounth integer,day integer, kind integer)";
        sqLiteDatabase.execSQL(sql);
    }

    private void insertType(SQLiteDatabase db){
        String sqlInsert="insert into typetb (typename,imageID,focusImageID,kind) values(?,?,?,?)";
        db.execSQL(sqlInsert,new Object[]{"其他", R.mipmap.one_more,R.mipmap.more_fs,0});
        db.execSQL(sqlInsert,new Object[]{"英语", R.mipmap.english,R.mipmap.english_fs,0});
        db.execSQL(sqlInsert,new Object[]{"高等数学", R.mipmap.math,R.mipmap.math_fs,0});
        db.execSQL(sqlInsert,new Object[]{"离散数学", R.mipmap.descrete_math,R.mipmap.descrete_math_fs,0});
        db.execSQL(sqlInsert,new Object[]{"概率论", R.mipmap.probability,R.mipmap.probability_fs,0});
        db.execSQL(sqlInsert,new Object[]{"线性代数", R.mipmap.linear,R.mipmap.linear_fs,0});

        db.execSQL(sqlInsert,new Object[]{"其他", R.mipmap.one_more,R.mipmap.more_fs,1});
        db.execSQL(sqlInsert,new Object[]{"计算机组成", R.mipmap.computer,R.mipmap.computer_fs,1});
        db.execSQL(sqlInsert,new Object[]{"计算机网络", R.mipmap.net,R.mipmap.net_fs,1});
        db.execSQL(sqlInsert,new Object[]{"操作系统", R.mipmap.os,R.mipmap.os_fs,1});
        db.execSQL(sqlInsert,new Object[]{"数据结构与算法", R.mipmap.datastructure,R.mipmap.datastructur_fs,1});
        db.execSQL(sqlInsert,new Object[]{"微机与汇编", R.mipmap.cpu,R.mipmap.cpu_fs,1});
        db.execSQL(sqlInsert,new Object[]{"编程语言", R.mipmap.c_language,R.mipmap.java_fs,1});
        db.execSQL(sqlInsert,new Object[]{"应用开发与Linux", R.mipmap.develop,R.mipmap.develop_fs,1});
        db.execSQL(sqlInsert,new Object[]{"人工智能", R.mipmap.ai,R.mipmap.ai_fs,1});
        db.execSQL(sqlInsert,new Object[]{"计算机图形学", R.mipmap.cp,R.mipmap.cp_fs,1});
    }

    /*
    向user_table插入一些基础的数据
     */
    private void insertUserTable(SQLiteDatabase db){
        String sqlInsert = "insert into user_table (username, password, register_type) values(?,?,?)";

        // 插入一些示例用户数据
        db.execSQL(sqlInsert, new Object[]{"root", "123456", 1});
        db.execSQL(sqlInsert, new Object[]{"maochangjingyan", "securePass456", 1});
        db.execSQL(sqlInsert, new Object[]{"alice", "alice2023!", 1});
        db.execSQL(sqlInsert, new Object[]{"bob_smith", "bobTheBuilder", 0});
        db.execSQL(sqlInsert, new Object[]{"emma_w", "emmaW@123", 0});
        db.execSQL(sqlInsert, new Object[]{"admin", "adminPassword", 1});
        db.execSQL(sqlInsert, new Object[]{"test_user", "123456", 0});
        db.execSQL(sqlInsert, new Object[]{"developer", "devPass789", 0});
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
