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
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivityDebug";
    private AppCompatButton editButton; // 增加数据
    private ImageButton moreButton; // 更多
    private AppCompatButton statisticsButton; // 可视化
    private AppCompatButton focusButton; // 专注
    private boolean isMenuOpen = false; // 存储更多菜单是否开启

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
    }

    // 入口点
    @Override
    protected void onCreate(Bundle savedInstanceState) { // 参数是该页面销毁时保存的数据,用于恢复
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // 设置视图

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        }); // 处理窗口避免内容被UI遮挡

        DBManager.initDB(this); // 初始化所需的数据库

        // 获取当前用户ID
        currentUserId = DBManager.getCurrentUserId();
        if (currentUserId == -1) {
            Log.e(TAG, "Error: currentUserId is -1. User might not be logged in or ID not set.");
        } else {
            Log.d(TAG, "MainActivity initialized for user ID: " + currentUserId);
        }

        initTime();

        // 设置按钮对应的icon
        editButton = findViewById(R.id.main_btn_edit);
        statisticsButton = findViewById(R.id.main_btn_statistics);
        focusButton = findViewById(R.id.main_btn_focus);
        moreButton = findViewById(R.id.main_btn_more);

        // 设置底边栏的icon
        navRecordContainer = findViewById(R.id.nav_record_container);
        navCommunityContainer = findViewById(R.id.nav_community_container);
        navMineContainer = findViewById(R.id.nav_mine_container);

        // 底边栏设置
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

    // 切换 Fragment, 使用事务方式, 先隐藏再通过筛选显示特定页面
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

    // 处理返回到该页面时的加载, 使该Activity可见
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivity onResume called.");
        currentUserId = DBManager.getCurrentUserId();
        if (currentUserId == -1) {
            Log.e(TAG, "Error: currentUserId is -1 in onResume.");
        }
    }

    // 处理该页面的点击事件
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_iv_back:
                Intent backjmp = new Intent(this, LoginActivity.class);
                startActivity(backjmp);
                break;
            case R.id.main_btn_edit:
                Log.d(TAG, "Edit (Record) button clicked. Navigating to RecordActivity.");
                toggleRadialMenu();
                Intent jmp = new Intent(this, RecordActivity.class); // 组件之间的通信
                jmp.putExtra("USER_ID", currentUserId); // 放入用户ID以供后续使用
                startActivity(jmp); // 启动对应组件
                break;
            case R.id.main_btn_statistics:
                Log.d(TAG, "Statistics button clicked. Navigating to StatisticsActivity.");
                toggleRadialMenu();
                Intent statsJmp = new Intent(this, StatisticsActivity.class);
                statsJmp.putExtra("USER_ID", currentUserId);
                startActivity(statsJmp);
                break;
            case R.id.main_btn_focus:
                Log.d(TAG, "Focus button clicked. (Add your custom action here)");
                toggleRadialMenu();
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

    // 二级菜单打开/关闭时的动画
    private void toggleRadialMenu() {
        if (isMenuOpen) {
            // 关闭菜单时
            animateMenuClose(editButton, 0);
            animateMenuClose(statisticsButton, 1);
            animateMenuClose(focusButton, 2);

            moreButton.animate().rotation(0f).setDuration(300).start();
            isMenuOpen = false;
        } else {
            // 打开菜单时
            editButton.setVisibility(View.VISIBLE);
            statisticsButton.setVisibility(View.VISIBLE);
            focusButton.setVisibility(View.VISIBLE);

            animateMenuOpen(editButton, 0);
            animateMenuOpen(statisticsButton, 1);
            animateMenuOpen(focusButton, 2);

            moreButton.animate().rotation(45f).setDuration(300).start();
            isMenuOpen = true;
        }
    }

    // 对应二级菜单的开启动画
    private void animateMenuOpen(View view, int index) {
        // 设置偏移位置
        double startAngleDegrees = -175;
        double angleIncrement = 40;
        double currentAngleDegrees = startAngleDegrees + (index * angleIncrement);
        double angleRad = Math.toRadians(currentAngleDegrees);
        float targetX = (float) (Math.cos(angleRad) * radius);
        float targetY = (float) (Math.sin(angleRad) * radius);

        Log.d(TAG, "Animating " + view.getId() + " to X: " + targetX + ", Y: " + targetY);

        // 设置动画
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

    // 二级菜单关闭
    private void animateMenuClose(View view, int index) {
        double startAngleDegrees = -175;
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
        // 关闭动画结束后, 二级菜单不再占用位置
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

                int selectedKind;
                int selectedTypeId = -1; // 新增：用于存储选定 TypeIn 的数据库ID
                int selectedFocusImageId = 0; // 新增：用于存储选定 TypeIn 的 focusImageID
                if (selectedMajorPos > 0) {
                    // 用户选择了专业课
                    // 从 majorCourseTypeList 中获取对应的 TypeIn 对象
                    // 因为 majorCourseNames 包含了提示项，所以实际数据索引需要减1
                    TypeIn selectedType = majorCourseTypeList.get(selectedMajorPos - 1);
                    projectName = selectedType.getTypename();
                    selectedKind = selectedType.getKind(); // 从 TypeIn 对象获取 kind
                    selectedTypeId = selectedType.getId(); // 获取数据库ID
                    selectedFocusImageId = selectedType.getFocusImageID(); // 获取 focusImageID
                } else if (selectedGeneralPos > 0) {
                    // 用户选择了通识课
                    // 从 generalCourseTypeList 中获取对应的 TypeIn 对象
                    TypeIn selectedType = generalCourseTypeList.get(selectedGeneralPos - 1);
                    projectName = selectedType.getTypename();
                    selectedKind = selectedType.getKind(); // 从 TypeIn 对象获取 kind
                    selectedTypeId = selectedType.getId(); // 获取数据库ID
                    selectedFocusImageId = selectedType.getFocusImageID(); // 获取 focusImageID
                } else {
                    Toast.makeText(MainActivity.this, "请选择一个专注类别！", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 如果所有验证都通过，则启动 Activity 并关闭对话框
                Intent intent = new Intent(MainActivity.this, FocusActivity.class);
                intent.putExtra("focus_mode", focusMode);
                intent.putExtra("project_name", projectName);
                intent.putExtra("focus_duration_ms", focusDurationMs);
                intent.putExtra("selected_kind",selectedKind);
                intent.putExtra("selected_focus_image_id", selectedFocusImageId); // <-- 传递 focusImageID
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

