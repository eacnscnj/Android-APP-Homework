package com.example.hello_world.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.hello_world.Database.CommentInfo;
import com.example.hello_world.Database.DBOpenHelper;
import com.example.hello_world.Database.LikeCommentHelper;
import com.example.hello_world.MainActivity;
import com.example.hello_world.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommunityFragment extends Fragment {

    private int currentUserId; // 当前登录用户ID，需要从外部传入或获取

    private LinearLayout containerLayout;  // 提升为类成员变量
    public CommunityFragment() {}

    public static CommunityFragment newInstance(int currentUserId) {
        CommunityFragment fragment = new CommunityFragment();
        Bundle args = new Bundle();
        args.putInt("CURRENT_USER_ID", currentUserId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentUserId = getArguments().getInt("CURRENT_USER_ID", -1);
        }

        // 注册监听器
        ((MainActivity) requireActivity()).setOnRecordChangedListener(() -> {
            // 回调触发时执行刷新操作
            refreshData();  // ← 你自己实现的数据刷新逻辑
        });
    }

    private void refreshData() {
        // TODO: 实现你的数据刷新逻辑，比如重新获取列表、通知 adapter 等
        loadShareCards();
        Log.d("CommunityFragment", "数据已刷新");
    }

    private void loadShareCards() {
        containerLayout.removeAllViews(); // ✅ 清空旧视图

        DBOpenHelper dbHelper = new DBOpenHelper(requireContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        LikeCommentHelper helper = new LikeCommentHelper(requireContext());

        Cursor cursor = db.rawQuery("SELECT * FROM share_record ORDER BY shareTime DESC", null);
        LayoutInflater inflater = LayoutInflater.from(requireContext());

        while (cursor.moveToNext()) {
            int shareId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            int shareUserId = cursor.getInt(cursor.getColumnIndexOrThrow("userId")); // 分享者ID
            int recordId = cursor.getInt(cursor.getColumnIndexOrThrow("recordId"));
            String shareNote = cursor.getString(cursor.getColumnIndexOrThrow("shareNote"));
            String shareTimeRaw = cursor.getString(cursor.getColumnIndexOrThrow("shareTime"));
            String shareTimeFormatted = formatTimestamp(shareTimeRaw);

            Cursor recordCursor = db.rawQuery("SELECT typename, studyTime FROM studyTimeTable WHERE id = ?",
                    new String[]{String.valueOf(recordId)});
            String typename = "";
            float studyTime = 0f;
            if (recordCursor.moveToFirst()) {
                typename = recordCursor.getString(recordCursor.getColumnIndexOrThrow("typename"));
                studyTime = recordCursor.getFloat(recordCursor.getColumnIndexOrThrow("studyTime"));
            }
            recordCursor.close();

            Cursor userCursor = db.rawQuery("SELECT nickname, avatar_path FROM user_table WHERE id = ?",
                    new String[]{String.valueOf(shareUserId)});
            String nickname = "用户" + shareUserId;
            String avatarPath = null;
            if (userCursor.moveToFirst()) {
                nickname = userCursor.getString(userCursor.getColumnIndexOrThrow("nickname"));
                avatarPath = userCursor.getString(userCursor.getColumnIndexOrThrow("avatar_path"));
            }
            userCursor.close();

            View card = inflater.inflate(R.layout.item_card_share, containerLayout, false);

            TextView tvUsername = card.findViewById(R.id.tv_username);
            TextView tvSubjectTime = card.findViewById(R.id.tv_subject_time);
            TextView tvContent = card.findViewById(R.id.tv_share_content);
            TextView tvLikeCount = card.findViewById(R.id.tv_like_count);
            TextView tvCommentCount = card.findViewById(R.id.tv_comment_count);
            LinearLayout commentSection = card.findViewById(R.id.comment_section);
            ImageButton btnLike = card.findViewById(R.id.btn_like);
            ImageButton btnComment = card.findViewById(R.id.btn_comment);
            ImageView ivAvatar = card.findViewById(R.id.iv_avatar);

            tvUsername.setText(nickname);
            tvSubjectTime.setText(typename + " | " + shareTimeFormatted + " | " + studyTime + " 小时");
            tvContent.setText(shareNote);
            tvLikeCount.setText(helper.getLikeCount(shareId) + " 赞");
            tvCommentCount.setText(helper.getCommentCount(shareId) + " 评论");

            if (!TextUtils.isEmpty(avatarPath)) {
                File avatarFile = new File(avatarPath);
                if (avatarFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(avatarPath);
                    ivAvatar.setImageBitmap(bitmap);
                }
            }

            btnLike.setOnClickListener(v -> {
                if (helper.isLiked(currentUserId, shareId)) {
                    helper.unlike(currentUserId, shareId);
                } else {
                    helper.like(currentUserId, shareId);
                }
                tvLikeCount.setText(helper.getLikeCount(shareId) + " 赞");
            });

            btnComment.setOnClickListener(v -> {
                if (commentSection.getVisibility() == View.VISIBLE) {
                    commentSection.setVisibility(View.GONE);
                } else {
                    commentSection.setVisibility(View.VISIBLE);
                    commentSection.removeAllViews();

                    List<CommentInfo> comments = helper.getComments(shareId);
                    for (CommentInfo comment : comments) {
                        // 获取评论者昵称
                        String commentUserNickname = helper.getUserNicknameById(comment.getUserId());
                        if (commentUserNickname == null || commentUserNickname.isEmpty()) {
                            commentUserNickname = "用户" + comment.getUserId();
                        }

                        // 格式化评论时间
                        String commentTimeFormatted = formatTimestamp(comment.getTime());

                        TextView commentView = new TextView(requireContext());
                        commentView.setText(commentUserNickname + " (" + commentTimeFormatted + "): " + comment.getContent());
                        commentView.setTextSize(14);
                        commentView.setPadding(8, 4, 8, 4);
                        commentSection.addView(commentView);
                    }

                    EditText etNewComment = new EditText(requireContext());
                    etNewComment.setHint("说点什么...");
                    etNewComment.setTextSize(14);
                    etNewComment.setPadding(8, 4, 8, 4);
                    commentSection.addView(etNewComment);

                    ImageButton btnSend = new ImageButton(requireContext());
                    btnSend.setImageResource(android.R.drawable.ic_menu_send);
                    btnSend.setBackgroundColor(0x00000000);
                    commentSection.addView(btnSend);

                    btnSend.setOnClickListener(sendView -> {
                        String content = etNewComment.getText().toString().trim();
                        if (!TextUtils.isEmpty(content)) {
                            String now = String.valueOf(System.currentTimeMillis());
                            helper.addComment(shareId, currentUserId, content, now);
                            etNewComment.setText("");
                            Toast.makeText(requireContext(), "评论成功", Toast.LENGTH_SHORT).show();
                            tvCommentCount.setText(helper.getCommentCount(shareId) + " 评论");

                            commentSection.removeAllViews();
                            btnComment.performClick(); // 关闭评论区
                            btnComment.performClick(); // 重新打开刷新评论
                        }
                    });
                }
            });

            containerLayout.addView(card);
        }

        cursor.close();
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);
        containerLayout = view.findViewById(R.id.community_container);  // 提升作用域

        loadShareCards();  // 初始化加载
        return view;
    }


    // 时间戳转中国常用日期格式字符串，例："2025-07-05 16:40"
    private String formatTimestamp(String timestampStr) {
        try {
            long timestamp = Long.parseLong(timestampStr);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
            return sdf.format(new Date(timestamp));
        } catch (Exception e) {
            return timestampStr; // 如果解析失败，原样返回
        }
    }
}
