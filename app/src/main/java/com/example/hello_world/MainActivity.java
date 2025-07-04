package com.example.hello_world;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hello_world.Database.AccountIn;
import com.example.hello_world.Database.DBManager;
import com.example.hello_world.Database.TypeIn;
import com.example.hello_world.fragments.RecordFragment;
import com.example.hello_world.fragments.CommunityFragment;
import com.example.hello_world.fragments.MineFragment;
import com.example.hello_world.adapter.AccountAdapter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity /* implements AccountAdapter.OnItemDeleteListener */ {

    private static final String TAG = "MainActivityDebug";

    private AppCompatButton editButton;
    private ImageButton moreButton;
    private AppCompatButton statisticsButton;
    private AppCompatButton focusButton; // 新增
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


    ListView todayLv;
    List<AccountIn>mDatas;
    AccountAdapter adapter;
    int year,month,day;
    private final int[] FOCUS_DURATIONS_MINUTES = {5, 10, 15, 20, 25, 30, 45, 60};

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
        focusButton = findViewById(R.id.main_btn_focus); // 获取引用
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
            case R.id.main_btn_focus: // <-- 新增的 case
                Log.d(TAG, "Focus button clicked. (Add your custom action here)");
                toggleRadialMenu(); // 点击后隐藏径向菜单，如果需要
                // 在这里添加 main_btn_focus 被点击时你希望执行的特定逻辑。
                showIntegratedFocusConfigDialog();
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
            // 关闭菜单时
            animateMenuClose(editButton, 0);
            animateMenuClose(statisticsButton, 1);
            animateMenuClose(focusButton, 2); // <-- 为 focusButton 添加关闭动画，使用新索引 (例如 2)

            moreButton.animate().rotation(0f).setDuration(300).start();
            isMenuOpen = false;
        } else {
            // 打开菜单时
            editButton.setVisibility(View.VISIBLE);
            statisticsButton.setVisibility(View.VISIBLE);
            focusButton.setVisibility(View.VISIBLE); // <-- 设置 focusButton 可见

            animateMenuOpen(editButton, 0);
            animateMenuOpen(statisticsButton, 1);
            animateMenuOpen(focusButton, 2); // <-- 为 focusButton 添加打开动画，使用新索引 (例如 2)

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

    private void showIntegratedFocusConfigDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("配置专注");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_focus_config, null);
        builder.setView(dialogView);

        //final EditText projectNameEditText = dialogView.findViewById(R.id.editTextProjectName);
        final Spinner spinnerMajorCourse = dialogView.findViewById(R.id.spinnerMajorCourse);   // 专业课 Spinner
        final Spinner spinnerGeneralCourse = dialogView.findViewById(R.id.spinnerGeneralCourse); // 通识课 Spinner
        final EditText editTextFocusDuration = dialogView.findViewById(R.id.editTextFocusDuration); // 新增
        final RadioGroup focusModeRadioGroup = dialogView.findViewById(R.id.radioGroupFocusMode);
        final RadioButton casualModeRadioButton = dialogView.findViewById(R.id.radioCasualMode); // 休闲模式
        final RadioButton strictModeRadioButton = dialogView.findViewById(R.id.radioStrictMode); // 强制模式
