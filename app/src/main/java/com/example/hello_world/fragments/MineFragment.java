package com.example.hello_world.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hello_world.Database.DBManager;
import com.example.hello_world.Database.UserInfo;
import com.example.hello_world.HelpActivity;
import com.example.hello_world.LoginActivity;
import com.example.hello_world.MainActivity;
import com.example.hello_world.R;
import com.example.hello_world.utils.AvatarPickerHelper;

import java.io.File;

public class MineFragment extends Fragment {

    public MineFragment() {}

    public static MineFragment newInstance(int currentUserId) {
        MineFragment fragment = new MineFragment();
        Bundle args = new Bundle();
        args.putInt("CURRENT_USER_ID", currentUserId); // 将用户ID放入Bundle
        fragment.setArguments(args); // 设置Bundle
        return fragment;
    }

    private TextView tvUsername;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);

        tvUsername = view.findViewById(R.id.tv_username);

        // 设置昵称
        int userId = DBManager.getCurrentUserId();
        UserInfo user = DBManager.getUserInfoById(userId);
        String nickname = user.getNickname();
        tvUsername.setText(nickname != null && !nickname.isEmpty() ? nickname : user.getUsername());

        // 点击修改昵称
        tvUsername.setOnClickListener(v -> showNicknameDialog(userId));

        return view;
    }

    //修改昵称
    private void showNicknameDialog(int userId) {
        Context context = getContext();
        if (context == null) return;

        EditText editText = new EditText(context);
        editText.setHint("请输入新的昵称");

        new AlertDialog.Builder(context)
                .setTitle("修改昵称")
                .setView(editText)
                .setPositiveButton("确认", (dialog, which) -> {
                    String newNickname = editText.getText().toString().trim();
                    if (!newNickname.isEmpty()) {
                        DBManager.updateUserNickname(userId, newNickname);
                        tvUsername.setText(newNickname);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private AvatarPickerHelper avatarPickerHelper;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ✅ 正确做法：获取 CardView 并为其设置点击事件

        // 使用帮助
        View helpCard = view.findViewById(R.id.card_help); // ID 是 card_help
        if (helpCard != null) {
            helpCard.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), HelpActivity.class);
                startActivity(intent);
            });
        }

        // 我的目标
//        View goalsCard = view.findViewById(R.id.card_goals); // ID 是 card_goals
//        if (goalsCard != null) {
//            goalsCard.setOnClickListener(v -> {
//                Intent intent = new Intent(getActivity(), HelpActivity.class);
//                startActivity(intent);
//            });
//        }

        // 我的记录
        View recordsCard = view.findViewById(R.id.card_records); // ID 是 card_records
        if (recordsCard != null) {
            recordsCard.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            });
        }

        // 退出登录按钮（额外添加）
        Button logoutButton = view.findViewById(R.id.btn_logout);
        if (logoutButton != null) {
            logoutButton.setOnClickListener(v -> {
                // 在这里处理退出登录的逻辑
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            });
        }

        //头像功能
        ImageView ivAvatar = view.findViewById(R.id.iv_avatar);

        avatarPickerHelper = new AvatarPickerHelper(this, requireContext(), ivAvatar);

            ivAvatar.setOnClickListener(v -> {
            avatarPickerHelper.launch();
        });

        // 加载已有头像（如果存在）
        File avatarFile = new File(requireContext().getFilesDir(), "avatar.jpg");
            if (avatarFile.exists()) {
            ivAvatar.setImageBitmap(BitmapFactory.decodeFile(avatarFile.getAbsolutePath()));
        }
    }
}
