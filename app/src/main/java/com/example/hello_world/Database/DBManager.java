package com.example.hello_world.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

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
//<<<<<<< Login_222
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
//=======
        values.put("typename", accountIn.getTypename());
        values.put("focusImageID", accountIn.getFocusImageID());
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
//>>>>>>> main
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

//<<<<<<< Login_222

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
    查找用户名是否存在，如果存在再去判断密码
     */
    private static boolean isUsernameExist(String username) {
        Cursor cursor = null;
        try {
            cursor = db.query(
                    "user_table",
                    new String[]{"id"},
                    "username = ?",
                    new String[]{username},
                    null, null, null
            );
            return cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    /*
    查找数据
     */
    public static UserInfo query_User_From_usertable(String username, String password) {
        Cursor cursor = null;

        if (!isUsernameExist(username)) {
            Log.d("Login", "用户名不存在: " + username);
            return null;
        }

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
                //成功匹配用户命与密码
                return new UserInfo(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("username")),
                        cursor.getString(cursor.getColumnIndexOrThrow("password")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("register_type"))
                );
            }else{ //密码错误
                Log.d("Login", "密码错误，用户名: " + username);
                return new UserInfo(-1, username,"", -1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }


}
//=======
    /**
     * 根据传入的 id 删除 studyTimeTable 表中的一条记录
     * @param id 要删除记录的 id
     * @return 返回受影响的行数，如果删除失败则返回0
     */
    public static int deleteItemFromStudyTimeTableById(int id) {
        int rowsAffected = 0;
        try {
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

    /**
     * 获取累计专注次数 (总条目数)
     * @return 总条目数
     */
    public static int getTotalFocusCount() {
        int count = 0;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(id) FROM studyTimeTable", null);
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e("DBManager", "Error getting total focus count: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }

    /**
     * 获取累计总时长
     * @return 总时长 (分钟)
     */
    public static float getTotalStudyTime() {
        float totalTime = 0.0f;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT SUM(studyTime) FROM studyTimeTable", null);
            if (cursor.moveToFirst()) {
                totalTime = cursor.getFloat(0);
            }
        } catch (Exception e) {
            Log.e("DBManager", "Error getting total study time: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return totalTime;
    }

    /**
     * 获取最久一次学习记录的日期 (最早的记录日期)
     * @return 以 Calendar 对象表示的最早记录日期，如果无记录则返回 null
     */
    @SuppressLint("Range")
    public static Calendar getFirstRecordDate() {
        Cursor cursor = null;
        Calendar calendar = null;
        try {
            // 查询最早的 year, mounth, day
            cursor = db.rawQuery("SELECT MIN(year), MIN(mounth), MIN(day) FROM studyTimeTable", null);
            if (cursor.moveToFirst()) {
                if (cursor.getInt(0) != 0) { // 假设 year 不会是 0
                    int year = cursor.getInt(0);
                    int month = cursor.getInt(1);
                    int day = cursor.getInt(2);

                    calendar = Calendar.getInstance();
                    calendar.set(year, month - 1, day); // Calendar 月份从 0 开始
                }
            }
        } catch (Exception e) {
            Log.e("DBManager", "Error getting first record date: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return calendar;
    }

    /**
     * 获取指定日期的专注次数
     * @param year 年
     * @param month 月 (1-12)
     * @param day 日
     * @return 当天专注次数
     */
    public static int getDailyFocusCount(int year, int month, int day) {
        int count = 0;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(id) FROM studyTimeTable WHERE year=? AND mounth=? AND day=?",
                    new String[]{String.valueOf(year), String.valueOf(month), String.valueOf(day)});
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e("DBManager", "Error getting daily focus count: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }

    /**
     * 获取指定日期的总时长
     * @param year 年
     * @param month 月 (1-12)
     * @param day 日
     * @return 当天总时长 (分钟)
     */
    public static float getDailyStudyTime(int year, int month, int day) {
        float totalTime = 0.0f;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT SUM(studyTime) FROM studyTimeTable WHERE year=? AND mounth=? AND day=?",
                    new String[]{String.valueOf(year), String.valueOf(month), String.valueOf(day)});
            if (cursor.moveToFirst()) {
                totalTime = cursor.getFloat(0);
            }
        } catch (Exception e) {
            Log.e("DBManager", "Error getting daily study time: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return totalTime;
    }

    /**
     * 获取指定日期的专注时间分布数据，按专注类型分组
     * @param year 年
     * @param month 月 (1-12)
     * @param day 日
     * @return Map<String, Float>，键为专注类型名称，值为该类型总时长
     */
    @SuppressLint("Range")
    public static java.util.Map<String, Float> getDailyStudyTimeDistribution(int year, int month, int day) {
        java.util.Map<String, Float> distribution = new java.util.LinkedHashMap<>(); // LinkedHashMap 保持插入顺序
        Cursor cursor = null;
        try {
            String sql = "SELECT typename, SUM(studyTime) FROM studyTimeTable WHERE year=? AND mounth=? AND day=? GROUP BY typename";
            cursor = db.rawQuery(sql, new String[]{String.valueOf(year), String.valueOf(month), String.valueOf(day)});

            while (cursor.moveToNext()) {
                String typename = cursor.getString(cursor.getColumnIndexOrThrow("typename"));
                float totalTime = cursor.getFloat(cursor.getColumnIndexOrThrow("SUM(studyTime)"));
                distribution.put(typename, totalTime);
            }
        } catch (Exception e) {
            Log.e("DBManager", "Error getting daily study time distribution: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return distribution;
    }
}
//>>>>>>> main