// --- 新增代码：为记录类型 Spinner 设置数据 ---
        // --- 1. 加载和设置专业课 Spinner ---
        List<TypeIn> majorCourseTypeList = DBManager.getTypeList(1); // 假设 kind=1 代表专业课
        List<String> majorCourseNames = new ArrayList<>();
        majorCourseNames.add("选择专业课"); // 添加提示项
        if (majorCourseTypeList != null && !majorCourseTypeList.isEmpty()) {
            for (TypeIn type : majorCourseTypeList) {
                majorCourseNames.add(type.getTypename());
            }
        }
        ArrayAdapter<String> majorCourseAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                majorCourseNames
        );
        majorCourseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMajorCourse.setAdapter(majorCourseAdapter);
        spinnerMajorCourse.setSelection(0); // 默认选中提示项

        // --- 2. 加载和设置通识课 Spinner ---
        List<TypeIn> generalCourseTypeList = DBManager.getTypeList(0); // 假设 kind=0 代表通识课
        List<String> generalCourseNames = new ArrayList<>();
        generalCourseNames.add("选择通识课"); // 添加提示项
        if (generalCourseTypeList != null && !generalCourseTypeList.isEmpty()) {
            for (TypeIn type : generalCourseTypeList) {
                generalCourseNames.add(type.getTypename());
            }
        }
        ArrayAdapter<String> generalCourseAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                generalCourseNames
        );
        generalCourseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGeneralCourse.setAdapter(generalCourseAdapter);

        spinnerGeneralCourse.setSelection(0); // 默认选中提示项

        spinnerGeneralCourse.setSelection(1); // 默认选中提示项


        // --- 3. 设置 Spinner 联动监听 (实现二选一逻辑) ---
        spinnerMajorCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 如果专业课 Spinner 选择了非“请选择专业课”项，则重置通识课 Spinner
                if (position > 0) {
                    spinnerGeneralCourse.setSelection(0); // 将通识课 Spinner 强制选回第一个提示项
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerGeneralCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 如果通识课 Spinner 选择了非“请选择通识课”项，则重置专业课 Spinner
                if (position > 0) {
                    spinnerMajorCourse.setSelection(0); // 将专业课 Spinner 强制选回第一个提示项
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        // --- Spinner 联动监听结束 ---

        // --- 4. 设置专注时长 EditText ---
        // 可以设置一个默认值，例如25分钟
        editTextFocusDuration.setText("25"); // 默认25分钟
        // 也可以设置光标在文本末尾
        editTextFocusDuration.setSelection(editTextFocusDuration.getText().length());


        // 设置默认选中休闲模式 (已在XML中设置，这里可省略或用于代码强制设置)
        casualModeRadioButton.setChecked(true);

        // --- 关键改变：不在这里直接设置 PositiveButton 的 OnClickListener ---
        builder.setPositiveButton("开始专注", null); // 设置为 null，我们将在对话框显示后手动处理点击事件
        builder.setNegativeButton("取消", null);

        final AlertDialog dialog = builder.create(); // 创建对话框实例
        dialog.show(); // 显示对话框


        // --- 关键改变：手动获取 PositiveButton 并设置监听器 ---
        Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 执行您的验证逻辑
                String projectName;
                int selectedMajorPos = spinnerMajorCourse.getSelectedItemPosition();
                int selectedGeneralPos = spinnerGeneralCourse.getSelectedItemPosition();

                if (selectedMajorPos > 0) {
                    projectName = majorCourseNames.get(selectedMajorPos);
                } else if (selectedGeneralPos > 0) {
                    projectName = generalCourseNames.get(selectedGeneralPos);
                } else {
                    Toast.makeText(MainActivity.this, "请选择一个专注类别！", Toast.LENGTH_SHORT).show();
                    // 这里不调用 dialog.dismiss(); 阻止对话框关闭
                    return;
                }

                String durationInput = editTextFocusDuration.getText().toString().trim();
                long focusDurationMs;
                if (durationInput.isEmpty()) {
                    Toast.makeText(MainActivity.this, "请输入专注时长！", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    int selectedDurationMinutes = Integer.parseInt(durationInput);
                    if (selectedDurationMinutes <= 0) {
                        Toast.makeText(MainActivity.this, "专注时长必须大于0分钟！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    focusDurationMs = (long) selectedDurationMinutes * 60 * 1000;
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "专注时长输入无效，请输入数字！", Toast.LENGTH_SHORT).show();
                    return;
                }

                String focusMode;
                int selectedModeId = focusModeRadioGroup.getCheckedRadioButtonId();
                if (selectedModeId == R.id.radioStrictMode) {
                    focusMode = "strict";
                } else {
                    focusMode = "casual";
                }

                // 如果所有验证都通过，则启动 Activity 并关闭对话框
                Intent intent = new Intent(MainActivity.this, FocusActivity.class);
                intent.putExtra("focus_mode", focusMode);
                intent.putExtra("project_name", projectName);
                intent.putExtra("focus_duration_ms", focusDurationMs);
                startActivity(intent);

                dialog.dismiss(); // 验证成功后手动关闭对话框
            }
        });
        // 可选：为取消按钮也设置监听，确保它能正常关闭对话框
        Button negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // 点击取消时关闭对话框
            }
        });
    }

}

