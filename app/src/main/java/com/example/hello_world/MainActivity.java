package com.example.hello_world;

import android.content.Intent;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

    ListView todayLv;
    List<AccountIn>mDatas;
    AccountAdapter adapter;
    int year,month,day;

    private void initTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH)+1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        todayLv=findViewById(R.id.main_lv);
        mDatas=new ArrayList<>();
        adapter = new AccountAdapter(this,mDatas);
        todayLv.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDBData();
    }

    private void loadDBData(){
        List<AccountIn> list = DBManager.getAccountListFromAccounttb(year, month, day);
        mDatas.clear();
        mDatas.addAll(list);
        adapter.notifyDataSetChanged();
    }

    final int search = R.id.main_iv_search;

    public void onClick(View view) {
        switch (view.getId()){
            case search:

                break;
            case R.id.main_btn_edit:
                Intent jmp=new Intent(this,RecordActivity.class);
                startActivity(jmp);
                //finish();
                break;
            case R.id.main_btn_more:

                break;
        }
    }
}