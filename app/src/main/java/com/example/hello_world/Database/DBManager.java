package com.example.hello_world.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

// 管理数据库的类
public class DBManager {
    private static SQLiteDatabase db;

    public static void initDB(Context context){
        DBOpenHelper helper = new DBOpenHelper(context);
        db = helper.getWritableDatabase();
        if (db != null) {
            Log.d("DBManager", "Database initialized successfully.");
        } else {
            Log.e("DBManager", "Database initialization failed!");
        }
    }

    @SuppressLint("Range")
    public static List<TypeIn> getTypeList(int kind){
        List<TypeIn> list = new ArrayList<>();

        String sql = "select * from typetb where kind = " + kind;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);

            while(cursor.moveToNext()){
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String typename = cursor.getString(cursor.getColumnIndexOrThrow("typename"));
                int imageID = cursor.getInt(cursor.getColumnIndexOrThrow("imageID"));
                // !!! 修正这里: focuseImageID -> focusImageID
                int focusImageID = cursor.getInt(cursor.getColumnIndexOrThrow("focusImageID")); // **已修正为 focusImageID**
                TypeIn typeIn = new TypeIn(id, typename, imageID, focusImageID, kind);
                list.add(typeIn);
            }
        } catch (Exception e) {
            Log.e("DBManager", "Error in getTypeList: " + e.getMessage(), e);
            if (cursor != null) {
                String[] columnNames = cursor.getColumnNames();
                StringBuilder sb = new StringBuilder("Available columns in typetb: [");
                for (String col : columnNames) {
                    sb.append(col).append(", ");
                }
                if (columnNames.length > 0) sb.setLength(sb.length() - 2);
                sb.append("]");
                Log.e("DBManager", sb.toString());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    // 记录表插入数据
    public static void insertItemToTable(AccountIn accountIn){
        ContentValues values = new ContentValues();
        values.put("typename", accountIn.getTypename());
        // !!! 修正这里: focuseImageID -> focusImageID
        values.put("focusImageID", accountIn.getFocusImageID()); // **已修正为 focusImageID**
        values.put("note", accountIn.getNote());
        values.put("studyTime", accountIn.getStudyTime());
        values.put("time", accountIn.getTime());
        values.put("year", accountIn.getYear());
        values.put("mounth", accountIn.getMounth());
        values.put("day", accountIn.getDay());
        values.put("kind", accountIn.getKind());
        long result = -1;
        try {
            result = db.insert("studyTimeTable", null, values);
            if (result == -1) {
                Log.e("DBManager", "Failed to insert into studyTimeTable! Values: " + values.toString());
            } else {
                Log.i("DBManager", "Inserted successfully into studyTimeTable, row ID: " + result);
            }
        } catch (Exception e) {
            Log.e("DBManager", "Error inserting into studyTimeTable: " + e.getMessage(), e);
            Cursor tableCursor = null;
            try {
                tableCursor = db.rawQuery("PRAGMA table_info(studyTimeTable);", null);
                StringBuilder sb = new StringBuilder("Available columns in studyTimeTable: [");
                while (tableCursor.moveToNext()) {
                    sb.append(tableCursor.getString(tableCursor.getColumnIndexOrThrow("name"))).append(", ");
                }
                if (tableCursor.getCount() > 0) sb.setLength(sb.length() - 2);
                sb.append("]");
                Log.e("DBManager", sb.toString());
            } catch (Exception e2) {
                Log.e("DBManager", "Could not get table info for studyTimeTable: " + e2.getMessage());
            } finally {
                if (tableCursor != null) tableCursor.close();
            }
        }
    }

    @SuppressLint("Range")
    public static List<AccountIn> getAccountListFromAccounttb(int year, int mounth, int day){
        List<AccountIn> list = new ArrayList<>();
        String sql = "select * from studyTimeTable where year=? and mounth=? and day=? order by id desc";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{String.valueOf(year), String.valueOf(mounth), String.valueOf(day)});
            while(cursor.moveToNext()){
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String typename = cursor.getString(cursor.getColumnIndexOrThrow("typename"));
                String note = cursor.getString(cursor.getColumnIndexOrThrow("note"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
                // !!! 修正这里: focuseImageID -> focusImageID
                int focusImageID = cursor.getInt(cursor.getColumnIndexOrThrow("focusImageID")); // **已修正为 focusImageID**
                int kind = cursor.getInt(cursor.getColumnIndexOrThrow("kind"));
                float studyTime = cursor.getFloat(cursor.getColumnIndexOrThrow("studyTime"));
                AccountIn accountIn = new AccountIn(id, typename, focusImageID, note, studyTime, year, mounth, day, kind);
                list.add(accountIn);
            }
        } catch (Exception e) {
            Log.e("DBManager", "Error in getAccountListFromAccounttb: " + e.getMessage(), e);
            if (cursor != null) {
                String[] columnNames = cursor.getColumnNames();
                StringBuilder sb = new StringBuilder("Available columns in studyTimeTable: [");
                for (String col : columnNames) {
                    sb.append(col).append(", ");
                }
                if (columnNames.length > 0) sb.setLength(sb.length() - 2);
                sb.append("]");
                Log.e("DBManager", sb.toString());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    @SuppressLint("Range")
    public static List<AccountIn> getAllAccountList() {
        List<AccountIn> list = new ArrayList<>();
        String sql = "select * from studyTimeTable order by year desc, mounth desc, day desc, id desc";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String typename = cursor.getString(cursor.getColumnIndexOrThrow("typename"));
                String note = cursor.getString(cursor.getColumnIndexOrThrow("note"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
                // !!! 修正这里: focuseImageID -> focusImageID
                int focusImageID = cursor.getInt(cursor.getColumnIndexOrThrow("focusImageID")); // **已修正为 focusImageID**
                int kind = cursor.getInt(cursor.getColumnIndexOrThrow("kind"));
                float studyTime = cursor.getFloat(cursor.getColumnIndexOrThrow("studyTime"));
                int year = cursor.getInt(cursor.getColumnIndexOrThrow("year"));
                int mounth = cursor.getInt(cursor.getColumnIndexOrThrow("mounth"));
                int day = cursor.getInt(cursor.getColumnIndexOrThrow("day"));

                AccountIn accountIn = new AccountIn(id, typename, focusImageID, note, studyTime, year, mounth, day, kind);
                list.add(accountIn);
            }
        } catch (Exception e) {
            Log.e("DBManager", "Error in getAllAccountList: " + e.getMessage(), e);
            if (cursor != null) {
                String[] columnNames = cursor.getColumnNames();
                StringBuilder sb = new StringBuilder("Available columns in studyTimeTable: [");
                for (String col : columnNames) {
                    sb.append(col).append(", ");
                }
                if (columnNames.length > 0) sb.setLength(sb.length() - 2);
                sb.append("]");
                Log.e("DBManager", sb.toString());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * 根据传入的 id 删除 studyTimeTable 表中的一条记录
     * @param id 要删除记录的 id
     * @return 返回受影响的行数，如果删除失败则返回0
     */
    public static int deleteItemFromStudyTimeTableById(int id) {
        int rowsAffected = 0;
        try {
            // "studyTimeTable" 是表名
            // "id=?" 是 where 子句，表示 id 等于传入的 id
            // new String[]{String.valueOf(id)} 是 whereArgs，提供 where 子句中的参数
            rowsAffected = db.delete("studyTimeTable", "id=?", new String[]{String.valueOf(id)});
            if (rowsAffected > 0) {
                Log.i("DBManager", "Deleted " + rowsAffected + " row from studyTimeTable with id: " + id);
            } else {
                Log.w("DBManager", "No row deleted from studyTimeTable with id: " + id);
            }
        } catch (Exception e) {
            Log.e("DBManager", "Error deleting from studyTimeTable with id " + id + ": " + e.getMessage(), e);
        }
        return rowsAffected;
    }
}