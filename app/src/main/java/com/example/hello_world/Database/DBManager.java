package com.example.hello_world.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

//管理数据库的类
public class DBManager {
    private static SQLiteDatabase db;

    public static void initDB(Context context){
        DBOpenHelper helper = new DBOpenHelper(context);
        db = helper.getWritableDatabase();
    }

    public static List<TypeIn> getTypeList(int kind){
        List<TypeIn>list = new ArrayList<>();

        String sql="select * from typetb where kind = " + kind;
        Cursor cursor = db.rawQuery(sql,null);

        while(cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String typename = cursor.getString(cursor.getColumnIndexOrThrow("typename"));
            int imageID = cursor.getInt(cursor.getColumnIndexOrThrow("imageID"));
            int focusImageID = cursor.getInt(cursor.getColumnIndexOrThrow("focusImageID"));
            TypeIn typeIn = new TypeIn(id,typename,imageID,focusImageID,kind);
            list.add(typeIn);
        }
        return list;
    }

    //记录表插入数据
    /*String typename;
    int focusImageID;
    String note;
    float studyTime;
    String time;
    int year;
    int mounth;
    int day;
    int kind;*/
    public static void insertItemToTable(AccountIn accountIn){
        ContentValues values = new ContentValues();
        values.put("typename",accountIn.getTypename());
        values.put("focuseImageID",accountIn.getFocusImageID());
        values.put("note",accountIn.getNote());
        values.put("studyTime",accountIn.getStudyTime());
        values.put("time",accountIn.getTime());
        values.put("year",accountIn.getYear());
        values.put("mounth",accountIn.getMounth());
        values.put("day",accountIn.getDay());
        values.put("kind",accountIn.getKind());
        db.insert("studyTimeTable",null,values);
        Log.i("animee","insert is ok");
    }

    public static List<AccountIn>getAccountListFromAccounttb(int year,int mounth,int day){
        List<AccountIn>list = new ArrayList<>();
        String sql = "select * from studyTimeTable where year=? and mounth=? and day=? order by id desc";
        Cursor cursor = db.rawQuery(sql,new String[]{year + "",mounth + "", day + ""});
        while(cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String typename = cursor.getString(cursor.getColumnIndexOrThrow("typename"));
            String note = cursor.getString(cursor.getColumnIndexOrThrow("note"));
            String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
            int focusImageID = cursor.getInt(cursor.getColumnIndexOrThrow("focusImageID"));
            int kind = cursor.getInt(cursor.getColumnIndexOrThrow("kind"));
            float studyTime = cursor.getFloat(cursor.getColumnIndexOrThrow("studyTime"));
            // AccountIn(int id, String typename, int focusImageID, String note, float studyTime, int year, int mounth, int day, int kind)
            AccountIn accountIn = new AccountIn(id, typename, focusImageID, note,studyTime, year, mounth, day, kind);
            list.add(accountIn);
        }
        return list;
    }


    /*
    user_table 增删改查
     */

    /*
    插入数据
     */
    public static int Insert_to_User_table(String username, String password, int register_type) {
        ContentValues values = new ContentValues();
        //填充占位符
        values.put("username", username);
        values.put("password", password);
        values.put("register_type", register_type);
        String nullColumnHack = "values(null,?,?,?)";
        //执行
        int insert = (int) db.insert("user_table", nullColumnHack, values);
        return insert;
    }

    /*
    查找数据
     */
    public static UserInfo query_User_From_usertable(String username, String password) {
        Cursor cursor = null;
        try {
            // 查询所有需要的字段
            cursor = db.query(
                    "user_table",
                    new String[]{"id", "username","password", "register_type"}, // 查询的列
                    "username = ? AND password = ?",                // WHERE条件
                    new String[]{username, password},               // 条件参数
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                // 从Cursor中提取数据并构建UserInfo对象
                /*
                待修改 ， 可能需要对用户名和密码作一定的约束
                 */
                return new UserInfo(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("username")),
                        cursor.getString(cursor.getColumnIndexOrThrow("password")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("register_type"))
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }


}
