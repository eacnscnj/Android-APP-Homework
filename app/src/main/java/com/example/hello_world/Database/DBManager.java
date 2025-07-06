package com.example.hello_world.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.hello_world.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.util.Map;
import java.util.LinkedHashMap; // 导入 LinkedHashMap

// 管理数据库的类
public class DBManager {
    private static SQLiteDatabase db;
    private static int currentUserId = -1; // **新增：存储当前登录用户的ID**

    public static void initDB(Context context) {
        DBOpenHelper helper = new DBOpenHelper(context);
        db = helper.getWritableDatabase();
        if (db != null) {
            Log.d("DBManager", "Database initialized successfully.");
        } else {
            Log.e("DBManager", "Database initialization failed!");
        }
    }

    // 存储当前用户id
    public static void setCurrentUserId(int userId) {
        currentUserId = userId;
        Log.d("DBManager", "Current user ID set to: " + currentUserId);
    }

    // 获取当前用户id
    public static int getCurrentUserId() {
        return currentUserId;
    }

    @SuppressLint("Range")
    public static List<TypeIn> getTypeList(int kind) {
        List<TypeIn> list = new ArrayList<>();
        String sql = "select * from typetb where kind = " + kind;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String typename = cursor.getString(cursor.getColumnIndexOrThrow("typename"));
                int imageID = cursor.getInt(cursor.getColumnIndexOrThrow("imageID"));
                int focusImageID = cursor.getInt(cursor.getColumnIndexOrThrow("focusImageID"));
                TypeIn typeIn = new TypeIn(id, typename, imageID, focusImageID, kind);
                list.add(typeIn);
            }
        } catch (Exception e) {
            Log.e("DBManager", "Error in getTypeList: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    // 记录表插入数据
    public static void insertItemToTable(AccountIn accountIn) {
        ContentValues values = new ContentValues();

        values.put("typename", accountIn.getTypename());
        values.put("focusImageID", accountIn.getFocusImageID());
        values.put("note", accountIn.getNote());
        values.put("studyTime", accountIn.getStudyTime());
        values.put("time", accountIn.getTime());
        values.put("year", accountIn.getYear());
        values.put("mounth", accountIn.getMounth());
        values.put("day", accountIn.getDay());
        values.put("kind", accountIn.getKind());
        values.put("userId", accountIn.getUserId()); // **新增：插入 userId**

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
        }

    }

    // 获取特定日期的专注记录
    @SuppressLint("Range")
    public static List<AccountIn> getAccountListFromAccounttb(int year, int mounth, int day, int userId) { // **修改：添加 userId 参数**
        List<AccountIn> list = new ArrayList<>();
        String sql = "select * from studyTimeTable where year=? and mounth=? and day=? AND userId = ? order by id desc"; //
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{String.valueOf(year), String.valueOf(mounth), String.valueOf(day), String.valueOf(userId)}); // **修改：传入 userId 参数**
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String typename = cursor.getString(cursor.getColumnIndexOrThrow("typename"));
                String note = cursor.getString(cursor.getColumnIndexOrThrow("note"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
                int focusImageID = cursor.getInt(cursor.getColumnIndexOrThrow("focusImageID"));
                int kind = cursor.getInt(cursor.getColumnIndexOrThrow("kind"));
                float studyTime = cursor.getFloat(cursor.getColumnIndexOrThrow("studyTime"));
                int recordUserId = cursor.getInt(cursor.getColumnIndexOrThrow("userId")); // 获取记录中的 userId
                AccountIn accountIn = new AccountIn(id, typename, focusImageID, note, studyTime, year, mounth, day, kind, recordUserId); // **修改：传入 userId**
                list.add(accountIn);
            }
        } catch (Exception e) {
            Log.e("DBManager", "Error in getAccountListFromAccounttb: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    // 获取所有时间段, 特定用户id的专注记录
    @SuppressLint("Range")
    public static List<AccountIn> getAllAccountList(int userId) {
        List<AccountIn> list = new ArrayList<>();
        String sql = "select * from studyTimeTable WHERE userId = ? order by year desc, mounth desc, day desc, id desc";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{String.valueOf(userId)});
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String typename = cursor.getString(cursor.getColumnIndexOrThrow("typename"));
                String note = cursor.getString(cursor.getColumnIndexOrThrow("note"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
                int focusImageID = cursor.getInt(cursor.getColumnIndexOrThrow("focusImageID"));
                int kind = cursor.getInt(cursor.getColumnIndexOrThrow("kind"));
                float studyTime = cursor.getFloat(cursor.getColumnIndexOrThrow("studyTime"));
                int year = cursor.getInt(cursor.getColumnIndexOrThrow("year"));
                int mounth = cursor.getInt(cursor.getColumnIndexOrThrow("mounth"));
                int day = cursor.getInt(cursor.getColumnIndexOrThrow("day"));
                int recordUserId = cursor.getInt(cursor.getColumnIndexOrThrow("userId")); // 获取记录中的 userId

                AccountIn accountIn = new AccountIn(id, typename, focusImageID, note, studyTime, year, mounth, day, kind, recordUserId); // **修改：传入 userId**
                list.add(accountIn);
            }
        } catch (Exception e) {
            Log.e("DBManager", "Error in getAllAccountList: " + e.getMessage(), e);
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
     * @param userId 删除操作的用户 ID，防止删除其他用户的记录
     * @return 返回受影响的行数，如果删除失败则返回0
     */
    public static int deleteItemFromStudyTimeTableById(int id, int userId) { // **修改：添加 userId 参数**
        int rowsAffected = 0;
        try {
            rowsAffected = db.delete("studyTimeTable", "id=? AND userId=?", new String[]{String.valueOf(id), String.valueOf(userId)});
            if (rowsAffected > 0) {
                Log.i("DBManager", "Deleted " + rowsAffected + " row from studyTimeTable with id: " + id + " for user: " + userId);
            } else {
                Log.w("DBManager", "No row deleted from studyTimeTable with id: " + id + " for user: " + userId + " (either not found or not owned).");
            }
        } catch (Exception e) {
            Log.e("DBManager", "Error deleting from studyTimeTable with id " + id + ": " + e.getMessage(), e);
        }
        return rowsAffected;
    }

    /**
     * 获取累计专注次数 (总条目数)
     * @param userId 用户的 ID
     * @return 总条目数
     */
    public static int getTotalFocusCount(int userId) { // **修改：添加 userId 参数**
        int count = 0;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(id) FROM studyTimeTable WHERE userId = ?", new String[]{String.valueOf(userId)}); //
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
     * @param userId 用户的 ID
     * @return 总时长 (分钟)
     */
    public static float getTotalStudyTime(int userId) { // **修改：添加 userId 参数**
        float totalTime = 0.0f;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT SUM(studyTime) FROM studyTimeTable WHERE userId = ?", new String[]{String.valueOf(userId)}); //
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
     * @param userId 用户的 ID
     * @return 以 Calendar 对象表示的最早记录日期，如果无记录则返回 null
     */
    @SuppressLint("Range")
    public static Calendar getFirstRecordDate(int userId) { // **修改：添加 userId 参数**
        Cursor cursor = null;
        Calendar calendar = null;
        try {
            cursor = db.rawQuery("SELECT MIN(year), MIN(mounth), MIN(day) FROM studyTimeTable WHERE userId = ?", new String[]{String.valueOf(userId)}); //
            if (cursor.moveToFirst()) {
                if (cursor.getInt(0) != 0 || cursor.getInt(1) != 0 || cursor.getInt(2) != 0) { // 检查是否有有效日期
                    int year = cursor.getInt(0);
                    int month = cursor.getInt(1);
                    int day = cursor.getInt(2);

                    calendar = Calendar.getInstance();
                    calendar.set(year, month - 1, day); // Calendar 月份从 0 开始
                }
            }
        } catch (Exception e) {
            Log.e("DBManager", "Error getting first record date for user " + userId + ": " + e.getMessage(), e);
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
     * @param userId 用户的 ID
     * @return 当天专注次数
     */
    public static int getDailyFocusCount(int year, int month, int day, int userId) { // **修改：添加 userId 参数**
        int count = 0;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(id) FROM studyTimeTable WHERE year=? AND mounth=? AND day=? AND userId=?",
                    new String[]{String.valueOf(year), String.valueOf(month), String.valueOf(day), String.valueOf(userId)}); //
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
     * @param userId 用户的 ID
     * @return 当天总时长 (分钟)
     */
    public static float getDailyStudyTime(int year, int month, int day, int userId) {
        float totalTime = 0.0f;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT SUM(studyTime) FROM studyTimeTable WHERE year=? AND mounth=? AND day=? AND userId=?",
                    new String[]{String.valueOf(year), String.valueOf(month), String.valueOf(day), String.valueOf(userId)}); //
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
     * @param userId 用户的 ID
     * @return Map<String, Float>，键为专注类型名称，值为该类型总时长
     */
    @SuppressLint("Range")
    public static Map<String, Float> getDailyStudyTimeDistribution(int year, int month, int day, int userId) {
        Map<String, Float> distribution = new LinkedHashMap<>();
        Cursor cursor = null;
        try {
            String sql = "SELECT typename, SUM(studyTime) FROM studyTimeTable WHERE year=? AND mounth=? AND day=? AND userId=? GROUP BY typename";
            cursor = db.rawQuery(sql, new String[]{String.valueOf(year), String.valueOf(month), String.valueOf(day), String.valueOf(userId)});

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

    /*
    user_table 增删改查
     */
    public static int Insert_to_User_table(String username, String password, int register_type) {
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        values.put("register_type", register_type);
        int insert = (int) db.insert("user_table", null, values);
        return insert;
    }

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

    public static UserInfo query_User_From_usertable(String username, String password) {
        Cursor cursor = null;

        if (!isUsernameExist(username)) {
            Log.d("Login", "用户名不存在: " + username);
            return null;
        }

        try {
            cursor = db.query(
                    "user_table",
                    new String[]{"id", "username", "password", "register_type", "nickname", "avatar_path"},
                    "username = ? AND password = ?",
                    new String[]{username, password},
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                UserInfo userInfo = new UserInfo(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("username")),
                        cursor.getString(cursor.getColumnIndexOrThrow("password")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("register_type")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nickname")),
                        cursor.getString(cursor.getColumnIndexOrThrow("avatar_path"))
                );
                // **在用户成功登录时，设置当前用户ID**
                setCurrentUserId(userInfo.get_id());
                return userInfo;
            } else {
                Log.d("Login", "密码错误，用户名: " + username);
                return new UserInfo(-1, username, "", -1, "", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    // 获取普通用户, 用于管理员显示
    @SuppressLint("Range")
    public static List<UserInfo> getAllRegularUsers() {
        List<UserInfo> userList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query(
                    "user_table",
                    new String[]{"id", "username", "password", "register_type", "nickname", "avatar_path"},
                    "register_type = ?",
                    new String[]{String.valueOf(0)}, // 0 代表普通用户
                    null, null, null
            );

            while (cursor != null && cursor.moveToNext()) {
                UserInfo userInfo = new UserInfo(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("username")),
                        cursor.getString(cursor.getColumnIndexOrThrow("password")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("register_type")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nickname")),
                        cursor.getString(cursor.getColumnIndexOrThrow("avatar_path"))
                );
                userList.add(userInfo);
            }
        } catch (Exception e) {
            Log.e("DBManager", "Error getting all regular users: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return userList;
    }

    // 用户删除账户时也删除所有专注
    public static boolean deleteUserAndTheirRecords(int userIdToDelete) {
        try {
            db.beginTransaction(); // 开始事务

            // 1. 删除该用户在 studyTimeTable 中的所有记录
            int recordsDeleted = db.delete("studyTimeTable", "userId=?", new String[]{String.valueOf(userIdToDelete)});
            Log.d("DBManager", "Deleted " + recordsDeleted + " study records for user ID: " + userIdToDelete);

            // 2. 删除 user_table 中的用户记录
            int userDeleted = db.delete("user_table", "id=?", new String[]{String.valueOf(userIdToDelete)});
            Log.d("DBManager", "Deleted " + userDeleted + " user record for user ID: " + userIdToDelete);

            if (userDeleted > 0) {
                db.setTransactionSuccessful(); // 事务成功
                return true;
            } else {
                Log.w("DBManager", "No user record found for deletion with ID: " + userIdToDelete);
                return false;
            }
        } catch (Exception e) {
            Log.e("DBManager", "Error deleting user and records for ID " + userIdToDelete + ": " + e.getMessage(), e);
            return false;
        } finally {
            db.endTransaction(); // 结束事务
        }
    }

    //昵称修改
    // 更新用户昵称
    public static void updateUserNickname(int userId, String newNickname) {
        ContentValues values = new ContentValues();
        values.put("nickname", newNickname);
        db.update("user_table", values, "id = ?", new String[]{String.valueOf(userId)});
    }


    //获取用户信息
    public static UserInfo getUserInfoById(int userId) {
        Cursor cursor = null;
        try {
            cursor = db.query(
                    "user_table",
                    new String[]{"id", "username", "password", "register_type", "nickname", "avatar_path"},
                    "id = ?",
                    new String[]{String.valueOf(userId)},
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                return new UserInfo(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("username")),
                        cursor.getString(cursor.getColumnIndexOrThrow("password")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("register_type")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nickname")),
                        cursor.getString(cursor.getColumnIndexOrThrow("avatar_path"))
                );
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    /**
     * 插入新的学科类型到 typetb 表。
     * 默认图标使用 R.mipmap.one_more 和 R.mipmap.more_fs。
     *
     * @param typename 新学科的名称。
     * @param kind     学科的种类 (0 for 公共课, 1 for 专业课)。
     * @return 插入成功返回 true，否则返回 false。
     */
    public static boolean insertNewType(String typename, int kind) {
        // 首先检查该 kind 下是否已经存在同名类型
        if (isTypeExists(typename, kind)) {
            Log.d("DBManager", "Type '" + typename + "' already exists for kind " + kind + ". Aborting insertion.");
            return false;
        }

        ContentValues values = new ContentValues();
        values.put("typename", typename);
        values.put("imageID", R.mipmap.cpu); // 使用默认图标
        values.put("focusImageID", R.mipmap.cpu_fs); // 使用默认选中图标
        values.put("kind", kind);

        long result = -1;
        try {
            result = db.insert("typetb", null, values);
            if (result == -1) {
                Log.e("DBManager", "Failed to insert new type '" + typename + "' for kind " + kind);
            } else {
                Log.i("DBManager", "New type '" + typename + "' inserted successfully for kind " + kind + ", row ID: " + result);
            }
        } catch (Exception e) {
            Log.e("DBManager", "Error inserting new type: " + e.getMessage(), e);
        }
        return result != -1;
    }

    @SuppressLint("Range")
    private static boolean isTypeExists(String typename, int kind) {
        Cursor cursor = null;
        try {
            cursor = db.query(
                    "typetb",
                    new String[]{"id"},
                    "typename = ? AND kind = ?",
                    new String[]{typename, String.valueOf(kind)},
                    null, null, null
            );
            return cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}