package com.example.hello_world;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.view.View; // 导入 View
import android.app.ActivityManager; // 导入 ActivityManager
import android.content.Context;     // 导入 Context
import android.widget.Toast;

public class FocusActivity extends AppCompatActivity {

    //private static final long FOCUS_DURATION = 25 * 60 * 1000; // 25分钟专注时间
    private static final long INTERVAL = 1000; // 1秒更新一次

    private Button screenOnButton;
    private Button pauseButton;
    private Button exitButton;
    private TextView timerTextView;
    private TextView poetryTextView;
    private TextView projectNameTextView; // 新增：显示项目名称的TextView
    private CountDownTimer countDownTimer;
    private boolean isPaused = false;
    private boolean screenOn = true;
    private long timeLeft;
    private String focusMode; // 存储选定的模式 ("strict" 或 "casual")
    private String projectName; // 新增：存储项目名称
    private long initialFocusDuration; // 新增：存储初始专注时长（毫秒）
    // 诗句库
    private final String[] POETRY = {
            "一寸光阴一寸金，寸金难买寸光阴。",
            "书山有路勤为径，学海无涯苦作舟。",
            "宝剑锋从磨砺出，梅花香自苦寒来。",
            "千磨万击还坚劲，任尔东西南北风。",
            "业精于勤，荒于嬉；行成于思，毁于随。",
            "锲而舍之，朽木不折；锲而不舍，金石可镂。",
            "莫等闲，白了少年头，空悲切。",
            "盛年不重来，一日难再晨。及时当勉励，岁月不待人。"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus);

        // 1. 在 onCreate() 中，获取 Intent 传递的模式。
        Intent intent = getIntent();
        if (intent != null) {
            focusMode = intent.getStringExtra("focus_mode");
            projectName = intent.getStringExtra("project_name"); // 从 Intent 获取项目名称
            initialFocusDuration = intent.getLongExtra("focus_duration_ms", 0); // 从 Intent 获取专注时长
        } else {
            focusMode = "casual";
            projectName = "无标题专注"; // 提供默认值
            initialFocusDuration = 25 * 60 * 1000; // 提供默认时长
        }
        // 初始化视图
        timerTextView = findViewById(R.id.timerTextView);
        poetryTextView = findViewById(R.id.poetryTextView);
        screenOnButton = findViewById(R.id.screenOnButton);
        pauseButton = findViewById(R.id.pauseButton);
        exitButton = findViewById(R.id.exitButton);
        projectNameTextView = findViewById(R.id.projectNameTextView);

        // 2. 根据获取到的模式，条件性地执行 UI 调整 和 屏幕固定。
        if ("strict".equals(focusMode)) {
            // UI 调整：隐藏暂停按钮，修改退出按钮文本
            pauseButton.setVisibility(View.GONE); // 将暂停按钮设置为 GONE（不占空间地隐藏）
            exitButton.setText("返回 (强制模式)"); // 更改退出按钮文本
            // 屏幕固定：如果是强制模式，调用 startLockTask()
            startLockTask(); // 启动屏幕固定
            Toast.makeText(this, "已进入专注强制模式，无法切换应用或退出！", Toast.LENGTH_LONG).show();
            // 您还可以进一步调整其他UI元素，比如改变颜色或显示锁定图标
            //timerTextView.setTextColor(getResources().getColor(android.R.color.holo_red_light)); // 例如，强制模式下文字变红
        } else {
            // 休闲模式下的 UI 默认状态，确保可见
            pauseButton.setVisibility(View.VISIBLE);
            exitButton.setText("结束");
            //timerTextView.setTextColor(getResources().getColor(android.R.color.holo_purple)); // 恢复默认白色
        }

        // 显示项目名称 (现在 projectNameTextView 已经不为 null 了)
        if (projectNameTextView != null) { // 添加一个非空检查是好的编程习惯
            projectNameTextView.setText("项目：" + (projectName != null ? projectName : "无标题专注"));
        }
        // 初始化倒计时，使用动态获取的时长
        timeLeft = initialFocusDuration;
        updateCountDownText();
        // 初始化倒计时
        //timeLeft = FOCUS_DURATION;
        //updateCountDownText();

        // 显示随机诗句
        showRandomPoetry();

        // 初始化屏幕常亮
        keepScreenOn(screenOn);

