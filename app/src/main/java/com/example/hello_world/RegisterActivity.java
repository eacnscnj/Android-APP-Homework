package com.example.hello_world;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

public class RegisterActivity extends AppCompatActivity {
    private EditText et_username;
    private EditText et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register2);


        //初始化控件
        et_username=findViewById(R.id.et_username);
        et_password=findViewById(R.id.et_password);
        //回退
        findViewById(R.id.toolbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //返回
                finish();
            }
        });

        //点击注册
        findViewById(R.id.make_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserInfo newUser=new UserInfo();
                String username=et_username.getText().toString();
                String password=et_password.getText().toString();
                newUser.getUsername(username);
                newUser.getPassword(password);


                if(TextUtils.isEmpty(username)||TextUtils.isEmpty(password)){
                    Toast.makeText(RegisterActivity.this, "请输入用户名或密码", Toast.LENGTH_SHORT).show();
                }else{
                    /*
                    向数据库中添加数据
                     */
                    int getSuccess= DBManager.Insert_to_User_table(username,password,0);
                    if(getSuccess>0){
                        Toast.makeText(RegisterActivity.this, "注册成功，请登录", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else{
                        Log.i("Tag","向数据库掺入数据失败");
                        /*
                        返回注册失败的逻辑 如用户已存在
                         */
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