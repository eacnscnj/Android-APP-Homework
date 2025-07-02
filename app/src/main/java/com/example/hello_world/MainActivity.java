package com.example.hello_world;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hello_world.Database.AccountIn;
import com.example.hello_world.Database.DBManager;
import com.example.hello_world.adapter.AccountAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AccountAdapter.OnItemDeleteListener {

    ListView todayLv;
    List<AccountIn>mDatas;
    AccountAdapter adapter;
    int year,month,day;
    private static final String TAG = "MainActivityDebug";

    private void initTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1; // 月份是从0开始的，所以需要加1
        day = calendar.get(Calendar.DAY_OF_MONTH);
        Log.d(TAG, "Current date initialized to: " + year + "-" + month + "-" + day);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 确保 DBManager 在 Activity 创建时被初始化
        DBManager.initDB(this); // 如果你的 initDB 放在 Application 类中，这里可以省略

        initTime();

        todayLv = findViewById(R.id.main_lv); // 确认你的 ListView ID
        mDatas = new ArrayList<>();
        adapter = new AccountAdapter(this, mDatas);
        todayLv.setAdapter(adapter);

        // !!! 设置删除监听器 !!!
        adapter.setOnItemDeleteListener(this);

        // Initial data load when activity is created
        loadDBData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivity onResume called.");
//        initTime(); // 再次确保日期是最新的
        loadDBData();
    }

    private void loadDBData(){
        List<AccountIn> list = DBManager.getAllAccountList();
        Log.d(TAG, "Loaded " + list.size() + " total items from DB.");
        mDatas.clear();
        mDatas.addAll(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemDeleted() {
        // 当 AccountAdapter 中的某个条目被删除时，会调用此方法
        Log.d(TAG, "onItemDeleted callback received, reloading data.");
        // 重新从数据库加载数据以确保UI完全同步
        loadDBData();
    }

    final int search = R.id.main_iv_search;

    public void onClick(View view) {
        switch (view.getId()){
            case search:
                Log.d(TAG, "Search button clicked.");
                break;
            case R.id.main_btn_edit:
                Log.d(TAG, "Edit (Record) button clicked. Navigating to RecordActivity.");
                Intent jmp=new Intent(this,RecordActivity.class);
                startActivity(jmp);
                finish();
                break;
            case R.id.main_btn_more:
                Log.d(TAG, "More button clicked.");
                break;
        }
    }
}