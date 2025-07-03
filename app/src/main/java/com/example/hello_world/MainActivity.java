package com.example.hello_world;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hello_world.Database.DBManager;
import com.example.hello_world.fragments.RecordFragment;
import com.example.hello_world.fragments.CommunityFragment;
import com.example.hello_world.fragments.MineFragment;

public class MainActivity extends AppCompatActivity /* implements AccountAdapter.OnItemDeleteListener */ {

    private static final String TAG = "MainActivityDebug";

    private AppCompatButton editButton;
    private ImageButton moreButton;
    private AppCompatButton statisticsButton;
    private boolean isMenuOpen = false;

    private int radius = 400;

    private int currentUserId; // 当前用户ID

    // 底栏容器
    private LinearLayout navRecordContainer, navCommunityContainer, navMineContainer;
    private LinearLayout currentSelectedContainer;

    // 三个Fragment
    private RecordFragment recordFragment;
    private CommunityFragment communityFragment;
    private MineFragment mineFragment;

    private void initTime() {
        // 这里如果不用时间可删，否则保留
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        DBManager.initDB(this);

        // 获取当前用户ID
        currentUserId = DBManager.getCurrentUserId();
        if (currentUserId == -1) {
            Log.e(TAG, "Error: currentUserId is -1. User might not be logged in or ID not set.");
        } else {
            Log.d(TAG, "MainActivity initialized for user ID: " + currentUserId);
        }

        initTime();

        // 以下注释，数据和适配器逻辑已移至 RecordFragment
        /*
        todayLv = findViewById(R.id.main_lv);
        mDatas = new ArrayList<>();
        adapter = new AccountAdapter(this, mDatas, currentUserId);
        todayLv.setAdapter(adapter);
        adapter.setOnItemDeleteListener(this);
        */

        editButton = findViewById(R.id.main_btn_edit);
        statisticsButton = findViewById(R.id.main_btn_statistics);
        if (statisticsButton == null) {
            Log.e(TAG, "Error: statisticsButton is null! Check R.id.main_btn_edit in XML.");
        } else {
            Log.d(TAG, "statisticsButton initialized successfully.");
        }
        moreButton = findViewById(R.id.main_btn_more);

        /*
        loadDBData();
        */

        navRecordContainer = findViewById(R.id.nav_record_container);
        navCommunityContainer = findViewById(R.id.nav_community_container);
        navMineContainer = findViewById(R.id.nav_mine_container);

        // 默认选中第一个
        currentSelectedContainer = navRecordContainer;
        currentSelectedContainer.setSelected(true);

        // 初始化 Fragment
        recordFragment = new RecordFragment();
        communityFragment = new CommunityFragment();
        mineFragment = new MineFragment();

        switchFragment(recordFragment);
    }

    // 设置底栏按钮状态
    private void selectNavContainer(LinearLayout selected) {
        if (currentSelectedContainer != null) {
            currentSelectedContainer.setSelected(false);
        }
        selected.setSelected(true);
        currentSelectedContainer = selected;
    }

    // 切换 Fragment
    private void switchFragment(androidx.fragment.app.Fragment targetFragment) {
        androidx.fragment.app.FragmentManager fm = getSupportFragmentManager();
        androidx.fragment.app.FragmentTransaction transaction = fm.beginTransaction();

        if (recordFragment.isAdded()) transaction.hide(recordFragment);
        if (communityFragment.isAdded()) transaction.hide(communityFragment);
        if (mineFragment.isAdded()) transaction.hide(mineFragment);

        if (!targetFragment.isAdded()) {
            transaction.add(R.id.fragment_container, targetFragment);
        }
        transaction.show(targetFragment);
        transaction.commitAllowingStateLoss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivity onResume called.");
        currentUserId = DBManager.getCurrentUserId();
        if (currentUserId == -1) {
            Log.e(TAG, "Error: currentUserId is -1 in onResume.");
        }
        // 数据刷新逻辑放到各Fragment中
    }

