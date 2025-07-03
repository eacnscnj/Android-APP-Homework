package com.example.hello_world;

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
    private ListView userListView;
    private UserListAdapter userListAdapter;
    // **修改这里：将 List<UserInfo> 改为 List<UserListAdapter.UserDisplayData>**
    private List<UserListAdapter.UserDisplayData> regularUsersDisplayData; // 存储用于显示的用户数据
    private ExecutorService executorService; // 用于后台加载数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        userListView = findViewById(R.id.admin_user_list_view);
        ImageView backButton = findViewById(R.id.admin_back_btn);

        // **初始化为 UserListAdapter.UserDisplayData 类型**
        regularUsersDisplayData = new ArrayList<>();
        userListAdapter = new UserListAdapter(this, regularUsersDisplayData);
        userListAdapter.setOnUserDeleteListener(this); // 设置删除监听器
        userListView.setAdapter(userListAdapter);

        executorService = Executors.newSingleThreadExecutor(); // 初始化线程池

        backButton.setOnClickListener(v -> {
            finish(); // 返回上一个Activity (LoginActivity)
        });

        loadRegularUsers(); // 加载用户数据
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 当Activity从后台回到前台时，重新加载用户数据，确保列表是最新的
        loadRegularUsers();
    }

    private void loadRegularUsers() {
        Log.d(TAG, "Loading regular users...");
        executorService.execute(() -> {
            // 在后台线程执行数据库操作
            List<UserInfo> users = DBManager.getAllRegularUsers();
            List<UserListAdapter.UserDisplayData> tempDisplayDataList = new ArrayList<>(); // 临时列表

            for (UserInfo user : users) {
                float totalStudyTime = DBManager.getTotalStudyTime(user.get_id());
                // **在这里创建 UserListAdapter.UserDisplayData 对象并添加到临时列表**
                tempDisplayDataList.add(new UserListAdapter.UserDisplayData(user, totalStudyTime));
            }

            // 在主线程更新UI
            runOnUiThread(() -> {
                // **直接将构建好的 tempDisplayDataList 传递给适配器**
                userListAdapter.setUsersDisplayData(tempDisplayDataList);
                Log.d(TAG, "Loaded " + tempDisplayDataList.size() + " regular users.");
            });
        });
    }

    @Override
    public void onUserDeleted(int userId) {
        // 用户删除成功后，重新加载列表
        Toast.makeText(this, "用户及其记录已删除！", Toast.LENGTH_SHORT).show();
        loadRegularUsers(); // 重新加载数据以刷新列表
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow(); // 关闭线程池，防止内存泄漏
        }
    }
}
