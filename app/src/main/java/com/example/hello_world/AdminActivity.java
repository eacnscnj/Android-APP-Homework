package com.example.hello_world;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hello_world.Database.DBManager;
import com.example.hello_world.Database.UserInfo;
import com.example.hello_world.adapter.UserListAdapter; // 确保导入 UserListAdapter

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminActivity extends AppCompatActivity implements UserListAdapter.OnUserDeleteListener {

    private static final String TAG = "AdminActivity";
    private ListView userListView; // 用户列表
    private UserListAdapter userListAdapter; // 中间件, 显示用户列表
    private List<UserListAdapter.UserDisplayData> regularUsersDisplayData; // 存储用于显示的用户数据
    private ExecutorService executorService; // 用于后台加载数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        userListView = findViewById(R.id.admin_user_list_view);
        ImageView backButton = findViewById(R.id.admin_back_btn);

        regularUsersDisplayData = new ArrayList<>();
        userListAdapter = new UserListAdapter(this, regularUsersDisplayData);
        userListAdapter.setOnUserDeleteListener(this); // 设置删除监听器
        userListView.setAdapter(userListAdapter);

        executorService = Executors.newSingleThreadExecutor(); // 初始化线程池

        backButton.setOnClickListener(v -> {
            Log.d(TAG, "Admin back button clicked. Navigating to LoginActivity.");
            // 创建一个 Intent 跳转到 LoginActivity
            Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            // 销毁当前的 AdminActivity
            finish();
        });

        loadRegularUsers(); // 加载用户数据
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 当Activity从后台回到前台时，重新加载用户数据，确保列表是最新的
        loadRegularUsers();
    }

    // 后台线程处理数据读取, 前台线程处理UI
    private void loadRegularUsers() {
        Log.d(TAG, "Loading regular users...");
        executorService.execute(() -> {
            // 后台线程数据库操作
            List<UserInfo> users = DBManager.getAllRegularUsers();
            List<UserListAdapter.UserDisplayData> tempDisplayDataList = new ArrayList<>();

            for (UserInfo user : users) {
                float totalStudyTime = DBManager.getTotalStudyTime(user.get_id());
                tempDisplayDataList.add(new UserListAdapter.UserDisplayData(user, totalStudyTime));
            }

            // 前台线程操作UI
            runOnUiThread(() -> {
                userListAdapter.setUsersDisplayData(tempDisplayDataList);
                Log.d(TAG, "Loaded " + tempDisplayDataList.size() + " regular users.");
            });
        });
    }

    // 删除用户
    @Override
    public void onUserDeleted(int userId) {
        // 用户删除成功后，重新加载列表
        Toast.makeText(this, "用户及其记录已删除！", Toast.LENGTH_SHORT).show();
        loadRegularUsers(); // 重新加载数据以刷新列表
    }

    // 关闭后台线程
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow(); // 关闭线程池，防止内存泄漏
        }
    }
}
