package com.example.hello_world;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton; // 确保导入
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

    private AppCompatButton editButton; // “记一下”按钮
    private ImageButton moreButton; // “更多”按钮
    private AppCompatButton statisticsButton;
    private boolean isMenuOpen = false; // 标志菜单是否打开

    private int radius = 400; // 展开圆的半径 (单位: 像素)。根据UI效果调整

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
        setContentView(R.layout.activity_main); // EdgeToEdge可以按需保留或移除

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        DBManager.initDB(this);

        initTime();

        todayLv = findViewById(R.id.main_lv);
        mDatas = new ArrayList<>();
        adapter = new AccountAdapter(this, mDatas);
        todayLv.setAdapter(adapter);
        adapter.setOnItemDeleteListener(this);

        // 获取按钮引用
        editButton = findViewById(R.id.main_btn_edit);
        statisticsButton = findViewById(R.id.main_btn_statistics);
        if (statisticsButton == null) {
            Log.e(TAG, "Error: statisticsButton is null! Check R.id.main_btn_edit in XML.");
        } else {
            Log.d(TAG, "statisticsButton initialized successfully.");
        }
        moreButton = findViewById(R.id.main_btn_more);

        loadDBData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivity onResume called.");
        loadDBData();
    }

    private void loadDBData() {
        List<AccountIn> list = DBManager.getAllAccountList();
        Log.d(TAG, "Loaded " + list.size() + " total items from DB.");
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
                toggleRadialMenu(); // 点击子菜单后，自动收起菜单
                Intent jmp=new Intent(this,RecordActivity.class);
                startActivity(jmp);
                break;
            case R.id.main_btn_statistics: // 新增 case
                Log.d(TAG, "Statistics button clicked. Navigating to StatisticsActivity.");
                toggleRadialMenu(); // 点击子菜单后，自动收起菜单
                Intent statsJmp = new Intent(this, StatisticsActivity.class);
                startActivity(statsJmp);
                break;
            case R.id.main_btn_more:
                Log.d(TAG, "More button clicked. Toggling radial menu.");
                toggleRadialMenu();
                break;
        }
    }

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
        double startAngleDegrees = -160; // 调整起始角度，例如从左下方开始
        double angleIncrement = 40; // 调整每个按钮之间的角度间隔

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