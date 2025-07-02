package com.example.hello_world;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.hello_world.adapter.RecordPagerAdapter;
import com.example.hello_world.fragment_record.BaseFragment;
import com.example.hello_world.fragment_record.MajorFragment;
import com.example.hello_world.fragment_record.PublicFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class RecordActivity extends AppCompatActivity {
    TabLayout tl;
    ViewPager vp;
    private List<Fragment> fragmentsList;
    private static final String TAG = "RecordActivityDebug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        tl=findViewById(R.id.record_tabs);
        vp=findViewById(R.id.record_viewpager);

        initViewPager();
    }

    private void initViewPager(){
        fragmentsList=new ArrayList<>();
        MajorFragment mf= new MajorFragment();
        PublicFragment pf=new PublicFragment();

        fragmentsList.add(pf);
        fragmentsList.add(mf);

        RecordPagerAdapter pagerAdapter = new RecordPagerAdapter(getSupportFragmentManager(),fragmentsList);
        vp.setAdapter(pagerAdapter);
        tl.setupWithViewPager(vp);
    }

    @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        Log.d(TAG, "onClick method triggered for view ID: " + view.getId()); // <-- 添加日志
        switch (view.getId()) {
            case R.id.record_back:
                Log.d(TAG, "Navigating back to MainActivity.");
                Intent jmp = new Intent(RecordActivity.this, MainActivity.class);
                startActivity(jmp);
                finish(); // 添加 finish() 确保 RecordActivity 被关闭
                break;
            case R.id.record_save_button: // 处理新增的保存按钮点击事件
                Log.d(TAG, "Save button clicked. Attempting to trigger save in fragment."); // <-- 添加日志
                // 获取当前显示的 Fragment
                int currentItem = vp.getCurrentItem();
                if (fragmentsList != null && currentItem < fragmentsList.size()) {
                    Fragment currentFragment = fragmentsList.get(currentItem);
                    if (currentFragment instanceof BaseFragment) {
                        Log.d(TAG, "Calling triggerSaveAccount() on BaseFragment."); // <-- 添加日志
                        // 调用 BaseFragment 中的保存方法
                        ((BaseFragment) currentFragment).triggerSaveAccount(); // 调用新的保存触发方法
                        // 保存后可以根据需要决定是否关闭当前Activity
                        // 这里我们选择关闭，回到MainActivity
//                        Intent jmpToMain = new Intent(RecordActivity.this, MainActivity.class);
//                        startActivity(jmpToMain);
//                        finish();
                    } else {
                        Log.e(TAG, "Current fragment is not an instance of BaseFragment: " + currentFragment.getClass().getName());
                    }
                } else {
                    Log.e(TAG, "fragmentsList is null or currentItem (" + currentItem + ") is out of bounds.");
                }
                break;
        }
    }
}