    /*
    private void loadDBData() {
        List<AccountIn> list = DBManager.getAllAccountList(currentUserId);
        Log.d(TAG, "Loaded " + list.size() + " items for user " + currentUserId + " from DB.");
        mDatas.clear();
        mDatas.addAll(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemDeleted() {
        Log.d(TAG, "onItemDeleted callback received, reloading data.");
        loadDBData();
    }
    */

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_iv_search:
                Log.d(TAG, "Search button clicked.");
                break;
            case R.id.main_btn_edit:
                Log.d(TAG, "Edit (Record) button clicked. Navigating to RecordActivity.");
                toggleRadialMenu();
                Intent jmp = new Intent(this, RecordActivity.class);
                jmp.putExtra("USER_ID", currentUserId);
                startActivity(jmp);
                break;
            case R.id.main_btn_statistics:
                Log.d(TAG, "Statistics button clicked. Navigating to StatisticsActivity.");
                toggleRadialMenu();
                Intent statsJmp = new Intent(this, StatisticsActivity.class);
                statsJmp.putExtra("USER_ID", currentUserId);
                startActivity(statsJmp);
                break;
            case R.id.main_btn_more:
                Log.d(TAG, "More button clicked. Toggling radial menu.");
                toggleRadialMenu();
                break;
            case R.id.nav_record_container:
                selectNavContainer(navRecordContainer);
                switchFragment(recordFragment);
                break;
            case R.id.nav_community_container:
                selectNavContainer(navCommunityContainer);
                switchFragment(communityFragment);
                break;
            case R.id.nav_mine_container:
                selectNavContainer(navMineContainer);
                switchFragment(mineFragment);
                break;
        }
    }

    // toggleRadialMenu 和动画相关代码保持不变

    private void toggleRadialMenu() {
        if (isMenuOpen) {
            animateMenuClose(editButton, 0);
            animateMenuClose(statisticsButton, 1);
            moreButton.animate().rotation(0f).setDuration(300).start();
            isMenuOpen = false;
        } else {
            editButton.setVisibility(View.VISIBLE);
            statisticsButton.setVisibility(View.VISIBLE);
            animateMenuOpen(editButton, 0);
            animateMenuOpen(statisticsButton, 1);
            moreButton.animate().rotation(45f).setDuration(300).start();
            isMenuOpen = true;
        }
    }

    private void animateMenuOpen(View view, int index) {
        double startAngleDegrees = -160;
        double angleIncrement = 40;
        double currentAngleDegrees = startAngleDegrees + (index * angleIncrement);
        double angleRad = Math.toRadians(currentAngleDegrees);
        float targetX = (float) (Math.cos(angleRad) * radius);
        float targetY = (float) (Math.sin(angleRad) * radius);

        Log.d(TAG, "Animating " + view.getId() + " to X: " + targetX + ", Y: " + targetY);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(view, "translationX", 0, targetX),
                ObjectAnimator.ofFloat(view, "translationY", 0, targetY),
                ObjectAnimator.ofFloat(view, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(view, "scaleX", 0.5f, 1f),
                ObjectAnimator.ofFloat(view, "scaleY", 0.5f, 1f)
        );
        animatorSet.setDuration(300);
        animatorSet.setStartDelay(index * 50);
        animatorSet.start();
    }

    private void animateMenuClose(View view, int index) {
        double startAngleDegrees = -160;
        double angleIncrement = 40;
        double currentAngleDegrees = startAngleDegrees + (index * angleIncrement);
        double angleRad = Math.toRadians(currentAngleDegrees);
        float startX = (float) (Math.cos(angleRad) * radius);
        float startY = (float) (Math.sin(angleRad) * radius);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(view, "translationX", startX, 0),
                ObjectAnimator.ofFloat(view, "translationY", startY, 0),
                ObjectAnimator.ofFloat(view, "alpha", 1f, 0f),
                ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.5f),
                ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.5f)
        );
        animatorSet.setDuration(300);
        animatorSet.setStartDelay(index * 50);
        animatorSet.addListener(new android.animation.Animator.AnimatorListener() {
            @Override public void onAnimationStart(android.animation.Animator animation) {}
            @Override public void onAnimationEnd(android.animation.Animator animation) {
                view.setVisibility(View.GONE);
            }
            @Override public void onAnimationCancel(android.animation.Animator animation) {}
            @Override public void onAnimationRepeat(android.animation.Animator animation) {}
        });
        animatorSet.start();
    }
}
