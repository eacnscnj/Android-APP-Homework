package com.example.hello_world;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hello_world.Database.AccountIn;
import com.example.hello_world.Database.DBManager;
import com.example.hello_world.Database.TypeIn;
import com.example.hello_world.adapter.AccountAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView todayLv;
    List<AccountIn>mDatas;
    AccountAdapter adapter;
    int year,month,day;
    private final int[] FOCUS_DURATIONS_MINUTES = {5, 10, 15, 20, 25, 30, 45, 60};
    private void initTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH)+1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        todayLv=findViewById(R.id.main_lv);
        mDatas=new ArrayList<>();
        adapter = new AccountAdapter(this,mDatas);
        todayLv.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDBData();
    }

    private void loadDBData(){
        List<AccountIn> list = DBManager.getAccountListFromAccounttb(year, month, day);
        mDatas.clear();
        mDatas.addAll(list);
        adapter.notifyDataSetChanged();
    }

    final int search = R.id.main_iv_search;

    public void onClick(View view) {
        switch (view.getId()){
            case search:

                break;
            case R.id.main_btn_edit:
                Intent jmp=new Intent(this,RecordActivity.class);
                startActivity(jmp);
                finish();
                break;
            case R.id.main_btn_more:

                break;
            case R.id.main_btn_focus:  // 专注功能按钮
                showIntegratedFocusConfigDialog();
                //Intent focusIntent = new Intent(this, FocusActivity.class);
                //startActivity(focusIntent);
                // 添加过渡动画（可选）
                // overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }
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