package com.example.hello_world;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log; // Make sure Log is imported
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hello_world.Database.DBManager;
import com.example.hello_world.Database.UserInfo;

public class LoginActivity extends AppCompatActivity {
    private EditText et_username;
    private EditText et_password;

    private static final String TAG = "LoginActivity"; // For logging

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Initialize controls
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);

        // Click to register
        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Jump to the registration page
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // Login
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = et_username.getText().toString().trim(); // Trim whitespace
                String password = et_password.getText().toString().trim(); // Trim whitespace

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "请输入用户名或密码", Toast.LENGTH_SHORT).show();
                } else {
                    // Check if the user is valid
                    UserInfo userInfo = DBManager.query_User_From_usertable(username, password);

                    if (userInfo == null) {
                        // This case handles when query_User_From_usertable returns null
                        // which means username does not exist
                        Toast.makeText(LoginActivity.this, "用户名不存在", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Login failed: Username '" + username + "' does not exist.");
                    } else if (userInfo.get_id() == -1) {
                        // This case handles incorrect password for an existing username
                        Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Login failed for '" + username + "': Incorrect password.");
                    } else {
                        // Login successful
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Login successful for user: " + userInfo.getUsername() + " (ID: " + userInfo.get_id() + ")");

                        // **Set the current user ID in DBManager**
                        DBManager.setCurrentUserId(userInfo.get_id());

                        // Navigate to MainActivity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        // No need to explicitly putExtra("USER_ID") here if DBManager handles it globally
                        startActivity(intent);
                        finish(); // Finish LoginActivity so user can't go back by pressing back button
                    }
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}