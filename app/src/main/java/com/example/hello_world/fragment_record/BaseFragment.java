package com.example.hello_world.fragment_record;

import android.inputmethodservice.KeyboardView;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountIn = new AccountIn();
        accountIn.setTypename("其他");
        accountIn.setFocusImageID(R.mipmap.more_fs);
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
        SimpleDateFormat t = new SimpleDateFormat("yyyy年mm月dd日 HH:mm");
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
                if (i == EditorInfo.IME_ACTION_DONE) {
                    String inputText = textView.getText().toString();
                    if(TextUtils.isEmpty(inputText)||inputText.equals("0")){
                        getActivity().finish();
                        return false;
                    }
                    float studyTime = Float.parseFloat(inputText);
                    accountIn.setStudyTime(studyTime);

                    saveAccountToDB();

                    getActivity().finish();
                    return true;
                }
                return false;
            }
        });
    }

    public abstract void saveAccountToDB() ;

}