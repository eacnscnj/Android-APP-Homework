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
        if (editButton == null) {
            Log.e(TAG, "Error: editButton is null! Check R.id.main_btn_edit in XML.");
        } else {
            Log.d(TAG, "editButton initialized successfully.");
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
        switch (view.getId()) {
            case R.id.main_iv_search:
                Log.d(TAG, "Search button clicked.");
                break;
            case R.id.main_btn_edit:
                Log.d(TAG, "Edit (Record) button clicked. Navigating to RecordActivity.");
                // 点击子菜单后，可以自动收起菜单
                toggleRadialMenu();
                Intent jmp = new Intent(this, RecordActivity.class);
                startActivity(jmp);
                // 不建议在这里 finish()，因为这会关闭 MainActivity，返回时无法看到。
                // 如果需要，可以在 RecordActivity 返回时刷新 MainActivity。
                // finish();
                break;
            case R.id.main_btn_more:
                Log.d(TAG, "More button clicked. Toggling radial menu.");
                toggleRadialMenu();
                break;
        }
    }

    /**
     * 切换径向菜单的展开/收起状态
     */
    private void toggleRadialMenu() {
        if (isMenuOpen) {
            // 收起菜单
            animateMenuClose(editButton, 0); // 假设只有一个子菜单“记一下”
            // animateMenuClose(anotherButton, 1); // 如果有更多按钮，按顺序传入
            isMenuOpen = false;
        } else {
            // 展开菜单
            editButton.setVisibility(View.VISIBLE); // 确保子菜单可见
            animateMenuOpen(editButton, 0); // 假设只有一个子菜单“记一下”
            // animateMenuOpen(anotherButton, 1); // 如果有更多按钮，按顺序传入
            isMenuOpen = true;
        }
    }

    /**
     * 展开子菜单动画
     * @param view 要展开的子菜单视图
     * @param index 子菜单在所有子菜单中的索引 (用于计算角度)
     */
    private void animateMenuOpen(View view, int index) {
        // 计算角度 (以弧度表示)
        // 这里只是一个简单的示例，假设只有1个子菜单，或者你需要均匀分布多个子菜单。
        // 如果有多个子菜单，你需要根据子菜单的数量来计算每个子菜单的角度。
        // 例如，如果有3个子菜单，角度可以是 -30度, 0度, 30度 (相对于垂直方向)
        double currentAngleDegrees = -135; // 示例：从左上角开始展开，每个间隔45度
        double angleRad = Math.toRadians(currentAngleDegrees);

        float targetX = (float) (Math.cos(angleRad) * radius);
        float targetY = (float) (Math.sin(angleRad) * radius);

        Log.d(TAG, "Animating " + view.getId() + " to X: " + targetX + ", Y: " + targetY);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(view, "translationX", 0, targetX),
                ObjectAnimator.ofFloat(view, "translationY", 0, targetY),
                ObjectAnimator.ofFloat(view, "alpha", 0f, 1f), // 淡入动画
                ObjectAnimator.ofFloat(view, "scaleX", 0.5f, 1f), // 缩放动画
                ObjectAnimator.ofFloat(view, "scaleY", 0.5f, 1f)
        );
        animatorSet.setDuration(300); // 动画持续时间
        animatorSet.start();
    }

    /**
     * 收起子菜单动画
     * @param view 要收起的子菜单视图
     * @param index 子菜单在所有子菜单中的索引 (与展开动画对应)
     */
    private void animateMenuClose(final View view, int index) {
        double currentAngleDegrees = -135; // 保持一致
        double angleRad = Math.toRadians(currentAngleDegrees);

        float startX = (float) (Math.cos(angleRad) * radius);
        float startY = (float) (Math.sin(angleRad) * radius);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(view, "translationX", startX, 0),
                ObjectAnimator.ofFloat(view, "translationY", startY, 0),
                ObjectAnimator.ofFloat(view, "alpha", 1f, 0f), // 淡出动画
                ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.5f), // 缩放动画
                ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.5f)
        );
        animatorSet.setDuration(300); // 动画持续时间
        animatorSet.addListener(new android.animation.Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(android.animation.Animator animation) {}
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                view.setVisibility(View.GONE); // 动画结束后隐藏视图
            }
            @Override
            public void onAnimationCancel(android.animation.Animator animation) {}
            @Override
            public void onAnimationRepeat(android.animation.Animator animation) {}
        });
        animatorSet.start();
    }
}