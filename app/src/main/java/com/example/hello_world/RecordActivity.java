package com.example.hello_world;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
    TabLayout tl; // 顶栏, 切换专业课和公共课
    ViewPager vp; // 支持滑动
    private List<Fragment> fragmentsList; // 存储fragment
    private static final String TAG = "RecordActivityDebug";

    // 初始化记录页面
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        tl=findViewById(R.id.record_tabs);
        vp=findViewById(R.id.record_viewpager);

        initViewPager();
    }

    // 初始化fragmentlist, 装载两个fragment, 并设置vp和tl
    private void initViewPager(){
        fragmentsList=new ArrayList<>();
        MajorFragment mf= new MajorFragment();
        PublicFragment pf=new PublicFragment();

        fragmentsList.add(pf);
        fragmentsList.add(mf);

        // adapter负责提供页面数量、每个页面的fragment实例和标签页的名称
        RecordPagerAdapter pagerAdapter = new RecordPagerAdapter(getSupportFragmentManager(),fragmentsList);
        vp.setAdapter(pagerAdapter);
        tl.setupWithViewPager(vp); // 点击tl的标签会切换vp, 滑动vp会切换tl, 以adapter为中间件
    }

    // 专注记录中处理点击事件
    @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        Log.d(TAG, "onClick method triggered for view ID: " + view.getId());
        switch (view.getId()) {
            case R.id.record_back:
                Log.d(TAG, "Navigating back to MainActivity.");
                Intent jmp = new Intent(RecordActivity.this, MainActivity.class);
                startActivity(jmp);
                finish();
                break;
            case R.id.record_save_button: // 处理新增的保存按钮点击事件
                Log.d(TAG, "Save button clicked. Attempting to trigger save in fragment.");
                // 获取当前显示的 Fragment
                int currentItem = vp.getCurrentItem();
                if (fragmentsList != null && currentItem < fragmentsList.size()) {
                    Fragment currentFragment = fragmentsList.get(currentItem);
                    if (currentFragment instanceof BaseFragment) {
                        Log.d(TAG, "Calling triggerSaveAccount() on BaseFragment.");
                        // 调用 BaseFragment 中的保存方法
                        ((BaseFragment) currentFragment).triggerSaveAccount(); // 调用新的保存触发方法
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