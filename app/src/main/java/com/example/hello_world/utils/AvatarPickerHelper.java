package com.example.hello_world.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.example.hello_world.Database.DBManager; // 导入 DBManager

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class AvatarPickerHelper {
    private final Context context;
    private final ImageView avatarView;
    private final int userId; // 新增：保存用户ID
    private final ActivityResultLauncher<String> legacyPicker;
    private final ActivityResultLauncher<PickVisualMediaRequest> modernPicker;

    public AvatarPickerHelper(ActivityResultCaller caller, Context context, ImageView avatarView, int userId) {
        this.context = context;
        this.avatarView = avatarView;
        this.userId = userId; // 初始化用户ID

        // Android 13+：使用系统相册选择器
        modernPicker = caller.registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(),
                uri -> {
                    if (uri != null) saveToAppStorage(uri);
                });

        // Android ≤ 12：传统方式
        legacyPicker = caller.registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) saveToAppStorage(uri);
                });
    }

    public void launch() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            modernPicker.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        } else {
            // Android 12 及以下需要权限
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                legacyPicker.launch("image/*");
            } else {
                Toast.makeText(context, "请开启读取权限", Toast.LENGTH_SHORT).show();
                // 💡 注意：此处您可能还需要添加请求权限的逻辑，例如使用 requestPermissions()
            }
        }
    }

    private void saveToAppStorage(Uri uri) {
        try {
            ContentResolver resolver = context.getContentResolver();
            InputStream in = resolver.openInputStream(uri);
            if (in == null) return;

            // ✅ 生成用户独有的头像文件名
            String fileName = "avatar_" + userId + ".jpg"; // 例如：avatar_123.jpg
            File avatarFile = new File(context.getFilesDir(), fileName);
            FileOutputStream out = new FileOutputStream(avatarFile);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            in.close();
            out.close();

            // 显示头像
            avatarView.setImageBitmap(BitmapFactory.decodeFile(avatarFile.getAbsolutePath()));

            // ✅ 更新数据库中的头像路径
            boolean success = DBManager.updateUserAvatarPath(userId, avatarFile.getAbsolutePath());
            if (success) {
                Toast.makeText(context, "头像设置成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "头像设置成功但数据库更新失败", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e("AvatarPicker", "Error saving image: " + e.getMessage());
            Toast.makeText(context, "头像设置失败", Toast.LENGTH_SHORT).show();
        }
    }
}