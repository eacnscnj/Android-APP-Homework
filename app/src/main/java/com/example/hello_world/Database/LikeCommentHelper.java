package com.example.hello_world.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.hello_world.Database.CommentInfo;

import java.util.ArrayList;
import java.util.List;

public class LikeCommentHelper {
    private SQLiteDatabase db;

    public LikeCommentHelper(Context context) {
        DBOpenHelper dbOpenHelper = new DBOpenHelper(context);
        db = dbOpenHelper.getWritableDatabase();
    }

    // ---------- 点赞功能（基于 shareId） ----------

    public boolean isLiked(int userId, int shareId) {
        Cursor cursor = db.rawQuery("SELECT * FROM like_info WHERE userId = ? AND shareId = ?",
                new String[]{String.valueOf(userId), String.valueOf(shareId)});
        boolean liked = cursor.getCount() > 0;
        cursor.close();
        return liked;
    }

    public void like(int userId, int shareId) {
        ContentValues values = new ContentValues();
        values.put("userId", userId);
        values.put("shareId", shareId);
        db.insert("like_info", null, values);
    }

    public void unlike(int userId, int shareId) {
        db.delete("like_info", "userId=? AND shareId=?",
                new String[]{String.valueOf(userId), String.valueOf(shareId)});
    }

    public int getLikeCount(int shareId) {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM like_info WHERE shareId=?",
                new String[]{String.valueOf(shareId)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // ---------- 评论功能（基于 shareId） ----------

    public int getCommentCount(int shareId) {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM comment_info WHERE shareId=?",
                new String[]{String.valueOf(shareId)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public List<CommentInfo> getComments(int shareId) {
        List<CommentInfo> commentList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM comment_info WHERE shareId=? ORDER BY time DESC",
                new String[]{String.valueOf(shareId)});
        while (cursor.moveToNext()) {
            try {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("commentId"));
                int uid = cursor.getInt(cursor.getColumnIndexOrThrow("userId"));
                String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
                commentList.add(new CommentInfo(id, shareId, uid, content, time));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return commentList;
    }

    public void addComment(int shareId, int userId, String content, String time) {
        ContentValues values = new ContentValues();
        values.put("shareId", shareId);
        values.put("userId", userId);
        values.put("content", content);
        values.put("time", time);
        db.insert("comment_info", null, values);
    }

    public String getUserNicknameById(int userId) {
        // 你可以用DB查询或者缓存查用户昵称
        // 这里给个示例写法，需根据你代码调整
        Cursor cursor = db.query("user_table", new String[]{"nickname"}, "id=?", new String[]{String.valueOf(userId)}, null, null, null);
        String nickname = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                nickname = cursor.getString(cursor.getColumnIndexOrThrow("nickname"));
            }
            cursor.close();
        }
        return nickname;
    }

}