        // 设置按钮点击监听
        setListeners();

        // 开始倒计时
        startTimer();
    }

    private void setListeners() {
        // 屏幕常亮开关
        screenOnButton.setOnClickListener(v -> {
            screenOn = !screenOn;
            keepScreenOn(screenOn);
            screenOnButton.setText("常亮：" + (screenOn ? "开" : "关"));
        });

        // 暂停/继续按钮
        pauseButton.setOnClickListener(v -> {
            if ("strict".equals(focusMode)) {
                // 强制模式下，即使按钮可见（虽然我们已将其GONE），点击也无效
                Toast.makeText(this, "强制模式下无法暂停！", Toast.LENGTH_SHORT).show();
                return; // 直接返回，不执行暂停逻辑
            }
            if (isPaused) {
                startTimer();
                pauseButton.setText("暂停");
            } else {
                pauseTimer();
                pauseButton.setText("继续");
            }
        });

        // 结束按钮
        exitButton.setOnClickListener(v -> {
            if ("strict".equals(focusMode)) {
                // 强制模式下，点击退出按钮只显示提示
                Toast.makeText(this, "强制模式下无法提前结束，请坚持！", Toast.LENGTH_SHORT).show();
            } else {
                // 休闲模式下，显示退出确认对话框
                showExitDialog();
            }
        });
    }

    private void startTimer() {
        isPaused = false;
        countDownTimer = new CountDownTimer(timeLeft, INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timerTextView.setText("00:00");
                timeLeft = 0;
                // 5. 在 CountDownTimer 的 onFinish() 方法中，如果之前启用了屏幕固定，则调用 stopLockTask() 解除锁定。
                if ("strict".equals(focusMode)) {
                    stopLockTask(); // 强制模式倒计时结束，解除屏幕固定
                }

                // 专注完成提示
                new AlertDialog.Builder(FocusActivity.this)
                        .setTitle("专注结束")
                        .setMessage("恭喜！您已完成一次专注")
                        .setPositiveButton("好的", (dialog, which) -> finishAndReturnToMain())
                        .setCancelable(false) // 强制用户点击“好的”
                        .show();
            }
        }.start();
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            isPaused = true;
        }
    }

    private void updateCountDownText() {
        int minutes = (int) (timeLeft / 1000) / 60;
        int seconds = (int) (timeLeft / 1000) % 60;

        timerTextView.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void showRandomPoetry() {
        int index = (int) (Math.random() * POETRY.length);
        poetryTextView.setText(POETRY[index]);
    }

    private void keepScreenOn(boolean on) {
        if (on) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void showExitDialog() {
        new AlertDialog.Builder(this)
                .setTitle("确认结束专注?")
                .setMessage("您确定要提前结束本次专注吗？")
                .setPositiveButton("确认", (dialog, which) -> finishAndReturnToMain())
                .setNegativeButton("取消", null)
                .setCancelable(true)
                .show();
    }

    private void finishAndReturnToMain() {
        Intent intent = new Intent(this, MainActivity.class);

        // 添加标志清除当前任务栈并创建新任务
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        // 如果当前是强制模式，并且 Activity 仍然处于锁定任务模式，则停止锁定
        if ("strict".equals(focusMode) && isInLockTaskMode()) {
            stopLockTask();
        }
    }
    /**
     * 4. 重写 onBackPressed() 方法，根据模式决定是否阻止返回并显示提示。
     */
    @Override
    public void onBackPressed() {
        if ("strict".equals(focusMode)) {
            // 在强制模式下，阻止返回并显示提示
            Toast.makeText(this, "强制模式下无法退出，请坚持完成专注！", Toast.LENGTH_SHORT).show();
            // 不调用 super.onBackPressed() 来阻止 Activity 关闭
        } else {
            // 休闲模式下，允许返回
            super.onBackPressed();
        }
    }

    /**
     * 辅助方法：检查当前是否处于锁定任务模式
     */
    private boolean isInLockTaskMode() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            // Android Q (API 29) 及更高版本
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                return activityManager.getLockTaskModeState() != ActivityManager.LOCK_TASK_MODE_NONE;
            } else {
                // 旧版本 API (Android P 及更早版本)
                return activityManager.isInLockTaskMode();
            }
        }
        return false;
    }
}