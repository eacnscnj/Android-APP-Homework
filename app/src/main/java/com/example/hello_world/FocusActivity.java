package com.example.hello_world;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class FocusActivity extends AppCompatActivity {

    private static final long FOCUS_DURATION = 25 * 60 * 1000; // 25分钟专注时间
    private static final long INTERVAL = 1000; // 1秒更新一次

    private Button screenOnButton;
    private Button pauseButton;
    private Button exitButton;
    private TextView timerTextView;
    private TextView poetryTextView;

    private CountDownTimer countDownTimer;
    private boolean isPaused = false;
    private boolean screenOn = true;
    private long timeLeft;

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

        // 初始化视图
        timerTextView = findViewById(R.id.timerTextView);
        poetryTextView = findViewById(R.id.poetryTextView);
        screenOnButton = findViewById(R.id.screenOnButton);
        pauseButton = findViewById(R.id.pauseButton);
        exitButton = findViewById(R.id.exitButton);

        // 初始化倒计时
        timeLeft = FOCUS_DURATION;
        updateCountDownText();

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
            if (isPaused) {
                startTimer();
                pauseButton.setText("暂停");
            } else {
                pauseTimer();
                pauseButton.setText("继续");
            }
        });

        // 结束按钮
        exitButton.setOnClickListener(v -> showExitDialog());
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
                // 专注完成提示
                new AlertDialog.Builder(FocusActivity.this)
                        .setTitle("专注结束")
                        .setMessage("恭喜！您已完成一次专注")
                        .setPositiveButton("好的", (dialog, which) -> finishAndReturnToRecord())
                        .setCancelable(false)
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
                .setPositiveButton("确认", (dialog, which) -> finishAndReturnToRecord())
                .setNegativeButton("取消", null)
                .setCancelable(true)
                .show();
    }

    private void finishAndReturnToRecord() {
        Intent intent = new Intent(this, RecordActivity.class);

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
    }
}