package com.example.hello_world;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
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
    List<AccountIn> mDatas;
    AccountAdapter adapter;
    int year, month, day;
    private static final String TAG = "MainActivityDebug";

    private AppCompatButton editButton;
    private ImageButton moreButton;
    private AppCompatButton statisticsButton;
    private boolean isMenuOpen = false;

    private int radius = 400;

    private int currentUserId; // **新增：存储当前用户ID**

    //用于切换底栏选中与跳转
    private LinearLayout navRecordContainer, navCommunityContainer, navMineContainer;
    private LinearLayout currentSelectedContainer;


    private void initTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        Log.d(TAG, "Current date initialized to: " + year + "-" + month + "-" + day);
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

        // **获取当前用户ID**
        currentUserId = DBManager.getCurrentUserId();
        if (currentUserId == -1) {
            Log.e(TAG, "Error: currentUserId is -1. User might not be logged in or ID not set.");
            // 考虑在此处跳转到登录界面或给出错误提示
            // finish();
            // Intent loginIntent = new Intent(this, LoginActivity.class);
            // startActivity(loginIntent);
            // return;
        } else {
            Log.d(TAG, "MainActivity initialized for user ID: " + currentUserId);
        }


        initTime();

        todayLv = findViewById(R.id.main_lv);
        mDatas = new ArrayList<>();
        adapter = new AccountAdapter(this, mDatas, currentUserId); // **修改：传入 currentUserId**
        todayLv.setAdapter(adapter);
        adapter.setOnItemDeleteListener(this);

        editButton = findViewById(R.id.main_btn_edit);
        statisticsButton = findViewById(R.id.main_btn_statistics);
        if (statisticsButton == null) {
            Log.e(TAG, "Error: statisticsButton is null! Check R.id.main_btn_edit in XML.");
        } else {
            Log.d(TAG, "statisticsButton initialized successfully.");
        }
        moreButton = findViewById(R.id.main_btn_more);

        loadDBData();




        navRecordContainer = findViewById(R.id.nav_record_container);
        navCommunityContainer = findViewById(R.id.nav_community_container);
        navMineContainer = findViewById(R.id.nav_mine_container);

// 默认选中第一个
        currentSelectedContainer = navRecordContainer;
        currentSelectedContainer.setSelected(true);

    }

    //设置底栏按钮状态
    private void selectNavContainer(LinearLayout selected) {
        if (currentSelectedContainer != null) {
            currentSelectedContainer.setSelected(false);
        }
        selected.setSelected(true);
        currentSelectedContainer = selected;
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivity onResume called.");
        // **在 onResume 再次获取确保用户 ID 是最新的，尽管通常在 onCreate 已经设置**
        currentUserId = DBManager.getCurrentUserId();
        if (currentUserId == -1) {
            Log.e(TAG, "Error: currentUserId is -1 in onResume.");
            // 再次处理未登录情况
        }
        loadDBData();
    }

    private void loadDBData() {
        // **修改：调用 DBManager.getAllAccountList() 时传入 currentUserId**
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

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.main_iv_search:
                Log.d(TAG, "Search button clicked.");
                break;
            case R.id.main_btn_edit:
                Log.d(TAG, "Edit (Record) button clicked. Navigating to RecordActivity.");
                toggleRadialMenu();
                Intent jmp=new Intent(this,RecordActivity.class);
                // **传递 currentUserId 到 RecordActivity**
                jmp.putExtra("USER_ID", currentUserId);
                startActivity(jmp);
                break;
            case R.id.main_btn_statistics:
                Log.d(TAG, "Statistics button clicked. Navigating to StatisticsActivity.");
                toggleRadialMenu();
                Intent statsJmp = new Intent(this, StatisticsActivity.class);
                // **传递 currentUserId 到 StatisticsActivity**
                statsJmp.putExtra("USER_ID", currentUserId);
                startActivity(statsJmp);
                break;
            case R.id.main_btn_more:
                Log.d(TAG, "More button clicked. Toggling radial menu.");
                toggleRadialMenu();
                break;
                //底栏选中
            case R.id.nav_record_container:
                selectNavContainer(navRecordContainer);
                // 跳转或切换界面
                break;

            case R.id.nav_community_container:
                selectNavContainer(navCommunityContainer);
                break;

            case R.id.nav_mine_container:
                selectNavContainer(navMineContainer);
                break;

        }
    }

    // ... (toggleRadialMenu, animateMenuOpen, animateMenuClose 方法保持不变) ...
    /**
     * 切换径向菜单的展开/收起状态
     * 子菜单在这里加入到收起和展开列表中
     */
    private void toggleRadialMenu() {
        if (isMenuOpen) {
            // 收起菜单
            animateMenuClose(editButton, 0);
            animateMenuClose(statisticsButton, 1);
            moreButton.animate().rotation(0f).setDuration(300).start();
            isMenuOpen = false;
        } else {
            // 展开菜单
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

    private void animateMenuClose(final View view, int index) {
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