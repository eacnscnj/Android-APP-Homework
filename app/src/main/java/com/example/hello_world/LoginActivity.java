package com.example.hello_world;

import android.content.Intent;
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

public class LoginActivity extends AppCompatActivity {
    private EditText et_username;
    private EditText et_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);


        //初始化控件
        et_username=findViewById(R.id.et_username);
        et_password=findViewById(R.id.et_password);
        //点击注册
        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到注册页面
                Intent intent  =new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        //登录
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username =et_username.getText().toString();
                String password=et_password.getText().toString();
                if(TextUtils.isEmpty(username)||TextUtils.isEmpty(password)){
                    Toast.makeText(LoginActivity.this, "请输入用户名或密码", Toast.LENGTH_SHORT).show();
                }else{
                    //判断用户是否合法
                    UserInfo userInfo= DBManager.query_User_From_usertable(username,password);
                    if(userInfo==null){
                        Toast.makeText(LoginActivity.this, "用户名无效", Toast.LENGTH_SHORT).show();
                    }else if(userInfo.get_id()==-1){
                        Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                        //Log.i("tag","登录失败失败失败失败失败失败失败失败失败失败");
                    }else{
                        /*
                        待修改 ，没有做出不同用户的区分
                         */
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        Intent intent =new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
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