package com.example.hello_world;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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
    TabLayout tl;
    ViewPager vp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        tl=findViewById(R.id.record_tabs);
        vp=findViewById(R.id.record_viewpager);

        initViewPager();
    }

    private void initViewPager(){
        List<Fragment>fragmentsList=new ArrayList<>();
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
        switch (view.getId()){
            case R.id.record_back:
                finish();
                break;
        }
    }
}