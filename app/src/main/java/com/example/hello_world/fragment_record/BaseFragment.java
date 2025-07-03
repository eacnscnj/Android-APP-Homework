package com.example.hello_world.fragment_record;

import android.inputmethodservice.KeyboardView;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hello_world.Database.AccountIn;
import com.example.hello_world.Database.DBManager;
import com.example.hello_world.Database.TypeIn;
import com.example.hello_world.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public abstract class BaseFragment extends Fragment {

    protected TypeBaseAdapter adapter;

    public BaseFragment() {
        // Required empty public constructor
    }
    EditText studyEdit;
    KeyboardView keyboardView;
    ImageView typeImage;
    TextView typeText,noteText,timeText;
    GridView typeGrid;
    List<TypeIn>typeInList;
    AccountIn accountIn;//输入的数据
    protected int currentUserId; // **新增：存储当前用户ID**

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountIn = new AccountIn();
        accountIn.setTypename("其他");
        accountIn.setFocusImageID(R.mipmap.more_fs);

        // **从 Arguments 中获取用户 ID**
        if (getArguments() != null) {
            currentUserId = getArguments().getInt("USER_ID", -1);
            if (currentUserId == -1) {
                Log.e("BaseFragment", "Error: User ID not found in arguments.");
                // 考虑处理未获取到用户ID的情况，例如显示错误信息或返回
            } else {
                Log.d("BaseFragment", "Fragment initialized for user ID: " + currentUserId);
            }
        } else {
            Log.e("BaseFragment", "Error: No arguments passed to fragment.");
            currentUserId = DBManager.getCurrentUserId(); // 备用：尝试从 DBManager 获取
            Log.e("BaseFragment", "Attempted to retrieve user ID from DBManager: " + currentUserId);
        }
        accountIn.setUserId(currentUserId); // **设置 accountIn 的 userId**
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_major, container, false);
        initView(view);
        setInitTime();

        loadDataToGrid();
        setGridListener();
        return view;
    }

    private void setInitTime() {
        Date date = new Date();
        SimpleDateFormat t = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String timee = t.format(date);
        timeText.setText(timee);
        accountIn.setTime(timee);

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        accountIn.setYear(year);
        accountIn.setMounth(month);
        accountIn.setDay(day);
    }

    private void setGridListener(){
        typeGrid.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        adapter.selectPosition = i;
                        adapter.notifyDataSetInvalidated();
                        TypeIn typeIn = typeInList.get(i);
                        String typename = typeIn.getTypename();
                        typeText.setText(typename);
                        accountIn.setTypename(typename);
                        typeImage.setImageResource(typeIn.getFocusImageID());
                        accountIn.setFocusImageID(typeIn.getFocusImageID());
                    }
                }
        );
    }

    public void loadDataToGrid(){
        // 此处不再直接调用 DBManager.getTypeList(kind)，而是由子类实现
        // 因为 getTypeList 不需要 userId 过滤
    }

    private void initView(View view){
        keyboardView = view.findViewById(R.id.fragment_record_keyboard);
        studyEdit = view.findViewById(R.id.fragment_record_top_edit);
        typeImage = view.findViewById(R.id.fragment_record_top_image);
        noteText = view.findViewById(R.id.fragment_record_notes);
        timeText = view.findViewById(R.id.fragment_record_time);
        typeGrid = view.findViewById(R.id.fragment_record_gridView);
        typeText = view.findViewById(R.id.fragment_container_view_text);

        studyEdit.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return false;
            }
        });
    }

    public void triggerSaveAccount() {
        String inputText = studyEdit.getText().toString();
        if (TextUtils.isEmpty(inputText) || inputText.equals("0") || Float.parseFloat(inputText) <= 0) {
            Toast.makeText(getContext(), "请输入有效的学习时长！", Toast.LENGTH_SHORT).show();
            return;
        }
        float studyTime = Float.parseFloat(inputText);
        accountIn.setStudyTime(studyTime);

        // 添加备注信息
        String note = noteText.getText().toString().trim();
        if (!TextUtils.isEmpty(note) && !note.equals("添加备注...")) {
            accountIn.setNote(note);
        } else {
            accountIn.setNote("");
        }

        // **再次确认 userId 已设置**
        if (accountIn.getUserId() == 0 || accountIn.getUserId() == -1) { // 检查 userId 是否有效
            accountIn.setUserId(DBManager.getCurrentUserId()); // 尝试从 DBManager 获取
            Log.w("BaseFragment", "accountIn.userId was not set, retrieving from DBManager: " + accountIn.getUserId());
        }

        Log.d("BaseFragmentDebug", "AccountIn data before saving:");
        Log.d("BaseFragmentDebug", "Type: " + accountIn.getTypename());
        Log.d("BaseFragmentDebug", "Study Time: " + accountIn.getStudyTime());
        Log.d("BaseFragmentDebug", "Date: " + accountIn.getYear() + "-" + accountIn.getMounth() + "-" + accountIn.getDay());
        Log.d("BaseFragmentDebug", "Time String: " + accountIn.getTime());
        Log.d("BaseFragmentDebug", "Note: " + accountIn.getNote());
        Log.d("BaseFragmentDebug", "User ID: " + accountIn.getUserId()); // **打印 userId**


        saveAccountToDB();
        Toast.makeText(getContext(), "记录已保存！", Toast.LENGTH_SHORT).show();
    }

    public abstract void saveAccountToDB() ;

}