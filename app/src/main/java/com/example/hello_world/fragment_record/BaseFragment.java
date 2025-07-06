package com.example.hello_world.fragment_record;

import android.inputmethodservice.KeyboardView;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    protected int currentUserId;
    private ImageView addSubjectBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountIn = new AccountIn();
        accountIn.setTypename("其他");
        accountIn.setFocusImageID(R.mipmap.more_fs);

        if (getArguments() != null) {
            currentUserId = getArguments().getInt("USER_ID", -1);
            if (currentUserId == -1) {
                Log.e("BaseFragment", "Error: User ID not found in arguments.");
            } else {
                Log.d("BaseFragment", "Fragment initialized for user ID: " + currentUserId);
            }
        } else {
            Log.e("BaseFragment", "Error: No arguments passed to fragment.");
            currentUserId = DBManager.getCurrentUserId(); // 备用：尝试从 DBManager 获取
            Log.e("BaseFragment", "Attempted to retrieve user ID from DBManager: " + currentUserId);
        }
        accountIn.setUserId(currentUserId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_major, container, false);
        initView(view);
        setInitTime();

        loadDataToGrid(); // 首次加载数据
        setGridListener();
        setAddSubjectListener(); // 设置添加学科监听器
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

    // 设置监听器
    private void setAddSubjectListener() {
        addSubjectBtn.setOnClickListener(v -> showAddSubjectDialog());
    }

    // 添加学科
    private void showAddSubjectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("添加新学科");

        // 设置输入框
        final EditText input = new EditText(getContext());
        input.setHint("请输入学科名称");
        builder.setView(input);

        // 设置确定按钮
        builder.setPositiveButton("添加", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String subjectName = input.getText().toString().trim();
                if (TextUtils.isEmpty(subjectName)) {
                    Toast.makeText(getContext(), "学科名称不能为空！", Toast.LENGTH_SHORT).show();
                } else {
                    addNewSubject(subjectName);
                }
            }
        });

        // 设置取消按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    // 添加学科后刷新页面
    private void addNewSubject(String subjectName) {
        boolean success = DBManager.insertNewType(subjectName, getFragmentKind());
        if (success) {
            Toast.makeText(getContext(), "学科“" + subjectName + "”添加成功！", Toast.LENGTH_SHORT).show();
            loadDataToGrid(); // 刷新 GridView
        } else {
            Toast.makeText(getContext(), "学科“" + subjectName + "”已存在或添加失败！", Toast.LENGTH_SHORT).show();
        }
    }


    public void loadDataToGrid(){
    }

    // 初始视图
    private void initView(View view){
        keyboardView = view.findViewById(R.id.fragment_record_keyboard);
        studyEdit = view.findViewById(R.id.fragment_record_top_edit);
        typeImage = view.findViewById(R.id.fragment_record_top_image);
        noteText = view.findViewById(R.id.fragment_record_notes);
        timeText = view.findViewById(R.id.fragment_record_time);
        typeGrid = view.findViewById(R.id.fragment_record_gridView);
        typeText = view.findViewById(R.id.fragment_container_view_text);
        addSubjectBtn = view.findViewById(R.id.fragment_record_add_subject_btn);

        studyEdit.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return false;
            }
        });
    }

    // 设置专注时间
    public void triggerSaveAccount() {
        String inputText = studyEdit.getText().toString();
        if (TextUtils.isEmpty(inputText) || inputText.equals("0") || Float.parseFloat(inputText) <= 0) {
            Toast.makeText(getContext(), "请输入有效的学习时长！", Toast.LENGTH_SHORT).show();
            return;
        }
        float studyTime = Float.parseFloat(inputText);
        accountIn.setStudyTime(studyTime);

        String note = noteText.getText().toString().trim();
        if (!TextUtils.isEmpty(note) && !note.equals("添加备注...")) {
            accountIn.setNote(note);
        } else {
            accountIn.setNote("");
        }

        if (accountIn.getUserId() == 0 || accountIn.getUserId() == -1) {
            accountIn.setUserId(DBManager.getCurrentUserId());
            Log.w("BaseFragment", "accountIn.userId was not set, retrieving from DBManager: " + accountIn.getUserId());
        }

        Log.d("BaseFragmentDebug", "AccountIn data before saving:");
        Log.d("BaseFragmentDebug", "Type: " + accountIn.getTypename());
        Log.d("BaseFragmentDebug", "Study Time: " + accountIn.getStudyTime());
        Log.d("BaseFragmentDebug", "Date: " + accountIn.getYear() + "-" + accountIn.getMounth() + "-" + accountIn.getDay());
        Log.d("BaseFragmentDebug", "Time String: " + accountIn.getTime());
        Log.d("BaseFragmentDebug", "Note: " + accountIn.getNote());
        Log.d("BaseFragmentDebug", "User ID: " + accountIn.getUserId());


        saveAccountToDB();
        Toast.makeText(getContext(), "记录已保存！", Toast.LENGTH_SHORT).show();
    }

    public abstract void saveAccountToDB() ;

    public abstract int getFragmentKind();
}